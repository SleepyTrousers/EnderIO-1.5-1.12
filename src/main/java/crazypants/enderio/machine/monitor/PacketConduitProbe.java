package crazypants.enderio.machine.monitor;

import static crazypants.enderio.power.PowerInterfaceBC.fromRF;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import crazypants.enderio.Log;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.NetworkPowerManager;
import crazypants.enderio.conduit.power.PowerConduitNetwork;
import crazypants.enderio.conduit.power.PowerTracker;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.network.IPacketEio;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerInterfaceBC;
import crazypants.enderio.power.PowerInterfaceRF;
import crazypants.util.Lang;

public class PacketConduitProbe implements IPacketEio {

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

  public static boolean canCreatePacket(World world, int x, int y, int z) {
    Block block = world.getBlock(x, y, z);
    if(block == null) {
      return false;
    }
    TileEntity te = world.getTileEntity(x, y, z);
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

  private int x;
  private int y;
  private int z;
  private ForgeDirection side;

  public PacketConduitProbe() {
  }

  public PacketConduitProbe(int x2, int y2, int z2, int side2) {
    this(x2, y2, z2, ForgeDirection.getOrientation(side2));
  }

  public PacketConduitProbe(int x, int y, int z, ForgeDirection side) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.side = side;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeShort(side.ordinal());

  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buffer) {
    x = buffer.readInt();
    y = buffer.readInt();
    z = buffer.readInt();
    side = ForgeDirection.getOrientation(buffer.readShort());
  }

  @Override
  public void handleClientSide(EntityPlayer player) {

  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    World world = player.worldObj;
    if(world == null) {
      Log.warn("MJReaderPacketHandler.sendInfoMessage: Could not handle packet as player world was null.");
      return;
    }
    Block block = world.getBlock(x, y, z);
    if(block == null) {
      return;
    }

    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileConduitBundle) {

      sendInfoMessage(player, (TileConduitBundle) te);

    } else

      if(te instanceof IInternalPowerReceptor) {

        IInternalPowerReceptor pr = (IInternalPowerReceptor) te;

        PowerInterfaceBC.fromRF(pr.getEnergyStored(null));

        pr.getEnergyStored(null);

        sendPowerReciptorInfo(player, block, fromRF(pr.getEnergyStored(null)), fromRF(pr.getMaxEnergyStored(null)), 0,
            0, PowerInterfaceRF.getPowerRequestMJ(ForgeDirection.NORTH, pr));

      } else if(te instanceof IPowerReceptor) {

        IPowerReceptor pr = (IPowerReceptor) te;
        PowerReceiver rec = pr.getPowerReceiver(side);
        if(rec == null) {
          player.addChatComponentMessage(new ChatComponentTranslation(block.getLocalizedName() + " " + Lang.localize("gui.mjReader.noPowerFromSide")));
        } else {
          sendPowerReciptorInfo(player, block, (float) rec.getEnergyStored(), (float) rec.getMaxEnergyStored(), (float) rec.getMinEnergyReceived(),
              (float) rec.getMaxEnergyReceived(),
              (float) rec.powerRequest());
        }

      }
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
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);

    if(conduit.getExternalConnections().isEmpty()) {
      sb.append(ITEM_HEADING);
      sb.append(" ");
      sb.append(ITEM_NO_CONNECTIONS);
      sb.append("\n");
      player.addChatComponentMessage(new ChatComponentText(sb.toString()));
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
    String[] lines = sb.toString().split("\n");
    for(String line : lines) {
      player.addChatComponentMessage(new ChatComponentText(line));
    }    
  }

  public static void sendInfoMessage(EntityPlayer player, NetworkPowerManager pm) {
    PowerTracker tracker = pm.getNetworkPowerTracker();
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append(NET_HEADING);
    player.addChatComponentMessage(new ChatComponentText(sb.toString()));

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
    
    String[] lines = sb.toString().split("\n");
    for(String line : lines) {
      player.addChatComponentMessage(new ChatComponentText(line));
    }      
  }

  public static void sendPowerConduitInfo(EntityPlayer player, IPowerConduit con, PowerTracker tracker) {
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append(ENERGY_CONDUIT);
    player.addChatComponentMessage(new ChatComponentText(sb.toString()));

    color = "\u00A79 ";
    sb = new StringBuilder();
    sb.append(color);
    sb.append(CON_BUF);
    sb.append(PowerDisplayUtil.formatPower(con.getEnergyStored()));
    sb.append(OF);
    sb.append(PowerDisplayUtil.formatPower(con.getMaxEnergyStored()));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    sb.append("\n");
    sb.append(AVE_OUT);
    sb.append(PowerDisplayUtil.formatPowerFloat(tracker.getAverageMjTickSent()));
    sb.append("\n");
    sb.append(AVE_IN);
    sb.append(PowerDisplayUtil.formatPowerFloat(tracker.getAverageMjTickRecieved()));
    
    String[] lines = sb.toString().split("\n");
    for(String line : lines) {
      player.addChatComponentMessage(new ChatComponentText(line));
    }      

  }

  private void sendPowerReciptorInfo(EntityPlayer player, Block block, float stored, float maxStored, float minRec, float maxRec, float request) {
    String color = "\u00A7a ";
    StringBuilder sb = new StringBuilder();
    sb.append(color);
    sb.append(block.getLocalizedName());
    player.addChatComponentMessage(new ChatComponentText(sb.toString()));

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

    String[] lines = sb.toString().split("\n");
    for(String line : lines) {
      player.addChatComponentMessage(new ChatComponentText(line));
    }      
  }

}
