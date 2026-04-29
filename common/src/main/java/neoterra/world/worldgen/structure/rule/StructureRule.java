package neoterra.world.worldgen.structure.rule;

import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import neoterra.registries.NTBuiltInRegistries;

public interface StructureRule {
    public static final Codec<StructureRule> DIRECT_CODEC = NTBuiltInRegistries.STRUCTURE_RULE_TYPE.byNameCodec().dispatch(StructureRule::codec, Function.identity());

	boolean test(RandomState randomState, BlockPos pos);

	default Optional<HolderSet<Structure>> structures() {
		return Optional.empty();
	}

	MapCodec<? extends StructureRule> codec();
}
