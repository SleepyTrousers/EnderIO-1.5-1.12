package crazypants.enderio.fluid;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.SmartTankFluidHandler;

import crazypants.enderio.machine.interfaces.IIoConfigurable;
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
    return te.getIoMode(from).canRecieveInput();
  }

  @Override
  protected boolean canDrain(@Nonnull EnumFacing from) {
    return te.getIoMode(from).canOutput();
  }

  @Override
  protected boolean canAccess(@Nonnull EnumFacing from) {
    return te.getIoMode(from).canInputOrOutput();
  }

}
