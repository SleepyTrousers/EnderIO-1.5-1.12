package crazypants.enderio.machine.monitor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.Log;
import crazypants.enderio.network.IPacketEio;

public class PacketPowerMonitor implements IPacketEio {

  int x;
  int y;
  int z;
  boolean engineControlEnabled;
  float startLevel;
  float stopLevel;

  //TODO:1.7
  //SignalColor 

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeBoolean(engineControlEnabled);
    buf.writeFloat(startLevel);
    buf.writeFloat(stopLevel);
    //      dos.writeShort(pm.signalColor.ordinal());
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buffer) {
    x = buffer.readInt();
    y = buffer.readInt();
    z = buffer.readInt();
    engineControlEnabled = buffer.readBoolean();
    startLevel = buffer.readFloat();
    stopLevel = buffer.readFloat();

    //signal color = DyeColor.fromIndex(data.readShort());
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    handle(player);
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    handle(player);
  }

  public void handle(EntityPlayer player) {
    TileEntity te = player.worldObj.getTileEntity(x, y, z);
    if(!(te instanceof TilePowerMonitor)) {
      Log.warn("createPowerMonitotPacket: Could not handle packet as TileEntity was not a TilePowerMonitor.");
      return;
    }
    TilePowerMonitor pm = (TilePowerMonitor) te;
    pm.engineControlEnabled = engineControlEnabled;
    pm.startLevel = startLevel;
    pm.stopLevel = stopLevel;
    //pm.signalColor = signal;
  }

  //  
  //  public static Packet createPowerMonitotPacket(TilePowerMonitor pm) {
  //    ByteArrayOutputStream bos = new ByteArrayOutputStream();
  //    DataOutputStream dos = new DataOutputStream(bos);
  //    try {
  //      dos.writeInt(PacketHandler.ID_POWER_MONITOR_PACKET);
  //      dos.writeInt(pm.xCoord);
  //      dos.writeInt(pm.yCoord);
  //      dos.writeInt(pm.zCoord);
  //      dos.writeBoolean(pm.engineControlEnabled);
  //      dos.writeFloat(pm.startLevel);
  //      dos.writeFloat(pm.stopLevel);
  //      dos.writeShort(pm.signalColor.ordinal());
  //
  //    } catch (IOException e) {
  //      // never thrown
  //    }
  //    Packet250CustomPayload pkt = new Packet250CustomPayload();
  //    pkt.channel = PacketHandler.CHANNEL;
  //    pkt.data = bos.toByteArray();
  //    pkt.length = bos.size();
  //    pkt.isChunkDataPacket = true;
  //    return pkt;
  //  }
  //
  //  private void handlPacket(DataInputStream data, INetworkManager manager, Player player) throws IOException {
  //    if(!(player instanceof EntityPlayer)) {
  //      Log.warn("createPowerMonitotPacket: Could not handle packet as player not an entity player.");
  //      return;
  //    }
  //    World world = ((EntityPlayer) player).worldObj;
  //    if(world == null) {
  //      Log.warn("createPowerMonitotPacket: Could not handle packet as player world was null.");
  //      return;
  //    }
  //
  //    int x = data.readInt();
  //    int y = data.readInt();
  //    int z = data.readInt();
  //    TileEntity te = world.getTileEntity(x, y, z);
  //    if(!(te instanceof TilePowerMonitor)) {
  //      Log.warn("createPowerMonitotPacket: Could not handle packet as TileEntity was not a TilePowerMonitor.");
  //      return;
  //    }
  //    TilePowerMonitor pm = (TilePowerMonitor) te;
  //    pm.engineControlEnabled = data.readBoolean();
  //    pm.startLevel = data.readFloat();
  //    pm.stopLevel = data.readFloat();
  //    pm.signalColor = DyeColor.fromIndex(data.readShort());
  //
  //  }

}
