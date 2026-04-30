# Добавить forest surfaces в PresetSurfaceRuleData

`common/src/main/java/neoterra/data/worldgen/preset/PresetSurfaceRuleData.java:18`

```java
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.surface.rule.NTSurfaceRules;
import neoterra.world.worldgen.surface.rule.StrataRule.Strata;

//TODO add forest surfaces
// maybe have a custom meadow or cherry forest surface ?
public class PresetSurfaceRuleData {
    
    public static SurfaceRules.RuleSource overworld(Preset preset, HolderGetter<DensityFunction> densityFunctions, HolderGetter<Noise> noise) {
```
