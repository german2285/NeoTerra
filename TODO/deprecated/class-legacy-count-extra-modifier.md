# @Deprecated на классе LegacyCountExtraModifier

`common/src/main/java/neoterra/world/worldgen/feature/placement/LegacyCountExtraModifier.java:16`

```java
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

@Deprecated
class LegacyCountExtraModifier extends PlacementModifier {
	public static final MapCodec<LegacyCountExtraModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("count").forGetter((p) -> p.count),
```
