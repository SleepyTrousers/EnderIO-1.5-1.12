package crazypants.enderio;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.Player;

public interface IPacketProcessor {

  boolean canProcessPacket(int packetID);
  
  //NB: Id already read
  void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player player) throws IOException;
  
}
