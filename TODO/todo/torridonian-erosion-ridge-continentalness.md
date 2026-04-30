# Erosion+ridge комбинации должны учитывать continentalness

`common/src/main/java/neoterra/world/worldgen/cell/terrain/Populators.java:234`

```java
		return TerrainPopulator.make(TerrainType.BADLANDS, ground, height, DEFAULT_EROSION, DEFAULT_WEIRDNESS, settings);
	}
	
	// TODO only use erosion + ridge combos that respect continentalness
	public static TerrainPopulator makeTorridonian(@Deprecated Seed seed, Noise ground, TerrainSettings.Terrain settings) {
		Noise plains = Noises.perlin(seed.next(), 100, 3);
		plains = Noises.warpPerlin(plains, seed.next(), 300, 1, 150.0F);
```
