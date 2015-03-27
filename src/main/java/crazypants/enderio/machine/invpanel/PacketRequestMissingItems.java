package crazypants.enderio.machine.invpanel;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.network.CompressedDataOutput;
import crazypants.enderio.network.MessageTileEntity;
import crazypants.enderio.network.NetworkUtil;
import crazypants.enderio.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class PacketRequestMissingItems extends MessageTileEntity<TileInventoryPanel> implements IMessageHandler<PacketRequestMissingItems, IMessage> {

  private byte[] compressed;

  public PacketRequestMissingItems() {
  }

  public PacketRequestMissingItems(TileInventoryPanel tile, List<Integer> missingIDs) {
    super(tile);
    try {
      CompressedDataOutput cdo = new CompressedDataOutput();
      try {
        cdo.writeVariable(missingIDs.size());
        for(Integer id : missingIDs) {
          cdo.writeVariable(id - InventoryDatabase.COMPLEX_DBINDEX_START);
        }
        compressed = cdo.getCompressed();
      } finally {
        cdo.close();
      }
    } catch(IOException ex) {
      compressed = new byte[0];
    }
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
  public IMessage onMessage(PacketRequestMissingItems message, MessageContext ctx) {
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if(te instanceof TileInventoryPanel) {
      TileInventoryPanel teInvPanel = (TileInventoryPanel) te;
      InventoryDatabaseServer db = teInvPanel.getDatabaseServer();
      try {
        List<InventoryDatabaseServer.ItemEntry> items = db.decompressMissingItems(message.compressed);
        if(!items.isEmpty()) {
          PacketHandler.sendTo(new PacketItemInfo(teInvPanel, items), player);
        }
      } catch (IOException ex) {
        Logger.getLogger(PacketItemInfo.class.getName()).log(Level.SEVERE, "Exception while reading missing item IDs", ex);
      }
    }
    return null;
  }

}
