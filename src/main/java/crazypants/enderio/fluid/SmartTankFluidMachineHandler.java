package crazypants.enderio.fluid;

import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class SmartTankFluidMachineHandler extends SmartTankFluidHandler {

  private final AbstractMachineEntity te;

  public SmartTankFluidMachineHandler(AbstractMachineEntity te, IFluidHandler... tanks) {
    super(tanks);
    this.te = te;
  }

  @Override
  protected boolean canFill(EnumFacing from) {
    IoMode mode = te.getIoMode(from);
    return mode != IoMode.PUSH && mode != IoMode.DISABLED;
  }

  @Override
  protected boolean canDrain(EnumFacing from) {
    IoMode mode = te.getIoMode(from);
    return mode != IoMode.PULL && mode != IoMode.DISABLED;
  }

  @Override
  protected boolean canAccess(EnumFacing from) {
    IoMode mode = te.getIoMode(from);
    return mode != IoMode.DISABLED;
  }

}
