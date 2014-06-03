package crazypants.enderio.machine.monitor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import crazypants.enderio.network.MessageTileEntity;
import crazypants.enderio.network.NetworkUtil;

public class PacketPowerInfo extends MessageTileEntity<TilePowerMonitor> {

  private NBTTagCompound nbtRoot;

  public PacketPowerInfo() {
  }

  public PacketPowerInfo(TilePowerMonitor tile) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    tile.writePowerInfoToNBT(nbtRoot);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(ctx, buf);
    NetworkUtil.writeNBTTagCompound(nbtRoot, buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(ctx, buf);
    nbtRoot = NetworkUtil.readNBTTagCompound(buf);
  }

  @Override
  protected void handleClientSide(EntityPlayer player, World worldObj, TilePowerMonitor tile) {
    tile.readPowerInfoFromNBT(nbtRoot);
  }
}