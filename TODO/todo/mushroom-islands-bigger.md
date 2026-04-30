# Сделать грибные острова крупнее

`common/src/main/java/neoterra/data/worldgen/preset/settings/Presets.java:150`

```java
			), 
			new StructureSettings(),
			new MiscellaneousSettings(false, 600, false, true, false, false, false, false, true, true, true, 1.0F, 0.75F)
		); 
	}
	
	//TODO make mushroom islands bigger
	public static Preset makeLegacyBeautiful() {
		return new Preset(
			new WorldSettings(
				new Continent(ContinentType.MULTI, DistanceFunction.EUCLIDEAN, 3000, 0.8F, 0.25F, 0.25F, 5, 0.26F, 4.33F),
				new ControlPoints(IslandPopulator.DEFAULT_INLAND_POINT, IslandPopulator.DEFAULT_COAST_POINT, 0.1F, 0.25F, 0.326F, 0.448F, 0.5F), 
```
