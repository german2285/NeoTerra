# @Deprecated на поле DEFAULT_WEIRDNESS

`common/src/main/java/neoterra/world/worldgen/cell/terrain/Populators.java:24`

```java
public class Populators {
	@Deprecated
	public static final Noise DEFAULT_EROSION = Erosion.LEVEL_4.source();
	@Deprecated
	public static final Noise DEFAULT_WEIRDNESS = Weirdness.MID_SLICE_NORMAL_DESCENDING.source();

	public static CellPopulator makeDeepOcean(@Deprecated int seed, float seaLevel) {
```
