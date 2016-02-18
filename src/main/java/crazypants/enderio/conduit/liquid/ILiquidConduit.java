package crazypants.enderio.conduit.liquid;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IExtractor;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.IFluidHandler;

public interface ILiquidConduit extends IConduit, IFluidHandler, IExtractor {

  boolean canOutputToDir(EnumFacing dir);

  boolean canExtractFromDir(EnumFacing dir);

  boolean canInputToDir(EnumFacing dir);
}
