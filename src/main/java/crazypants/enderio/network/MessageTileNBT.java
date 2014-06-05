package crazypants.enderio.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.Log;

/**
 * Created by CrazyPants on 27/02/14.
 */
public class MessageTileNBT implements IMessage, IMessageHandler<MessageTileNBT, IMessage> {

  TileEntity te;

  int x;
  int y;
  int z;
  NBTTagCompound tags;

  boolean renderOnUpdate = false;

  public MessageTileNBT() {

  }

  public MessageTileNBT(TileEntity te) {
    this.te = te;
    x = te.xCoord;
    y = te.yCoord;
    z = te.zCoord;
    tags = new NBTTagCompound();
    te.writeToNBT(tags);
  }

  @Override
  public void toBytes(ByteBuf buffer) {
    buffer.writeInt(x);
    buffer.writeInt(y);
    buffer.writeInt(z);
    NetworkUtil.writeNBTTagCompound(tags, buffer);
  }

  @Override
  public void fromBytes(ByteBuf dis) {
    x = dis.readInt();
    y = dis.readInt();
    z = dis.readInt();
    tags = NetworkUtil.readNBTTagCompound(dis);
  }

  @Override
  public IMessage onMessage(MessageTileNBT msg, MessageContext ctx) {
    te = handle(ctx.getServerHandler().playerEntity.worldObj);
    if(te != null && renderOnUpdate) {
      te.getWorldObj().markBlockForUpdate(x,y,z);
    }
    return null;
  }

  private TileEntity handle(World world) {
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
