package crazypants.enderio.machine.drain;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.MessageTileEntity;
import crazypants.enderio.network.NetworkUtil;

public class PacketDrain extends MessageTileEntity<TileDrain> implements IMessageHandler<PacketDrain, IMessage> {

  private NBTTagCompound nbtRoot;

  public PacketDrain() {
  }

  public PacketDrain(TileDrain tile) {
    super(tile);
    nbtRoot = new NBTTagCompound();
    if(tile.tank.getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.tank.writeToNBT(tankRoot);
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
  public IMessage onMessage(PacketDrain message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileDrain tile = message.getTileEntity(player.worldObj);
    if(tile == null) {
      return null;
    }
    if(message.nbtRoot.hasKey("tank")) {
      NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("tank");
      tile.tank.readFromNBT(tankRoot);
    } else {
      tile.tank.setFluid(null);
    }
    return null;
  }
}