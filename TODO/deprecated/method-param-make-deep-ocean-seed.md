# @Deprecated на параметре seed метода makeDeepOcean

`common/src/main/java/neoterra/world/worldgen/cell/terrain/Populators.java:27`

```java
	@Deprecated
	public static final Noise DEFAULT_WEIRDNESS = Weirdness.MID_SLICE_NORMAL_DESCENDING.source();

	public static CellPopulator makeDeepOcean(@Deprecated int seed, float seaLevel) {
		Noise hills = Noises.perlin(++seed, 150, 3);
		hills = Noises.mul(hills, seaLevel * 0.7F);
```
