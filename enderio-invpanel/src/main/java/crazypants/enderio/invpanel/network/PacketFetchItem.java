package crazypants.enderio.invpanel.network;

import crazypants.enderio.invpanel.client.ItemEntry;
import crazypants.enderio.invpanel.invpanel.InventoryPanelContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFetchItem implements IMessage {

  private int generation;
  private int dbID;
  private int targetSlot;
  private int count;

  public PacketFetchItem() {
  }

  public PacketFetchItem(int generation, ItemEntry entry, int targetSlot, int count) {
    this.generation = generation;
    this.dbID = entry.getDbID();
    this.targetSlot = targetSlot;
    this.count = count;
  }

  @Override
  public void fromBytes(ByteBuf bb) {
    generation = bb.readInt();
    dbID = bb.readInt();
    targetSlot = bb.readShort();
    count = bb.readShort();
  }

  @Override
  public void toBytes(ByteBuf bb) {
    bb.writeInt(generation);
    bb.writeInt(dbID);
    bb.writeShort(targetSlot);
    bb.writeShort(count);
  }

  public static class Handler implements IMessageHandler<PacketFetchItem, IMessage> {

    @Override
    public IMessage onMessage(PacketFetchItem message, MessageContext ctx) {
      EntityPlayerMP player = ctx.getServerHandler().player;
      if (player.openContainer instanceof InventoryPanelContainer) {
        InventoryPanelContainer ipc = (InventoryPanelContainer) player.openContainer;
        ipc.executeFetchItems(player, message.generation, message.dbID, message.targetSlot, message.count);
      }
      return null;
    }
  }
}
