package neoterra.world.worldgen.structure.rule;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import neoterra.concurrent.Resource;
import neoterra.world.worldgen.GeneratorContext;
import neoterra.world.worldgen.NTRandomState;
import neoterra.world.worldgen.cell.Cell;
import neoterra.world.worldgen.cell.heightmap.WorldLookup;
import neoterra.world.worldgen.cell.terrain.Terrain;
import neoterra.world.worldgen.cell.terrain.TerrainType;

record CellTest(Optional<HolderSet<Structure>> structures, float cutoff, Set<Terrain> terrainTypeBlacklist) implements StructureRule {
	public static final MapCodec<CellTest> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RegistryCodecs.homogeneousList(Registries.STRUCTURE).optionalFieldOf("structures").forGetter(CellTest::structures),
		Codec.FLOAT.fieldOf("cutoff").forGetter(CellTest::cutoff),
		Codec.STRING.xmap(TerrainType::get, Terrain::getName).listOf().fieldOf("terrain_type_blacklist").forGetter((set) -> set.terrainTypeBlacklist().stream().toList())
	).apply(instance, CellTest::new));

	public CellTest(Optional<HolderSet<Structure>> structures, float cutoff, List<Terrain> terrainTypeBlacklist) {
		this(structures, cutoff, ImmutableSet.copyOf(terrainTypeBlacklist));
	}

	@Override
	public boolean test(RandomState randomState, BlockPos pos) {
		if((Object) randomState instanceof NTRandomState rtfRandomState) {
			@Nullable
			GeneratorContext generatorContext = rtfRandomState.generatorContext();
			if(generatorContext != null) {
				WorldLookup worldLookup = generatorContext.lookup;
				try (Resource<Cell> resource = Cell.getResource()) {
					Cell cell = resource.get();
					worldLookup.applyCell(cell.reset(), pos.getX(), pos.getZ(), false);
					if(cell.riverMask < this.cutoff || this.terrainTypeBlacklist.contains(cell.terrain)) {
						return false;
					}
				}
			}
			return true;
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public MapCodec<CellTest> codec() {
		return CODEC;
	}
}
