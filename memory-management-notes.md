# Memory Management in Java

# Garbage Collection

- What: Free up space by removing dead/unused objects in the memory heap and avoid memory leaks
- Each GC triggers stop-the-world events
    - The larger the memory = The longer the stop-the-world time

# Java Memory Model (JVM)

- JVM Heap memory is physically divided into 2 parts - Young and Old Generation

## Young Generation

- Purpose: All new objects are created
- Garbage collection: Minor GC
- Consist: Eden memory and two survivor memory spaces
- Implications:
    - Since there are only short-lived objects, Minor GC is very fast and application is not
      affected
- Process:
    - Most newly created objects are located in eden memory space
    - When eden space is filled, minor gc is performed and all survivor objects are moved to one of
      the survivor spaces
    - Minor GC also checks the survivor objects and move to the other survivor space; thus at a
      time, one survivor is always empty
    - Objects that survived many GC cycles are moved to Old generation
        - Usually done by setting a threshold for age of young generation objects

## Old Generation ("Tenured")

- Purpose: Objects that are long-lived and survived many rounds of Minor GC
- Garbage collection: Major GC
- Implications:
    - All application threads are stopped until GC operation completes, i.e. application will be
      unresponsive for the GC duration
        - Thus, if there's a responsive application, when major GC is ongoing, will notice a lot of
          timeout errors
    - Takes a long time as the GC checks all the live objects
        - Duration depends on GC strategy

## Permanent Generation ("Perm Gen")

- Purpose: Application metadata required by the JVM to describe the classes and methods used
    - Not part of Java Heap memory
    - Populated by JVM at runtime based on classes used
    - Also contains Java SE library classes and methods
- Garbage collection: Objects are garbage collected in a full garbage collection

### Memory Area

- Purpose: Store class structure (runtime constants and static variables), code for methods and
  constructors
    - Runtime constant pool: per-class runtime representation of constant pool in a class

# Memory Pool

- Created by JVM memory managers to create a pool of immutable objects
    - E.g.: String pool
- Can belong to Heap or Permanent Generation, depending on JVM memory manager implementation

# Java Heap Memory Switches

| VM Switch | VM Switch Description             |
|-----------|-----------------------------------|
| -Xms      | Initial heap size when JVM starts |
| -Xmx | Maximum heap size                 |
| -Xmn| Size of young generation, rest to Old Generation |
| -XX:PermGen | Initial size of Perm Gen memory |
| -XX:MaxPermGen | Maximum Perm Gen size |
| -XX:SurvivorRatio| Ratio of eden space and survivor space <br/> Default value = 8, i.e. 80% is eden space and 10% for each survivor space |
| -XX:NewRatio| Ratio of old and new generation sizes <br/> Default value = 2 |

## Java Memory Tuning

- Java has automatic garbage collection which involves 3 steps:

1. Marking: identifies which objects are in use and not in use
2. Normal deletion: GC removes unused objects and reclaim the free space to be allocated to other
   objects
3. Deletion with compacting: For better performance, after deleting unused objects, all the survived
   objects can be moved to be togher
    1. This will increase the performance of allocation of memory to newer objects

- There are 2 problems with a simple mark and delete approach:
    - Not efficient because most of the newly created objects will become unused
    - Objects that are in-use for multiple garbage collection cycle are most likely to be in-use for
      future cycles
- Hence, the generational garbage collection is meant to overcome the shortcomings

### Tuning approaches

- See [JVM Tuning with G1 GC](https://marknienaber.medium.com/jvm-tuning-with-g1-gc-76f27535f054)

# Garbage Collection

- Eden -> S0 -> S1 -> Old Gen
    - Survivor spaces exist to reduce the number of objects sent to the old generation and thus
      reduces the occurrence of major GC
        - Also because although many objects cannot be deleted after a minor GC, they do not live
          long

## Garbage-Collection roots

- Garbage collection will occur on objects that tare not reachable
    - When are objects considered not reachable?
        - As long as the application can reach these objects via the GC roots, they are considered
          reachable and will not be garbage collected
        - GC roots are always reachable (referenced by the JVM)
          - Simplified: a local variable of reference type that will always point to an object in the heap (provided it is not null)
- 4 kinds of GC roots:
    - Local variables
        - Kept alive by thread stack
    - Active java threads
    - Static variables and constants
        - Classes themselves can be Gc-ed which will remove all referenced static variables
    - JNI references (java objects that the native code has created as part of JNI call)

## Garbage Collector

- By default, Java 9 onwards uses Garbage-first (G1) collector

### G1 Collector

- Concurrent collector that optimizes throughput and latency
    - Application is thus not stopped
- Upon startup, the JVM sets the region size (1MB to 32MB depending on heap size) where the eden,
  survivor, and old generations are logical sets of thetse regions and are not contiguous

# Monitoring

- There are multiple ways to monitor the memory usage and garbage collection activity

## Console

- `jstat`: JVM statistics
    - See [Oracle jstat](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jstat.html)
- Application logs
    - `-Xloggc:/tmp/gc.log`: information about GC at each collection
    - `-XX:HeapDumpPath=/tmp/my-heap-dump.hprof`: heap dump

See [Oracle Throughput and Footprint Measurement](https://docs.oracle.com/en/java/javase/18/gctuning/garbage-collector-implementation.html#GUID-A24775AB-16A3-4B86-9963-76E5AC398A3E)

- Default JVM options and values
    - Run `java -XX:+PrintFlagsFinal -version` to see the available VM flags and default values
    - Run `jinfo -flags <pid>` to see the full command that was executed for running process

## UI

- VisualVM
    - VisualVM heapdump: `/private/var/folders/...`
- Jconsole
    - Part of JDK

# Out Of Memory Exception

## Cause of exception

-

See [Oracle Java 8 OOM Exception](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/memleaks002.html)

## Metrics to monitor

- When garbage collection happened
- How often GC is happening in the JVM
- How much memory is being collected each time
- How long GC is running for in the JVM
- Percentage of time spent by JVM for garbage collection
- What type of garbage collection happened - minor or full GC?
- JVM heap and non-heap memory usage
- CPU utilization of the JVM

### Average heap usage after each garbage collection

- If the baseline heap usage is consistently increasing after each garbage collection, it may
  indicate:
    - Application's memory requirements are growing
    - Memory leak (i.e. application is neglecting release references to objects that are no longer
      needed, unintentionally preventing them from getting garbage collected)

### Old generation size

- It should stay flat under normal circumstances
- If there is an unexpected increase in this metric, it could signal:
    - Java application is creating long-lived objects
    - Creating more humongous objects (they automatically get allocated to regions in the old
      generation)

# Resources

- [Java (JVM) Memory Model](https://www.digitalocean.com/community/tutorials/java-jvm-memory-model-memory-management-in-java#java-jvm-memory-model)
- [G1 Garbage Collector](https://www.oracle.com/technetwork/tutorials/tutorials-1876574.html)
- [Java Memory Management ](https://www.datadoghq.com/blog/java-memory-management/#jvm-runtime-metrics)
- [How does Garbage Collection work](https://www.alibabacloud.com/blog/595387)
- [Java SE 17 standard options for java](https://docs.oracle.com/en/java/javase/17/docs/specs/man/java.html#standard-options-for-java)
- [How to read GC activity logs](https://www.linkedin.com/pulse/javajvm-logsgc-logsg1gc-monday-jvm-logs-g1gc-phases-ślusarski/)


# TODO
https://trello.com/c/PG9SVsf2/158-java-memory-sharing-4-hours
## Immediate
### Reasoning
- [X] How to deal with humongous objects?
    - "In-order to reduce copying overhead, the Humongous objects are not included in any evacuation pause. A full garbage collection cycle compacts Humongous objects in place." - [source](https://www.oracle.com/technical-resources/articles/java/g1gc.html)
    - [ ] Flyweight 
    - "Humongous objects need contiguous space -> thus need help to prevent because may exit if cannot find enough contiguous space"
    - Mine: Some people recommend to increase the region size so that objects do not automatically fall under humongous objects
    - https://devblogs.microsoft.com/java/whats-the-deal-with-humongous-objects-in-java/ 

### Monitoring
- [ ] WRITE: What are the tools that I have used to help troubleshoot memory issues?
- [?] How the prometheus metrics could have helped
    - `jvm_gc_seconds.max`
        - `minorGC`
        - `G1 Evacuation Phase`
        - `G1 Humongous Allocation`


## Less Immediate

### Learning
- [ ] Learning
   - Indexing
   - Memory management
   - Monitoring
      - Pros
      - Cons
      - Industrial examples
   - Debug tools

### Extra
- [ ] Typical causes of high memory usage
- [ ] How to identify memory leak
- [ ] After thoughts
- [ ] Size of objects

### Understand
- [ ] Flame graph 
- [ ] async-profiler vs java profiler recorder

# Extra useful information
## Taming the mixed collections
- Affected flags:
  - `-XX:InitiatingHeapOccupancyPercent`
    - For changing marking threshold
  - `-XX:G1MixedGCLiveThresholdPercent` and `-XX:G1HeapWastePercent`
    - To change mixed garbage collections decisions
  - `-XX:G1MixedGCCountTarget` and `-XX:G1OldCSetRegionThresholdPercent`
    - When want to adjust CSet for old regions (C set is collection set - i.e. a set of regions that should be collected in the next cycle)
  - `XX:G1MixedGCLiveThresholdPercent`
    - For changing the threshold which determines whether a region should be added to the CSet or not
      - Only regions whose live data percentage are less than the threshold will be added to the CSet
    - The higher the threshold, the more likely a region will be added to the CSet, i.e. more mixed GC evacuation and longer evacuation time will happen
    - Old regions with most garbage is chose for
  - `-XX:G1HeapWastePercent`
    - Amount of reclaimable space, expressed as a % of the heap size that G1 will stop doing mixed GC's
    - If the amount of space that can be reclaimed from old generation regions compared to the total heap is less than this, G1 will stop mixed GCs