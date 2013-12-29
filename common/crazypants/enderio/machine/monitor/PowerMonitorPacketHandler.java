package crazypants.enderio.machine.monitor;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.Player;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.Log;
import crazypants.enderio.PacketHandler;
import crazypants.util.DyeColor;

public class PowerMonitorPacketHandler implements IPacketProcessor {

  @Override
  public boolean canProcessPacket(int packetID) {
    return PacketHandler.ID_POWER_MONITOR_PACKET == packetID;
  }

  @Override
  public void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player player) throws IOException {
    if(packetID == PacketHandler.ID_POWER_MONITOR_PACKET) {
      handlPacket(data, manager, player);
    }

  }

  public static Packet createPowerMonitotPacket(TilePowerMonitor pm) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_POWER_MONITOR_PACKET);
      dos.writeInt(pm.xCoord);
      dos.writeInt(pm.yCoord);
      dos.writeInt(pm.zCoord);
      dos.writeBoolean(pm.engineControlEnabled);
      dos.writeFloat(pm.startLevel);
      dos.writeFloat(pm.stopLevel);
      dos.writeShort(pm.signalColor.ordinal());

    } catch (IOException e) {
      // never thrown
    }
    Packet250CustomPayload pkt = new Packet250CustomPayload();
    pkt.channel = PacketHandler.CHANNEL;
    pkt.data = bos.toByteArray();
    pkt.length = bos.size();
    pkt.isChunkDataPacket = true;
    return pkt;
  }

  private void handlPacket(DataInputStream data, INetworkManager manager, Player player) throws IOException {
    if(!(player instanceof EntityPlayer)) {
      Log.warn("createPowerMonitotPacket: Could not handle packet as player not an entity player.");
      return;
    }
    World world = ((EntityPlayer) player).worldObj;
    if(world == null) {
      Log.warn("createPowerMonitotPacket: Could not handle packet as player world was null.");
      return;
    }

    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof TilePowerMonitor)) {
      Log.warn("createPowerMonitotPacket: Could not handle packet as TileEntity was not a TilePowerMonitor.");
      return;
    }
    TilePowerMonitor pm = (TilePowerMonitor) te;
    pm.engineControlEnabled = data.readBoolean();
    pm.startLevel = data.readFloat();
    pm.stopLevel = data.readFloat();
    pm.signalColor = DyeColor.fromIndex(data.readShort());

  }

}
