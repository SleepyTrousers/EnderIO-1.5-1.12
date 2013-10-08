package crazypants.enderio.machine.power;

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

public class CapacitorBankPacketHandler implements IPacketProcessor {

  public CapacitorBankPacketHandler() {
  }

  @Override
  public boolean canProcessPacket(int packetID) {
    return packetID == PacketHandler.ID_CAP_BANK_IO_PACKET;
  }

  public static Packet createMaxInputOutputPacket(TileCapacitorBank cb) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_CAP_BANK_IO_PACKET);
      dos.writeInt(cb.xCoord);
      dos.writeInt(cb.yCoord);
      dos.writeInt(cb.zCoord);
      dos.writeInt(cb.getMaxInput());
      dos.writeInt(cb.getMaxOutput());
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

  @Override
  public void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player player) throws IOException {
    if(!(player instanceof EntityPlayer)) {
      Log.warn("CapacitorBankPacketHandler:handleChannelSelectedPacket: Could not handle packet as player not an entity player.");
      return;
    }
    World world = ((EntityPlayer) player).worldObj;
    if(world == null) {
      Log.warn("CapacitorBankPacketHandler:handleChannelSelectedPacket: Could not handle packet as player world was null.");
      return;
    }

    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof TileCapacitorBank)) {
      Log.warn("CapacitorBankPacketHandler:handleChannelSelectedPacket: Could not handle packet as TileEntity was not a CapacitorBank.");
      return;
    }
    TileCapacitorBank cb = (TileCapacitorBank) te;
    cb.setMaxInput(data.readInt());
    cb.setMaxOutput(data.readInt());
  }

}
