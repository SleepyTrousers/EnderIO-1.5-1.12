package crazypants.enderio.teleport.packet;

import crazypants.enderio.network.IPacketEio;
import crazypants.enderio.teleport.TileTravelAnchor;
import crazypants.enderio.teleport.TileTravelAnchor.AccessMode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by CrazyPants on 27/02/14.
 */
public class PacketAccessMode implements IPacketEio {

  int x;
  int y;
  int z;
  TileTravelAnchor.AccessMode mode;

  public PacketAccessMode() {
  }

  public PacketAccessMode(int x, int y, int z, TileTravelAnchor.AccessMode mode) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.mode = mode;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf dos) {
    dos.writeInt(x);
    dos.writeInt(y);
    dos.writeInt(z);
    dos.writeShort(mode.ordinal());
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf data) {
    x = data.readInt();
    y = data.readInt();
    z = data.readInt();
    mode = TileTravelAnchor.AccessMode.values()[data.readShort()];
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
     TileEntity te = player.worldObj.getTileEntity(x, y, z);
    if(te instanceof TileTravelAnchor) {
      ((TileTravelAnchor) te).setAccessMode(mode);
      player.worldObj.markBlockForUpdate(x, y, z);
      player.worldObj.markTileEntityChunkModified(x,y,z,te);      
    }
  }
}
