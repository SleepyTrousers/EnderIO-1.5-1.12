package crazypants.enderio.machine.reservoir;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.IFluidHandler;

class TankNeighbour {

  final IFluidHandler container;
  final EnumFacing fillFromDir;

  TankNeighbour(IFluidHandler container, EnumFacing fillFromDir) {
    this.container = container;
    this.fillFromDir = fillFromDir;
  }

}
