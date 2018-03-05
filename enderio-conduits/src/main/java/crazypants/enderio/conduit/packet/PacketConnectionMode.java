package crazypants.enderio.conduit.packet;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConnectionMode extends AbstractConduitPacket<IConduit> {

  private EnumFacing dir;
  private boolean next;

  public PacketConnectionMode() {
  }

  public PacketConnectionMode(IConduit con, EnumFacing dir, boolean next) {
    super(con.getBundle().getEntity(), con);
    this.dir = dir;
    this.next = next;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if (dir != null) {
      buf.writeShort(dir.ordinal());
    } else {
      buf.writeShort(-1);
    }
    buf.writeBoolean(next);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    short ord = buf.readShort();
    if (ord < 0) {
      dir = null;
    } else {
      dir = EnumFacing.values()[ord];
    }
    next = buf.readBoolean();

  }

  public static class Handler implements IMessageHandler<PacketConnectionMode, IMessage> {

    @Override
    public IMessage onMessage(PacketConnectionMode message, MessageContext ctx) {
      IConduit conduit = message.getConduit(ctx);
      if (conduit instanceof IServerConduit) {
        ConnectionMode mode = message.next ? ((IServerConduit) conduit).getNextConnectionMode(message.dir)
            : ((IServerConduit) conduit).getPreviousConnectionMode(message.dir);
        if (conduit instanceof IRedstoneConduit) {
          ((IRedstoneConduit) conduit).forceConnectionMode(message.dir, mode);
        } else if (conduit instanceof IServerConduit) {
          ((IServerConduit) conduit).setConnectionMode(message.dir, mode);
        }
        IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
        message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);
      }
      return null;
    }
  }

}
