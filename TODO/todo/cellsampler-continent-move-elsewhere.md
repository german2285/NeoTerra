# Перенести CONTINENT.read в другое место

`common/src/main/java/neoterra/world/worldgen/densityfunction/CellSampler.java:123`

```java
			public float read(Cell cell, Heightmap heightmap) {
				return cell.height;
			}
		},
		CONTINENT("continent") {
			
			//TODO move this somewhere else
			@Override
			public float read(Cell cell, Heightmap heightmap) {
				Levels levels = heightmap.levels();
				ControlPoints controlPoints = heightmap.controlPoints();
```
