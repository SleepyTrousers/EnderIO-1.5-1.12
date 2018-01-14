package crazypants.enderio.powertools.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.ThreadedNetworkWrapper;

import crazypants.enderio.powertools.EnderIOPowerTools;
import crazypants.enderio.powertools.machine.monitor.PacketPowerMonitorGraph;
import crazypants.enderio.powertools.machine.monitor.PacketPowerMonitorStatData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

  public static final @Nonnull ThreadedNetworkWrapper INSTANCE = new ThreadedNetworkWrapper(EnderIOPowerTools.MODID); // sic! not DOMAIN!

  private static int ID = 0;

  public static int nextID() {
    return ID++;
  }

  public static void sendToAllAround(IMessage message, TileEntity te) {
    INSTANCE.sendToAllAround(message, te);
  }

  public static void sendTo(@Nonnull IMessage message, EntityPlayerMP player) {
    INSTANCE.sendTo(message, player);
  }

  public static void sendToServer(@Nonnull IMessage message) {
    INSTANCE.sendToServer(message);
  }

  public static void init(FMLInitializationEvent event) {
    INSTANCE.registerMessage(PacketPowerMonitorGraph.ClientHandler.class, PacketPowerMonitorGraph.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketPowerMonitorGraph.ServerHandler.class, PacketPowerMonitorGraph.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketPowerMonitorStatData.ClientHandler.class, PacketPowerMonitorStatData.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketPowerMonitorStatData.ServerHandler.class, PacketPowerMonitorStatData.class, nextID(), Side.SERVER);
  }

}
