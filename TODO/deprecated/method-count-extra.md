# @Deprecated на методе countExtra(...)

`common/src/main/java/neoterra/world/worldgen/feature/placement/NTPlacementModifiers.java:38`

```java
    public static FastPoissonModifier poisson(int radius, float scale, float jitter, float biomeFade, int densityVariationScale, float densityVariation) {
		 return new FastPoissonModifier(radius, scale, jitter, biomeFade, densityVariationScale, densityVariation);
	}
    
    @Deprecated
    public static LegacyCountExtraModifier countExtra(int count, float extraChance, int extraCount) {
    	return new LegacyCountExtraModifier(count, extraChance, extraCount);
    }
```
