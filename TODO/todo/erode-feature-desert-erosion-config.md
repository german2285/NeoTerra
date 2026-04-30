# Вынести desert erosion variance в конфиг

`common/src/main/java/neoterra/world/worldgen/feature/ErodeFeature.java:60`

```java
			Levels levels = heightmap.levels();
			Noise rand = Noises.white(heightmap.climate().randomSeed(), 1);
			// TODO expose desert erosion variance to config
			Noise desertErosionVariance = Noises.mul(Noises.perlin(435, 8, 1), levels.scale(16));
			BlockPos.MutableBlockPos pos = new MutableBlockPos();
			Config config = placeContext.config();
			for(int x = 0; x < 16; x++) {
```
