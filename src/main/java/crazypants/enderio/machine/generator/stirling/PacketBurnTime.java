package crazypants.enderio.machine.generator.stirling;

import io.netty.buffer.ByteBuf;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.util.ClientUtil;

public class PacketBurnTime extends MessageTileEntity<TileEntityStirlingGenerator> implements IMessageHandler<PacketBurnTime, IMessage> {

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
    ClientUtil.setStirlingBurnTime(message, message.x, message.y, message.z);
    return null;
  }
}
