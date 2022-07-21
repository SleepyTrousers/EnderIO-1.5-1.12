package crazypants.enderio.conduit.liquid;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IExtractor;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

public interface ILiquidConduit extends IConduit, IFluidHandler, IExtractor {

    boolean canOutputToDir(ForgeDirection dir);

    boolean canExtractFromDir(ForgeDirection dir);

    boolean canInputToDir(ForgeDirection dir);
}
