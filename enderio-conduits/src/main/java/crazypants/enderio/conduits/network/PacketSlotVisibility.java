package crazypants.enderio.conduits.network;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.conduits.gui.ExternalConnectionContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSlotVisibility extends AbstractConduitPacket<IConduit> {

  private boolean filterVisible;
  private boolean upgradeVisible;

  public PacketSlotVisibility() {
  }

  public PacketSlotVisibility(@Nonnull IConduit conduit, boolean filterVisible, boolean upgradeVisible) {
    super(conduit);
    this.filterVisible = filterVisible;
    this.upgradeVisible = upgradeVisible;
  }

  @Override
  public void fromBytes(ByteBuf bb) {
    super.fromBytes(bb);
    int value = bb.readUnsignedByte();
    filterVisible = (value & 1) != 0;
    upgradeVisible = (value & 2) != 0;
  }

  @Override
  public void toBytes(ByteBuf bb) {
    super.toBytes(bb);
    int value = (filterVisible ? 1 : 0) | (upgradeVisible ? 2 : 0);
    bb.writeByte(value);
  }

  public static class Handler implements IMessageHandler<PacketSlotVisibility, IMessage> {

    @Override
    public IMessage onMessage(PacketSlotVisibility message, MessageContext ctx) {
      IConduit conduit = message.getConduit(ctx);
      EntityPlayerMP player = ctx.getServerHandler().player;
      if (conduit != null && player.openContainer instanceof ExternalConnectionContainer) {
        ExternalConnectionContainer ecc = (ExternalConnectionContainer) player.openContainer;
        ecc.setInOutSlotsVisible(message.filterVisible, message.upgradeVisible, conduit);
      }
      return null;
    }
  }
}
