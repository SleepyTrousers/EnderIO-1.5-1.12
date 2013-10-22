package crazypants.enderio.machine.monitor;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.NumberFormat;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import cpw.mods.fml.common.network.Player;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.Log;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduit.power.PowerConduitNetwork;
import crazypants.enderio.conduit.power.PowerTracker;
import crazypants.enderio.power.IInternalPowerReceptor;

public class MJReaderPacketHandler implements IPacketProcessor {

  private static final NumberFormat INT_NF = NumberFormat.getIntegerInstance();

  private static final NumberFormat FLOAT_NF = NumberFormat.getInstance();
  static {
    FLOAT_NF.setMinimumFractionDigits(1);
    FLOAT_NF.setMaximumFractionDigits(1);
  }

  public static boolean canCreatePacket(World world, int x, int y, int z) {
    int id = world.getBlockId(x, y, z);
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(id <= 0) {
      return false;
    }
    Block block = Block.blocksList[id];
    if(block == null) {
      return false;
    }
    if(te instanceof TileConduitBundle) {
      TileConduitBundle tcb = (TileConduitBundle) te;
      IPowerConduit conduit = tcb.getConduit(IPowerConduit.class);
      if(conduit == null) {
        return false;
      }
      return true;
    }
    if(te instanceof IInternalPowerReceptor) {
      return true;
    }
    if(te instanceof IPowerReceptor) {
      return true;
    }
    return false;
  }

  public static Packet createInfoRequestPacket(int x, int y, int z, int side) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_MJ_READER_INFO_REQUEST);
      dos.writeInt(x);
      dos.writeInt(y);
      dos.writeInt(z);
      dos.writeInt(side);
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
    return PacketHandler.ID_MJ_READER_INFO_REQUEST == packetID;
  }

  @Override
  public void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player player) throws IOException {
    if(packetID == PacketHandler.ID_MJ_READER_INFO_REQUEST) {
      sendInfoMessage(data, player);
    } else {
      Log.error("MJReaderPacketHandler.processPacket: Recieved unkown packet with ID " + packetID);
    }
  }

  private void sendInfoMessage(DataInputStream data, Player p) throws IOException {
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    int side = data.readInt();

    EntityPlayer player = (EntityPlayer) p;
    World world = player.worldObj;
    if(world == null) {
      Log.warn("MJReaderPacketHandler.sendInfoMessage: Could not handle packet as player world was null.");
      return;
    }

    int id = world.getBlockId(x, y, z);
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(id <= 0) {
      Log.warn("MJReaderPacketHandler.sendInfoMessage: Invalid block id " + id);
      return;
    }
    Block block = Block.blocksList[id];
    if(block == null) {
      Log.warn("MJReaderPacketHandler.sendInfoMessage: Null block for is " + id);
      return;
    }

    if(te instanceof TileConduitBundle) {

      TileConduitBundle tcb = (TileConduitBundle) te;
      IPowerConduit conduit = tcb.getConduit(IPowerConduit.class);
      if(conduit == null) {
        return;
      }
      PowerConduitNetwork pcn = (PowerConduitNetwork) conduit.getNetwork();
      NetworkPowerManager pm = pcn.getPowerManager();
      PowerTracker tracker = pm.getTracker(conduit);
      if(tracker != null) {
        sendPowerConduitInfo(player, conduit, tracker);
      } else {
        sendNetworkInfo(player, pm);
      }

    } else if(te instanceof IInternalPowerReceptor) {

      IInternalPowerReceptor pr = (IInternalPowerReceptor) te;
      PowerHandler ph = pr.getPowerHandler();
      if(ph == null) {
        player.sendChatToPlayer(ChatMessageComponent.createFromText(block.getLocalizedName() + " cannot recieve power from this side."));
      }

      sendPowerReciptorInfo(player, block, ph.getEnergyStored(), ph.getMaxEnergyStored(), ph.getMinEnergyReceived(), ph.getMaxEnergyReceived(), ph
          .getPowerReceiver().powerRequest());

    } else if(te instanceof IPowerReceptor) {

      IPowerReceptor pr = (IPowerReceptor) te;
      PowerReceiver rec = pr.getPowerReceiver(ForgeDirection.values()[side]);
      if(rec == null) {
        player.sendChatToPlayer(ChatMessageComponent.createFromText(block.getLocalizedName() + " cannot recieve power from this side."));
      }

      sendPowerReciptorInfo(player, block, rec.getEnergyStored(), rec.getMaxEnergyStored(), rec.getMinEnergyReceived(), rec.getMaxEnergyReceived(),
          rec.powerRequest());

    }

  }

  private void sendNetworkInfo(EntityPlayer player, NetworkPowerManager pm) {
    PowerTracker tracker = pm.getNetworkPowerTracker();
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append("Power Network");
    player.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));

    color = "\u00A79 ";
    sb = new StringBuilder();
    sb.append(color);
    sb.append(" Conduit Storage: ");
    sb.append(INT_NF.format(pm.getPowerInConduits()));
    sb.append(" of ");
    sb.append(INT_NF.format(pm.getMaxPowerInConduits()));
    sb.append(" MJ");
    sb.append("\n");
    sb.append(" Capacitor Bank Storage: ");
    sb.append(INT_NF.format(pm.getPowerInCapacitorBanks()));
    sb.append(" of ");
    sb.append(INT_NF.format(pm.getMaxPowerInCapacitorBanks()));
    sb.append(" MJ");
    sb.append("\n");
    sb.append(" Machine Buffers: ");
    sb.append(INT_NF.format(pm.getPowerInReceptors()));
    sb.append(" of ");
    sb.append(INT_NF.format(pm.getMaxPowerInReceptors()));
    sb.append(" MJ");
    sb.append("\n");
    sb.append(" Average output over 5 seconds: ");
    sb.append(FLOAT_NF.format(tracker.getAverageMjTickSent()));
    sb.append("\n");
    sb.append(" Average input over 5 seconds: ");
    sb.append(FLOAT_NF.format(tracker.getAverageMjTickRecieved()));
    player.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));
  }

  private void sendPowerConduitInfo(EntityPlayer player, IPowerConduit con, PowerTracker tracker) {
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append("Power Conduit");
    player.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));

    color = "\u00A79 ";
    sb = new StringBuilder();
    sb.append(color);
    sb.append(" Internal Buffer: ");
    sb.append(INT_NF.format(con.getPowerHandler().getEnergyStored()));
    sb.append(" of ");
    sb.append(INT_NF.format(con.getPowerHandler().getMaxEnergyStored()));
    sb.append(" MJ");
    sb.append("\n");
    sb.append(" Average output over 5 seconds: ");
    sb.append(FLOAT_NF.format(tracker.getAverageMjTickSent()));
    sb.append("\n");
    sb.append(" Average input over 5 seconds: ");
    sb.append(FLOAT_NF.format(tracker.getAverageMjTickRecieved()));
    player.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));

  }

  private void sendPowerReciptorInfo(EntityPlayer player, Block block, float stored, float maxStored, float minRec, float maxRec, float request) {
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append(block.getLocalizedName());
    player.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));

    color = "\u00A79 ";
    sb = new StringBuilder();
    sb.append(color);
    sb.append(" Internal Buffer: ");
    sb.append(INT_NF.format(stored));
    sb.append(" of ");
    sb.append(INT_NF.format(maxStored));
    sb.append(" MJ");
    sb.append("\n");

    sb.append(" Per tick request range: ");
    sb.append(INT_NF.format(minRec));
    sb.append(" - ");
    sb.append(INT_NF.format(maxRec));
    sb.append("\n");

    sb.append(" Current request: ");
    sb.append(INT_NF.format(request));
    sb.append(" MJ.");

    player.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));
  }

}
