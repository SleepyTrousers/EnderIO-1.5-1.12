package crazypants.enderio.conduit;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.network.Player;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.Log;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduit.redstone.SignalColor;

public class ConduitPacketHandler implements IPacketProcessor {

  private static enum ConTypeEnum {

    POWER(IPowerConduit.class),
    FLUID(ILiquidConduit.class),
    ITEM(IItemConduit.class),
    REDSTONE(IRedstoneConduit.class);

    final Class<? extends IConduit> baseType;

    private ConTypeEnum(Class<? extends IConduit> baseType) {
      this.baseType = baseType;
    }

    public Class<? extends IConduit> getBaseType() {
      return baseType;
    }

    public static ConTypeEnum get(IConduit con) {
      Class<? extends IConduit> from = con.getBaseConduitType();
      for (ConTypeEnum ct : ConTypeEnum.values()) {
        if(ct.getBaseType() == from) {
          return ct;
        }
      }
      return null;
    }

  }

  public static Packet createConnectionModePacket(IConduitBundle bundle, IConduit con, ForgeDirection dir) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_CONDUIT_CON_MODE);
      dos.writeInt(bundle.getEntity().xCoord);
      dos.writeInt(bundle.getEntity().yCoord);
      dos.writeInt(bundle.getEntity().zCoord);

      dos.writeShort(dir.ordinal());

      dos.writeShort(con.getConectionMode(dir).ordinal());

      ConTypeEnum type = ConTypeEnum.get(con);
      if(type == null) {
        dos.writeShort(-1);
      } else {
        dos.writeShort(type.ordinal());
      }

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

  public static Packet createSignalColorPacket(IConduitBundle bundle, ForgeDirection dir, SignalColor color) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_CONDUIT_SIGNAL_COL);
      dos.writeInt(bundle.getEntity().xCoord);
      dos.writeInt(bundle.getEntity().yCoord);
      dos.writeInt(bundle.getEntity().zCoord);

      dos.writeShort(dir.ordinal());

      dos.writeShort(color.ordinal());

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
    return PacketHandler.ID_CONDUIT_CON_MODE == packetID || packetID == PacketHandler.ID_CONDUIT_SIGNAL_COL;
  }

  @Override
  public void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player player) throws IOException {
    if(PacketHandler.ID_CONDUIT_CON_MODE == packetID) {
      processConnectionModePacket(data, player);
    } else if(packetID == PacketHandler.ID_CONDUIT_SIGNAL_COL) {
      processSignalColorPacket(data, player);
    }
  }

  private void processSignalColorPacket(DataInputStream data, Player player) throws IOException {
    World world = getWorld(player);
    if(world == null) {
      return;
    }
    IConduitBundle conBun = getConduitBundle(data, world);
    if(conBun == null) {
      return;
    }
    ForgeDirection dir = ForgeDirection.values()[data.readShort()];
    SignalColor col = SignalColor.values()[data.readShort()];
    IConduit con = conBun.getConduit(IRedstoneConduit.class);
    if(con instanceof IInsulatedRedstoneConduit) { //yeah, I know
      ((IInsulatedRedstoneConduit) con).setSignalColor(dir, col);
    } else {
      Log.warn("processSignalColorPacket: Could not handle as conduit not found in bundle.");
      return;
    }

    world.markBlockForUpdate(conBun.getEntity().xCoord, conBun.getEntity().yCoord, conBun.getEntity().zCoord);

  }

  private void processConnectionModePacket(DataInputStream data, Player player) throws IOException {
    World world = getWorld(player);
    if(world == null) {
      return;
    }
    IConduitBundle conBun = getConduitBundle(data, world);
    if(conBun == null) {
      return;
    }
    ForgeDirection dir = ForgeDirection.values()[data.readShort()];
    ConnectionMode conMode = ConnectionMode.values()[data.readShort()];

    int typeOrdinal = data.readShort();
    if(typeOrdinal < 0) {
      Log.warn("processConnectionModePacket: Could not handle due to unknow conduit type.");
      return;
    }
    ConTypeEnum conType = ConTypeEnum.values()[typeOrdinal];

    IConduit con = conBun.getConduit(conType.getBaseType());
    if(con != null) {
      System.out.println("ConduitPacketHandler.processConnectionModePacket: " + con.getClass());
      if(con instanceof IInsulatedRedstoneConduit) { //yeah, I know
        System.out.println("ConduitPacketHandler.processConnectionModePacket: Done did a force");
        ((IInsulatedRedstoneConduit) con).forceConnectionMode(dir, conMode);
      } else {
        con.setConnectionMode(dir, conMode);
      }
    } else {
      Log.warn("processConnectionModePacket: Could not handle as conduit not found in bundle.");
      return;
    }

    world.markBlockForUpdate(conBun.getEntity().xCoord, conBun.getEntity().yCoord, conBun.getEntity().zCoord);
  }

  private IConduitBundle getConduitBundle(DataInputStream data, World world) throws IOException {
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      Log.warn("processConnectionModePacket: Could not handle packet as TileEntity was not a Conduit Bundle.");
      return null;
    }
    IConduitBundle conBun = (IConduitBundle) te;
    return conBun;
  }

  private World getWorld(Player player) {
    if(!(player instanceof EntityPlayer)) {
      Log.warn("processConnectionModePacket: Could not handle packet as player not an entity player.");
      return null;
    }
    World world = ((EntityPlayer) player).worldObj;
    if(world == null) {
      Log.warn("processConnectionModePacket: Could not handle packet as player world was null.");
      return null;
    }
    return world;
  }

}
