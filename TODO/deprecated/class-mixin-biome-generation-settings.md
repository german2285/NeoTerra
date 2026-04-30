# @Deprecated на интерфейсе MixinBiomeGenerationSettings

`common/src/main/java/neoterra/mixin/MixinBiomeGenerationSettings.java:18`

```java
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

//TODO do this with access wideners instead
@Deprecated
@Mixin(BiomeGenerationSettings.class)
public interface MixinBiomeGenerationSettings {
	@Accessor
	List<HolderSet<PlacedFeature>> getFeatures();
```
