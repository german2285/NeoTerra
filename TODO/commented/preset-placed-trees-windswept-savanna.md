# Отключённая регистрация TREES_WINDSWEPT_SAVANNA

`common/src/main/java/neoterra/data/worldgen/preset/PresetPlacedFeatures.java:184`

```java
        	PlacementUtils.register(ctx, REDWOOD_TREES, features.getOrThrow(PresetConfiguredFeatures.REDWOOD_TREES), NTPlacementModifiers.poisson(6, 0.3F, 0.25F, 250, 0.75F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, JUNGLE_TREES, features.getOrThrow(PresetConfiguredFeatures.JUNGLE_TREES), NTPlacementModifiers.poisson(6, 0.4F, 0.2F, 400, 0.75F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, JUNGLE_EDGE_TREES, features.getOrThrow(PresetConfiguredFeatures.JUNGLE_EDGE_TREES), NTPlacementModifiers.poisson(8, 0.35F, 0.25F, 350, 0.75F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	
//			TODO shattered savanna
//        	PlacementUtils.register(ctx, VegetationPlacements.TREES_WINDSWEPT_SAVANNA, features.getOrThrow(VegetationFeatures.TREES_SAVANNA), NTPlacementModifiers.disabled());
        }
		NTCommon.debug("PresetPlacedFeatures.bootstrap: complete in {} ms (customBiomeFeatures={}, erosionDecorator={}, naturalSnowDecorator={}, smoothLayerDecorator={}, strataDecorator={}, vanillaSprings={}, vanillaLavaSprings={}, vanillaLavaLakes={})",
			System.currentTimeMillis() - t0, miscellaneous.customBiomeFeatures, miscellaneous.erosionDecorator, miscellaneous.naturalSnowDecorator, miscellaneous.smoothLayerDecorator, miscellaneous.strataDecorator, miscellaneous.vanillaSprings, miscellaneous.vanillaLavaSprings, miscellaneous.vanillaLavaLakes);
	}
```
