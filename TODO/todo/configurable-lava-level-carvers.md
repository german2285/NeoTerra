# Сделать lava level конфигурируемым

`common/src/main/java/neoterra/data/worldgen/preset/PresetConfiguredCarvers.java:30`

```java
import neoterra.world.worldgen.heightproviders.LegacyCarverHeight;

public class PresetConfiguredCarvers {

	//TODO make lava level configurable
	public static void bootstrap(Preset preset, BootstrapContext<ConfiguredWorldCarver<?>> ctx) {
		NTCommon.debug("PresetConfiguredCarvers.bootstrap: starting");
		long t0 = System.currentTimeMillis();
		CaveSettings caveSettings = preset.caves();
```
