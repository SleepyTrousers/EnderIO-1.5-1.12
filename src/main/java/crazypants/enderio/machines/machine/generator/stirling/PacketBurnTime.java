package crazypants.enderio.machines.machine.generator.stirling;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

public class PacketBurnTime extends MessageTileEntity<TileStirlingGenerator> implements IMessageHandler<PacketBurnTime, IMessage> {

  public int burnTime;
  public int totalBurnTime;
  public boolean isLavaFired;

  public PacketBurnTime() {
  }

  public PacketBurnTime(@Nonnull TileStirlingGenerator tile) {
    super(tile);
    burnTime = tile.burnTime;
    totalBurnTime = tile.totalBurnTime;
    isLavaFired = tile.isLavaFired;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(burnTime);
    buf.writeInt(totalBurnTime);
    buf.writeBoolean(isLavaFired);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    burnTime = buf.readInt();
    totalBurnTime = buf.readInt();
    isLavaFired = buf.readBoolean();
  }

  @Override
  public IMessage onMessage(PacketBurnTime message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    if (player != null) {
      TileStirlingGenerator tile = message.getTileEntity(player.world);
      if (tile != null) {
        tile.burnTime = message.burnTime;
        tile.totalBurnTime = message.totalBurnTime;
        tile.isLavaFired = message.isLavaFired;
      }
    }
    return null;
  }

}
