package crazypants.enderio.machine.generator.zombie;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.MessageTileEntity;
import crazypants.enderio.network.NetworkUtil;

public class PacketZombieTank extends MessageTileEntity<TileZombieGenerator> implements IMessageHandler<PacketZombieTank, IMessage> {

  private NBTTagCompound nbtRoot;

  public PacketZombieTank() {
  }

  public PacketZombieTank(TileZombieGenerator tile) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    if(tile.fuelTank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.fuelTank.writeToNBT(tankRoot);
      nbtRoot.setTag("tank", tankRoot);
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    NetworkUtil.writeNBTTagCompound(nbtRoot, buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    nbtRoot = NetworkUtil.readNBTTagCompound(buf);
  }

  @Override
  public IMessage onMessage(PacketZombieTank message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileZombieGenerator tile = message.getTileEntity(player.worldObj);
    if (tile != null) {
      if(message.nbtRoot.hasKey("tank")) {
        NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("tank");
        tile.fuelTank.readFromNBT(tankRoot);
      } else {
        tile.fuelTank.setFluid(null);
      }
    }
    return null;
  }
}