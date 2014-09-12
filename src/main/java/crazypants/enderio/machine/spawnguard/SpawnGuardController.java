package crazypants.enderio.machine.spawnguard;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.wireless.IWirelessCharger;
import crazypants.enderio.machine.wireless.WirelessChargerController;
import crazypants.util.BlockCoord;

public class SpawnGuardController {

  public static SpawnGuardController instance = new SpawnGuardController();

  
  static {
    FMLCommonHandler.instance().bus().register(SpawnGuardController.instance);
    MinecraftForge.EVENT_BUS.register(SpawnGuardController.instance);
  }

  private Map<Integer, Map<BlockCoord, TileSpawnGuard>> perWorldGuards = new HashMap<Integer, Map<BlockCoord, TileSpawnGuard>>();

  private SpawnGuardController() {
  }

  public void registerGuard(TileSpawnGuard guard) {
    if(guard == null) {
      return;
    }    
    Map<BlockCoord, TileSpawnGuard> chargers = getGuardsForWorld(guard.getWorldObj());
    chargers.put(guard.getLocation(), guard);
  }

  public void deregisterGuard(TileSpawnGuard guard) {
    if(guard == null) {
      return;
    }
    Map<BlockCoord, TileSpawnGuard> chargers = getGuardsForWorld(guard.getWorldObj());
    chargers.remove(guard.getLocation());
  }
  
  @SubscribeEvent
  public void onEntitySpawn(LivingSpawnEvent evt) {
    Map<BlockCoord, TileSpawnGuard> guards = getGuardsForWorld(evt.world);
    for(TileSpawnGuard guard : guards.values()) {
      if(guard.isSpawnPrevented(evt.entityLiving)) {        
        evt.setResult(Result.DENY);
        return;
      }
    }    
  }
  
  private Map<BlockCoord, TileSpawnGuard> getGuardsForWorld(World world) {
    Map<BlockCoord, TileSpawnGuard> res = perWorldGuards.get(world.provider.dimensionId);
    if(res == null) {
      res = new HashMap<BlockCoord, TileSpawnGuard>();
      perWorldGuards.put(world.provider.dimensionId, res);
    }
    return res;
  }
  
}
