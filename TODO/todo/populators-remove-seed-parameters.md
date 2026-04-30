# Убрать seed-параметры из Populators

`common/src/main/java/neoterra/world/worldgen/cell/terrain/Populators.java:20`

```java
import neoterra.world.worldgen.noise.module.Noises;
import neoterra.world.worldgen.util.Seed;

//TODO remove all the seed parameters
public class Populators {
	@Deprecated
	public static final Noise DEFAULT_EROSION = Erosion.LEVEL_4.source();
```
