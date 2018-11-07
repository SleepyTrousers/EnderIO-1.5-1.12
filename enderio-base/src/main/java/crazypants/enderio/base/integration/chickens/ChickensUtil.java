package crazypants.enderio.base.integration.chickens;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ChickensUtil {

  @SubscribeEvent
  public static void preConfig(EnderIOLifecycleEvent.Config.Pre event) {
    CapturedMob.addToUnspawnableList(new ResourceLocation("chickens", "chickenschicken"));
  }

}
