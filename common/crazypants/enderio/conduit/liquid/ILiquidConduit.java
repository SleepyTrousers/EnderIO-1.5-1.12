package crazypants.enderio.conduit.liquid;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.util.DyeColor;

public interface ILiquidConduit extends IConduit, IFluidHandler {

  boolean canOutputToDir(ForgeDirection dir);

  boolean isExtractingFromDir(ForgeDirection dir);

  void setExtractionRedstoneMode(RedstoneControlMode mode, ForgeDirection dir);

  RedstoneControlMode getExtractioRedstoneMode(ForgeDirection dir);

  void setExtractionSignalColor(ForgeDirection dir, DyeColor col);

  DyeColor getExtractionSignalColor(ForgeDirection dir);

}
