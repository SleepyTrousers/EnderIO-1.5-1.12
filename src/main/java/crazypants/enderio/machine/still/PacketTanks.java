package crazypants.enderio.machine.still;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import crazypants.enderio.network.AbstractPacketTileEntity;
import crazypants.enderio.network.NetworkUtil;

public class PacketTanks extends AbstractPacketTileEntity<TileVat> {

  private NBTTagCompound nbtRoot;

  public PacketTanks() {
  }

  public PacketTanks(TileVat tile) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    if(tile.inputTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.inputTank.writeToNBT(tankRoot);
      nbtRoot.setTag("inputTank", tankRoot);
    }
    if(tile.outputTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.outputTank.writeToNBT(tankRoot);
      nbtRoot.setTag("outputTank", tankRoot);
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
  protected void handleClientSide(EntityPlayer player, World worldObj, TileVat tile) {
    if(nbtRoot.hasKey("inputTank")) {
      NBTTagCompound tankRoot = nbtRoot.getCompoundTag("inputTank");
      tile.inputTank.readFromNBT(tankRoot);
    } else {
      tile.inputTank.setFluid(null);
    }
    if(nbtRoot.hasKey("outputTank")) {
      NBTTagCompound tankRoot = nbtRoot.getCompoundTag("outputTank");
      tile.outputTank.readFromNBT(tankRoot);
    } else {
      tile.outputTank.setFluid(null);
    }
  }

}
