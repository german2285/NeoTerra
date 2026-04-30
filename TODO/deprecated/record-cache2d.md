# @Deprecated на record Cache2d

`common/src/main/java/neoterra/world/worldgen/noise/module/Cache2d.java:8`

```java
import com.mojang.serialization.MapCodec;
import neoterra.world.worldgen.util.PosUtil;

@Deprecated
public record Cache2d(Noise noise, ThreadLocal<Cached> cache) implements Noise {
	public static final MapCodec<Cache2d> CODEC = Noise.HOLDER_HELPER_CODEC.xmap(Cache2d::new, Cache2d::noise).fieldOf("value");
	
	public Cache2d(Noise noise) {
```
