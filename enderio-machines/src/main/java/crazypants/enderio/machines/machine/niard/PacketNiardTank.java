package crazypants.enderio.machines.machine.niard;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNiardTank extends MessageTileEntity<TileNiard> {

  private NBTTagCompound tag;

  public PacketNiardTank() {
  }

  public PacketNiardTank(@Nonnull TileNiard tile) {
    super(tile);
    tag = tile.getInputTank().writeToNBT(new NBTTagCompound());
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    ByteBufUtils.writeTag(buf, tag);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    tag = ByteBufUtils.readTag(buf);
  }

  public static class Handler implements IMessageHandler<PacketNiardTank, IMessage> {

    @Override
    public IMessage onMessage(PacketNiardTank message, MessageContext ctx) {
      EntityPlayer player = EnderIO.proxy.getClientPlayer();
      TileNiard tile = message.getTileEntity(player.world);
      if (tile != null) {
        tile.getInputTank().readFromNBT(message.tag);
      }
      return null;
    }
  }
}