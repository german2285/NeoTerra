# Голый TODO в PerformanceConfig.read

`common/src/main/java/neoterra/config/PerformanceConfig.java:17`

```java
    public static final int MAX_BATCH_COUNT = 20;
    public static final int MAX_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;

    //TODO
    public static DataResult<PerformanceConfig> read(Path path) {
    	return DataResult.success(makeDefault());
    }
```
