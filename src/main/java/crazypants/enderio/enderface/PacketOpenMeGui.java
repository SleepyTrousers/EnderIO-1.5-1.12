package crazypants.enderio.enderface;

//TODO: Applied Energistics
public class PacketOpenMeGui {

  //  private void handleMeTerminalPacket(DataInputStream data, INetworkManager manager, Player p) throws IOException {
  //    if(!(p instanceof EntityPlayerMP)) {
  //      return;
  //    }
  //    EntityPlayerMP player = (EntityPlayerMP) p;
  //    int x = data.readInt();
  //    int y = data.readInt();
  //    int z = data.readInt();
  //
  //    Container container = null;
  //    try {
  //      container = MeProxy.createMeTerminalContainer(player, x, y, z, false);
  //    } catch (Exception e) {
  //      Log.warn("EnderfacePacketProcessor.openRemoteGui: Could not open remote AE GUI. " + e);
  //    }
  //    openRemoteGui(player, GuiHandler.GUI_ID_ME_ACCESS_TERMINAL, player.worldObj, x, y, z, container);
  //  }
  //
  //  public static Packet250CustomPayload createMePacket(int x, int y, int z) {
  //    ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
  //    DataOutputStream dos = new DataOutputStream(bos);
  //    try {
  //      dos.writeInt(PacketHandler.ID_ME_TERMINAL);
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
  //  public static Packet250CustomPayload createPacketEnderface(int x, int y, int z) {
  //    ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
  //    DataOutputStream dos = new DataOutputStream(bos);
  //    try {
  //      dos.writeInt(PacketHandler.ID_ENDERFACE);
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

}
