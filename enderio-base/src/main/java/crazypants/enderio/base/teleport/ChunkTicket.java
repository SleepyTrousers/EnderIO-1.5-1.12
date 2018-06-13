package crazypants.enderio.base.teleport;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.DiagnosticsConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

/**
 * A short-term chunkloading manager meant to keep both source and target chunk of a long-range player teleport chunkloaded for 5 seconds. This could help
 * prevent the server from losing track of the player.
 * 
 */
@EventBusSubscriber(modid = EnderIO.MODID)
public class ChunkTicket {
  private final @Nonnull Ticket ticket;
  private long discardTime;

  private ChunkTicket(@Nonnull Ticket ticket) {
    this.ticket = ticket;
    this.discardTime = EnderIO.proxy.getServerTickCount() + 5 * 20;
  }

  private boolean isForPlayer(@Nonnull EntityPlayerMP player, @Nonnull World world) {
    if (ticket.getPlayerName().equals(player.getUniqueID().toString()) && ticket.world == world) {
      discardTime = EnderIO.proxy.getServerTickCount() + 5 * 20;
      return true;
    }
    return false;
  }

  private boolean shallDiscard() {
    if (discardTime < EnderIO.proxy.getServerTickCount()) {
      Log.debug("Discarding ticket for ", ticket.getPlayerName());
      ForgeChunkManager.releaseTicket(ticket);
      return true;
    }
    return false;
  }

  private static final List<ChunkTicket> TICKETS = new NNList<>();

  @SubscribeEvent
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    // must register something, but the code that read that back is null-safe---in which case tickets are silently discarded on world load. Which is perfect of
    // the teleport tickets
    ForgeChunkManager.setForcedChunkLoadingCallback(EnderIO.MODID, null);
  }

  @SubscribeEvent
  public static void onWorldUnload(WorldEvent.Unload event) {
    TICKETS.clear();
  }

  @SubscribeEvent
  public static void onServerTick(ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.START && !TICKETS.isEmpty()) {
      for (Iterator<ChunkTicket> i = TICKETS.iterator(); i.hasNext();) {
        if (i.next().shallDiscard()) {
          i.remove();
        }
      }
    }
  }

  public static void loadChunk(@Nonnull EntityPlayerMP player, @Nonnull World world, @Nonnull BlockPos pos) {
    if (DiagnosticsConfig.experimentalChunkLoadTeleport.get()) {
      String playerID = player.getUniqueID().toString();
      ChunkTicket discard = null;
      if (!TICKETS.isEmpty()) {
        for (Iterator<ChunkTicket> i = TICKETS.iterator(); i.hasNext() && discard == null;) {
          discard = i.next();
          if (!discard.isForPlayer(player, world)) {
            discard = null;
          }
        }
      }
      if (discard == null) {
        Ticket ticket = ForgeChunkManager.requestPlayerTicket(EnderIO.instance, playerID, world, Type.NORMAL);
        if (ticket != null) {
          ticket.setChunkListDepth(2);
          TICKETS.add(discard = new ChunkTicket(ticket));
        }
      }
      if (discard != null) {
        ForgeChunkManager.forceChunk(discard.ticket, new ChunkPos(pos));
        Log.debug("Forcing chunk ", new ChunkPos(pos), " for " + discard.ticket.getPlayerName());
      }
    }
  }
}