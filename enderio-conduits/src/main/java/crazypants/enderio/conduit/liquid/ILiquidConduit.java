package crazypants.enderio.conduit.liquid;

import crazypants.enderio.base.conduit.IExtractor;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ILiquidConduit extends IFluidHandler, IExtractor {

  boolean canOutputToDir(@Nonnull EnumFacing dir);

  boolean canExtractFromDir(@Nonnull EnumFacing dir);

  boolean canInputToDir(@Nonnull EnumFacing dir);

  /**
   * Used to get the capability of the conduit for the given direction
   *
   * @param from side for the capability
   * @return returns the connection with reference to the relevant side
   */
  IFluidHandler getFluidDir(@Nullable EnumFacing from);

  boolean canFill(EnumFacing dir, FluidStack resource);

  boolean canDrain(EnumFacing dir, FluidStack resource);
}
