package crazypants.enderio.item;

import java.util.List;

import com.enderio.core.common.util.ChatUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduit.power.PowerConduitNetwork;
import crazypants.enderio.conduit.power.PowerTracker;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.PowerDisplayUtil;
import crazypants.enderio.power.rf.PowerInterfaceRF;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConduitProbe implements IMessage, IMessageHandler<PacketConduitProbe, IMessage> {

  private static final String OF = " " + EnderIO.lang.localize("gui.powerMonitor.of") + " ";
  private static final String CON_STORAGE = " " + EnderIO.lang.localize("gui.powerMonitor.monHeading1") + ": ";
  private static final String CAP_BANK_STOR = " " + EnderIO.lang.localize("gui.powerMonitor.monHeading2") + ": ";
  private static final String MACH_BUF_STOR = " " + EnderIO.lang.localize("gui.powerMonitor.monHeading3") + ": ";
  private static final String AVE_OUT = " " + EnderIO.lang.localize("gui.powerMonitor.monHeading4") + ": ";
  private static final String AVE_IN = " " + EnderIO.lang.localize("gui.powerMonitor.monHeading5") + ": ";

  private static final String NET_HEADING = EnderIO.lang.localize("gui.mjReader.networkHeading");
  private static final String CON_BUF = " " + EnderIO.lang.localize("gui.mjReader.conduitBuffer") + ": ";

  private static final String ITEM_HEADING = EnderIO.lang.localize("gui.mjReader.itemHeading");
  private static final String ITEM_NO_CONNECTIONS = EnderIO.lang.localize("gui.mjReader.itemNoConnections");

  private static final String ENERGY_CONDUIT = EnderIO.lang.localize("itemPowerConduit");
  private static final String REQUEST_RANGE = " " + EnderIO.lang.localize("gui.mjReader.requestRange") + ": ";;
  private static final String CUR_REQUEST = " " + EnderIO.lang.localize("gui.mjReader.currentRequest") + ": ";;

  public static boolean canCreatePacket(World world, int x, int y, int z) {
    
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileConduitBundle) {
      TileConduitBundle tcb = (TileConduitBundle) te;
      return tcb.getConduit(IPowerConduit.class) != null || tcb.getConduit(IItemConduit.class) != null;
    }
    if(te instanceof IInternalPoweredTile) {
      return true;
    }
    if (te instanceof IHasConduitProbeData) {
      return true;
    }
    return false;
  }

  private long pos;
  private EnumFacing side;

  public PacketConduitProbe() {
  }

  public PacketConduitProbe(BlockPos pos, EnumFacing side) {
    this.pos = pos.toLong();
    this.side = side;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeLong(pos);
    if(side == null) {
      buf.writeShort(-1);
    } else {
      buf.writeShort(side.ordinal());
    }

  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    pos = buffer.readLong();
    short ord = buffer.readShort();
    if(ord < 0) {
      side = null;
    } else {
      side = EnumFacing.VALUES[ord];
    }
  }

  @Override
  public IMessage onMessage(PacketConduitProbe message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    World world = player.worldObj;
    BlockPos pos = BlockPos.fromLong(message.pos);
    if (world == null || !world.isBlockLoaded(pos)) {
      return null;
    }
    Block block = world.getBlockState(pos).getBlock();

    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileConduitBundle) {
      sendInfoMessage(player, (TileConduitBundle) te);
    } else if(te instanceof IInternalPowerReceiver) {
      IInternalPowerReceiver pr = (IInternalPowerReceiver) te;
      sendPowerReciptorInfo(player, block, pr.getEnergyStored(null), pr.getMaxEnergyStored(null), 0,
            0, PowerInterfaceRF.getPowerRequest(EnumFacing.NORTH, pr));

    } else if (te instanceof IHasConduitProbeData) {
      ChatUtil.sendNoSpam(player, ((IHasConduitProbeData) te).getConduitProbeData());
    }
    return null;
  }

  public static void sendInfoMessage(EntityPlayer player, TileConduitBundle tcb) {

    if(tcb.getConduit(IItemConduit.class) != null) {
      sendInfoMessage(player, tcb.getConduit(IItemConduit.class), null);
    }
    IPowerConduit conduit = tcb.getConduit(IPowerConduit.class);
    if(conduit != null) {
      sendInfoMessage(player, conduit);
    }
  }

  public static void sendInfoMessage(EntityPlayer player, IPowerConduit conduit) {
    PowerConduitNetwork pcn = (PowerConduitNetwork) conduit.getNetwork();
    NetworkPowerManager pm = pcn.getPowerManager();
    PowerTracker tracker = pm.getTracker(conduit);
    if(tracker != null) {
      sendPowerConduitInfo(player, conduit, tracker);
    } else {
      sendInfoMessage(player, pm);
    }
  }

  public static void sendInfoMessage(EntityPlayer player, IItemConduit conduit, ItemStack input) {
    TextFormatting color;
    StringBuilder sb = new StringBuilder();

    if (conduit.getExternalConnections().isEmpty()) {
      sb.append(ITEM_HEADING);
      sb.append(" ");
      sb.append(ITEM_NO_CONNECTIONS);
      sb.append("\n");
    } else {
      for (EnumFacing dir : conduit.getExternalConnections()) {
        ConnectionMode mode = conduit.getConnectionMode(dir);
        color = TextFormatting.GREEN;
        
        sb.append(color);
        sb.append(ITEM_HEADING);
        sb.append(" ");
        sb.append(EnderIO.lang.localize("gui.mjReader.connectionDir"));
        sb.append(" ");
        sb.append(dir);
        sb.append("\n");

        ItemConduitNetwork icn = (ItemConduitNetwork) conduit.getNetwork();
        if (icn != null && mode.acceptsInput()) {
          color = TextFormatting.BLUE;
          sb.append(color + " ");

          if (input == null) {
            sb.append(EnderIO.lang.localize("gui.mjReader.extractedItems"));
          } else {
            sb.append(EnderIO.lang.localize("gui.mjReader.extractedItem"));
            sb.append(" ");
            sb.append(input.getDisplayName());
          }
          sb.append(" ");
          List<String> targets = icn.getTargetsForExtraction(conduit.getLocation().getLocation(dir), conduit, input);
          if (targets.isEmpty()) {
            sb.append(" ");
            sb.append(EnderIO.lang.localize("gui.mjReader.noOutputs"));
            sb.append(".\n");
          } else {
            sb.append(" ");
            sb.append(EnderIO.lang.localize("gui.mjReader.insertedInto"));
            sb.append("\n");
            for (String str : targets) {
              sb.append("  - ");
              sb.append(str);
              sb.append(" ");
              sb.append("\n");
            }
          }
        }
        if (icn != null && mode.acceptsOutput()) {
          color = TextFormatting.BLUE;
          sb.append(color + " ");

          List<String> targets = icn.getInputSourcesFor(conduit, dir, input);
          if (targets.isEmpty()) {
            if (input == null) {
              sb.append(EnderIO.lang.localize("gui.mjReader.noItems"));
            } else {
              sb.append(EnderIO.lang.localize("gui.mjReader.noItem"));
              sb.append(" ");
              sb.append(input.getDisplayName());
            }
          } else {
            if (input == null) {
              sb.append(EnderIO.lang.localize("gui.mjReader.receiveItems"));
            } else {
              sb.append(EnderIO.lang.localize("gui.mjReader.receiveItem1"));
              sb.append(" ");
              sb.append(input.getDisplayName());
              sb.append(" ");
              sb.append(EnderIO.lang.localize("gui.mjReader.receiveItem2"));
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
    }
    String[] lines = sb.toString().split("\n");
    ChatUtil.sendNoSpam(player, lines);
  }

  public static void sendInfoMessage(EntityPlayer player, NetworkPowerManager pm) {
    PowerTracker tracker = pm.getNetworkPowerTracker();
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append(NET_HEADING);
    sb.append("\n");
    
    color = "\u00A79 ";
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
    sb.append(PowerDisplayUtil.formatPowerFloat(tracker.getAverageRfTickSent()));
    sb.append("\n");
    sb.append(AVE_IN);
    sb.append(PowerDisplayUtil.formatPowerFloat(tracker.getAverageRfTickRecieved()));
    
    String[] lines = sb.toString().split("\n");
    ChatUtil.sendNoSpam(player, lines);
  }

  public static void sendPowerConduitInfo(EntityPlayer player, IPowerConduit con, PowerTracker tracker) {
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append(ENERGY_CONDUIT);

    color = "\u00A79 ";
    sb.append(color);
    sb.append(CON_BUF);
    sb.append(PowerDisplayUtil.formatPower(con.getEnergyStored(null)));
    sb.append(OF);
    sb.append(PowerDisplayUtil.formatPower(con.getMaxEnergyStored(null)));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    sb.append("\n");
    sb.append(AVE_OUT);
    sb.append(PowerDisplayUtil.formatPowerFloat(tracker.getAverageRfTickSent()));
    sb.append("\n");
    sb.append(AVE_IN);
    sb.append(PowerDisplayUtil.formatPowerFloat(tracker.getAverageRfTickRecieved()));
    
    String[] lines = sb.toString().split("\n");
    ChatUtil.sendNoSpam(player, lines);
  }

  private void sendPowerReciptorInfo(EntityPlayer player, Block block, int stored, int maxStored, int minRec, int maxRec, int request) {
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append(block.getLocalizedName());

    color = "\u00A79 ";
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

    String[] lines = sb.toString().split("\n");
    ChatUtil.sendNoSpam(player, lines);
  }

  public static interface IHasConduitProbeData {

    String[] getConduitProbeData();

  }
}
