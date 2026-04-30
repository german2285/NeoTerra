# @Deprecated на классе LegacyCarverHeight

`common/src/main/java/neoterra/world/worldgen/heightproviders/LegacyCarverHeight.java:12`

```java
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;

@Deprecated // pretty sure this can be replicated with UniformHeight
public class LegacyCarverHeight extends HeightProvider {
	public static final MapCodec<LegacyCarverHeight> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("min").forGetter((h) -> h.min),
```
