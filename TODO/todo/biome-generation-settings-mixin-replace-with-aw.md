# Заменить MixinBiomeGenerationSettings на access wideners

`common/src/main/java/neoterra/mixin/MixinBiomeGenerationSettings.java:17`

```java
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

//TODO do this with access wideners instead
@Deprecated
@Mixin(BiomeGenerationSettings.class)
public interface MixinBiomeGenerationSettings {
	@Accessor
	List<HolderSet<PlacedFeature>> getFeatures();
```
