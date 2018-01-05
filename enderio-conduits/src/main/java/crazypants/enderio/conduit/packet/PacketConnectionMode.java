package crazypants.enderio.conduit.packet;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
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
    super(con.getBundle().getEntity(), con);
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
    if(conduit instanceof IRedstoneConduit) {
      ((IRedstoneConduit)conduit).forceConnectionMode(message.dir, message.mode);
    } else {
      conduit.setConnectionMode(message.dir, message.mode);
    }
    IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
    message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);    
    return null;
  }

}
