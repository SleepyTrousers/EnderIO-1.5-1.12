package crazypants.enderio.base.loot;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class Loot {

  @SubscribeEvent
  public static void preInit(EnderIOLifecycleEvent.PreInit event) {
    LootFunctionManager.registerFunction(new LootSelector.Serializer());
    LootFunctionManager.registerFunction(new SetRandomEnergy.Serializer());
    LootFunctionManager.registerFunction(new SetRandomDarkUpgrade.Serializer());
    LootFunctionManager.registerFunction(new UseThings.Serializer());
    AnvilCapacitorRecipe.create();
  }

}
