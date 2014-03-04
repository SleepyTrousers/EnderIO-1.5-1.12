package crazypants.enderio.conduit.liquid;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IExtractor;

public interface ILiquidConduit extends IConduit, IFluidHandler, IExtractor {

  boolean canOutputToDir(ForgeDirection dir);

  boolean isExtractingFromDir(ForgeDirection dir);

}
