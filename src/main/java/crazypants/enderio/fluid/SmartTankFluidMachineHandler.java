package crazypants.enderio.fluid;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.SmartTankFluidHandler;

import crazypants.enderio.machine.interfaces.IIoConfigurable;
import crazypants.enderio.machine.modes.IoMode;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class SmartTankFluidMachineHandler extends SmartTankFluidHandler {

  private final @Nonnull IIoConfigurable te;

  public SmartTankFluidMachineHandler(@Nonnull IIoConfigurable te, IFluidHandler... tanks) {
    super(tanks);
    this.te = te;
  }

  @Override
  protected boolean canFill(@Nonnull EnumFacing from) {
    IoMode mode = te.getIoMode(from);
    return mode != IoMode.PUSH && mode != IoMode.DISABLED;
  }

  @Override
  protected boolean canDrain(@Nonnull EnumFacing from) {
    IoMode mode = te.getIoMode(from);
    return mode != IoMode.PULL && mode != IoMode.DISABLED;
  }

  @Override
  protected boolean canAccess(@Nonnull EnumFacing from) {
    IoMode mode = te.getIoMode(from);
    return mode != IoMode.DISABLED;
  }

}
