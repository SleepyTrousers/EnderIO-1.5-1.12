package crazypants.enderio.machine.generator.combustion;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import crazypants.enderio.network.AbstractPacketTileEntity;
import crazypants.enderio.network.NetworkUtil;

public class PacketTanks extends AbstractPacketTileEntity<TileCombustionGenerator> {

  private NBTTagCompound nbtRoot;

  public PacketTanks() {
  }

  public PacketTanks(TileCombustionGenerator tile) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    if(tile.coolantTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.coolantTank.writeToNBT(tankRoot);
      nbtRoot.setTag("coolantTank", tankRoot);
    }
    if(tile.fuelTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.fuelTank.writeToNBT(tankRoot);
      nbtRoot.setTag("fuelTank", tankRoot);
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
  protected void handleClientSide(EntityPlayer player, World worldObj, TileCombustionGenerator tile) {
    if(nbtRoot.hasKey("coolantTank")) {
      NBTTagCompound tankRoot = nbtRoot.getCompoundTag("coolantTank");
      tile.coolantTank.readFromNBT(tankRoot);
    } else {
      tile.coolantTank.setFluid(null);
    }
    if(nbtRoot.hasKey("fuelTank")) {
      NBTTagCompound tankRoot = nbtRoot.getCompoundTag("fuelTank");
      tile.fuelTank.readFromNBT(tankRoot);
    } else {
      tile.fuelTank.setFluid(null);
    }
  }

}
