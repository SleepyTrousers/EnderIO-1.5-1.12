package crazypants.enderio.machine.generator.combustion;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;
import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

public class PacketCombustionTank extends MessageTileEntity<TileCombustionGenerator> implements IMessageHandler<PacketCombustionTank, IMessage> {

  public NBTTagCompound nbtRoot;

  public PacketCombustionTank() {
  }

  public PacketCombustionTank(@Nonnull TileCombustionGenerator tile) { // TODO: DONE111
    super(tile);
    nbtRoot = new NBTTagCompound();
    if (tile.getCoolantTank().getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.getCoolantTank().writeToNBT(tankRoot);
      nbtRoot.setTag("coolantTank", tankRoot);
    }
    if (tile.getFuelTank().getFluidAmount() > 0) {
      NBTTagCompound tankRoot = new NBTTagCompound();
      tile.getFuelTank().writeToNBT(tankRoot);
      nbtRoot.setTag("fuelTank", tankRoot);
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
  public IMessage onMessage(PacketCombustionTank message, MessageContext ctx) {
    TileCombustionGenerator tile = message.getTileEntity(EnderIO.proxy.getClientWorld());
    if (tile != null) {
      if (message.nbtRoot.hasKey("coolantTank")) {
        NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("coolantTank");
        tile.getCoolantTank().readFromNBT(tankRoot);
      } else {
        tile.getCoolantTank().setFluid(null);
      }
      if (message.nbtRoot.hasKey("fuelTank")) {
        NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("fuelTank");
        tile.getFuelTank().readFromNBT(tankRoot);
      } else {
        tile.getFuelTank().setFluid(null);
      }
    }
    return null;
  }

}
