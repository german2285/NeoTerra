# @Deprecated на параметре seed приватного метода makePlains

`common/src/main/java/neoterra/world/worldgen/cell/terrain/Populators.java:79`

```java
		return TerrainPopulator.make(TerrainType.FLATS, ground, height, DEFAULT_EROSION, DEFAULT_WEIRDNESS, settings);
    }
    
    private static TerrainPopulator makePlains(@Deprecated Seed seed, Noise ground, TerrainSettings.Terrain noiseSettings, TerrainSettings.Terrain scalingSettings, float verticalScale) {
    	int scaleH = Math.round(250.0F * noiseSettings.horizontalScale);
      	
		Noise erosion = Noises.perlin(seed.next(), scaleH * 2, 3, 3.75F);
```
