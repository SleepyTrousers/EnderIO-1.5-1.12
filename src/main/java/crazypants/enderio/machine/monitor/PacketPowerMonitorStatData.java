package crazypants.enderio.machine.monitor;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.machine.monitor.TilePowerMonitor.StatData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketPowerMonitorStatData extends MessageTileEntity<TilePowerMonitor> {

  private TilePowerMonitor.StatData data = null;

  public PacketPowerMonitorStatData() {
  }

  private PacketPowerMonitorStatData(TilePowerMonitor tile) {
    super(tile);
  }

  public static IMessage requestUpdate(TilePowerMonitor te) {
    PacketPowerMonitorStatData msg = new PacketPowerMonitorStatData(te);
    return msg;
  }

  public static IMessage sendUpdate(TilePowerMonitor te, TilePowerMonitor.StatData data) {
    PacketPowerMonitorStatData msg = new PacketPowerMonitorStatData(te);
    msg.data = data;
    return msg;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    if (buf.readByte() != 1) {
      data = null;
    } else {
      data = new StatData();
      data.powerInConduits = buf.readInt();
      data.maxPowerInConduits = buf.readInt();
      data.powerInCapBanks = buf.readLong();
      data.maxPowerInCapBanks = buf.readLong();
      data.powerInMachines = buf.readLong();
      data.maxPowerInMachines = buf.readLong();
      data.aveRfSent = buf.readFloat();
      data.aveRfReceived = buf.readFloat();
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if (data == null) {
      buf.writeByte(0);
    } else {
      buf.writeByte(1);
      buf.writeInt(data.powerInConduits);
      buf.writeInt(data.maxPowerInConduits);
      buf.writeLong(data.powerInCapBanks);
      buf.writeLong(data.maxPowerInCapBanks);
      buf.writeLong(data.powerInMachines);
      buf.writeLong(data.maxPowerInMachines);
      buf.writeFloat(data.aveRfSent);
      buf.writeFloat(data.aveRfReceived);
    }
  }

  public static class ServerHandler implements IMessageHandler<PacketPowerMonitorStatData, IMessage> {

    @Override
    public IMessage onMessage(PacketPowerMonitorStatData msg, MessageContext ctx) {
      TilePowerMonitor te = msg.getTileEntity(ctx.getServerHandler().playerEntity.world);
      if (te != null) {
        NetworkPowerManager powerManager = te.getPowerManager();
        if (powerManager != null) {
          return sendUpdate(te, new StatData(powerManager));
        }
      }
      return null;
    }
  }

  public static class ClientHandler implements IMessageHandler<PacketPowerMonitorStatData, IMessage> {

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketPowerMonitorStatData msg, MessageContext ctx) {
      EntityPlayer player = EnderIO.proxy.getClientPlayer();
      if (player != null) {
        TilePowerMonitor te = msg.getTileEntity(player.world);
        if (te != null) {
          te.statData = msg.data;
        }
      }
      return null;
    }
  }

}
