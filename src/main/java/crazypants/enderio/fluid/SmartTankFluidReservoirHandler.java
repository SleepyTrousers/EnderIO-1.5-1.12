package crazypants.enderio.fluid;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.SmartTankFluidHandler;

import crazypants.enderio.machine.reservoir.TileReservoir;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class SmartTankFluidReservoirHandler extends SmartTankFluidHandler {

  private final @Nonnull TileReservoir te;

  public SmartTankFluidReservoirHandler(@Nonnull TileReservoir te, IFluidHandler... tanks) {
    super(tanks);
    this.te = te;
  }

  @Override
  protected boolean canFill(@Nonnull EnumFacing from) {
    return true;
  }

  @Override
  protected boolean canDrain(@Nonnull EnumFacing from) {
    return te.canRefill;
  }

  @Override
  protected boolean canAccess(@Nonnull EnumFacing from) {
    return true;
  }

}
