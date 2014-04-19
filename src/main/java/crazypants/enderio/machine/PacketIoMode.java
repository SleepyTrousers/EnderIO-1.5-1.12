package crazypants.enderio.machine;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.network.IPacketEio;

public class PacketIoMode implements IPacketEio {

  private int x;
  private int y;
  private int z;
  private IoMode mode;
  private ForgeDirection face;

  public PacketIoMode() {
  }

  public PacketIoMode(IIoConfigurable cont, ForgeDirection face) {
    this.x = cont.getLocation().x;
    this.y = cont.getLocation().y;
    this.z = cont.getLocation().z;
    this.face = face;
    mode = cont.getIoMode(face);
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeShort((short) mode.ordinal());
    buf.writeShort((short) face.ordinal());

  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    mode = IoMode.values()[buf.readShort()];
    face = ForgeDirection.values()[buf.readShort()];
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    handle(player);
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    handle(player);
  }

  private void handle(EntityPlayer player) {
    TileEntity te = player.worldObj.getTileEntity(x, y, z);
    if(te instanceof IIoConfigurable) {
      IIoConfigurable me = (IIoConfigurable) te;
      me.setIoMode(face, mode);
      player.worldObj.markBlockForUpdate(x, y, z);
    }
  }

}
