# Что делает SyncOption.SKIP_WHEN_EMPTY

`fabric/src/main/java/neoterra/platform/fabric/RegistryUtilImpl.java:45`

```java
	public static <T> void createDataRegistry(ResourceKey<Registry<T>> key, Codec<T> codec, boolean synced) {
		NTCommon.debug("Fabric RegistryUtilImpl.createDataRegistry: key={}, synced={}", key.location(), synced);
		if(synced) {
			DynamicRegistries.registerSynced(key, codec); // TODO what does SyncOption.SKIP_WHEN_EMPTY do?
		} else {
			DynamicRegistries.register(key, codec);
		}
	}
```
