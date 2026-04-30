# @Deprecated на классе ColumnDecorator

`common/src/main/java/neoterra/world/worldgen/feature/ColumnDecorator.java:9`

```java
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.noise.module.Noises;

@Deprecated
public class ColumnDecorator {
	private static final Noise VARIANCE = Noises.perlin(0, 100, 1);

    public static void fillDownSolid(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int from, int to, BlockState state) {
```
