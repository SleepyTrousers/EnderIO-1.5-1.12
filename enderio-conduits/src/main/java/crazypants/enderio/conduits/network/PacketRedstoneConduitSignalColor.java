package crazypants.enderio.conduits.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduits.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.util.EnumReader;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRedstoneConduitSignalColor extends AbstractConduitPacket.Sided<IRedstoneConduit> {

  private @Nonnull DyeColor col = DyeColor.BLACK;
  private boolean isInput;

  public PacketRedstoneConduitSignalColor() {
  }

  public PacketRedstoneConduitSignalColor(@Nonnull IRedstoneConduit con, @Nonnull EnumFacing dir, boolean isInput) {
    super(con, dir);
    this.col = isInput ? con.getInputSignalColor(dir) : con.getOutputSignalColor(dir);
    this.isInput = isInput;
  }

  @Override
  public void write(@Nonnull ByteBuf buf) {
    super.write(buf);
    buf.writeShort(col.ordinal());
    buf.writeBoolean(isInput);
  }

  @Override
  public void read(@Nonnull ByteBuf buf) {
    super.read(buf);
    col = EnumReader.get(DyeColor.class, buf.readShort());
    isInput = buf.readBoolean();
  }

  public static class Handler implements IMessageHandler<PacketRedstoneConduitSignalColor, IMessage> {

    @Override
    public IMessage onMessage(PacketRedstoneConduitSignalColor message, MessageContext ctx) {
      final IRedstoneConduit conduit = message.getConduit(ctx);
      if (conduit != null) {
        if (message.isInput) {
          conduit.setInputSignalColor(message.dir, message.col);
        } else {
          conduit.setOutputSignalColor(message.dir, message.col);
        }
        IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
        message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);
      }
      return null;
    }
  }

}
