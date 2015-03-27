package crazypants.enderio.machine.invpanel;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.MessageTileEntity;
import crazypants.enderio.network.NetworkUtil;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class PacketItemList extends MessageTileEntity<TileInventoryPanel> implements IMessageHandler<PacketItemList, IMessage> {

  private byte[] compressed;

  public PacketItemList() {
  }

  public PacketItemList(TileInventoryPanel tile, byte[] compressed) {
    super(tile);
    this.compressed = compressed;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    compressed = NetworkUtil.readByteArray(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    NetworkUtil.writeByteArray(buf, compressed);
  }

  @Override
  public IMessage onMessage(PacketItemList message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if(te instanceof TileInventoryPanel) {
      TileInventoryPanel teInvPanel = (TileInventoryPanel) te;
      InventoryDatabaseClient db = teInvPanel.getDatabaseClient();
      try {
        db.readCompressedItemList(message.compressed);
      } catch (IOException ex) {
        Logger.getLogger(PacketItemInfo.class.getName()).log(Level.SEVERE,
                "Exception while reading item list", ex);
      }
    }
    return null;
  }

}
