package neoterra.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import neoterra.NTCommon;
import neoterra.client.data.NTLanguageProvider;
import neoterra.client.data.NTTranslationKeys;
import neoterra.platform.RegistryUtil;
import neoterra.registries.NTRegistries;
import neoterra.world.worldgen.biome.modifier.BiomeModifier;

public class NTFabric implements ModInitializer, DataGeneratorEntrypoint {

	@Override
	public void onInitialize() {
		NTCommon.bootstrap();

		RegistryUtil.createDataRegistry(NTRegistries.BIOME_MODIFIER, BiomeModifier.CODEC, false);
	}

	//TODO merge this with forge's datagen since they're the same now
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		Pack pack = fabricDataGenerator.createPack();

		pack.addProvider((FabricDataOutput output) -> new NTLanguageProvider.EnglishUS(output));
		pack.addProvider((FabricDataOutput output) -> PackMetadataGenerator.forFeaturePack(output, Component.translatable(NTTranslationKeys.METADATA_DESCRIPTION)));
	}
}