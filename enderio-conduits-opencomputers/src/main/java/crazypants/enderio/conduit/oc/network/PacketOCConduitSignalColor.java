package crazypants.enderio.conduit.oc.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.oc.conduit.IOCConduit;
import crazypants.enderio.conduits.network.AbstractConduitPacket;
import crazypants.enderio.util.EnumReader;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOCConduitSignalColor extends AbstractConduitPacket.Sided<IOCConduit> {

  private @Nonnull DyeColor col;

  public PacketOCConduitSignalColor() {
    col = DyeColor.BLACK;
  }

  public PacketOCConduitSignalColor(@Nonnull IOCConduit con, @Nonnull EnumFacing dir) {
    super(con, dir);
    this.col = con.getSignalColor(dir);
  }

  @Override
  public void write(@Nonnull ByteBuf buf) {
    super.write(buf);
    buf.writeShort(col.ordinal());
  }

  @Override
  public void read(@Nonnull ByteBuf buf) {
    super.read(buf);
    col = EnumReader.get(DyeColor.class, buf.readShort());
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
