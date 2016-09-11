package crazypants.enderio.conduit.redstone;

import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.IConduitBundle;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.redstone.IBundledRedstoneProvider;

public class ConduitBundledRedstoneProvider implements IBundledRedstoneProvider {

  @Override
  public int getBundledRedstoneOutput(World world, int x, int y, int z, int side) {
    TileEntity inputTE = world.getTileEntity(x, y, z);
    if (inputTE != null && inputTE instanceof IConduitBundle) {
      IConduitBundle bundle = ((IConduitBundle) inputTE);
      if (bundle.hasType(IInsulatedRedstoneConduit.class)) {
        IInsulatedRedstoneConduit conduit = bundle.getConduit(IInsulatedRedstoneConduit.class);
        Set<Signal> networkOutputs = conduit.getNetworkOutputs(ForgeDirection.UNKNOWN);
        int out = 0;
        for (Signal signal : networkOutputs) {
          int index = signal.color.ordinal();
          if (signal.strength != 0) {
            out |= (1 << 15 - index);
          }
        }
        return out;
      }
      return 0;
    }
    return -1;
  }

  public static void register() {
    ComputerCraftAPI.registerBundledRedstoneProvider(new ConduitBundledRedstoneProvider());
  }
}
