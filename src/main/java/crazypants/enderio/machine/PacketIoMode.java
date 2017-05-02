package crazypants.enderio.machine;

import com.enderio.core.common.util.BlockCoord;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketIoMode implements IMessage, IMessageHandler<PacketIoMode, IMessage> {

  private int x;
  private int y;
  private int z;
  private IoMode mode;
  private EnumFacing face;

  public PacketIoMode() {
  }

  public PacketIoMode(IIoConfigurable cont) {
    BlockCoord location = cont.getLocation();
    this.x = location.x;
    this.y = location.y;
    this.z = location.z;
    this.mode = IoMode.NONE;
    this.face = null;
  }

  public PacketIoMode(IIoConfigurable cont, EnumFacing face) {
    BlockCoord location = cont.getLocation();
    this.x = location.x;
    this.y = location.y;
    this.z = location.z;
    this.face = face;
    mode = cont.getIoMode(face);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeShort((short) mode.ordinal());
    if(face != null) {
      buf.writeShort((short) face.ordinal());
    } else {
      buf.writeShort(-1);
    }

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    mode = IoMode.values()[buf.readShort()];
    short ord = buf.readShort();
    if(ord < 0) {
      face = null;
    } else {
      face = EnumFacing.values()[ord];
    }
  }

  @Override
  public IMessage onMessage(PacketIoMode message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity te = player.world.getTileEntity(new BlockPos(message.x, message.y, message.z));
    if(te instanceof IIoConfigurable) {
      IIoConfigurable me = (IIoConfigurable) te;
      if(message.face == null) {
        me.clearAllIoModes();
      } else {
        me.setIoMode(message.face, message.mode);
      }
    }
    return null;
  }
}
