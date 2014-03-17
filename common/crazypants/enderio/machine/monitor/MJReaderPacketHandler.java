package crazypants.enderio.machine.monitor;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import crazypants.enderio.GuiHandler;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.Log;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduit.power.PowerConduitNetwork;
import crazypants.enderio.conduit.power.PowerTracker;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.util.Lang;

public class MJReaderPacketHandler implements IPacketProcessor {

  static final MJReaderPacketHandler instance = new MJReaderPacketHandler();

  public static MJReaderPacketHandler getInstance() {
    return instance;
  }

  private static final String OF = " " + Lang.localize("gui.powerMonitor.of") + " ";
  private static final String CON_STORAGE = " " + Lang.localize("gui.powerMonitor.monHeading1") + ": ";
  private static final String CAP_BANK_STOR = " " + Lang.localize("gui.powerMonitor.monHeading2") + ": ";
  private static final String MACH_BUF_STOR = " " + Lang.localize("gui.powerMonitor.monHeading3") + ": ";
  private static final String AVE_OUT = " " + Lang.localize("gui.powerMonitor.monHeading4") + ": ";
  private static final String AVE_IN = " " + Lang.localize("gui.powerMonitor.monHeading5") + ": ";

  private static final String NET_HEADING = Lang.localize("gui.mjReader.networkHeading");
  private static final String CON_BUF = " " + Lang.localize("gui.mjReader.conduitBuffer") + ": ";

  private static final String ITEM_HEADING = Lang.localize("gui.mjReader.itemHeading");
  private static final String ITEM_NO_CONNECTIONS = Lang.localize("gui.mjReader.itemNoConnections");

  private static final String ENERGY_CONDUIT = Lang.localize("itemPowerConduit");
  private static final String REQUEST_RANGE = " " + Lang.localize("gui.mjReader.requestRange") + ": ";;
  private static final String CUR_REQUEST = " " + Lang.localize("gui.mjReader.currentRequest") + ": ";;

  public static Packet createOpenConduitGuiPacket(IConduitBundle bundle, ForgeDirection dir) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_MJ_READER_OPEN_CONDUIT_UI);
      TileEntity te = bundle.getEntity();
      dos.writeInt(te.xCoord);
      dos.writeInt(te.yCoord);
      dos.writeInt(te.zCoord);
      dos.writeInt(dir.ordinal());
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
      return tcb.getConduit(IPowerConduit.class) != null || tcb.getConduit(IItemConduit.class) != null;
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
    return PacketHandler.ID_MJ_READER_INFO_REQUEST == packetID || PacketHandler.ID_MJ_READER_OPEN_CONDUIT_UI == packetID;
  }

  @Override
  public void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player player) throws IOException {
    if(packetID == PacketHandler.ID_MJ_READER_INFO_REQUEST) {
      sendInfoMessage(data, player);
    } else if(packetID == PacketHandler.ID_MJ_READER_OPEN_CONDUIT_UI) {
      handleUiPacket(data, player);
    } else {
      Log.error("MJReaderPacketHandler.processPacket: Recieved unkown packet with ID " + packetID);
    }
  }

  private void handleUiPacket(DataInputStream data, Player p) throws IOException {
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
    player.openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + side, player.worldObj, x, y, z);
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

      sendInfoMessage(player, (TileConduitBundle) te);

    } else if(te instanceof IInternalPowerReceptor) {

      IInternalPowerReceptor pr = (IInternalPowerReceptor) te;
      PowerHandler ph = pr.getPowerHandler();
      if(ph == null) {
        player.sendChatToPlayer(ChatMessageComponent.createFromText(block.getLocalizedName() + " " + Lang.localize("gui.mjReader.noPowerFromSide")));
      }

      sendPowerReciptorInfo(player, block, ph.getEnergyStored(), ph.getMaxEnergyStored(), ph.getMinEnergyReceived(), ph.getMaxEnergyReceived(), ph
          .getPowerReceiver().powerRequest());

    } else if(te instanceof IPowerReceptor) {

      IPowerReceptor pr = (IPowerReceptor) te;
      PowerReceiver rec = pr.getPowerReceiver(ForgeDirection.values()[side]);
      if(rec == null) {
        player.sendChatToPlayer(ChatMessageComponent.createFromText(block.getLocalizedName() + " " + Lang.localize("gui.mjReader.noPowerFromSide")));
      } else {
        sendPowerReciptorInfo(player, block, rec.getEnergyStored(), rec.getMaxEnergyStored(), rec.getMinEnergyReceived(), rec.getMaxEnergyReceived(),
            rec.powerRequest());
      }

    }

  }

  public void sendInfoMessage(EntityPlayer player, TileConduitBundle tcb) {

    if(tcb.getConduit(IItemConduit.class) != null) {
      sendInfoMessage(player, tcb.getConduit(IItemConduit.class), null);
    }
    IPowerConduit conduit = tcb.getConduit(IPowerConduit.class);
    if(conduit != null) {
      sendInfoMessage(player, conduit);
    }
  }

  public void sendInfoMessage(EntityPlayer player, IPowerConduit conduit) {
    PowerConduitNetwork pcn = (PowerConduitNetwork) conduit.getNetwork();
    NetworkPowerManager pm = pcn.getPowerManager();
    PowerTracker tracker = pm.getTracker(conduit);
    if(tracker != null) {
      sendPowerConduitInfo(player, conduit, tracker);
    } else {
      sendInfoMessage(player, pm);
    }
  }

  public void sendInfoMessage(EntityPlayer player, IItemConduit conduit, ItemStack input) {
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);

    if(conduit.getExternalConnections().isEmpty()) {
      sb.append(ITEM_HEADING);
      sb.append(" ");
      sb.append(ITEM_NO_CONNECTIONS);
      sb.append("\n");
      player.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));
      return;
    }
    for (ForgeDirection dir : conduit.getExternalConnections()) {
      ConnectionMode mode = conduit.getConectionMode(dir);

      sb.append(ITEM_HEADING);
      sb.append(" ");
      sb.append(Lang.localize("gui.mjReader.connectionDir"));
      sb.append(" ");
      sb.append(dir);
      sb.append("\n");

      ItemConduitNetwork icn = (ItemConduitNetwork) conduit.getNetwork();
      if(mode.acceptsInput()) {
        color = "\u00A79 ";
        sb.append(color);

        if(input == null) {
          sb.append(Lang.localize("gui.mjReader.extractedItems"));
        } else {
          sb.append(Lang.localize("gui.mjReader.extractedItem"));
          sb.append(" ");
          sb.append(input.getDisplayName());
        }
        sb.append(" ");
        List<String> targets = icn.getTargetsForExtraction(conduit.getLocation().getLocation(dir), conduit, input);
        if(targets.isEmpty()) {
          sb.append(" ");
          sb.append(Lang.localize("gui.mjReader.noOutputs"));
          sb.append(".\n");
        } else {
          sb.append(" ");
          sb.append(Lang.localize("gui.mjReader.insertedInto"));
          sb.append("\n");
          for (String str : targets) {
            sb.append("  - ");
            sb.append(str);
            sb.append(" ");
            sb.append("\n");
          }
        }
      }
      if(mode.acceptsOutput()) {
        color = "\u00A79 ";
        sb.append(color);

        List<String> targets = icn.getInputSourcesFor(conduit, dir, input);
        if(targets.isEmpty()) {
          if(input == null) {
            sb.append(Lang.localize("gui.mjReader.noItems"));
          } else {
            sb.append(Lang.localize("gui.mjReader.noItem"));
            sb.append(" ");
            sb.append(input.getDisplayName());
          }
        } else {
          if(input == null) {
            sb.append(Lang.localize("gui.mjReader.receiveItems"));
          } else {
            sb.append(Lang.localize("gui.mjReader.receiveItem1"));
            sb.append(" ");
            sb.append(input.getDisplayName());
            sb.append(" ");
            sb.append(Lang.localize("gui.mjReader.receiveItem2"));
          }
          sb.append("\n");
          for (String str : targets) {
            sb.append("  - ");
            sb.append(str);
            sb.append("\n");
          }
        }

      }
    }
    player.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));

  }

  public void sendInfoMessage(EntityPlayer player, NetworkPowerManager pm) {
    PowerTracker tracker = pm.getNetworkPowerTracker();
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append(NET_HEADING);
    player.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));

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
    player.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));
  }

  private void sendPowerConduitInfo(EntityPlayer player, IPowerConduit con, PowerTracker tracker) {
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append(ENERGY_CONDUIT);
    player.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));

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

    player.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));
  }

}
