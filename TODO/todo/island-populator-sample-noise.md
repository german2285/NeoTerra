# Семплировать шум для архипелагов в IslandPopulator

`common/src/main/java/neoterra/world/worldgen/cell/terrain/populator/IslandPopulator.java:76`

```java
        cell.height = NoiseUtil.lerp(lowerHeight, upperHeight, alpha);
    }
    
    private static IslandType upperPopulator(Levels levels, int depth) {
    	// TODO sample noise for this, thisll give us the islands we want
    	float archipelagoMaxAlpha = 0.01F;
		float archipelagoMin = levels.water(5);
		float archipelagoMax = levels.water(depth);
```
