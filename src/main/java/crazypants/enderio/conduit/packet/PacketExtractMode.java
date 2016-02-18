package crazypants.enderio.conduit.packet;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.IExtractor;
import crazypants.enderio.machine.RedstoneControlMode;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketExtractMode extends AbstractConduitPacket<IExtractor> implements IMessageHandler<PacketExtractMode, IMessage> {

  private EnumFacing dir;
  private RedstoneControlMode mode;
  private DyeColor color;

  public PacketExtractMode() {
  }

  public PacketExtractMode(IExtractor con, EnumFacing dir) {
    super(con.getBundle().getEntity(), ConTypeEnum.get(con));
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

  @Override
  public IMessage onMessage(PacketExtractMode message, MessageContext ctx) {
    message.getTileCasted(ctx).setExtractionRedstoneMode(message.mode, message.dir);
    message.getTileCasted(ctx).setExtractionSignalColor(message.dir, message.color);
    message.getWorld(ctx).markBlockForUpdate(new BlockPos(message.x, message.y, message.z));
    return null;
  }

}
