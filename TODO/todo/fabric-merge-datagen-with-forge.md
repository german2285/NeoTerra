# Слить fabric datagen с forge'овой

`fabric/src/main/java/neoterra/fabric/NTFabric.java:34`

```java
		NTCommon.bootstrap();
		NTCommon.debug("Registering Fabric biome modifier applier (server-starting hook)");
		FabricBiomeModifierApplier.register();
		NTCommon.debug("Fabric onInitialize complete");
	}

	//TODO merge this with forge's datagen since they're the same now
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		NTCommon.debug("Fabric entry point: onInitializeDataGenerator");
		Pack pack = fabricDataGenerator.createPack();
```
