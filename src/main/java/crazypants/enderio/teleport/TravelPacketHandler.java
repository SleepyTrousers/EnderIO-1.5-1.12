package crazypants.enderio.teleport;

import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.PacketHandler;

//TODO:1.7
public class TravelPacketHandler implements IPacketProcessor/*
                                                             * ,
                                                             * IConnectionHandler
                                                             */{

  @Override
  public boolean canProcessPacket(int packetID) {
    return packetID == PacketHandler.ID_TRAVEL_AUTH_ACCESS_MODE || packetID == PacketHandler.ID_TRAVEL_AUTH_GUI || packetID == PacketHandler.ID_TRAVEL_PLATFORM
        || packetID == PacketHandler.ID_TRAVEL_STAFF_DRAIN
        || packetID == PacketHandler.ID_TRAVEL_SETTINGS;
  }

  //  public static Packet createAccessModePacket(int x, int y, int z, AccessMode mode) {
  //    ByteArrayOutputStream bos = new ByteArrayOutputStream();
  //    DataOutputStream dos = new DataOutputStream(bos);
  //    try {
  //      dos.writeInt(PacketHandler.ID_TRAVEL_AUTH_ACCESS_MODE);
  //      dos.writeInt(x);
  //      dos.writeInt(y);
  //      dos.writeInt(z);
  //      dos.writeShort(mode.ordinal());
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
  //  private void processAuthModePacket(DataInputStream data, Player player) throws IOException {
  //    if(!(player instanceof EntityPlayer)) {
  //      return;
  //    }
  //    EntityPlayer ep = (EntityPlayer) player;
  //    int x = data.readInt();
  //    int y = data.readInt();
  //    int z = data.readInt();
  //    AccessMode mode = AccessMode.values()[data.readShort()];
  //
  //    TileEntity te = ep.worldObj.getTileEntity(x, y, z);
  //    if(te instanceof TileTravelAnchor) {
  //      ((TileTravelAnchor) te).setAccessMode(mode);
  //      ep.worldObj.markBlockForUpdate(x, y, z);
  //    }
  //
  //  }
  //
  //  public static Packet createOpenAuthGuiPacket(int x, int y, int z) {
  //    ByteArrayOutputStream bos = new ByteArrayOutputStream();
  //    DataOutputStream dos = new DataOutputStream(bos);
  //    try {
  //      dos.writeInt(PacketHandler.ID_TRAVEL_AUTH_GUI);
  //      dos.writeInt(x);
  //      dos.writeInt(y);
  //      dos.writeInt(z);
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
  //  private void processOpenGuiPacket(DataInputStream data, Player player) throws IOException {
  //    if(!(player instanceof EntityPlayer)) {
  //      return;
  //    }
  //    EntityPlayer ep = (EntityPlayer) player;
  //    int x = data.readInt();
  //    int y = data.readInt();
  //    int z = data.readInt();
  //    ep.openGui(EnderIO.instance, GuiHandler.GUI_ID_TRAVEL_AUTH, ep.worldObj, x, y, z);
  //  }
  //
  //
  //
  //  public static Packet createTravelSettingsPacket() {
  //    ByteArrayOutputStream bos = new ByteArrayOutputStream();
  //    DataOutputStream dos = new DataOutputStream(bos);
  //    try {
  //      dos.writeInt(PacketHandler.ID_TRAVEL_SETTINGS);
  //      dos.writeBoolean(Config.travelAnchorEnabled);
  //      dos.writeInt(Config.travelAnchorMaxDistance);
  //
  //      dos.writeBoolean(Config.travelStaffEnabled);
  //      dos.writeBoolean(Config.travelStaffBlinkEnabled);
  //      dos.writeBoolean(Config.travelStaffBlinkThroughSolidBlocksEnabled);
  //      dos.writeBoolean(Config.travelStaffBlinkThroughClearBlocksEnabled);
  //
  //      dos.writeInt(Config.travelStaffBlinkPauseTicks);
  //      dos.writeInt(Config.travelStaffMaxDistance);
  //      dos.writeInt(Config.travelStaffMaxPowerIo);
  //      dos.writeInt(Config.travelStaffMaxBlinkDistance);
  //      dos.writeFloat(Config.travelStaffPowerPerBlock);
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
  //  private void processSettingsPacket(DataInputStream data, Player player) throws IOException {
  //    Config.travelAnchorEnabled = data.readBoolean();
  //    Config.travelAnchorMaxDistance = data.readInt();
  //
  //    Config.travelStaffEnabled = data.readBoolean();
  //    Config.travelStaffBlinkEnabled = data.readBoolean();
  //    Config.travelStaffBlinkThroughSolidBlocksEnabled = data.readBoolean();
  //    Config.travelStaffBlinkThroughClearBlocksEnabled = data.readBoolean();
  //    Config.travelStaffBlinkPauseTicks = data.readInt();
  //    Config.travelStaffMaxDistance = data.readInt();
  //    Config.travelStaffMaxPowerIo = data.readInt();
  //    Config.travelStaffMaxBlinkDistance = data.readInt();
  //    Config.travelStaffPowerPerBlock = data.readFloat();
  //  }
  //
  //
  //

  //
  //  }
  //
  //  @Override
  //  public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
  //    //send config values to players when they log in
  //    PacketDispatcher.sendPacketToPlayer(createTravelSettingsPacket(), player);
  //  }
  //
  //  @Override
  //  public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
  //    return null;
  //  }
  //
  //  @Override
  //  public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
  //  }
  //
  //  @Override
  //  public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
  //  }
  //
  //  @Override
  //  public void connectionClosed(INetworkManager manager) {
  //  }
  //
  //  @Override
  //  public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
  //  }

}
