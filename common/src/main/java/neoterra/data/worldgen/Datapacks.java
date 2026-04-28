package neoterra.data.worldgen;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.Cloner;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataGenerator.PackGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.resources.RegistryDataLoader;
import neoterra.client.data.NTTranslationKeys;
import neoterra.data.worldgen.preset.PresetConfiguredFeatures;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.data.worldgen.tags.NTBlockTagsProvider;
import neoterra.data.worldgen.tags.NTDensityFunctionTagsProvider;
import neoterra.platform.DataGenUtil;
import neoterra.registries.NTRegistries;
import neoterra.world.worldgen.feature.NTFeatures;
import neoterra.world.worldgen.feature.SwampSurfaceFeature;
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.structure.rule.StructureRule;

public class Datapacks {

	public static DataGenerator makeMudSwamps(RegistryAccess registryAccess, Path dataGenPath, Path dataGenOutputPath) {
		DataGenerator dataGenerator = new DataGenerator(dataGenPath, SharedConstants.getCurrentVersion(), true);
		PackGenerator packGenerator = dataGenerator.new PackGenerator(true, "Mud Swamps", new PackOutput(dataGenOutputPath));
		CompletableFuture<HolderLookup.Provider> lookup = CompletableFuture.supplyAsync(() -> {
			RegistrySetBuilder builder = new RegistrySetBuilder();
			builder.add(Registries.CONFIGURED_FEATURE, (ctx) -> {
				FeatureUtils.register(ctx, PresetConfiguredFeatures.SWAMP_SURFACE, NTFeatures.SWAMP_SURFACE, new SwampSurfaceFeature.Config(Blocks.CLAY.defaultBlockState(), Blocks.GRAVEL.defaultBlockState(), Blocks.MUD.defaultBlockState()));
			});
			Cloner.Factory factory = new Cloner.Factory();
			RegistryDataLoader.WORLDGEN_REGISTRIES.forEach(registryData -> registryData.runWithArguments(factory::addCodec));
			factory.addCodec(NTRegistries.NOISE, Noise.DIRECT_CODEC);
			factory.addCodec(NTRegistries.STRUCTURE_RULE, StructureRule.DIRECT_CODEC);
			factory.addCodec(NTRegistries.PRESET, Preset.DIRECT_CODEC);
			return builder.buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), registryAccess,factory).patches();
		});
		packGenerator.addProvider((output) -> {
			return DataGenUtil.createRegistryProvider(output, lookup);
		});
		packGenerator.addProvider((output) -> {
			return PackMetadataGenerator.forFeaturePack(output, Component.translatable(NTTranslationKeys.MUD_SWAMPS_METADATA_DESCRIPTION));
		});
		return dataGenerator;
	}

	public static DataGenerator makePreset(Preset preset, RegistryAccess registryAccess, Path dataGenPath, Path dataGenOutputPath, String presetName) {
		DataGenerator dataGenerator = new DataGenerator(dataGenPath, SharedConstants.getCurrentVersion(), true);
		PackGenerator packGenerator = dataGenerator.new PackGenerator(true, presetName, new PackOutput(dataGenOutputPath));
		CompletableFuture<HolderLookup.Provider> lookup = CompletableFuture.supplyAsync(() -> preset.buildPatch(registryAccess));
		
		packGenerator.addProvider((output) -> {
			return DataGenUtil.createRegistryProvider(output, lookup);
		});
		packGenerator.addProvider((output) -> {
			return new NTDensityFunctionTagsProvider(output, lookup);
		});
		packGenerator.addProvider((output) -> {
			return new NTBlockTagsProvider(preset, output, lookup);
		});
		packGenerator.addProvider((output) -> {
			return PackMetadataGenerator.forFeaturePack(output, Component.translatable(NTTranslationKeys.PRESET_METADATA_DESCRIPTION));
		});
		return dataGenerator;
	}
}
