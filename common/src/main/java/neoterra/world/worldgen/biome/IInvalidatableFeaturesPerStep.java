package neoterra.world.worldgen.biome;

// Реализуется через Mixin на ChunkGenerator (см. neoterra.fabric.mixin.MixinChunkGenerator).
// Позволяет сбросить мемоизированный кэш ChunkGenerator.featuresPerStep после того,
// как биомы получили модификации через IModifiableBiome — без этого кэш остаётся
// построенным по немодифицированным биомам, и applyBiomeDecoration падает с
// IndexOutOfBoundsException, не находя добавленные фичи в индексе.
public interface IInvalidatableFeaturesPerStep {

	void neoterra$invalidateFeaturesPerStep();
}
