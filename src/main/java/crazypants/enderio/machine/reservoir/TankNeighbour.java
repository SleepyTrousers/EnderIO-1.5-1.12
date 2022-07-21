package crazypants.enderio.machine.reservoir;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

class TankNeighbour {

    final IFluidHandler container;
    final ForgeDirection fillFromDir;

    TankNeighbour(IFluidHandler container, ForgeDirection fillFromDir) {
        this.container = container;
        this.fillFromDir = fillFromDir;
    }
}
