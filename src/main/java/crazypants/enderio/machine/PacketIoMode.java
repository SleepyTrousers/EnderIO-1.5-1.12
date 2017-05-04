package crazypants.enderio.machine;

import com.enderio.core.common.network.MessageTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketIoMode extends MessageTileEntity<TileEntity> {

  IoMode mode;
  EnumFacing face;

  public PacketIoMode() {
  }

  public <E extends TileEntity & IIoConfigurable> PacketIoMode(E cont) {
    super(cont);
    this.mode = IoMode.NONE;
    this.face = null;
  }

  public <E extends TileEntity & IIoConfigurable> PacketIoMode(E cont, EnumFacing face) {
    super(cont);
    this.face = face;
    mode = cont.getIoMode(face);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort((short) mode.ordinal());
    if (face != null) {
      buf.writeShort((short) face.ordinal());
    } else {
      buf.writeShort(-1);
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    mode = IoMode.values()[buf.readShort()];
    short ord = buf.readShort();
    if (ord < 0) {
      face = null;
    } else {
      face = EnumFacing.values()[ord];
    }
  }

  public static class Handler implements IMessageHandler<PacketIoMode, IMessage> {

    @Override
    public IMessage onMessage(PacketIoMode message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().playerEntity;
      TileEntity te = message.getTileEntity(player.worldObj);
      if (te instanceof IIoConfigurable) {
        IIoConfigurable me = (IIoConfigurable) te;
        if (message.face == null) {
          me.clearAllIoModes();
        } else {
          me.setIoMode(message.face, message.mode);
        }
      }
      return null;
    }

  }
}
