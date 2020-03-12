package crazypants.enderio.conduits.network;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.conduits.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.util.EnumReader;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConnectionMode extends AbstractConduitPacket.Sided<IConduit> {

  private @Nonnull ConnectionMode mode = ConnectionMode.NOT_SET;

  public PacketConnectionMode() {
  }

  public PacketConnectionMode(@Nonnull IConduit con, @Nonnull EnumFacing dir, @Nonnull ConnectionMode mode) {
    super(con, dir);
    this.mode = mode;
  }

  @Override
  public void write(@Nonnull ByteBuf buf) {
    super.write(buf);
    buf.writeShort(mode.ordinal());
  }

  @Override
  public void read(@Nonnull ByteBuf buf) {
    super.read(buf);
    mode = EnumReader.get(ConnectionMode.class, buf.readShort());
  }

  public static class Handler implements IMessageHandler<PacketConnectionMode, IMessage> {

    @Override
    public IMessage onMessage(PacketConnectionMode message, MessageContext ctx) {
      IConduit conduit = message.getConduit(ctx);
      if (conduit instanceof IServerConduit) {
        if (conduit instanceof IRedstoneConduit) {
          ((IRedstoneConduit) conduit).forceConnectionMode(message.dir, message.mode);
        } else if (conduit instanceof IServerConduit) {
          ((IServerConduit) conduit).setConnectionMode(message.dir, message.mode);
        }
        IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
        message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);
      }
      return null;
    }
  }

}
