package crazypants.enderio.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.ByteBufUtils;
import crazypants.enderio.Log;

public class AbstractPacketTileEntity<T extends TileEntity> implements IPacketEio {

  protected int x;
  protected int y;
  protected int z;

  private Class<? extends TileEntity> tileClass;

  protected AbstractPacketTileEntity() {
  }

  protected AbstractPacketTileEntity(T tile) {
    tileClass = tile.getClass();
    x = tile.xCoord;
    y = tile.yCoord;
    z = tile.zCoord;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    ByteBufUtils.writeUTF8String(buf, tileClass.getName());

  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
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

  @Override
  public void handleClientSide(EntityPlayer player) {
    T tile = getTileEntity(player.worldObj);
    if(tile != null) {
      handleClientSide(player, player.worldObj, tile);
    }
  }

  protected void handleClientSide(EntityPlayer player, World worldObj, T tile) {
    handle(player, worldObj, tile);
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    T tile = getTileEntity(player.worldObj);
    if(tile != null) {
      handleServerSide(player, player.worldObj, tile);
    }
  }

  protected void handleServerSide(EntityPlayer player, World worldObj, T tile) {
    handle(player, worldObj, tile);
  }

  protected void handle(EntityPlayer player, World worldObj, T tile) {

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

}
