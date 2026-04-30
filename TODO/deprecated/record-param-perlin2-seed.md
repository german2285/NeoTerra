# @Deprecated на параметре seed record'а Perlin2

`common/src/main/java/neoterra/world/worldgen/noise/module/Perlin2.java:10`

```java
import neoterra.world.worldgen.noise.NoiseUtil;
import neoterra.world.worldgen.noise.function.Interpolation;

public record Perlin2(@Deprecated int seed, float frequency, int octaves, float lacunarity, float gain, Interpolation interpolation, float min, float max) implements Noise {
	public static final MapCodec<Perlin2> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("seed").forGetter(Perlin2::seed),
```
