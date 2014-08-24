package crazypants.enderio.conduit.gas;

import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.util.BlockCoord;

public class GasUtil {

  public static IGasHandler getExternalGasHandler(IBlockAccess world, BlockCoord bc) {
    IGasHandler con = getGasHandler(world, bc);
    return (con != null && !(con instanceof IConduitBundle)) ? con : null;
  }

  public static IGasHandler getGasHandler(IBlockAccess world, BlockCoord bc) {
    return getGasHandler(world, bc.x, bc.y, bc.z);
  }

  public static IGasHandler getGasHandler(IBlockAccess world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    return getGasHandler(te);
  }

  public static IGasHandler getGasHandler(TileEntity te) {
    if(te instanceof IGasHandler) {
      if(te instanceof IPipeTile) {
        if(((IPipeTile) te).getPipeType() != PipeType.FLUID) {
          return null;
        }
      }
      return (IGasHandler) te;
    }
    return null;
  }

  public static boolean isGasValid(GasStack gas) {
    if(gas != null) {
      String name = gas.getGas().getLocalizedName();
      if(name != null && !name.trim().isEmpty()) {
        return true;
      }
    }
    return false;
  }

}
