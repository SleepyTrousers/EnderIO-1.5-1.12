package crazypants.enderio.machine.generator.stirling;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBurnTime extends MessageTileEntity<TileEntityStirlingGenerator> implements IMessageHandler<PacketBurnTime, IMessage>, Runnable {

  public int burnTime;
  public int totalBurnTime;
  
  public PacketBurnTime() {
  }

  public PacketBurnTime(TileEntityStirlingGenerator tile) {
    super(tile);
    burnTime = tile.burnTime;
    totalBurnTime = tile.totalBurnTime;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(burnTime);
    buf.writeInt(totalBurnTime);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    burnTime = buf.readInt();
    totalBurnTime = buf.readInt();
  }
  
  @Override
  public IMessage onMessage(PacketBurnTime message, MessageContext ctx) {
    Minecraft.getMinecraft().addScheduledTask(message);
    return null;
  }

  @Override
  public void run() {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    if (player != null && player.worldObj != null) {
      TileEntityStirlingGenerator tile = getTileEntity(player.worldObj);
      if (tile != null) {
        tile.burnTime = burnTime;
        tile.totalBurnTime = totalBurnTime;
      }
    }
  }
}
