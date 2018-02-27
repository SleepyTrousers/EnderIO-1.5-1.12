package crazypants.enderio.base.integration.chickens;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ChickensUtil {

  @SubscribeEvent
  public static void preConfig(EnderIOLifecycleEvent.Config.Pre event) {
    Config.soulVesselUnspawnableList.add(new ResourceLocation("chickens", "chickenschicken"));
  }

}
