package crazypants.enderio.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMagnetState implements IMessage {

  public enum SlotType {
    INVENTORY,
    ARMOR,
    BAUBLES
  }

  public PacketMagnetState() {
  }

  private boolean isActive;
  private SlotType type;
  private int slot;

  public PacketMagnetState(SlotType slottype, int slot, boolean isActive) {
    this.type = slottype;
    this.slot = slot;
    this.isActive = isActive;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeShort(type.ordinal());
    buf.writeInt(slot);
    buf.writeBoolean(isActive);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    type = SlotType.values()[buf.readShort()];
    slot = buf.readInt();
    isActive = buf.readBoolean();
  }

  public static class Handler implements IMessageHandler<PacketMagnetState, IMessage> {
    @Override
    public IMessage onMessage(PacketMagnetState message, MessageContext ctx) {
      EntityPlayerMP player = ctx.getServerHandler().player;
      MagnetController.setMagnetActive(player, message.type, message.slot, message.isActive);
      return null;
    }
  }

}
