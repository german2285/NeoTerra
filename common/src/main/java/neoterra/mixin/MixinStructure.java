package neoterra.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

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

	@WrapOperation(
		method = "findValidGenerationPoint",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/structure/Structure;isValidBiome(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationStub;Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;)Z"
		)
	)
	private boolean neoterra$applyStructureRules(GenerationStub stub, GenerationContext context, Operation<Boolean> original) {
		if (!original.call(stub, context)) {
			return false;
		}

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
				return false;
			}
		}
		return true;
	}
}
