package crazypants.enderio.machines.machine.obelisk.inhibitor;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.machines.EnderIOMachines;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
public class InhibitorHandler {

  private final static @Nonnull Map<World, Map<BlockPos, WeakReference<TileInhibitorObelisk>>> HANDLERS = new WeakHashMap<>();

  public static void register(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull TileInhibitorObelisk teleportInhibitor) {
    if (!world.isRemote) {
      HANDLERS.computeIfAbsent(world, w -> new HashMap<>()).put(pos.toImmutable(), new WeakReference<>(teleportInhibitor));
    }
  }

  public static boolean isInhibited(@Nonnull World world, @Nonnull BlockPos pos1, @Nonnull BlockPos pos2) {
    if (!world.isRemote) {
      for (Iterator<Entry<BlockPos, WeakReference<TileInhibitorObelisk>>> itr = HANDLERS.computeIfAbsent(world, w -> new HashMap<>()).entrySet().iterator(); itr
          .hasNext();) {
        Entry<BlockPos, WeakReference<TileInhibitorObelisk>> next = itr.next();
        BlockPos blockPos = next.getKey();
        TileInhibitorObelisk inhibitor = next.getValue().get();
        if (blockPos == null || !world.isBlockLoaded(blockPos) || inhibitor == null || !inhibitor.hasWorld() || inhibitor.isInvalid()
            || world.getTileEntity(blockPos) != inhibitor) {
          itr.remove();
        } else if (inhibitor.isActive() && (inhibitor.getBounds().contains(pos1) || inhibitor.getBounds().contains(pos2))) {
          return true;
        }
      }
    }
    return false;
  }

  // Ender IO's teleporting
  @SubscribeEvent
  public static void onTeleport(TeleportEntityEvent event) {
    if (isInhibited(event.getEntity().world, BlockCoord.get(event.getEntity()), event.getTarget())) {
      event.setCanceled(true);
    }
  }

  // Forge's event for endermen and enderpearl teleporting
  @SubscribeEvent
  public static void onEnderTeleport(EnderTeleportEvent event) {
    if (isInhibited(event.getEntity().world, BlockCoord.get(event.getEntity()), new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ()))) {
      event.setCanceled(true);
    }
  }

}
