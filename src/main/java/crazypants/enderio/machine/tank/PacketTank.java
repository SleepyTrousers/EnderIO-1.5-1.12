package crazypants.enderio.machine.tank;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import crazypants.enderio.machine.generator.combustion.TileCombustionGenerator;
import crazypants.enderio.network.AbstractPacketTileEntity;
import crazypants.enderio.network.NetworkUtil;

public class PacketTank extends AbstractPacketTileEntity<TileTank> {

  private NBTTagCompound nbtRoot;

  public PacketTank() {
  }

  public PacketTank(TileTank tile) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    if(tile.tank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.tank.writeToNBT(tankRoot);
      nbtRoot.setTag("tank", tankRoot);
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
  protected void handleClientSide(EntityPlayer player, World worldObj, TileTank tile) {
    if(nbtRoot.hasKey("tank")) {
      NBTTagCompound tankRoot = nbtRoot.getCompoundTag("tank");
      tile.tank.readFromNBT(tankRoot);
    } else {
      tile.tank.setFluid(null);
    }
  }
}