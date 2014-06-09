package crazypants.enderio.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.Log;

public abstract class MessageTileEntity<T extends TileEntity> implements IMessage {

  protected int x;
  protected int y;
  protected int z;

  private Class<? extends TileEntity> tileClass;

  protected MessageTileEntity() {
  }

  protected MessageTileEntity(T tile) {
    tileClass = tile.getClass();
    x = tile.xCoord;
    y = tile.yCoord;
    z = tile.zCoord;
  }

  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    ByteBufUtils.writeUTF8String(buf, tileClass.getName());

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    String str = ByteBufUtils.readUTF8String(buf);
    try {
      tileClass = (Class<TileEntity>) Class.forName(str);
    } catch (Exception e) {
      Log.error("AbstractPacketTileEntity could not load tile entity class: " + str);
    }
  }

  protected T getTileEntity(World worldObj) {
    if(worldObj == null) {
      return null;
    }
    TileEntity te = worldObj.getTileEntity(x, y, z);
    if(te == null) {
      return null;
    }
    if(tileClass.isAssignableFrom(te.getClass())) {
      return (T) te;
    }
    return null;
  }

  protected World getWorld(MessageContext ctx) {
      return ctx.getServerHandler().playerEntity.worldObj;
  }
}
