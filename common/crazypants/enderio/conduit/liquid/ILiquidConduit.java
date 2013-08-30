package crazypants.enderio.conduit.liquid;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import crazypants.enderio.conduit.IConduit;

public interface ILiquidConduit extends IConduit, ITankContainer {

  static final int VOLUME_PER_CONNECTION = LiquidContainerRegistry.BUCKET_VOLUME / 4;

  public static final String ICON_KEY = "enderio:liquidConduit";
  public static final String ICON_CORE_KEY = "enderio:liquidConduitCore";
  public static final String ICON_EXTRACT_KEY = "enderio:liquidConduitExtract";

  void setFluidType(LiquidStack fluidType);

  LiquidStack getFluidType();

  ConduitTank getTank();

  ITankContainer getExternalHandler(ForgeDirection direction);

  boolean canOutputToDir(ForgeDirection dir);

  boolean isExtractingFromDir(ForgeDirection dir);

  void setExtractingFromDir(ForgeDirection dir, boolean extracting);

  int fill(ForgeDirection from, LiquidStack resource, boolean doFill, boolean doPush, int pushToken);
  
  String getTextureSheetForLiquid();

}
