package neoterra.world.worldgen.densityfunction.tile.filter;

import neoterra.world.worldgen.cell.Cell;
import neoterra.world.worldgen.densityfunction.tile.Size;

public interface Filterable {
    int getBlockX();
    
    int getBlockZ();
    
    Size getBlockSize();
    
    Cell[] getBacking();
    
    Cell getCellRaw(int x, int z);
}
