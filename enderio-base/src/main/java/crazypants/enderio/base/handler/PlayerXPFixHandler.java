package crazypants.enderio.base.handler;

import java.lang.reflect.Field;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.xp.XpUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;

@EventBusSubscriber(modid = EnderIO.MODID)
public class PlayerXPFixHandler {

  // ORH doesn't work in unit tests, neither does RH...
  private static Field lastExperienceScore = null;
  private static boolean errored = false;
  static {
    try {
      if ("EntityPlayerMP".equals(EntityPlayerMP.class.getSimpleName())) {
        lastExperienceScore = ObfuscationReflectionHelper.findField(EntityPlayerMP.class, "lastExperienceScore");
      } else {
        lastExperienceScore = ObfuscationReflectionHelper.findField(EntityPlayerMP.class, "field_184856_bZ");
      }
      lastExperienceScore.setAccessible(true);
    } catch (UnableToFindFieldException e) {
      Log.error("Failed to access player object, disabling XP fixer. Reason: " + e.getLocalizedMessage());
      e.printStackTrace();
      errored = true;
    }
  }

  public static boolean isActive() {
    return !errored;
  }

  @SubscribeEvent
  public static void onPlayerTick(PlayerTickEvent event) {
    try {
      if (!errored && event.phase == Phase.START && event.player instanceof EntityPlayerMP
          && event.player.experienceTotal != lastExperienceScore.getInt(event.player)) {
        recalcPlayerXP(event);
      }
    } catch (IllegalArgumentException | IllegalAccessException e) {
      Log.error("Failed to access player object, disabling XP fixer. Reason: " + e.getLocalizedMessage());
      e.printStackTrace();
      errored = true;
    }
  }

  // public for unit tests only!
  public static void recalcPlayerXP(PlayerTickEvent event) {
    int level = XpUtil.getLevelForExperience(event.player.experienceTotal);
    int experienceForLevel = XpUtil.getExperienceForLevel(level);
    int experience = event.player.experienceTotal - experienceForLevel;
    event.player.experienceLevel = level;
    event.player.experience = (float) experience / (float) event.player.xpBarCap();
  }

  // public for unit tests only!
  public static void setErrored(boolean errored) {
    PlayerXPFixHandler.errored = errored;
  }

}
