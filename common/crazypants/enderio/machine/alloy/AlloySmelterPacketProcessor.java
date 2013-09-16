package crazypants.enderio.machine.alloy;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.Player;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.PacketHandler;

public class AlloySmelterPacketProcessor implements IPacketProcessor {

  @Override
  public boolean canProcessPacket(int packetID) {
    return packetID == PacketHandler.ID_ALLOY_SMELTING_MODE_PACKET;
  }

  @Override
  public void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player player) throws IOException {
    handleSmeltingModePacket(data, manager, player);
  }

  private void handleSmeltingModePacket(DataInputStream data, INetworkManager manager, Player player) throws IOException {
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    boolean val = data.readBoolean();
    EntityPlayerMP p = (EntityPlayerMP) player;
    TileEntity te = p.worldObj.getBlockTileEntity(x, y, z);
    if (te instanceof TileAlloySmelter) {
      TileAlloySmelter me = (TileAlloySmelter) te;
      me.setFurnaceRecipesEnabled(val);
      p.worldObj.markBlockForUpdate(x, y, z);
    }

  }

  
  public static Packet getSmeltingModePacket(TileAlloySmelter te) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_ALLOY_SMELTING_MODE_PACKET);
      dos.writeInt(te.xCoord);
      dos.writeInt(te.yCoord);
      dos.writeInt(te.zCoord);
      dos.writeBoolean(te.areFurnaceRecipesEnabled());
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
  
}
