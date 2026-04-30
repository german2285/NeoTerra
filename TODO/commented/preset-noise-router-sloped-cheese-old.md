## Старая реализация slopedCheeseCaves / finalDensity

`common/src/main/java/neoterra/data/worldgen/preset/PresetNoiseRouterData.java:87-89`

```java
        DensityFunction slopedCheese = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.SLOPED_CHEESE);
//        DensityFunction entrances = DensityFunctions.min(slopedCheese, DensityFunctions.mul(DensityFunctions.constant(5.0), NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.ENTRANCES)));
//        DensityFunction slopedCheeseCaves = DensityFunctions.rangeChoice(slopedCheese, -1000000.0, 1.5625, entrances, NoiseRouterData.underground(densityFunctions, noiseParams, slopedCheese));
//        DensityFunction finalDensity = DensityFunctions.min(NoiseRouterData.postProcess(slideOverworld(slopedCheeseCaves, -worldDepth)), NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.NOODLE));

        DensityFunction entrances = caves.entranceCaveProbability > 0.0F ? DensityFunctions.min(slopedCheese, DensityFunctions.mul(DensityFunctions.constant(5.0D), DensityFunctions.interpolated(NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.ENTRANCES)))) : slopedCheese;

        DensityFunction slopedCheeseRange = DensityFunctions.mul(DensityFunctions.rangeChoice(slopedCheese, -1000000.0D, cheeseCaveDepthOffset, entrances, DensityFunctions.interpolated(slideOverworld(underground(caves.cheeseCaveProbability, densityFunctions, noiseParams, slopedCheese), -worldDepth))), DensityFunctions.constant(0.64)).squeeze();
        DensityFunction finalDensity = DensityFunctions.min(slopedCheeseRange, NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.NOODLE));
```

Закомментированы старые версии `entrances` / `slopedCheeseCaves` / `finalDensity` с константами `1.5625` и `5.0`. Заменены на варианты с `caves.entranceCaveProbability` и `cheeseCaveDepthOffset` ниже. Если новая реализация устоялась — три строки можно удалить.
