package crazypants.enderio.machine.alloy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.network.IPacketEio;

public class PacketClientState implements IPacketEio {

  private int x;
  private int y;
  private int z;

  private TileAlloySmelter.Mode mode;

  public PacketClientState() {

  }

  public PacketClientState(TileAlloySmelter tile) {
    x = tile.xCoord;
    y = tile.yCoord;
    z = tile.zCoord;
    mode = tile.getMode();
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeShort(mode.ordinal());
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    short ordinal = buf.readShort();
    mode = TileAlloySmelter.Mode.values()[ordinal];

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
    if(te instanceof TileAlloySmelter) {
      TileAlloySmelter me = (TileAlloySmelter) te;
      me.setMode(mode);
      player.worldObj.markBlockForUpdate(x, y, z);
    }
  }

}
