package neoterra.data.worldgen.preset.biomepatch.neoforge;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.RemoveFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import neoterra.NTCommon;
import neoterra.data.worldgen.preset.PresetBiomeModifierData;
import neoterra.data.worldgen.preset.biomepatch.BiomeFeaturePatches;
import neoterra.data.worldgen.preset.biomepatch.Filter;
import neoterra.data.worldgen.preset.biomepatch.Order;
import neoterra.data.worldgen.preset.biomepatch.PatchAdd;
import neoterra.data.worldgen.preset.biomepatch.PatchReplace;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.world.worldgen.biome.modifier.neoforge.PrependFeaturesBiomeModifier;

public final class PatchesToNeoForgeBiomeModifiers {

	public static void bootstrap(BootstrapContext<BiomeModifier> ctx, Preset preset) {
		NTCommon.debug("PatchesToNeoForgeBiomeModifiers.bootstrap: building NeoForge BiomeModifiers from preset");
		HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
		HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
		HolderSet<Biome> overworld = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);

		BiomeFeaturePatches patches = PresetBiomeModifierData.collectPatches(preset, placedFeatures, biomes);
		int prependCount = (int) patches.adds().stream().filter(p -> p.order() == Order.PREPEND).count();
		int appendCount = patches.adds().size() - prependCount;
		NTCommon.debug("Registering {} adds ({} prepend / {} append) and {} replaces (each emits _remove + _add)",
			patches.adds().size(), prependCount, appendCount, patches.replaces().size());

		for (PatchAdd patch : patches.adds()) {
			HolderSet<Biome> biomeSet = resolveBiomes(patch.filter(), overworld);
			ResourceKey<BiomeModifier> id = convertKey(patch.id());
			BiomeModifier modifier = patch.order() == Order.PREPEND
				? new PrependFeaturesBiomeModifier(biomeSet, patch.features(), patch.step())
				: new AddFeaturesBiomeModifier(biomeSet, patch.features(), patch.step());
			ctx.register(id, modifier);
		}

		for (PatchReplace patch : patches.replaces()) {
			HolderSet<Biome> biomeSet = patch.biomes().orElse(overworld);
			Map<ResourceKey<PlacedFeature>, Holder<PlacedFeature>> replacements = patch.replacements();
			HolderSet<PlacedFeature> removed = HolderSet.direct(replacements.keySet().stream().map(placedFeatures::getOrThrow).toList());
			// distinct() — иначе несколько keys, ведущих на один placed feature
			// (напр. TREES_BADLANDS и TREES_WINDSWEPT_SAVANNA → badlandsTrees), приведут
			// к дублю в HolderSet, и AddFeaturesBiomeModifier добавит фичу дважды,
			// что валит FeatureSorter с "Feature order cycle".
			HolderSet<PlacedFeature> added = HolderSet.direct(replacements.values().stream().distinct().toList());

			ResourceKey<BiomeModifier> removeId = convertKey(patch.id().withSuffix("_remove"));
			ResourceKey<BiomeModifier> addId = convertKey(patch.id().withSuffix("_add"));

			ctx.register(removeId, new RemoveFeaturesBiomeModifier(biomeSet, removed, EnumSet.of(patch.step())));
			ctx.register(addId, new AddFeaturesBiomeModifier(biomeSet, added, patch.step()));
		}
	}

	private static HolderSet<Biome> resolveBiomes(Optional<Filter> filter, HolderSet<Biome> fallback) {
		if (filter.isEmpty()) {
			return fallback;
		}
		Filter f = filter.get();
		if (f.behavior() != Filter.Behavior.WHITELIST) {
			throw new IllegalStateException("Filter.BLACKLIST is not supported when migrating to native NeoForge biome modifiers; refactor the patch to a whitelist or extend the converter.");
		}
		// Preserve original semantics: WHITELIST with empty HolderSet matches nothing,
		// so the resulting BiomeModifier becomes a deliberately disabled no-op (mirrors the legacy Filter#test behavior).
		return f.biomes();
	}

	private static ResourceKey<BiomeModifier> convertKey(ResourceLocation location) {
		return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, location);
	}
}
