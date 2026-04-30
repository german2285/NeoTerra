# Перевести регистрацию SurfaceRules на MapCodec

`common/src/main/java/neoterra/world/worldgen/surface/rule/NTSurfaceRules.java:31`

```java
	public static StrataRule strata(ResourceLocation name, Holder<Noise> selector, List<Strata> strata, int iterations) {
		return new StrataRule(name, selector, strata, iterations);
	}
	
	public static void register(String name, MapCodec<? extends SurfaceRules.RuleSource> value) {
		RegistryUtil.register(BuiltInRegistries.MATERIAL_RULE, name, value); //TODO: Convert to MapCodec
		registeredCount++;
	}
}
```
