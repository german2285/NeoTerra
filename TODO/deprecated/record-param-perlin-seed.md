# @Deprecated на параметре seed record'а Perlin

`common/src/main/java/neoterra/world/worldgen/noise/module/Perlin.java:10`

```java
import neoterra.world.worldgen.noise.NoiseUtil;
import neoterra.world.worldgen.noise.function.Interpolation;

record Perlin(@Deprecated int seed, float frequency, int octaves, float lacunarity, float gain, Interpolation interpolation, float min, float max) implements Noise {
	public static final MapCodec<Perlin> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("seed").forGetter(Perlin::seed),
```
