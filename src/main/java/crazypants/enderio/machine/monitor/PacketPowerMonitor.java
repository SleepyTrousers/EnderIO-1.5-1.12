package crazypants.enderio.machine.monitor;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.Log;

public class PacketPowerMonitor implements IMessage, IMessageHandler<PacketPowerMonitor, IMessage> {

  long pos;
  boolean engineControlEnabled;
  float startLevel;
  float stopLevel;

  public PacketPowerMonitor() {
  }

  public PacketPowerMonitor(TilePowerMonitor pm) {
    pos = pm.getPos().toLong();
    engineControlEnabled = pm.engineControlEnabled;
    startLevel = pm.startLevel;
    stopLevel = pm.stopLevel;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeLong(pos);
    buf.writeBoolean(engineControlEnabled);
    buf.writeFloat(startLevel);
    buf.writeFloat(stopLevel);

  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    pos = buffer.readLong();
    engineControlEnabled = buffer.readBoolean();
    startLevel = buffer.readFloat();
    stopLevel = buffer.readFloat();
  }

  @Override
  public IMessage onMessage(PacketPowerMonitor message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    BlockPos blockPos = BlockPos.fromLong(pos);
    TileEntity te = player.worldObj.getTileEntity(blockPos);
    if (!(te instanceof TilePowerMonitor)) {
      Log.warn("createPowerMonitotPacket: Could not handle packet as TileEntity was not a TilePowerMonitor.");
      return null;
    }
    TilePowerMonitor pm = (TilePowerMonitor) te;
    pm.engineControlEnabled = message.engineControlEnabled;
    pm.startLevel = message.startLevel;
    pm.stopLevel = message.stopLevel;
    player.worldObj.markBlockForUpdate(blockPos);
    return null;
  }

}
