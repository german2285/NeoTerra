package neoterra.world.worldgen.biome.modifier.neoforge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo.BiomeInfo;
import neoterra.neoforge.mixin.MixinBiomeGenerationSettingsPlainsBuilder;

public record PrependFeaturesBiomeModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements BiomeModifier {
	public static final MapCodec<PrependFeaturesBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Biome.LIST_CODEC.fieldOf("biomes").forGetter(PrependFeaturesBiomeModifier::biomes),
		PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(PrependFeaturesBiomeModifier::features),
		GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(PrependFeaturesBiomeModifier::step)
	).apply(instance, PrependFeaturesBiomeModifier::new));

	@Override
	public void modify(Holder<Biome> biome, Phase phase, BiomeInfo.Builder builder) {
		if (phase != Phase.ADD) return;
		if (!this.biomes.contains(biome)) return;
		if (!(builder.getGenerationSettings() instanceof MixinBiomeGenerationSettingsPlainsBuilder builderAccessor)) {
			throw new IllegalStateException("Expected BiomeGenerationSettings.PlainBuilder via MixinBiomeGenerationSettingsPlainsBuilder accessor");
		}

		List<List<Holder<PlacedFeature>>> featureSteps = builderAccessor.getFeatures();
		int index = this.step.ordinal();
		while (index >= featureSteps.size()) {
			featureSteps.add(Collections.emptyList());
		}

		List<Holder<PlacedFeature>> existing = featureSteps.get(index);
		List<Holder<PlacedFeature>> prepended = new ArrayList<>(existing.size() + this.features.size());
		this.features.forEach(prepended::add);
		prepended.addAll(existing);
		featureSteps.set(index, prepended);
	}

	@Override
	public MapCodec<PrependFeaturesBiomeModifier> codec() {
		return CODEC;
	}
}
