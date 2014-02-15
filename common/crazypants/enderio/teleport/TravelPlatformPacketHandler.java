package crazypants.enderio.teleport;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet28EntityVelocity;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import crazypants.enderio.EnderIO;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.render.RenderUtil;
import crazypants.vecmath.Vector3d;

public class TravelPlatformPacketHandler implements IPacketProcessor {

  @Override
  public boolean canProcessPacket(int packetID) {
    return packetID == PacketHandler.ID_TRAVEL_PLATFORM;
  }

  public static Packet createMovePacket(int x, int y, int z, int powerUse, boolean conserveMotion) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_TRAVEL_PLATFORM);
      dos.writeInt(x);
      dos.writeInt(y);
      dos.writeInt(z);
      dos.writeInt(powerUse);
      dos.writeBoolean(conserveMotion);
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
    int powerUse = data.readInt();
    boolean conserveMotion = data.readBoolean();

    EntityPlayer ep = (EntityPlayer) player;

    ep.worldObj.playSoundEffect(ep.posX, ep.posY, ep.posZ, "mob.endermen.portal", 1.0F, 1.0F);

    ep.playSound("mob.endermen.portal", 1.0F, 1.0F);

    ep.setPositionAndUpdate(x + 0.5, y + 1.1, z + 0.5);

    ep.worldObj.playSoundEffect(x, y, z, "mob.endermen.portal", 1.0F, 1.0F);

    if(conserveMotion) {
      Vector3d velocityVex = RenderUtil.getLookVecEio(ep);
      Packet28EntityVelocity p = new Packet28EntityVelocity(ep.entityId, velocityVex.x, velocityVex.y, velocityVex.z);
      PacketDispatcher.sendPacketToPlayer(p, player);
    }

    if(powerUse > 0 && ep.getCurrentEquippedItem() != null && ep.getCurrentEquippedItem().itemID == ModObject.itemTravelStaff.actualId) {
      ItemStack item = ep.getCurrentEquippedItem().copy();
      EnderIO.itemTravelStaff.extractInternal(item, powerUse);
      ep.setCurrentItemOrArmor(0, item);

    }

  }
}
