# Старая реализация RegistryUtil до @ExpectPlatform-прокси

`common/src/main/java/neoterra/platform/RegistryUtil.java:17-39`

```java
@Deprecated
public final class RegistryUtil {

//	public static <T> void register(Registry<T> registry, String name, T value) {
//		getWritable(registry).register(NTRegistries.createKey(registry.key(), name), value, RegistrationInfo.BUILT_IN);
//	}
//
//	@ExpectPlatform
//	public static Registry<BiomeModifier> getBiomeModifierRegistry() {
//		throw new IllegalStateException();
//	}
//
//	@ExpectPlatform
//	public static <T> WritableRegistry<T> getWritable(Registry<T> registry) {
//		throw new IllegalStateException();
//	}
//
//	@ExpectPlatform
//	public static <T> Registry<T> createRegistry(ResourceKey<? extends Registry<T>> key) {
//		throw new IllegalStateException();
//	}
//
//	@ExpectPlatform
//	public static <T> void createDataRegistry(ResourceKey<? extends Registry<T>> key, Codec<T> codec) {
//		throw new IllegalStateException();
//	}

	@ExpectPlatform
	public static <T> void register(Registry<T> registry, String name, T value) {
		throw new IllegalStateException();
	}

	@ExpectPlatform
	public static <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
		throw new IllegalStateException();
	}
```
