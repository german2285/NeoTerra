# @Deprecated на поле ADDITIONAL_NOISE_ROUTER_FUNCTIONS

`common/src/main/java/neoterra/tags/NTDensityFunctionTags.java:11`

```java
public final class NTDensityFunctionTags {
	// i dont like this but dont know what to do about it
	// note: this should only include functions not present in the NoiseRouter
	@Deprecated
	public static final TagKey<DensityFunction> ADDITIONAL_NOISE_ROUTER_FUNCTIONS = resolve("additional_noise_router_functions");
	
    private static TagKey<DensityFunction> resolve(String path) {
```
