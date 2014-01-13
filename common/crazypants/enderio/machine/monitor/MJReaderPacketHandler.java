package crazypants.enderio.machine.monitor;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
import crazypants.enderio.EnderIO;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.Log;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduit.power.PowerConduitNetwork;
import crazypants.enderio.conduit.power.PowerTracker;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.power.IInternalPowerReceptor;

public class MJReaderPacketHandler implements IPacketProcessor {

  private static final String OF = " " + EnderIO.localize("gui.powerMonitor.of") + " ";
  private static final String CON_STORAGE = " " + EnderIO.localize("gui.powerMonitor.monHeading1") + ": ";
  private static final String CAP_BANK_STOR = " " + EnderIO.localize("gui.powerMonitor.monHeading2") + ": ";
  private static final String MACH_BUF_STOR = " " + EnderIO.localize("gui.powerMonitor.monHeading3") + ": ";
  private static final String AVE_OUT = " " + EnderIO.localize("gui.powerMonitor.monHeading4") + ": ";
  private static final String AVE_IN = " " + EnderIO.localize("gui.powerMonitor.monHeading5") + ": ";

  private static final String NET_HEADING = EnderIO.localize("gui.mjReader.networkHeading");
  private static final String CON_BUF = " " + EnderIO.localize("gui.mjReader.conduitBuffer") + ": ";

  private static final String ENERGY_CONDUIT = EnderIO.localize("itemPowerConduit");
  private static final String REQUEST_RANGE = " " + EnderIO.localize("gui.mjReader.requestRange") + ": ";;
  private static final String CUR_REQUEST = " " + EnderIO.localize("gui.mjReader.currentRequest") + ": ";;

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
        player.sendChatToPlayer(ChatMessageComponent.func_111066_d(block.getLocalizedName() + " cannot recieve power from this side."));
      }

      sendPowerReciptorInfo(player, block, ph.getEnergyStored(), ph.getMaxEnergyStored(), ph.getMinEnergyReceived(), ph.getMaxEnergyReceived(), ph
          .getPowerReceiver().powerRequest());

    } else if(te instanceof IPowerReceptor) {

      IPowerReceptor pr = (IPowerReceptor) te;
      PowerReceiver rec = pr.getPowerReceiver(ForgeDirection.values()[side]);
      if(rec == null) {
        player.sendChatToPlayer(ChatMessageComponent.func_111066_d(block.getLocalizedName() + " cannot recieve power from this side."));
      } else {
        sendPowerReciptorInfo(player, block, rec.getEnergyStored(), rec.getMaxEnergyStored(), rec.getMinEnergyReceived(), rec.getMaxEnergyReceived(),
            rec.powerRequest());
      }

    }

  }

  private void sendNetworkInfo(EntityPlayer player, NetworkPowerManager pm) {
    PowerTracker tracker = pm.getNetworkPowerTracker();
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append(NET_HEADING);
    player.sendChatToPlayer(ChatMessageComponent.func_111066_d(sb.toString()));

    color = "\u00A79 ";
    sb = new StringBuilder();
    sb.append(color);
    sb.append(CON_STORAGE);
    sb.append(PowerDisplayUtil.formatPower(pm.getPowerInConduits()));
    sb.append(OF);
    sb.append(PowerDisplayUtil.formatPower(pm.getMaxPowerInConduits()));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    sb.append("\n");
    sb.append(CAP_BANK_STOR);
    sb.append(PowerDisplayUtil.formatPower(pm.getPowerInCapacitorBanks()));
    sb.append(OF);
    sb.append(PowerDisplayUtil.formatPower(pm.getMaxPowerInCapacitorBanks()));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    sb.append("\n");
    sb.append(MACH_BUF_STOR);
    sb.append(PowerDisplayUtil.formatPower(pm.getPowerInReceptors()));
    sb.append(OF);
    sb.append(PowerDisplayUtil.formatPower(pm.getMaxPowerInReceptors()));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    sb.append("\n");
    sb.append(AVE_OUT);
    sb.append(PowerDisplayUtil.formatPowerFloat(tracker.getAverageMjTickSent()));
    sb.append("\n");
    sb.append(AVE_IN);
    sb.append(PowerDisplayUtil.formatPowerFloat(tracker.getAverageMjTickRecieved()));
    player.sendChatToPlayer(ChatMessageComponent.func_111066_d(sb.toString()));
  }

  private void sendPowerConduitInfo(EntityPlayer player, IPowerConduit con, PowerTracker tracker) {
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append(ENERGY_CONDUIT);
    player.sendChatToPlayer(ChatMessageComponent.func_111066_d(sb.toString()));

    color = "\u00A79 ";
    sb = new StringBuilder();
    sb.append(color);
    sb.append(CON_BUF);
    sb.append(PowerDisplayUtil.formatPower(con.getPowerHandler().getEnergyStored()));
    sb.append(OF);
    sb.append(PowerDisplayUtil.formatPower(con.getPowerHandler().getMaxEnergyStored()));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    sb.append("\n");
    sb.append(AVE_OUT);
    sb.append(PowerDisplayUtil.formatPowerFloat(tracker.getAverageMjTickSent()));
    sb.append("\n");
    sb.append(AVE_IN);
    sb.append(PowerDisplayUtil.formatPowerFloat(tracker.getAverageMjTickRecieved()));
    player.sendChatToPlayer(ChatMessageComponent.func_111066_d(sb.toString()));

  }

  private void sendPowerReciptorInfo(EntityPlayer player, Block block, float stored, float maxStored, float minRec, float maxRec, float request) {
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append(block.getLocalizedName());
    player.sendChatToPlayer(ChatMessageComponent.func_111066_d(sb.toString()));

    color = "\u00A79 ";
    sb = new StringBuilder();
    sb.append(color);
    sb.append(CON_BUF);
    sb.append(PowerDisplayUtil.formatPower(stored));
    sb.append(OF);
    sb.append(PowerDisplayUtil.formatPower(maxStored));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    sb.append("\n");

    sb.append(REQUEST_RANGE);
    sb.append(PowerDisplayUtil.formatPower(minRec));
    sb.append(" - ");
    sb.append(PowerDisplayUtil.formatPower(maxRec));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    sb.append("\n");

    sb.append(CUR_REQUEST);
    sb.append(PowerDisplayUtil.formatPower(request));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());

    player.sendChatToPlayer(ChatMessageComponent.func_111066_d(sb.toString()));
  }

}
