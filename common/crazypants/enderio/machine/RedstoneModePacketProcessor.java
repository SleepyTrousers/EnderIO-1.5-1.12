package crazypants.enderio.machine;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.Player;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.machine.hypercube.TileHyperCube;
import crazypants.enderio.machine.power.TileCapacitorBank;

public class RedstoneModePacketProcessor implements IPacketProcessor {

  public static Packet getRedstoneControlPacket(AbstractMachineEntity te) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_MACHINE_REDSTONE_PACKET);
      dos.writeInt(te.xCoord);
      dos.writeInt(te.yCoord);
      dos.writeInt(te.zCoord);
      dos.writeShort((short) te.getRedstoneControlMode().ordinal());
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

  public static Packet getRedstoneControlPacket(TileCapacitorBank te) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_CAP_BANK_REDSTONE_PACKET);
      dos.writeInt(te.xCoord);
      dos.writeInt(te.yCoord);
      dos.writeInt(te.zCoord);
      dos.writeShort((short) te.getInputControlMode().ordinal());
      dos.writeShort((short) te.getOutputControlMode().ordinal());
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
    return packetID == PacketHandler.ID_MACHINE_REDSTONE_PACKET || packetID == PacketHandler.ID_CAP_BANK_REDSTONE_PACKET;
  }

  @Override
  public void processPacket(int id, INetworkManager manager, DataInputStream data, Player player) throws IOException {
    if(id == PacketHandler.ID_MACHINE_REDSTONE_PACKET) {
      handleRedstoneControlPacket(data, manager, player);
    } else if(id == PacketHandler.ID_CAP_BANK_REDSTONE_PACKET) {
      handleCapBankRedstoneControlPacket(data, manager, player);
    
    } else {
      FMLLog.warning("RedstoneModePacketProcessor: Recieved unknow packet: " + id);
    }

  }

  private void handleRedstoneControlPacket(DataInputStream data, INetworkManager manager, Player player) throws IOException {
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    short ordinal = data.readShort();
    EntityPlayerMP p = (EntityPlayerMP) player;
    TileEntity te = p.worldObj.getBlockTileEntity(x, y, z);
    if (te instanceof AbstractMachineEntity) {
      AbstractMachineEntity me = (AbstractMachineEntity) te;
      me.setRedstoneControlMode(RedstoneControlMode.values()[ordinal]);
      p.worldObj.markBlockForUpdate(x, y, z);
    }

  }

  private void handleCapBankRedstoneControlPacket(DataInputStream data, INetworkManager manager, Player player) throws IOException {
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    short inputOrdinal = data.readShort();
    short outputOrdinal = data.readShort();
    EntityPlayerMP p = (EntityPlayerMP) player;
    TileEntity te = p.worldObj.getBlockTileEntity(x, y, z);
    if (te instanceof TileCapacitorBank) {
      TileCapacitorBank cb = (TileCapacitorBank) te;
      cb.setInputControlMode(RedstoneControlMode.values()[inputOrdinal]);
      cb.setOutputControlMode(RedstoneControlMode.values()[outputOrdinal]);
      p.worldObj.markBlockForUpdate(x, y, z);
    }

  }

}
