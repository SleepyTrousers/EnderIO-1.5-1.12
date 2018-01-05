package crazypants.enderio.conduit.liquid;

import crazypants.enderio.base.conduit.IExtractor;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface ILiquidConduit extends IFluidHandler, IExtractor {

  boolean canOutputToDir(EnumFacing dir);

  boolean canExtractFromDir(EnumFacing dir);

  boolean canInputToDir(EnumFacing dir);
}
