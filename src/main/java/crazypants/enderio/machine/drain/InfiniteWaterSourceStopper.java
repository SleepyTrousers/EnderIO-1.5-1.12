package crazypants.enderio.machine.drain;

import info.loenwind.waterhooks.WaterFormEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.util.BlockCoord;

import cpw.mods.fml.common.Optional;

public class InfiniteWaterSourceStopper {

  private static final InfiniteWaterSourceStopper instance = new InfiniteWaterSourceStopper();
  
  public static InfiniteWaterSourceStopper getInstance() { return instance; }
  
  public static void register() {
    MinecraftForge.EVENT_BUS.register(instance);
  }
 

  private Map<Integer, Map<IWaterSensitive, Object>> teblMap = new HashMap<Integer, Map<IWaterSensitive, Object>>();
  
  @Optional.Method(modid = "waterhooks|API")
  @SubscribeEvent
  public void onWaterForming(WaterFormEvent event) {
    Map<IWaterSensitive, Object> tebl = teblMap.get(event.world.provider.dimensionId);
    if (tebl != null) {
      BlockCoord bc = new BlockCoord(event.x, event.y, event.z);
      for (IWaterSensitive hook : tebl.keySet()) {
        if (hook.preventInfiniteWaterForming(event.world, bc)) {
          event.setCanceled(true);
          return;
        }
      }
    }
  }

  @SubscribeEvent
  public void onWorldUnload(WorldEvent.Unload event) {
    teblMap.remove(event.world.provider.dimensionId);
  }
  
  public void unregister(World world, IWaterSensitive hook) {
    Map<IWaterSensitive, Object> tebl = teblMap.get(world.provider.dimensionId);
    if (tebl != null) {
      tebl.remove(hook);
      if (tebl.isEmpty()) {
        teblMap.remove(world.provider.dimensionId);
      }
    }
  }
  
  public void register(World world, IWaterSensitive hook) {
    Map<IWaterSensitive, Object> tebl = teblMap.get(world.provider.dimensionId);
    if (tebl == null) {
      tebl = new WeakHashMap<IWaterSensitive, Object>();
      teblMap.put(world.provider.dimensionId, tebl);
    }
    tebl.put(hook, null);
  }

}
