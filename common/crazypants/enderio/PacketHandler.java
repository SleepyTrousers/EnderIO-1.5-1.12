package crazypants.enderio;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import crazypants.util.PacketUtil;

public class PacketHandler implements IPacketHandler {

  public static int ID_ENDERFACE = 1;

  public static final int ID_TILE_ENTITY = 2;
  public static final int ID_MACHINE_REDSTONE_PACKET = 3;

  public static final int ID_CAP_BANK_REDSTONE_PACKET = 4;
  public static final int ID_CAP_BANK_IO_PACKET = 5;

  public static final int ID_ALLOY_SMELTING_MODE_PACKET = 6;

  public static final int ID_TRANSCEIVER_IO_MODE = 7;
  public static final int ID_TRANSCEIVER_PUBLIC_CHANNEL_LIST = 8;
  public static final int ID_TRANSCEIVER_ADD_REMOVE_CHANNEL = 9;
  public static final int ID_TRANSCEIVER_PRIVATE_CHANNEL_LIST = 10;
  public static final int ID_TRANSCEIVER_CHANNEL_SELECTED = 11;
  public static final int ID_TRANSCEIVER_REDSTONE_MODE = 12;

  public static final int ID_MJ_READER_INFO_REQUEST = 13;
  public static final int ID_POWER_MONITOR_PACKET = 14;

  public static final int ID_YETA_WRENCH_MODE_PACKET = 15;

  public static final int ID_CONDUIT_CON_MODE = 16;
  public static final int ID_CONDUIT_SIGNAL_COL = 17;
  public static final int ID_CONDUIT_EXTRACT_MODE = 18;
  public static final int ID_CONDUIT_ITEM_FILTER = 19;
  public static final int ID_CONDUIT_ITEM_LOOP = 20;
  public static final int ID_CONDUIT_ITEM_CHANNEL = 21;

  public static final int ID_CONDUIT_FLUID_LEVEL = 22;

  public static final int ID_TRAVEL_PLATFORM = 23;
  public static final int ID_TRAVEL_STAFF_DRAIN = 24;
  public static final int ID_TRAVEL_SETTINGS = 25;
  public static final int ID_TRAVEL_AUTH_GUI = 26;
  public static final int ID_TRAVEL_AUTH_ACCESS_MODE = 27;

  public static final int ID_ME_TERMINAL = 28;

  public static final int ID_MJ_READER_OPEN_CONDUIT_UI = 29;

  public static final String CHANNEL = "EnderIO";

  public static PacketHandler instance;

  private List<IPacketProcessor> processors = new CopyOnWriteArrayList<IPacketProcessor>();

  public void addPacketProcessor(IPacketProcessor processor) {
    processors.add(processor);
  }

  public void removePacketProcessor(IPacketProcessor processor) {
    processors.remove(processor);
  }

  public PacketHandler() {
    instance = this;
  }

  @Override
  public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
    if(packet.data != null && packet.data.length <= 0) {
      return;
    }

    DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
    try {
      int id = data.readInt();
      if(id == ID_TILE_ENTITY && player instanceof EntityPlayer) {
        PacketUtil.handleTileEntityPacket(((EntityPlayer) player).worldObj, false, data);
      } else {
        for (IPacketProcessor proc : processors) {
          if(proc.canProcessPacket(id)) {
            proc.processPacket(id, manager, data, player);
            return;
          }
        }
        Log.warn("PacketHandler.onPacketData: Recieved packet of unknown type: " + id);
      }
    } catch (IOException ex) {
      FMLCommonHandler.instance().raiseException(ex, "PacketHandler.onPacketData", false);
    } finally {
      try {
        data.close();
      } catch (IOException e) {
        Log.debug("Error closing data input stream: " + e.getMessage());
      }
    }
  }

  public static Packet getPacket(TileEntity te) {
    return PacketUtil.createTileEntityPacket(CHANNEL, ID_TILE_ENTITY, te);
  }

}
