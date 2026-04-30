# @Deprecated на параметре seed метода makeSteppe

`common/src/main/java/neoterra/world/worldgen/cell/terrain/Populators.java:61`

```java
	public static CellPopulator makeCoast(Levels levels) {
		return new OceanPopulator(TerrainType.COAST, Noises.constant(levels.water));
	}
	
    public static TerrainPopulator makeSteppe(@Deprecated Seed seed, Noise ground, TerrainSettings.Terrain settings) {
        int scaleH = Math.round(250.0F * settings.horizontalScale);

        Noise erosion = Noises.perlin(seed.next(), scaleH * 2, 3, 3.75F);
```
