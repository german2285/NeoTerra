# Старый LinearSplineFunction.builder в PresetTerrainProvider

`common/src/main/java/neoterra/data/worldgen/preset/PresetTerrainProvider.java:18-20`

```java
		WorldSettings worldSettings = preset.world();
		WorldSettings.Properties properties = worldSettings.properties;
		WorldSettings.ControlPoints controlPoints = worldSettings.controlPoints;
		
		TerrainSettings terrainSettings = preset.terrain();
		
//		return LinearSplineFunction.builder(ridge)
//			.addPoint(-1.0D, -0.4D)
//			.build();
		return DensityFunctions.constant(0.0D);
	}
}
```
