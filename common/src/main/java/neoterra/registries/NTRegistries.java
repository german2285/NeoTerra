package neoterra.registries;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.world.worldgen.feature.chance.ChanceModifier;
import neoterra.world.worldgen.feature.template.decorator.TemplateDecorator;
import neoterra.world.worldgen.feature.template.placement.TemplatePlacement;
import neoterra.world.worldgen.noise.domain.Domain;
import neoterra.world.worldgen.noise.function.CurveFunction;
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.structure.rule.StructureRule;

public class NTRegistries {
	public static final ResourceKey<Registry<MapCodec<? extends Noise>>> NOISE_TYPE = createKey("worldgen/noise_type");
	public static final ResourceKey<Registry<MapCodec<? extends Domain>>> DOMAIN_TYPE = createKey("worldgen/domain_type");
	public static final ResourceKey<Registry<MapCodec<? extends CurveFunction>>> CURVE_FUNCTION_TYPE = createKey("worldgen/curve_function_type");
	public static final ResourceKey<Registry<MapCodec<? extends ChanceModifier>>> CHANCE_MODIFIER_TYPE = createKey("worldgen/chance_modifier_type");
	public static final ResourceKey<Registry<MapCodec<? extends TemplatePlacement<?>>>> TEMPLATE_PLACEMENT_TYPE = createKey("worldgen/template_placement_type");
	public static final ResourceKey<Registry<MapCodec<? extends TemplateDecorator<?>>>> TEMPLATE_DECORATOR_TYPE = createKey("worldgen/template_decorator_type");
	public static final ResourceKey<Registry<MapCodec<? extends StructureRule>>> STRUCTURE_RULE_TYPE = createKey("worldgen/structure_rule_type");

	public static final ResourceKey<Registry<Noise>> NOISE = createKey("worldgen/noise");
	public static final ResourceKey<Registry<StructureRule>> STRUCTURE_RULE = createKey("worldgen/structure_rule");

	@Deprecated
	public static final ResourceKey<Registry<Preset>> PRESET = createKey("worldgen/preset");
	
	public static <T> ResourceKey<T> createKey(ResourceKey<? extends Registry<T>> registryKey, String valueKey) {
		return ResourceKey.create(registryKey, NTCommon.location(valueKey));
	}

	private static <T> ResourceKey<Registry<T>> createKey(String key) {
		return ResourceKey.createRegistryKey(NTCommon.location(key));
	}
}
