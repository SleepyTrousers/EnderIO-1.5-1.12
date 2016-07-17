package crazypants.enderio.machine.invpanel.remote;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.machine.invpanel.TileInventoryPanel;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ServerRemoteGuiManager {

  private ServerRemoteGuiManager() {
  }

  public static void openGui(EntityPlayerMP player, World world, BlockPos pos) {
    int x = pos.getX();
    int y = (world.provider.getDimension() << 8) | pos.getY();
    int z = pos.getZ();
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileInventoryPanel) {
      PacketHandler.INSTANCE.sendTo(new PacketPrimeInventoryPanelRemote((TileInventoryPanel) te), player);
    }
    player.openGui(EnderIO.instance, GuiHandler.GUI_ID_INVENTORY_PANEL_REMOTE, player.worldObj, x, y, z);
    Ticker.create();
  }

}
