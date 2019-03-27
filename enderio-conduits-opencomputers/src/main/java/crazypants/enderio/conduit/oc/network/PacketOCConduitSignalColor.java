package crazypants.enderio.conduit.oc.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.oc.conduit.IOCConduit;
import crazypants.enderio.conduits.network.AbstractConduitPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOCConduitSignalColor extends AbstractConduitPacket<IOCConduit> {

  private EnumFacing dir;
  private DyeColor col;

  public PacketOCConduitSignalColor() {
  }

  public PacketOCConduitSignalColor(@Nonnull IOCConduit con, @Nonnull EnumFacing dir) {
    super(con);
    this.dir = dir;
    col = con.getSignalColor(dir);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort(dir.ordinal());
    buf.writeShort(col.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    dir = EnumFacing.values()[buf.readShort()];
    col = DyeColor.values()[buf.readShort()];
  }

  public static class Handler implements IMessageHandler<PacketOCConduitSignalColor, IMessage> {

    @Override
    public IMessage onMessage(PacketOCConduitSignalColor message, MessageContext ctx) {
      IOCConduit con = message.getConduit(ctx);
      if (con != null) {
        con.setSignalColor(message.dir, message.col);
        IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
        message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);
      }
      return null;
    }

  }

}
