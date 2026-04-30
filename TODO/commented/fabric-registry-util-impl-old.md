# Старые getWritable/createRegistry/createDataRegistry в Fabric RegistryUtilImpl

`fabric/src/main/java/neoterra/platform/fabric/RegistryUtilImpl.java:19-30`

```java
public class RegistryUtilImpl {
	
//	public static <T> WritableRegistry<T> getWritable(Registry<T> registry) {
//		return (WritableRegistry<T>) registry;
//	}
//
//	@SuppressWarnings("unchecked")
//	public static <T> Registry<T> createRegistry(ResourceKey<? extends Registry<T>> key) {
//		return FabricRegistryBuilder.createSimple((ResourceKey<Registry<T>>) key).buildAndRegister();
//	}
//
//	public static <T> void createDataRegistry(ResourceKey<? extends Registry<T>> key, Codec<T> codec) {
//		DynamicRegistries.register(key, codec);
//	}

	public static <T> void register(Registry<T> registry, String name, T value) {
		NTCommon.debug("Fabric RegistryUtilImpl.register: registry={}, name={}", registry.key().location(), name);
		Registry.register(registry, NTCommon.location(name), value);
	}
```
