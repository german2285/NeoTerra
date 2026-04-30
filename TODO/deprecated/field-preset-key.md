# @Deprecated на поле KEY (ResourceKey<Preset>)

`common/src/main/java/neoterra/data/worldgen/preset/settings/Preset.java:45`

```java
		MiscellaneousSettings.CODEC.fieldOf("miscellaneous").forGetter(Preset::miscellaneous)
	).apply(instance, Preset::new));
	
	@Deprecated
	public static final ResourceKey<Preset> KEY = NTRegistries.createKey(NTRegistries.PRESET, "preset");
	
	public Preset copy() {
```
