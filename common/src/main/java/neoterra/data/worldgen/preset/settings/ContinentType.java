package neoterra.data.worldgen.preset.settings;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import neoterra.world.worldgen.GeneratorContext;
import neoterra.world.worldgen.cell.continent.Continent;
import neoterra.world.worldgen.cell.continent.advanced.AdvancedContinentGenerator;
import neoterra.world.worldgen.cell.continent.fancy.FancyContinentGenerator;
import neoterra.world.worldgen.cell.continent.infinite.InfiniteContinentGenerator;
import neoterra.world.worldgen.cell.continent.simple.MultiContinentGenerator;
import neoterra.world.worldgen.cell.continent.simple.SingleContinentGenerator;
import neoterra.world.worldgen.util.Seed;

public enum ContinentType implements StringRepresentable {
    MULTI {
        
    	@Override
        public MultiContinentGenerator create(Seed seed, GeneratorContext context) {
            return new MultiContinentGenerator(seed, context);
        }
    }, 
    SINGLE {
        
    	@Override
        public SingleContinentGenerator create(Seed seed, GeneratorContext context) {
            return new SingleContinentGenerator(seed, context);
        }
    }, 
    MULTI_IMPROVED {
        
    	@Override
        public AdvancedContinentGenerator create(Seed seed, GeneratorContext context) {
            return new AdvancedContinentGenerator(seed, context);
        }
    }, 
    EXPERIMENTAL {
        
    	@Override
        public FancyContinentGenerator create(Seed seed, GeneratorContext context) {
            return new FancyContinentGenerator(seed, context);
        }
    },
    INFINITE {
        
    	@Override
        public InfiniteContinentGenerator create(Seed seed, GeneratorContext context) {
            return new InfiniteContinentGenerator(context);
        }
    };
	
	public static final Codec<ContinentType> CODEC = StringRepresentable.fromEnum(ContinentType::values);
    
    public abstract Continent create(Seed seed, GeneratorContext context);

    @Override
	public String getSerializedName() {
		return this.name();
	}
}
