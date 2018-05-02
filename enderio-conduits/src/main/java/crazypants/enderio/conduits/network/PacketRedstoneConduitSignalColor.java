package crazypants.enderio.conduits.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduits.conduit.redstone.IRedstoneConduit;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRedstoneConduitSignalColor extends AbstractConduitPacket<IRedstoneConduit> {

  private EnumFacing dir;
  private DyeColor col;
  private boolean isInput;

  public PacketRedstoneConduitSignalColor() {
  }

  public PacketRedstoneConduitSignalColor(@Nonnull IRedstoneConduit con, EnumFacing dir, boolean isInput) {
    super(con);
    this.dir = dir;
    if (isInput) {
      col = con.getInputSignalColor(dir);
    } else {
      col = con.getOutputSignalColor(dir);
    }
    this.isInput = isInput;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if (dir == null) {
      buf.writeShort(-1);
    } else {
      buf.writeShort(dir.ordinal());
    }
    buf.writeShort(col.ordinal());
    buf.writeBoolean(isInput);
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
    col = DyeColor.values()[buf.readShort()];
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
