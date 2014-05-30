package crazypants.enderio.machine.generator.zombie;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import crazypants.enderio.machine.generator.combustion.TileCombustionGenerator;
import crazypants.enderio.network.AbstractPacketTileEntity;
import crazypants.enderio.network.NetworkUtil;

public class PacketTank extends AbstractPacketTileEntity<TileZombieGenerator> {

  private NBTTagCompound nbtRoot;

  public PacketTank() {
  }

  public PacketTank(TileZombieGenerator tile) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    if(tile.fuelTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.fuelTank.writeToNBT(tankRoot);
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
  protected void handleClientSide(EntityPlayer player, World worldObj, TileZombieGenerator tile) {
    if(nbtRoot.hasKey("tank")) {
      NBTTagCompound tankRoot = nbtRoot.getCompoundTag("tank");
      tile.fuelTank.readFromNBT(tankRoot);
    } else {
      tile.fuelTank.setFluid(null);
    }
  }
}