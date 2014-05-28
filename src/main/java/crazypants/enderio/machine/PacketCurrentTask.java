package crazypants.enderio.machine;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import crazypants.enderio.machine.tank.TileTank;
import crazypants.enderio.network.AbstractPacketTileEntity;
import crazypants.enderio.network.NetworkUtil;

public class PacketCurrentTask extends AbstractPacketTileEntity<AbstractPoweredTaskEntity> {

  private NBTTagCompound nbtRoot;

  public PacketCurrentTask() {
  }

  public PacketCurrentTask(AbstractPoweredTaskEntity tile) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    if(tile.currentTask != null) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.currentTask.writeToNBT(tankRoot);
      nbtRoot.setTag("currentTask", tankRoot);
    }
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
  protected void handleClientSide(EntityPlayer player, World worldObj, AbstractPoweredTaskEntity tile) {
    if(nbtRoot.hasKey("currentTask")) {
      NBTTagCompound tankRoot = nbtRoot.getCompoundTag("currentTask");
      tile.currentTask = PoweredTask.readFromNBT(nbtRoot.getCompoundTag("currentTask"));
    } else {
      tile.currentTask = null;
    }
  }
}
