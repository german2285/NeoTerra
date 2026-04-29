package neoterra;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import net.minecraft.resources.ResourceLocation;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.platform.RegistryUtil;
import neoterra.registries.NTBuiltInRegistries;
import neoterra.registries.NTRegistries;
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
		if (Boolean.getBoolean("neoterra.debug")) {
			Configurator.setLevel("NeoTerra", Level.DEBUG);
			LOGGER.info("Debug logging enabled (-Dneoterra.debug=true)");
		}
		LOGGER.debug("Bootstrap starting");
		NTBuiltInRegistries.bootstrap();
		LOGGER.debug("  built-in registries ready");
		TemplatePlacements.bootstrap();
		TemplateDecorators.bootstrap();
		NTChanceModifiers.bootstrap();
		NTPlacementModifiers.bootstrap();
		NTDensityFunctions.bootstrap();
		LOGGER.debug("  feature subsystems registered (templates, chance, placement, density functions)");
		Noises.bootstrap();
		Domains.bootstrap();
		CurveFunctions.bootstrap();
		LOGGER.debug("  noise subsystems registered (noises, domains, curves)");
		NTFeatures.bootstrap();
		NTHeightProviderTypes.bootstrap();
		NTFloatProviderTypes.bootstrap();
		NTSurfaceRules.bootstrap();
		StructureRules.bootstrap();
		LOGGER.debug("  features, providers, surface and structure rules registered");

		RegistryUtil.createDataRegistry(NTRegistries.NOISE, Noise.DIRECT_CODEC, false);
		RegistryUtil.createDataRegistry(NTRegistries.PRESET, Preset.DIRECT_CODEC, false);
		RegistryUtil.createDataRegistry(NTRegistries.STRUCTURE_RULE, StructureRule.DIRECT_CODEC, false);
		LOGGER.debug("  data registries created (NOISE, PRESET, STRUCTURE_RULE)");
		LOGGER.debug("Bootstrap complete");
	}

	public static ResourceLocation location(String name) {
		if (name.contains(":")) return ResourceLocation.parse(name);
		return ResourceLocation.fromNamespaceAndPath(NTCommon.MOD_ID, name);
	}
}
