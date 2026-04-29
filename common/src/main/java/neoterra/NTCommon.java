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
	// NeoForge's bundled log4j2.xml puts a ThresholdFilter at INFO on the console appender,
	// so DEBUG events from a logger whose level is DEBUG still get dropped at the sink.
	// When the user opts in via -Dneoterra.debug=true we route debug() through info()
	// to bypass the appender threshold; otherwise it stays at the silent debug level.
	public static final boolean DEBUG_ENABLED = Boolean.getBoolean("neoterra.debug");

	public static void debug(String message) {
		if (DEBUG_ENABLED) LOGGER.info(message);
		else LOGGER.debug(message);
	}

	public static void debug(String message, Object arg1) {
		if (DEBUG_ENABLED) LOGGER.info(message, arg1);
		else LOGGER.debug(message, arg1);
	}

	public static void debug(String message, Object arg1, Object arg2) {
		if (DEBUG_ENABLED) LOGGER.info(message, arg1, arg2);
		else LOGGER.debug(message, arg1, arg2);
	}

	public static void debug(String message, Object... args) {
		if (DEBUG_ENABLED) LOGGER.info(message, args);
		else LOGGER.debug(message, args);
	}

	public static void bootstrap() {
		if (DEBUG_ENABLED) {
			Configurator.setLevel("NeoTerra", Level.DEBUG);
			LOGGER.info("Debug logging enabled (-Dneoterra.debug=true)");
		}
		debug("Bootstrap starting");
		NTBuiltInRegistries.bootstrap();
		debug("  built-in registries ready");
		TemplatePlacements.bootstrap();
		TemplateDecorators.bootstrap();
		NTChanceModifiers.bootstrap();
		NTPlacementModifiers.bootstrap();
		NTDensityFunctions.bootstrap();
		debug("  feature subsystems registered (templates, chance, placement, density functions)");
		Noises.bootstrap();
		Domains.bootstrap();
		CurveFunctions.bootstrap();
		debug("  noise subsystems registered (noises, domains, curves)");
		NTFeatures.bootstrap();
		NTHeightProviderTypes.bootstrap();
		NTFloatProviderTypes.bootstrap();
		NTSurfaceRules.bootstrap();
		StructureRules.bootstrap();
		debug("  features, providers, surface and structure rules registered");

		RegistryUtil.createDataRegistry(NTRegistries.NOISE, Noise.DIRECT_CODEC, false);
		RegistryUtil.createDataRegistry(NTRegistries.PRESET, Preset.DIRECT_CODEC, false);
		RegistryUtil.createDataRegistry(NTRegistries.STRUCTURE_RULE, StructureRule.DIRECT_CODEC, false);
		debug("  data registries created (NOISE, PRESET, STRUCTURE_RULE)");
		debug("Bootstrap complete");
	}

	public static ResourceLocation location(String name) {
		if (name.contains(":")) return ResourceLocation.parse(name);
		return ResourceLocation.fromNamespaceAndPath(NTCommon.MOD_ID, name);
	}
}
