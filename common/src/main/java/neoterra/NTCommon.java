package neoterra;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.platform.RegistryUtil;
import neoterra.registries.NTBuiltInRegistries;
import neoterra.registries.NTRegistries;
import neoterra.world.worldgen.biome.modifier.BiomeModifiers;
import neoterra.world.worldgen.densityfunction.NTDensityFunctions;
import neoterra.world.worldgen.feature.NTFeatures;
import neoterra.world.worldgen.feature.chance.NTChanceModifiers;
import neoterra.world.worldgen.feature.placement.NTPlacementModifiers;
import neoterra.world.worldgen.feature.template.decorator.TemplateDecorators;
import neoterra.world.worldgen.feature.template.placement.TemplatePlacements;
import neoterra.world.worldgen.floatproviders.NTFloatProviderTypes;
import neoterra.world.worldgen.heightproviders.NTHeightProviderTypes;
import neoterra.world.worldgen.noise.domain.Domains;
import neoterra.world.worldgen.noise.function.CurveFunctions;
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.noise.module.Noises;
import neoterra.world.worldgen.structure.rule.StructureRule;
import neoterra.world.worldgen.structure.rule.StructureRules;
import neoterra.world.worldgen.surface.rule.NTSurfaceRules;

public class NTCommon {
	public static final String MOD_ID = "neoterra";
	public static final Logger LOGGER = LogManager.getLogger("NeoTerra");

	public static void bootstrap() {
		NTBuiltInRegistries.bootstrap();
		TemplatePlacements.bootstrap();
		TemplateDecorators.bootstrap();
		NTChanceModifiers.bootstrap();
		NTPlacementModifiers.bootstrap();
		NTDensityFunctions.bootstrap();
		Noises.bootstrap();
		Domains.bootstrap();
		CurveFunctions.bootstrap();
		NTFeatures.bootstrap();
		NTHeightProviderTypes.bootstrap();
		NTFloatProviderTypes.bootstrap();
		BiomeModifiers.bootstrap();
		NTSurfaceRules.bootstrap();
		StructureRules.bootstrap();

		RegistryUtil.createDataRegistry(NTRegistries.NOISE, Noise.DIRECT_CODEC, false);
		RegistryUtil.createDataRegistry(NTRegistries.PRESET, Preset.DIRECT_CODEC, false);
		RegistryUtil.createDataRegistry(NTRegistries.STRUCTURE_RULE, StructureRule.DIRECT_CODEC, false);
	}

	public static ResourceLocation location(String name) {
		if (name.contains(":")) return ResourceLocation.parse(name);
		return ResourceLocation.fromNamespaceAndPath(NTCommon.MOD_ID, name);
	}
}
