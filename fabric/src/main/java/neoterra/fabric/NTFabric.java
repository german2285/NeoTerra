package neoterra.fabric;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import neoterra.NTCommon;
import neoterra.client.data.NTLanguageProvider;
import neoterra.client.data.NTTranslationKeys;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.data.worldgen.preset.settings.Presets;
import neoterra.data.worldgen.tags.NTBlockTagsProvider;
import neoterra.data.worldgen.tags.NTDensityFunctionTagsProvider;
import neoterra.fabric.biome.FabricBiomeModifierApplier;
import neoterra.platform.DataGenUtil;

public class NTFabric implements ModInitializer, DataGeneratorEntrypoint {

	@Override
	public void onInitialize() {
		NTCommon.debug("Fabric entry point: onInitialize");
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

		pack.addProvider((FabricDataOutput output) -> new NTLanguageProvider.EnglishUS(output));
		pack.addProvider((FabricDataOutput output) -> PackMetadataGenerator.forFeaturePack(output, Component.translatable(NTTranslationKeys.METADATA_DESCRIPTION)));

		Preset ntDefaultPreset = Presets.makeNTDefault();
		CompletableFuture<HolderLookup.Provider> ntDefaultPresetLookup = fabricDataGenerator.getRegistries().thenApplyAsync(provider -> {
			NTCommon.debug("NTFabric datagen: building patch from Presets.makeNTDefault()");
			return ntDefaultPreset.buildPatch(provider);
		});
		pack.addProvider((FabricDataOutput output) -> DataGenUtil.createRegistryProvider(output, ntDefaultPresetLookup));
		pack.addProvider((FabricDataOutput output) -> new NTDensityFunctionTagsProvider(output, ntDefaultPresetLookup));
		pack.addProvider((FabricDataOutput output) -> new NTBlockTagsProvider(ntDefaultPreset, output, ntDefaultPresetLookup));

		NTCommon.debug("Fabric onInitializeDataGenerator: registered LanguageProvider, PackMetadataGenerator, NT default preset registry provider, density function tags, block tags");
	}
}