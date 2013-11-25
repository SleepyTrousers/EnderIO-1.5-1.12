package crazypants.enderio.conduit.liquid;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.redstone.SignalColor;
import crazypants.enderio.machine.RedstoneControlMode;

public interface ILiquidConduit extends IConduit, IFluidHandler {

  static final int VOLUME_PER_CONNECTION = FluidContainerRegistry.BUCKET_VOLUME / 4;

  public static final String ICON_KEY = "enderio:liquidConduit";
  public static final String ICON_EMPTY_KEY = "enderio:emptyLiquidConduit";
  public static final String ICON_CORE_KEY = "enderio:liquidConduitCore";
  public static final String ICON_EXTRACT_KEY = "enderio:liquidConduitExtract";
  public static final String ICON_EMPTY_EXTRACT_KEY = "enderio:emptyLiquidConduitExtract";
  public static final String ICON_INSERT_KEY = "enderio:liquidConduitInsert";
  public static final String ICON_EMPTY_INSERT_KEY = "enderio:emptyLiquidConduitInsert";

  void setFluidType(FluidStack fluidType);

  FluidStack getFluidType();

  ConduitTank getTank();

  IFluidHandler getExternalHandler(ForgeDirection direction);

  boolean canOutputToDir(ForgeDirection dir);

  boolean isExtractingFromDir(ForgeDirection dir);

  void setExtractionRedstoneMode(RedstoneControlMode mode, ForgeDirection dir);

  RedstoneControlMode getExtractioRedstoneMode(ForgeDirection dir);

  void setExtractionSignalColor(ForgeDirection dir, SignalColor col);

  SignalColor getExtractionSignalColor(ForgeDirection dir);

  int fill(ForgeDirection from, FluidStack resource, boolean doFill, boolean doPush, int pushToken);

}
