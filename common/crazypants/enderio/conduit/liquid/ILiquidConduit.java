package crazypants.enderio.conduit.liquid;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.conduit.IConduit;

public interface ILiquidConduit extends IConduit, IFluidHandler {

  static final int VOLUME_PER_CONNECTION = FluidContainerRegistry.BUCKET_VOLUME / 4;

  public static final String ICON_KEY = "enderio:liquidConduit";
  public static final String ICON_EMPTY_KEY = "enderio:emptyLiquidConduit";
  public static final String ICON_CORE_KEY = "enderio:liquidConduitCore";
  public static final String ICON_EXTRACT_KEY = "enderio:liquidConduitExtract";
  public static final String ICON_EMPTY_EXTRACT_KEY = "enderio:emptyLiquidConduitExtract";

  void setFluidType(FluidStack fluidType);

  FluidStack getFluidType();

  ConduitTank getTank();

  IFluidHandler getExternalHandler(ForgeDirection direction);

  boolean canOutputToDir(ForgeDirection dir);

  boolean isExtractingFromDir(ForgeDirection dir);

  void setExtractingFromDir(ForgeDirection dir, boolean extracting);

  int fill(ForgeDirection from, FluidStack resource, boolean doFill, boolean doPush, int pushToken);

}
