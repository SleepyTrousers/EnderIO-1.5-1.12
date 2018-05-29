package crazypants.enderio.invpanel.remote;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.invpanel.invpanel.GuiInventoryPanel;
import crazypants.enderio.invpanel.invpanel.InventoryPanelContainer;
import crazypants.enderio.invpanel.invpanel.TileInventoryPanel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ClientRemoteGuiManager implements IGuiHandler {

  private ClientRemoteGuiManager() {
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, int posLow, int dim, int posHigh) {
    long posl = ((long) posHigh << 32) | (posLow & 0xffffffffL);
    BlockPos pos = BlockPos.fromLong(posl);
    World targetWorld = world;
    if (world.provider.getDimension() != dim) {
      targetWorld = DimensionManager.getWorld(dim);
      if (targetWorld == null) {
        Log.warn("Unexpected failure to get dimension " + dim + " for the Inventory Panel Remote");
        return null;
      }
    }
    TileEntity te = targetWorld.getTileEntity(pos);
    if (te instanceof TileInventoryPanel) {
      return new InventoryPanelContainer(player.inventory, (TileInventoryPanel) te);
    }
    Log.warn("Unexpected failure to get tileentity at " + pos + " in dimension " + dim + " for the Inventory Panel Remote. Got: " + te);
    return null;
  }

  static TileInventoryPanel targetTE;
  static long targetTEtime;

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, int x, int y, int z) {
    TileInventoryPanel te;
    if (targetTE != null && targetTEtime >= EnderIO.proxy.getTickCount()) {
      te = targetTE;
      targetTE = null;
    } else {
      te = new TileInventoryPanel();
    }
    return new GuiInventoryPanel(te, new InventoryPanelContainer(player.inventory, te));
  }

}
