package neoterra.mixin; 

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.base.Suppliers;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.NoiseHolder;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import neoterra.NTCommon;
import neoterra.concurrent.ThreadPools;
import neoterra.config.PerformanceConfig;
import neoterra.data.worldgen.preset.settings.Preset;
import neoterra.registries.NTRegistries;
import neoterra.tags.NTDensityFunctionTags;
import neoterra.world.worldgen.GeneratorContext;
import neoterra.world.worldgen.NTRandomState;
import neoterra.world.worldgen.densityfunction.CellSampler;
import neoterra.world.worldgen.densityfunction.NoiseFunction;
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.noise.module.Noises;

@Mixin(RandomState.class)
@Implements(@Interface(iface = NTRandomState.class, prefix = "neoterra$NTRandomState$"))
class MixinRandomState {
	private DensityFunction.Visitor densityFunctionWrapper;
	@Shadow
	@Final
    private SurfaceSystem surfaceSystem;
	
	@Deprecated
	private boolean hasContext;
	@Nullable
	private GeneratorContext generatorContext;
	@Nullable
	private Preset preset;
	
	private long seed;
	
	@Redirect(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/NoiseRouter;mapAll(Lnet/minecraft/world/level/levelgen/DensityFunction$Visitor;)Lnet/minecraft/world/level/levelgen/NoiseRouter;"
		),
		method = "<init>",
		require = 1
	)
	private NoiseRouter RandomState(NoiseRouter router, DensityFunction.Visitor visitor, NoiseGeneratorSettings noiseGeneratorSettings, HolderGetter<NormalNoise.NoiseParameters> params, final long seed) {
		this.seed = seed;
		this.densityFunctionWrapper = new DensityFunction.Visitor() {
			
			@Override
			public DensityFunction apply(DensityFunction function) {
				if(function instanceof NoiseFunction.Marker marker) {
					return new NoiseFunction(marker.noise(), (int) seed);
				}
				if(function instanceof CellSampler.Marker marker) {
					MixinRandomState.this.hasContext |= true;
					return new CellSampler(Suppliers.memoize(() -> MixinRandomState.this.generatorContext.lookup), marker.field());
				}
				return visitor.apply(function);
			}

			@Override
			public NoiseHolder visitNoise(NoiseHolder noiseHolder) {
	            return visitor.visitNoise(noiseHolder);
	        }
		};
		return router.mapAll(this.densityFunctionWrapper);
	}

	public void neoterra$NTRandomState$initialize(RegistryAccess registries) {
		RegistryLookup<Preset> presets = registries.lookupOrThrow(NTRegistries.PRESET);
		RegistryLookup<Noise> noises = registries.lookupOrThrow(NTRegistries.NOISE);
		RegistryLookup<DensityFunction> functions = registries.lookupOrThrow(Registries.DENSITY_FUNCTION);

		functions.get(NTDensityFunctionTags.ADDITIONAL_NOISE_ROUTER_FUNCTIONS).ifPresent((set) -> {
			set.forEach((function) -> function.value().mapAll(this.densityFunctionWrapper));
		});

		presets.get(Preset.KEY).ifPresentOrElse((presetHolder) -> {
			this.preset = presetHolder.value();

			if(this.hasContext) {
				PerformanceConfig config = PerformanceConfig.read(PerformanceConfig.DEFAULT_FILE_PATH)
					.resultOrPartial(NTCommon.LOGGER::error)
					.orElseGet(PerformanceConfig::makeDefault);
				this.generatorContext = GeneratorContext.makeCached(this.preset, noises, (int) this.seed, config.tileSize(), config.batchCount(), ThreadPools.availableProcessors() > 4);
			}
		}, () -> {
			if(this.hasContext) {
//				throw new IllegalStateException("Missing preset!");
			}
		});
	}
	
	@Nullable
	public Preset neoterra$NTRandomState$preset() {
		return this.preset;
	}
	
	@Nullable
	public GeneratorContext neoterra$NTRandomState$generatorContext() {
		return this.generatorContext;
	}

	@Nullable
	public DensityFunction neoterra$NTRandomState$wrap(DensityFunction function) {
		return function.mapAll(this.densityFunctionWrapper);
	}

	public Noise neoterra$NTRandomState$seed(Noise noise) {
		return Noises.shiftSeed(noise, (int) this.seed);
	}
}
