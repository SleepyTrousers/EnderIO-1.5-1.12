package crazypants.enderio.machine.invpanel;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketFetchItem implements IMessage, IMessageHandler<PacketFetchItem, IMessage> {

  private int dbID;
  private int targetSlot;
  private int count;

  public PacketFetchItem() {
  }

  public PacketFetchItem(InventoryDatabaseClient.ItemEntry entry, int targetSlot, int count) {
    this.dbID = entry.dbID;
    this.targetSlot = targetSlot;
    this.count = count;
  }

  @Override
  public void fromBytes(ByteBuf bb) {
    dbID = bb.readInt();
    targetSlot = bb.readShort();
    count = bb.readShort();
  }

  @Override
  public void toBytes(ByteBuf bb) {
    bb.writeInt(dbID);
    bb.writeShort(targetSlot);
    bb.writeShort(count);
  }

  @Override
  public IMessage onMessage(PacketFetchItem message, MessageContext ctx) {
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;
    if(player.openContainer instanceof InventoryPanelContainer) {
      InventoryPanelContainer ipc = (InventoryPanelContainer) player.openContainer;
      ipc.executeFetchItems(player, message.dbID, message.targetSlot, message.count);
    }
    return null;
  }
}
