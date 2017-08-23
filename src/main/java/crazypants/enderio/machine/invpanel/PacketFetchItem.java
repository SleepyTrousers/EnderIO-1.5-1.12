package crazypants.enderio.machine.invpanel;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.Log;
import crazypants.enderio.machine.invpanel.client.ItemEntry;

public class PacketFetchItem implements IMessage, IMessageHandler<PacketFetchItem, IMessage> {

  private int generation;
  private int dbID;
  private int targetSlot;
  private int count;

  public PacketFetchItem() {
  }

  public PacketFetchItem(int generation, ItemEntry entry, int targetSlot, int count) {
    this.generation = generation;
    this.dbID = entry.dbID;
    this.targetSlot = targetSlot;
    this.count = count;
    Log.info("Looking up item " + Item.itemRegistry.getNameForObject(entry.getItem()) + ". ID: " + entry.itemID + "  side: " + FMLCommonHandler.instance().getEffectiveSide());
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

  @Override
  public IMessage onMessage(PacketFetchItem message, MessageContext ctx) {
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;
    if(player.openContainer instanceof InventoryPanelContainer) {
      InventoryPanelContainer ipc = (InventoryPanelContainer) player.openContainer;
      ipc.executeFetchItems(player, message.generation, message.dbID, message.targetSlot, message.count);
    }
    return null;
  }
}
