package crazypants.enderio.invpanel.remote;

import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.invpanel.init.InvpanelObject;
import crazypants.enderio.invpanel.invpanel.TileInventoryPanel;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ServerRemoteGuiManager {

  private ServerRemoteGuiManager() {
  }

  public static void openGui(EntityPlayerMP player, World world, BlockPos pos) {
    long posl = pos.toLong();
    int x = (int) posl;
    int y = world.provider.getDimension();
    int z = (int) (posl >>> 32);
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileInventoryPanel) {
      PacketHandler.INSTANCE.sendTo(new PacketPrimeInventoryPanelRemote((TileInventoryPanel) te), player);
    }
    InvpanelObject.blockInventoryPanel.openGui(player.world, player, x, y, z);
    Ticker.create();
  }

}
