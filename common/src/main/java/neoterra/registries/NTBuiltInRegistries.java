package neoterra.registries;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import neoterra.platform.RegistryUtil;
import neoterra.world.worldgen.feature.chance.ChanceModifier;
import neoterra.world.worldgen.feature.template.decorator.TemplateDecorator;
import neoterra.world.worldgen.feature.template.placement.TemplatePlacement;
import neoterra.world.worldgen.noise.domain.Domain;
import neoterra.world.worldgen.noise.function.CurveFunction;
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.structure.rule.StructureRule;

public class NTBuiltInRegistries {
	public static final Registry<MapCodec<? extends Noise>> NOISE_TYPE = RegistryUtil.createRegistry(NTRegistries.NOISE_TYPE);
	public static final Registry<MapCodec<? extends Domain>> DOMAIN_TYPE = RegistryUtil.createRegistry(NTRegistries.DOMAIN_TYPE);
	public static final Registry<MapCodec<? extends CurveFunction>> CURVE_FUNCTION_TYPE = RegistryUtil.createRegistry(NTRegistries.CURVE_FUNCTION_TYPE);
	public static final Registry<MapCodec<? extends ChanceModifier>> CHANCE_MODIFIER_TYPE = RegistryUtil.createRegistry(NTRegistries.CHANCE_MODIFIER_TYPE);
	public static final Registry<MapCodec<? extends TemplatePlacement<?>>> TEMPLATE_PLACEMENT_TYPE = RegistryUtil.createRegistry(NTRegistries.TEMPLATE_PLACEMENT_TYPE);
	public static final Registry<MapCodec<? extends TemplateDecorator<?>>> TEMPLATE_DECORATOR_TYPE = RegistryUtil.createRegistry(NTRegistries.TEMPLATE_DECORATOR_TYPE);
	public static final Registry<MapCodec<? extends StructureRule>> STRUCTURE_RULE_TYPE = RegistryUtil.createRegistry(NTRegistries.STRUCTURE_RULE_TYPE);

	public static void bootstrap() {
	}
}
