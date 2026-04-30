# @Deprecated на параметре seed публичного метода makePlains

`common/src/main/java/neoterra/world/worldgen/cell/terrain/Populators.java:97`

```java
      	return TerrainPopulator.make(TerrainType.FLATS, ground, height, DEFAULT_EROSION, DEFAULT_WEIRDNESS, scalingSettings);
    }

    public static TerrainPopulator makePlains(@Deprecated Seed seed, Noise ground, TerrainSettings.Terrain settings, float verticalScale) {
    	return makePlains(seed, ground, settings, settings, verticalScale);
    }
```
