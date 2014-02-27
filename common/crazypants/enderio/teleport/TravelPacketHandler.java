package crazypants.enderio.teleport;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet28EntityVelocity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.Log;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.teleport.TileTravelAnchor.AccessMode;
import crazypants.util.Util;
import crazypants.vecmath.Vector3d;

public class TravelPacketHandler implements IPacketProcessor, IConnectionHandler {

  @Override
  public boolean canProcessPacket(int packetID) {
    return packetID == PacketHandler.ID_TRAVEL_AUTH_ACCESS_MODE || packetID == PacketHandler.ID_TRAVEL_AUTH_GUI || packetID == PacketHandler.ID_TRAVEL_PLATFORM
        || packetID == PacketHandler.ID_TRAVEL_STAFF_DRAIN
        || packetID == PacketHandler.ID_TRAVEL_SETTINGS;
  }

  public static Packet createAccessModePacket(int x, int y, int z, AccessMode mode) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_TRAVEL_AUTH_ACCESS_MODE);
      dos.writeInt(x);
      dos.writeInt(y);
      dos.writeInt(z);
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

  private void processAuthModePacket(DataInputStream data, Player player) throws IOException {
    if(!(player instanceof EntityPlayer)) {
      return;
    }
    EntityPlayer ep = (EntityPlayer) player;
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    AccessMode mode = AccessMode.values()[data.readShort()];

    TileEntity te = ep.worldObj.getBlockTileEntity(x, y, z);
    if(te instanceof TileTravelAnchor) {
      ((TileTravelAnchor) te).setAccessMode(mode);
      ep.worldObj.markBlockForUpdate(x, y, z);
    }

  }

  public static Packet createOpenAuthGuiPacket(int x, int y, int z) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_TRAVEL_AUTH_GUI);
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

  private void processOpenGuiPacket(DataInputStream data, Player player) throws IOException {
    if(!(player instanceof EntityPlayer)) {
      return;
    }
    EntityPlayer ep = (EntityPlayer) player;
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    ep.openGui(EnderIO.instance, GuiHandler.GUI_ID_TRAVEL_AUTH, ep.worldObj, x, y, z);
  }

  public static Packet createDrainPowerPacket(int powerUse) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_TRAVEL_STAFF_DRAIN);
      dos.writeInt(powerUse);
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

  public static Packet createTravelSettingsPacket() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_TRAVEL_SETTINGS);
      dos.writeBoolean(Config.travelAnchorEnabled);
      dos.writeInt(Config.travelAnchorMaxDistance);

      dos.writeBoolean(Config.travelStaffEnabled);
      dos.writeBoolean(Config.travelStaffBlinkEnabled);
      dos.writeBoolean(Config.travelStaffBlinkThroughSolidBlocksEnabled);
      dos.writeBoolean(Config.travelStaffBlinkThroughClearBlocksEnabled);

      dos.writeInt(Config.travelStaffBlinkPauseTicks);
      dos.writeInt(Config.travelStaffMaxDistance);
      dos.writeInt(Config.travelStaffMaxPowerIo);
      dos.writeInt(Config.travelStaffMaxBlinkDistance);
      dos.writeFloat(Config.travelStaffPowerPerBlock);

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

  private void processSettingsPacket(DataInputStream data, Player player) throws IOException {
    Config.travelAnchorEnabled = data.readBoolean();
    Config.travelAnchorMaxDistance = data.readInt();

    Config.travelStaffEnabled = data.readBoolean();
    Config.travelStaffBlinkEnabled = data.readBoolean();
    Config.travelStaffBlinkThroughSolidBlocksEnabled = data.readBoolean();
    Config.travelStaffBlinkThroughClearBlocksEnabled = data.readBoolean();
    Config.travelStaffBlinkPauseTicks = data.readInt();
    Config.travelStaffMaxDistance = data.readInt();
    Config.travelStaffMaxPowerIo = data.readInt();
    Config.travelStaffMaxBlinkDistance = data.readInt();
    Config.travelStaffPowerPerBlock = data.readFloat();
  }

  @Override
  public void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player player) throws IOException {
    if(packetID == PacketHandler.ID_TRAVEL_PLATFORM) {
      processMovePacket(data, player);
    } else if(packetID == PacketHandler.ID_TRAVEL_STAFF_DRAIN) {
      processDrainPacket(data, player);
    } else if(packetID == PacketHandler.ID_TRAVEL_SETTINGS) {
      processSettingsPacket(data, player);
    } else if(packetID == PacketHandler.ID_TRAVEL_AUTH_GUI) {
      processOpenGuiPacket(data, player);
    } else if(packetID == PacketHandler.ID_TRAVEL_AUTH_ACCESS_MODE) {
      processAuthModePacket(data, player);
    } else {
      Log.warn("Recieved unkown packet with ID " + packetID);
    }

  }

  private void processDrainPacket(DataInputStream data, Player player) throws IOException {
    if(!(player instanceof EntityPlayer)) {
      return;
    }
    int powerUse = data.readInt();
    EntityPlayer ep = (EntityPlayer) player;
    if(ItemTravelStaff.isEquipped(ep)) {
      EnderIO.itemTravelStaff.extractInternal(ep.getCurrentEquippedItem(), powerUse);
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
    ep.fallDistance = 0;

    if(conserveMotion) {
      Vector3d velocityVex = Util.getLookVecEio(ep);
      Packet28EntityVelocity p = new Packet28EntityVelocity(ep.entityId, velocityVex.x, velocityVex.y, velocityVex.z);
      PacketDispatcher.sendPacketToPlayer(p, player);
    }

    if(ItemTravelStaff.isEquipped(ep)) {
      EnderIO.itemTravelStaff.extractInternal(ep.getCurrentEquippedItem(), powerUse);
    }

  }

  @Override
  public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
    //send config values to players when they log in
    PacketDispatcher.sendPacketToPlayer(createTravelSettingsPacket(), player);
  }

  @Override
  public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
    return null;
  }

  @Override
  public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
  }

  @Override
  public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
  }

  @Override
  public void connectionClosed(INetworkManager manager) {
  }

  @Override
  public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
  }
}
