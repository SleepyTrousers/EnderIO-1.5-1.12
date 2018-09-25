package crazypants.enderio.powertools.machine.monitor;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.render.util.DynaTextureProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketPowerMonitorGraph extends MessageTileEntity<TilePowerMonitor> {

  private int no, collectCount, pos;
  private byte[] data;

  public PacketPowerMonitorGraph() {
  }

  private PacketPowerMonitorGraph(@Nonnull TilePowerMonitor tile) {
    super(tile);
  }

  public static IMessage requestUpdate(@Nonnull TilePowerMonitor te, int no) {
    PacketPowerMonitorGraph msg = new PacketPowerMonitorGraph(te);
    msg.no = no;
    msg.collectCount = -1;
    msg.pos = -1;
    msg.data = null;
    return msg;
  }

  public static IMessage sendUpdate(@Nonnull TilePowerMonitor te, int no) {
    PacketPowerMonitorGraph msg = new PacketPowerMonitorGraph(te);
    msg.no = no;
    msg.collectCount = te.stats[no].getCollectCount();
    msg.pos = te.stats[no].getPos();
    msg.data = te.stats[no].getData();
    return msg;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    no = buf.readShort();
    collectCount = buf.readShort();
    if (collectCount >= 0) {
      pos = buf.readShort();
      data = new byte[StatArray.BYTES * 2];
      buf.readBytes(data);
    } else {
      pos = -1;
      data = null;
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort(no);
    buf.writeShort(collectCount);
    if (collectCount >= 0) {
      buf.writeShort(pos);
      buf.writeBytes(data);
    }
  }

  public static class ServerHandler implements IMessageHandler<PacketPowerMonitorGraph, IMessage> {

    @Override
    public IMessage onMessage(PacketPowerMonitorGraph msg, MessageContext ctx) {
      TilePowerMonitor te = msg.getTileEntity(ctx.getServerHandler().player.world);
      if (te != null && msg.no >= 0 && msg.no < te.stats.length) {
        return sendUpdate(te, msg.no);
      }
      return null;
    }
  }

  public static class ClientHandler implements IMessageHandler<PacketPowerMonitorGraph, IMessage> {

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(PacketPowerMonitorGraph msg, MessageContext ctx) {
      EntityPlayer player = EnderIO.proxy.getClientPlayer();
      if (player != null) {
        TilePowerMonitor te = msg.getTileEntity(player.world);
        if (te != null && msg.no >= 0 && msg.no < te.stats.length) {
          te.stats[msg.no].setCollectCount(msg.collectCount);
          te.stats[msg.no].setPos(msg.pos);
          te.stats[msg.no].setData(msg.data);
          if (msg.no == te.stats.length - 1 && te.dynaTextureProvider != null) {
            ((DynaTextureProvider) te.dynaTextureProvider).updateTexture();
          }
        }
      }
      return null;
    }
  }

}
