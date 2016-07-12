package crazypants.enderio.fluid;

import crazypants.enderio.machine.reservoir.TileReservoir;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class SmartTankFluidReservoirHandler extends SmartTankFluidHandler {

  private final TileReservoir te;

  public SmartTankFluidReservoirHandler(TileReservoir te, IFluidHandler... tanks) {
    super(tanks);
    this.te = te;
  }

  @Override
  protected boolean canFill(EnumFacing from) {
    return true;
  }

  @Override
  protected boolean canDrain(EnumFacing from) {
    return te.canRefill;
  }

  @Override
  protected boolean canAccess(EnumFacing from) {
    return true;
  }

}
