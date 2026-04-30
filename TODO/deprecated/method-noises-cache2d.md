## @Deprecated на фабричном методе Noises.cache2d(...)

`common/src/main/java/neoterra/world/worldgen/noise/module/Noises.java:410`

```java
	public static Noise warp(Noise input, Domain domain) {
		return new Warp(input, domain);
	}

	@Deprecated
	public static Noise cache2d(Noise input) {
		return new Cache2d(input);
	}
	
	public static Noise erosion(Noise input, int seed, int octaves, float strength, float gridSize, float amplitude, float lacunarity, float distanceFallOff, Erosion.BlendMode blendMode) {
```

Парный к `record-cache2d.md` — фабрика конструирует тот же `Cache2d`. Должна уйти вместе с самим record'ом.
