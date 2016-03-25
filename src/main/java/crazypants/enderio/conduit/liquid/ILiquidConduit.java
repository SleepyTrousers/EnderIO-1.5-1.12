package crazypants.enderio.conduit.liquid;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.conduit.IExtractor;

public interface ILiquidConduit extends IFluidHandler, IExtractor {

  boolean canOutputToDir(EnumFacing dir);

  boolean canExtractFromDir(EnumFacing dir);

  boolean canInputToDir(EnumFacing dir);
}
