package crazypants.enderio.teleport;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.Player;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;

public class TravelPlatformPacketHandler implements IPacketProcessor {

  @Override
  public boolean canProcessPacket(int packetID) {
    return packetID == PacketHandler.ID_TRAVEL_PLATFORM;
  }

  public static Packet createMovePacket(int x, int y, int z) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_TRAVEL_PLATFORM);
      dos.writeInt(x);
      dos.writeInt(y);
      dos.writeInt(z);
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
    if(packetID == PacketHandler.ID_TRAVEL_PLATFORM) {
      processMovePacket(data, player);
    } else {
      Log.warn("Recieved unkown packet with ID " + packetID);
    }

  }

  private void processMovePacket(DataInputStream data, Player player) throws IOException {
    if(!(player instanceof EntityPlayer)) {
      return;
    }
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    EntityPlayer ep = (EntityPlayer) player;
    ep.setPositionAndUpdate(x + 0.5, y + 1.1, z + 0.5);

    if(ep.getCurrentEquippedItem() != null && ep.getCurrentEquippedItem().itemID == ModObject.itemTravelStaff.actualId) {
      //ItemStack item = ep.getCurrentEquippedItem();
      //      item.setItemDamage(item.getItemDamage() - 100);
      //      ep.setCurrentItemOrArmor(0, item);
      //ep.inventory.onInventoryChanged();
      //EnderIO.itemTravelStaff.setDamage(item, item.getItemDamage() - 100);
    }

  }

}
