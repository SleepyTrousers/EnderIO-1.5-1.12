package crazypants.enderio.machine.tank;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;
import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTankFluid extends MessageTileEntity<TileTank> implements IMessageHandler<PacketTankFluid, IMessage> {

  private NBTTagCompound nbtRoot;

  public PacketTankFluid() {
  }

  public PacketTankFluid(TileTank tile) {
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
  public IMessage onMessage(PacketTankFluid message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileTank tile = message.getTileEntity(player.world);
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