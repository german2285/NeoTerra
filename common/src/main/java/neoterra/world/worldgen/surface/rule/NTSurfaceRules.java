package neoterra.world.worldgen.surface.rule;

import java.util.List;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;
import neoterra.NTCommon;
import neoterra.platform.RegistryUtil;
import neoterra.world.worldgen.noise.module.Noise;
import neoterra.world.worldgen.surface.rule.StrataRule.Strata;

public class NTSurfaceRules {
	private static int registeredCount = 0;

	public static void bootstrap() {
		NTCommon.debug("NTSurfaceRules.bootstrap: starting");
		long t0 = System.currentTimeMillis();
		registeredCount = 0;
		register("strata", StrataRule.CODEC);
		NTCommon.debug("NTSurfaceRules.bootstrap: registered {} surface rule types in {} ms", registeredCount, System.currentTimeMillis() - t0);
	}
	
	public static StrataRule strata(ResourceLocation name, Holder<Noise> selector, List<Strata> strata, int iterations) {
		return new StrataRule(name, selector, strata, iterations);
	}
	
	public static void register(String name, MapCodec<? extends SurfaceRules.RuleSource> value) {
		RegistryUtil.register(BuiltInRegistries.MATERIAL_RULE, name, value); //TODO: Convert to MapCodec
		registeredCount++;
	}
}
