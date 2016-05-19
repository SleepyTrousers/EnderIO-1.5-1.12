package crazypants.enderio.conduit.packet;

import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConnectionMode extends AbstractConduitPacket<IConduit> implements IMessageHandler<PacketConnectionMode, IMessage>{

  private EnumFacing dir;
  private ConnectionMode mode;

  public PacketConnectionMode() {
  }

  public PacketConnectionMode(IConduit con, EnumFacing dir) {
    super(con.getBundle().getEntity(), ConTypeEnum.get(con));
    this.dir = dir;
    mode = con.getConnectionMode(dir);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if(dir != null) {
      buf.writeShort(dir.ordinal());
    } else {
      buf.writeShort(-1);
    }
    buf.writeShort(mode.ordinal());
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
    mode = ConnectionMode.values()[buf.readShort()];

  }

  @Override
  public IMessage onMessage(PacketConnectionMode message, MessageContext ctx) {
    IConduit conduit = message.getTileCasted(ctx);
    if(conduit == null) {
      return null;
    }
    if(conduit instanceof IInsulatedRedstoneConduit) {
      ((IInsulatedRedstoneConduit)conduit).forceConnectionMode(message.dir, message.mode);
    } else {
      conduit.setConnectionMode(message.dir, message.mode);
    }
    message.getWorld(ctx).markBlockForUpdate(new BlockPos(message.x, message.y, message.z));
    return null;
  }

}
