package crazypants.enderio.conduits.network;

import com.enderio.core.common.util.DyeColor;
import crazypants.enderio.base.conduit.IExtractor;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketExtractMode extends AbstractConduitPacket<IExtractor> {

  private EnumFacing dir;
  private RedstoneControlMode mode;
  private DyeColor color;

  public PacketExtractMode() {
  }

  public PacketExtractMode(IExtractor con, EnumFacing dir) {
    super(con.getBundle().getEntity(), con);
    this.dir = dir;
    mode = con.getExtractionRedstoneMode(dir);
    color = con.getExtractionSignalColor(dir);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if(dir == null) {
      buf.writeShort(-1);
    }else {
      buf.writeShort(dir.ordinal());
    }
    buf.writeShort(mode.ordinal());
    buf.writeShort(color.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    short ord = buf.readShort();
    if(ord < 0) {
      dir = null;
    } else {
      dir = EnumFacing.values()[ord];
    }
    mode = RedstoneControlMode.values()[buf.readShort()];
    color = DyeColor.values()[buf.readShort()];
  }

  public static class Handler implements IMessageHandler<PacketExtractMode, IMessage> {
    @Override
    public IMessage onMessage(PacketExtractMode message, MessageContext ctx) {
      message.getConduit(ctx).setExtractionRedstoneMode(message.mode, message.dir);
      message.getConduit(ctx).setExtractionSignalColor(message.dir, message.color);
      IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
      message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);
      return null;
    }
  }
}
