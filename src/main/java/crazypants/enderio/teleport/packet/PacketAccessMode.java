package crazypants.enderio.teleport.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.teleport.TileTravelAnchor;

/**
 * Created by CrazyPants on 27/02/14.
 */
public class PacketAccessMode implements IMessage, IMessageHandler<PacketAccessMode, IMessage> {

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
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeShort(mode.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    mode = TileTravelAnchor.AccessMode.values()[buf.readShort()];
  }

  public IMessage onMessage(PacketAccessMode message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity te = player.worldObj.getTileEntity(x, y, z);
    if(te instanceof TileTravelAnchor) {
      ((TileTravelAnchor) te).setAccessMode(mode);
      player.worldObj.markBlockForUpdate(x, y, z);
      player.worldObj.markTileEntityChunkModified(x,y,z,te);      
    }
    return null;
  }
}
