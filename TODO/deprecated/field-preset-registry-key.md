# @Deprecated на поле PRESET (ResourceKey<Registry<Preset>>)

`common/src/main/java/neoterra/registries/NTRegistries.java:30`

```java
	public static final ResourceKey<Registry<Noise>> NOISE = createKey("worldgen/noise");
	public static final ResourceKey<Registry<StructureRule>> STRUCTURE_RULE = createKey("worldgen/structure_rule");

	@Deprecated
	public static final ResourceKey<Registry<Preset>> PRESET = createKey("worldgen/preset");
	
	public static <T> ResourceKey<T> createKey(ResourceKey<? extends Registry<T>> registryKey, String valueKey) {
```
