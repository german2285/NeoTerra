## @Deprecated на методе probabilityDensity(...)

`common/src/main/java/neoterra/data/worldgen/preset/PresetNoiseRouterData.java:163`

```java
    private static DensityFunction jaggednessPerformanceHack() {
    	return DensityFunctions.add(DensityFunctions.zero(), DensityFunctions.zero());
    }

    // do this a different way, since this affects the size of the cave as well
    @Deprecated
    private static DensityFunction probabilityDensity(float probability, DensityFunction function) {
    	if(probability == 0.0F) {
    		return DensityFunctions.constant(1.0F);
    	}
    	return DensityFunctions.add(DensityFunctions.constant(1.0F - probability), function);
    }
```

Используется в том же файле в строках 59 (`NoiseRouterData.ENTRANCES`), 60 (`NoiseRouterData.SPAGHETTI_2D`), 111 (`caveCheese`). Автор оставил пометку `// do this a different way, since this affects the size of the cave as well` — текущая реализация подмешивает `1 - probability` константой, что одновременно влияет и на вероятность спавна, и на размер пещеры.
