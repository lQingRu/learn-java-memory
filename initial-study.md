# Java Memory

## Memory allocation

### Stack

- Uses:
    - Execute threads
    - Store local variables (primitives, references to objects)
        - As stack can only remove the last item added (FIFO), works perfect for local variables as
          they come and go when enter or exit functions
- Used to execute a thread and may have short-lived values and references to other objects
- Variables that are stored exist for as long as the function that created them are running
- Memory is allocated and freed without managing the memory allocation
- Stack is populated during compile time

### Heap

- Uses:
    - Stores objects and JRE classes
- Stack does not work well for data whose lifecycle does not depend on individual functions
    - Heap then allows the adding or removal of data whenever we want
- Heap is not managed automatically
    - We need to free allocated memory ourselves when these blocks are no longer needed
- Objects in the heap are slower to access than stack
- Heap is dynamically allocated

## Garbage Collection

### How GC cleans up

- GC free heap memory by destroying the objects that don’t contain a reference so the memory can be
  reclaimed
- Various ways to dereference objects:
    - Making a reference null
    - Assigning a reference to another
    - Using an anonymous object
        - `register(new Student());`

# Understanding

## JVM

![img_6.png](static/img/img_6.png)

- BUT hard to find out thread memory used in thread stack
- Components affecting metaspace and stack size:
    - Circuit breaker
    - Service discovery
    - Message broker
    - Databases
    - ...

## Memory limits

1. Java:
   ![img.png](static/img/img.png)

- Around 60% limit hit, then OOM

2. openjdk:8u171-jre-alpine (just a minor version different from (1))

- Linux killed container
- Did not even get OOM, but no reply because container died

3. Java 11

- Similar to (1)
  ![img_1.png](static/img/img_1.png)
  ![img_2.png](static/img/img_2.png)

## Spring Boot

- SB can take up quite a lot of metaspace
  ![img_5.png](static/img/img_5.png)
- Beware of other parts of Java memory in addition to the heap that can take a significant part of
  memory

## Best practices

- Explicitly set JVM limits
  ![img_4.png](static/img/img_4.png)![img_3.png](static/img/img_3.png)
    - Relativity might be hard to know how much exactly set aside because java may only take a
      partial memory of machine
- Use proper tools to determine memory limits to set
    - Load simulator (e.g.: https://artillery.io/)
    - Memory metrics (e.g.: VisualVM, standard JDK tool)

# Questions

- How often does GC do cleaning up?
- How many threads running?
- Where does L1,L2,L3 cache reside?
- Do we need to switch to another GC?
- Any changes in key memory configuration from different JDK version would affect?
- How java memory perform different in containers?

# Extra

- Consider using `synchronized` to prevent write conflicts between different threads

# Resources

- https://www.youtube.com/watch?v=LCSqZyjBwWA&t=426s
- https://blogs.oracle.com/javamagazine/post/java-and-the-modern-cpu-part-1-memory-and-the-cache-hierarchy
- https://www.youtube.com/watch?v=zM1orGrPNSU
- https://www.quora.com/What-happens-to-dereferenced-objects-in-Java-Are-they-erased-from-the-memory-or-remain-there-inaccessible

## To read

- https://stackoverflow.com/questions/6915633/how-to-increase-the-rate-of-gc-calls-in-java
- https://docs.oracle.com/cd/E15289_01/JRPTG/tune_footprint.htm

# Contextualized

1. InitExternalStores -> another class execution
2. List of all profileIds
3. List of all minIO objects
4. Initialize ACL cache (?)
5. For loop, batch profiles (List<Long>)
    - For each batch, peekIterator on minIO
    - For each batch, retrieve te, sel, searchable fields
    - SetACL

## Hypothesis

- Batch size is too huge, objects too huge
- GC not clearing override objects??

# Tests

## Test 1

- Configuration:
    - BATCH_SIZE = 10000
    - NUM_OF_BATCH = 200
    - "Index" process = Summation to 10000
    - `List<ProfileBean> profileBeans = ProfileMockUtil.getProfileBeansByProfiles(profileBatch);`
      created in each batch

### `SingleIndexerService`:  New objects in every batch

**Results**
![img_2.png](img_2.png)
![img_3.png](img_3.png)
![img_4.png](img_4.png)
![img_5.png](img_5.png)
(- 25000, 1000)

### `RereferencedIndexerService`:  Re-referenced objects in every batch

**Results**
![img_10.png](img_10.png)
![img_7.png](img_7.png)
![img_8.png](img_8.png)
![img_9.png](img_9.png)

- Weird that even re-referenced has minimal GC activity, upon debugging saw this:
  ![img_11.png](img_11.png)
    - Increased number of `ProfileBean` by 1000

## Test 2

- Configuration:
    - BATCH_SIZE = 10000
    - NUM_OF_BATCH = 200
    - "Index" process = Summation to 10000
    - `List<ProfileBean> profileBeans = ProfileMockUtil.getProfileBeansByProfiles(profileBatch);`
      created in each batch
    - Add `System.gc()` - Though this is not 100% trigger GC, but increased frequency in GC

### `SingleIndexerService`:  New objects in every batch

![img_16.png](img_16.png)
![img_17.png](img_17.png)
![img_18.png](img_18.png)

### `RereferencedIndexerService`:  Re-referenced objects in every batch

**Results**

- ![img_12.png](img_12.png)
- ![img_13.png](img_13.png)
- ![img_14.png](img_14.png)
- ![img_15.png](img_15.png)

## Test 3

- Configuration:
    - BATCH_SIZE = 10000
    - NUM_OF_BATCH = 200
    - "Index" process = Summation to 10000
    - `List<ProfileBean> profileBeans = ProfileMockUtil.getProfileBeansByProfiles(profileBatch);`
      created in each batch
    - Add `System.gc()` - Though this is not 100% trigger GC, but increased frequency in GC
    - In blocks of 100

### `SingleIndexerService`:  New objects in every batch

![img_19.png](img_19.png)
![img_20.png](img_20.png)
![img_21.png](img_21.png)

- Stopped already
  ![img_22.png](img_22.png)
