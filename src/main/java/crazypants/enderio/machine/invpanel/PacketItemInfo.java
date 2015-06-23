package crazypants.enderio.machine.invpanel;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.invpanel.client.InventoryDatabaseClient;
import crazypants.enderio.machine.invpanel.server.InventoryDatabaseServer;
import crazypants.enderio.machine.invpanel.server.ItemEntry;

public class PacketItemInfo extends MessageTileEntity<TileInventoryPanel> implements IMessageHandler<PacketItemInfo, IMessage> {

  private int generation;
  private byte[] compressed;

  public PacketItemInfo() {
  }

  public PacketItemInfo(TileInventoryPanel tile, InventoryDatabaseServer db, List<ItemEntry> items) {
    super(tile);
    this.generation = db.generation;
    try {
      compressed = db.compressItemInfo(items);
    } catch (IOException ex) {
      Logger.getLogger(PacketItemInfo.class.getName()).log(Level.SEVERE, "Exception while compressing items", ex);
      compressed = new byte[0];
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    generation = buf.readInt();
    compressed = NetworkUtil.readByteArray(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(generation);
    NetworkUtil.writeByteArray(buf, compressed);
  }

  @Override
  public IMessage onMessage(PacketItemInfo message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if(te instanceof TileInventoryPanel) {
      TileInventoryPanel teInvPanel = (TileInventoryPanel) te;
      InventoryDatabaseClient db = teInvPanel.getDatabaseClient(message.generation);
      try {
        db.readCompressedItems(message.compressed);
      } catch (IOException ex) {
        Logger.getLogger(PacketItemInfo.class.getName()).log(Level.SEVERE, "Exception while reading item info", ex);
      }
    }
    return null;
  }
}
