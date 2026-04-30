# Старый способ выставления Y через world.getHeight(WORLD_SURFACE)

`common/src/main/java/neoterra/world/worldgen/feature/BushFeature.java:70`

```java
        return false;
	}

    private boolean place(LevelAccessor world, BlockPos.MutableBlockPos center, BlockPos.MutableBlockPos pos, RandomSource random, Config config) {
//        center.setY(world.getHeight(Heightmap.Types.WORLD_SURFACE, center.getX(), center.getZ()));

        // don't replace solid blocks
        if (!BlockUtils.canTreeReplace(world, center)) {
            return false;
        }
```
