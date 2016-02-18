package crazypants.enderio.conduit.packet;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRedstoneConduitSignalColor extends AbstractConduitPacket<IInsulatedRedstoneConduit> implements IMessageHandler<PacketRedstoneConduitSignalColor, IMessage> {

  private EnumFacing dir;
  private DyeColor col;

  public PacketRedstoneConduitSignalColor() {
  }

  public PacketRedstoneConduitSignalColor(IInsulatedRedstoneConduit con, EnumFacing dir) {
    super(con.getBundle().getEntity(), ConTypeEnum.REDSTONE);
    this.dir = dir;
    col = con.getSignalColor(dir);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if(dir == null) {
      buf.writeShort(-1);
    }else {
      buf.writeShort(dir.ordinal());
    }
    buf.writeShort(col.ordinal());
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
    col = DyeColor.values()[buf.readShort()];
  }

  @Override
  public IMessage onMessage(PacketRedstoneConduitSignalColor message, MessageContext ctx) {
    message.getTileCasted(ctx).setSignalColor(message.dir, message.col);
    message.getWorld(ctx).markBlockForUpdate(new BlockPos(message.x, message.y, message.z));
    return null;
  }

}
