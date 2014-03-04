package crazypants.enderio.network;

import crazypants.enderio.Log;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by CrazyPants on 27/02/14.
 */
public class PacketTileEntityNbt implements IPacketEio {

  TileEntity te;

  int x;
  int y;
  int z;
  NBTTagCompound tags;

  boolean renderOnUpdate = false;

  public PacketTileEntityNbt() {

  }

  public PacketTileEntityNbt(TileEntity te) {
    this.te = te;
    x = te.xCoord;
    y = te.yCoord;
    z = te.zCoord;
    tags = new NBTTagCompound();
    te.writeToNBT(tags);
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buffer) {
    buffer.writeInt(x);
    buffer.writeInt(y);
    buffer.writeInt(z);
    NetworkUtil.writeNBTTagCompound(tags, buffer);
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf dis) {
    x = dis.readInt();
    y = dis.readInt();
    z = dis.readInt();
    tags = NetworkUtil.readNBTTagCompound(dis);
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    te = handle(player.getEntityWorld());
    if(te != null && renderOnUpdate) {
      te.getWorldObj().markBlockForUpdate(x,y,z);
    }
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    System.out.println("crazypants.enderio.network.PacketTileEntity.handleServerSide");
    te = handle(player.getEntityWorld());
    if(te != null) {
      te.getWorldObj().markBlockForUpdate(x,y,z);
    }
  }

  private TileEntity handle(World world) {
    System.out.println("crazypants.enderio.network.PacketTileEntity.handle");
    if(world == null) {
      Log.warn("PacketUtil.handleTileEntityPacket: TileEntity null world processing tile entity packet.");
      return null;
    }
    TileEntity te = world.getTileEntity(x, y, z);
    if(te == null) {
      Log.warn("PacketUtil.handleTileEntityPacket: TileEntity null when processing tile entity packet.");
      return null;
    }
    te.readFromNBT(tags);
    return te;
  }
}
