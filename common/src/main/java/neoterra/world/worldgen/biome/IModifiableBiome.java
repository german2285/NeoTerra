package neoterra.world.worldgen.biome;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import neoterra.data.worldgen.preset.biomepatch.BiomeFeaturePatches;

// Реализуется через Mixin на Biome (см. neoterra.fabric.mixin.MixinBiome).
// На NeoForge не используется — там за модификации отвечает нативный BiomeModifier API
// (см. neoterra.data.worldgen.preset.biomepatch.neoforge.PatchesToNeoForgeBiomeModifiers).
public interface IModifiableBiome {

	void neoterra$applyPatches(BiomeFeaturePatches patches, Holder<Biome> self, HolderSet<Biome> overworldFallback);
}
