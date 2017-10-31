package crazypants.enderio.conduit.liquid;

import net.minecraft.util.EnumFacing;
import crazypants.enderio.conduit.IExtractor;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface ILiquidConduit extends IFluidHandler, IExtractor {

  boolean canOutputToDir(EnumFacing dir);

  boolean canExtractFromDir(EnumFacing dir);

  boolean canInputToDir(EnumFacing dir);
}
