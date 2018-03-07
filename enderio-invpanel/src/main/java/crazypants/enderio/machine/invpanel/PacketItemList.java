package crazypants.enderio.machine.invpanel;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.enderio.core.common.network.NetworkUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.machine.invpanel.client.InventoryDatabaseClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketItemList implements IMessage, IMessageHandler<PacketItemList, IMessage> {

  private int windowId;
  private int generation;
  private byte[] compressed;

  public PacketItemList() {
  }

  public PacketItemList(int windowId, int generation, byte[] compressed) {
    this.windowId = windowId;
    this.generation = generation;
    this.compressed = compressed;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    windowId = buf.readInt();
    generation = buf.readInt();
    compressed = NetworkUtil.readByteArray(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(windowId);
    buf.writeInt(generation);
    NetworkUtil.writeByteArray(buf, compressed);
  }

  @Override
  public IMessage onMessage(PacketItemList message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    if (player.openContainer.windowId == message.windowId && player.openContainer instanceof InventoryPanelContainer) {
      InventoryPanelContainer ipc = (InventoryPanelContainer) player.openContainer;
      TileInventoryPanel teInvPanel = ipc.getTe();
      InventoryDatabaseClient db = teInvPanel.getDatabaseClient(message.generation);
      try {
        List<Integer> missingItems = db.readCompressedItemList(message.compressed);
        if(missingItems != null) {
          return new PacketRequestMissingItems(message.windowId, db.getGeneration(), missingItems);
        }
      } catch (IOException ex) {
        Logger.getLogger(PacketItemInfo.class.getName()).log(Level.SEVERE, "Exception while reading item list", ex);
      }
    }
    return null;
  }

}
