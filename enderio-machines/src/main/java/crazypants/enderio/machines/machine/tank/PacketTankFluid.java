package crazypants.enderio.machines.machine.tank;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;

import crazypants.enderio.base.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTankFluid extends MessageTileEntity<TileTank> {

  private NBTTagCompound nbtRoot;

  public PacketTankFluid() {
  }

  public PacketTankFluid(@Nonnull TileTank tile) {
    super(tile);
    nbtRoot = tile.tank.writeToNBT(new NBTTagCompound());
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

  public static class Handler implements IMessageHandler<PacketTankFluid, IMessage> {

    @Override
    public IMessage onMessage(PacketTankFluid message, MessageContext ctx) {
      EntityPlayer player = EnderIO.proxy.getClientPlayer();
      TileTank tile = message.getTileEntity(player.world);
      if (tile != null) {
        tile.tank.readFromNBT(message.nbtRoot);
        tile.updateLight();
      }
      return null;
    }
  }
}