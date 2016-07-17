package crazypants.enderio.machine.invpanel.remote;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.Log;
import crazypants.enderio.machine.invpanel.GuiInventoryPanel;
import crazypants.enderio.machine.invpanel.InventoryPanelContainer;
import crazypants.enderio.machine.invpanel.TileInventoryPanel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ClientRemoteGuiManager implements IGuiHandler {

  public static void create() {
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_INVENTORY_PANEL_REMOTE, new ClientRemoteGuiManager());
  }

  private ClientRemoteGuiManager() {
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    int d = y >> 8;
    y = y & 0xff;
    World targetWorld = world;
    if (world.provider.getDimension() != d) {
      targetWorld = DimensionManager.getWorld(d);
      if (targetWorld == null) {
        Log.warn("Unexpected failure to get dimension " + d + " for the Inventory Panel Remote");
        return null;
      }
    }
    TileEntity te = targetWorld.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileInventoryPanel) {
      return new InventoryPanelContainer(player.inventory, (TileInventoryPanel) te);
    }
    Log.warn("Unexpected failure to get tileentiyt at " + x + "/" + y + "/" + z + " for the Inventory Panel Remote");
    return null;
  }

  static TileInventoryPanel targetTE;
  static long targetTEtime;

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    int d = y >> 8;
    y = y & 0xff;
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
