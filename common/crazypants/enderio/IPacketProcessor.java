package crazypants.enderio;

public interface IPacketProcessor {

  boolean canProcessPacket(int packetID);

  //TODO:1.7
  //NB: Id already read
  //void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player player) throws IOException;

}
