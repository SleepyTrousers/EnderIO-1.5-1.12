package crazypants.enderio.machine.monitor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import crazypants.enderio.machine.tank.TileTank;
import crazypants.enderio.network.AbstractPacketTileEntity;
import crazypants.enderio.network.NetworkUtil;

public class PacketPowerInfo extends AbstractPacketTileEntity<TilePowerMonitor> {

  private NBTTagCompound nbtRoot;

  public PacketPowerInfo() {
  }

  public PacketPowerInfo(TilePowerMonitor tile) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    tile.writePowerInfoToNBT(nbtRoot);
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.encode(ctx, buf);
    NetworkUtil.writeNBTTagCompound(nbtRoot, buf);
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.decode(ctx, buf);
    nbtRoot = NetworkUtil.readNBTTagCompound(buf);
  }

  @Override
  protected void handleClientSide(EntityPlayer player, World worldObj, TilePowerMonitor tile) {
    tile.readPowerInfoFromNBT(nbtRoot);
  }
}