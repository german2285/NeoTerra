# Альтернативная инициализация islandsOceans

`common/src/main/java/neoterra/world/worldgen/cell/heightmap/Heightmap.java:133`

```java
        CellPopulator deepOcean = Populators.makeDeepOcean(ctx.seed.next(), levels.water);
        CellPopulator shallowOcean = Populators.makeShallowOcean(ctx.levels);
        CellPopulator coast = Populators.makeCoast(ctx.levels);
        
        //pass coast/ocean spline to makeIslandPopulator instead of deepOcean
//        CellPopulator islandsOceans = new ContinentLerper3(coast, shallowOcean, deepOcean, controlPoints.deepOcean, controlPoints.shallowOcean, controlPoints.coast);
        CellPopulator oceans = new ContinentLerper3(deepOcean, shallowOcean, coast, controlPoints.deepOcean, controlPoints.shallowOcean, controlPoints.coast);
        CellPopulator terrain = new ContinentLerper2(oceans, land, controlPoints.shallowOcean, controlPoints.inland);

        Noise beachNoise = Noises.perlin2(ctx.seed.next(), 20, 1);
```
