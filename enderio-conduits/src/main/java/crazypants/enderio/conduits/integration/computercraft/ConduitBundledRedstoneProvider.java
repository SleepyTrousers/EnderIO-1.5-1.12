package crazypants.enderio.conduits.integration.computercraft;

import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.redstone.signals.Signal;
import crazypants.enderio.conduits.conduit.redstone.IRedstoneConduit;
import dan200.computercraft.api.redstone.IBundledRedstoneProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ConduitBundledRedstoneProvider implements IBundledRedstoneProvider {
  @Override
  public int getBundledRedstoneOutput(@Nonnull World world, @Nonnull BlockPos blockPos, @Nonnull EnumFacing enumFacing) {
    TileEntity te = world.getTileEntity(blockPos);
    if (!(te instanceof IConduitBundle)) return -1;

    IConduitBundle bundle = (IConduitBundle) te;
    IRedstoneConduit conduit = bundle.getConduit(IRedstoneConduit.class);
    if (conduit == null) return -1;

    int out = 0;
    for (Signal output : conduit.getNetworkOutputs(null)) {
      out |= (output.getStrength() == 0 ? 0 : 1) << (15 - output.getColor().ordinal());
    }
    return out;
  }
}
