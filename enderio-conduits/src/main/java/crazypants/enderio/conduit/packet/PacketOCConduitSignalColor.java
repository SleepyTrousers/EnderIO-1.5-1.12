package crazypants.enderio.conduit.packet;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.oc.IOCConduit;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOCConduitSignalColor extends AbstractConduitPacket<IOCConduit> implements
    IMessageHandler<PacketOCConduitSignalColor, IMessage> {

  private EnumFacing dir;
  private DyeColor col;

  public PacketOCConduitSignalColor() {
  }

  public PacketOCConduitSignalColor(IOCConduit con, EnumFacing dir) {
    super(con.getBundle().getEntity(), con);
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

  @Override
  public IMessage onMessage(PacketOCConduitSignalColor message, MessageContext ctx) {
    message.getTileCasted(ctx).setSignalColor(message.dir, message.col);
    IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
    message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);
    return null;
  }

}
