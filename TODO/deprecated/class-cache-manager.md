# @Deprecated на классе CacheManager

`common/src/main/java/neoterra/concurrent/cache/CacheManager.java:12`

```java
import neoterra.concurrent.cache.map.LongMap;
import neoterra.concurrent.cache.map.StampedBoundLongMap;

@Deprecated
public class CacheManager {
	private static final List<Cache<?>> CACHES = Collections.synchronizedList(new LinkedList<>());
	
    public static <V extends ExpiringEntry> Cache<V> createCache(long expireTime, long pollInterval, TimeUnit unit) {
```
