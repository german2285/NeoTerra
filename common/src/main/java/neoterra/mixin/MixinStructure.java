package neoterra.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import neoterra.NTCommon;
import neoterra.registries.NTRegistries;
import neoterra.world.worldgen.structure.rule.StructureRule;

@Mixin(Structure.class)
public class MixinStructure {
	@Unique
	private static boolean neoterra$logged_findValidGenerationPoint;

	// `findValidGenerationPoint` calls `isValidBiome` from a synthetic Optional.filter lambda,
	// not directly — so a WrapOperation on the call site can't be located. Inject at RETURN
	// after Mojang's biome filter has already run, then drop the stub if any rule rejects it.
	@Inject(
		method = "findValidGenerationPoint",
		at = @At("RETURN"),
		cancellable = true
	)
	private void neoterra$applyStructureRules(GenerationContext context, CallbackInfoReturnable<Optional<GenerationStub>> cir) {
		Optional<GenerationStub> result = cir.getReturnValue();
		if (result.isEmpty()) {
			return;
		}
		GenerationStub stub = result.get();

		RegistryAccess registries = context.registryAccess();
		RegistryLookup<StructureRule> structureRules = registries.lookupOrThrow(NTRegistries.STRUCTURE_RULE);
		Structure self = (Structure) (Object) this;

		if (!neoterra$logged_findValidGenerationPoint) {
			neoterra$logged_findValidGenerationPoint = true;
			NTCommon.debug("MixinStructure.findValidGenerationPoint: first call evaluating {} StructureRules at {}", structureRules.listElements().count(), stub.position());
		}

		for (StructureRule rule : structureRules.listElements().map(Holder::value).toList()) {
			Optional<HolderSet<Structure>> applyTo = rule.structures();
			if (applyTo.isPresent() && applyTo.get().stream().noneMatch(h -> h.value() == self)) {
				continue;
			}
			if (!rule.test(context.randomState(), stub.position())) {
				cir.setReturnValue(Optional.empty());
				return;
			}
		}
	}
}
