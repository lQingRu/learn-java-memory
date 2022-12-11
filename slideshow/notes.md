# Indexing:
- Arango: 1.30am ~ 1.35am
- Solr: 1.35am ~ 6am ===> 17:35 ~ 22:00



# Types of GC logs
- Problems to look out for in gc logs: https://www.papertrail.com/solution/tips/7-problems-to-look-out-for-when-analyzing-garbage-collection-logs/
- What GC logs mean: https://krzysztofslusarski.github.io/2021/08/10/monday-phases.html
## General  
- `Evacuation pause`: live objects are copied from 1 region to another region
- Format is: `GC (age of GC) Pause <region: Young, Full> (xx) (reason for GC) <before GC> -> <after GC> (<total available heap space>)`
   - Heap may shrink or expand, which may be because: https://stackoverflow.com/questions/2617159/java-heap-keeps-on-shrinking-what-is-happening-in-this-graph-of-heap-size
   - e.g.: After full GC, heap shrink

1. 
```
GC(1) Concurrent Mark Cycle
GC(1) Pause Remark xxx
GC(1) Pause Cleanup xxx
GC(1) Concurrent Mark Cycle xxx
```
   - `Pause Remark, Cleanup` - done at the end of concurrent cycle
      - `Pause Remark`: performs global processing of references, reclaiming empty regions, class unloading and some other G1GC internal cleanup
      - `Pause Cleanup` - this phase decides is any mixed collection needed

## Specific to indexing
https://www.linkedin.com/pulse/javajvm-logsgc-logsg1gc-monday-jvm-logs-g1gc-phases-ślusarski/
1. `Pause Young (Normal) (G1 Evacuation Phase)`
   - `G1 Evacuation Phase`: Classic reason, request for creating new object failed because there was not enough space in the eden
   - `Normal`: classic cleanup of the young generation
2. `Pause Young (Concurrent Start) (G1 Humongous Allocation)`
   - `Concurrent start`: cleanup of the young generation with preparation for concurrent mark phase
   - `G1 Humongous Allocation`: request for creating a new humongous object failed (??)
3. `Concurrent Undo Cycle`
4. `Pause Young (Prepare Mixed) (G1 Evacuation Pause)`
   - `Prepare Mixed`: cleanup of the young generation with preparation for cleaning the old generation
5. `Pause Young (Normal) (G1 Preventive Collection)`
6. `Pause Young (Normal) (GCLocker Initiated GC)`
   - `GCLocker Initiated GC`: G1GC cannot start immediately when any thread is in the JNI critical section, GC has to wait for threads to exit such a section - such a situation is marked as GCLocker Initiated GC (???)

# Analysis of GC logs
## Arango
- Almost all are just `Pause Young (Normal) (G1 Evacuation Phase)`
## Solr
- Around the start of solr indexing, there's a concurrent remark...


## What we want:
- G1GC to be triggered by as many `G1 Evacuation Phase`
- G1GC to not have `Pause Full` collections  
   - Long clear of the whole heap

# Questions:
1. Why only some cases there's the `Concurrent Mark Cycle`? 
2. Why there are gc logs that jump?
   - e.g.:
   ```
   GC(245) Concurrent Mark Cycle
   GC(245) Pause Remark xxx
   GC(246) Pause Young (Normal) (G1 Evacuation Pause) xxx
   GC(245) Pause Cleanup xxx
   GC(245) Concurrent Mark Cycle xxx
   ```
   - I think they are running concurrently, since can have >1 worker thread on GC