package crazypants.enderio.conduit.liquid;

import crazypants.enderio.base.conduit.IExtractor;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public interface ILiquidConduit extends IFluidHandler, IExtractor {

  boolean canOutputToDir(@Nonnull EnumFacing dir);

  boolean canExtractFromDir(@Nonnull EnumFacing dir);

  boolean canInputToDir(@Nonnull EnumFacing dir);
}
