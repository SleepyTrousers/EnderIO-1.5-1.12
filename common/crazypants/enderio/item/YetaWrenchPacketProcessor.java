package crazypants.enderio.item;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.Player;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.conduit.ConduitDisplayMode;

public class YetaWrenchPacketProcessor implements IPacketProcessor {

  public static Packet getSmeltingModePacket(int slot, ConduitDisplayMode mode) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_YETA_WRENCH_MODE_PACKET);
      dos.writeInt(slot);
      dos.writeShort(mode.ordinal());
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
  public boolean canProcessPacket(int packetID) {
    return packetID == PacketHandler.ID_YETA_WRENCH_MODE_PACKET;
  }

  @Override
  public void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player playerIn) throws IOException {

    int slot = -1;
    if(!(playerIn instanceof EntityPlayerMP)) {
      return;
    }

    EntityPlayerMP player = (EntityPlayerMP) playerIn;
    slot = data.readInt();
    short ordinal = data.readShort();

    ItemStack stack = null;
    if(slot > -1 && slot < 9) {
      stack = player.inventory.getStackInSlot(slot);
    }
    if(stack == null) {
      return;
    }
    ConduitDisplayMode.setDisplayMode(stack, ConduitDisplayMode.values()[ordinal]);

  }
}
