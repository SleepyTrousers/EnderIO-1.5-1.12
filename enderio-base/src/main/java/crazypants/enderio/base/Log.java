package crazypants.enderio.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import crazypants.enderio.base.config.config.DiagnosticsConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public final class Log {

  // private static final boolean inDev = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
  private static boolean inDev = System.getProperty("INDEV") != null;
  private static boolean suppressDebugMessages = false;

  public static final Logger LOGGER = LogManager.getLogger(EnderIO.MODID);

  public static void warn(Object... msg) {
    LOGGER.warn(() -> join(msg));
  }

  public static void error(Object... msg) {
    LOGGER.error(() -> join(msg));
  }

  public static void info(Object... msg) {
    LOGGER.info(() -> join(msg));
  }

  public static void debug(Object... msg) {
    if (inDev) {
      LOGGER.info(() -> "INDEV: " + join(msg));
    } else if (!suppressDebugMessages) {
      LOGGER.debug(() -> join(msg));
    }
  }

  public static void livetraceNBT(Object... msg) {
    if (DiagnosticsConfig.debugTraceNBTActivityExtremelyDetailed.get()) {
      LOGGER.info(join("", msg));
    }
  }

  public static String join(CharSequence delimiter, Object... elements) {
    StringBuilder joiner = new StringBuilder();
    for (Object cs : elements) {
      if (joiner.length() != 0) {
        joiner.append(delimiter);
      }
      joiner.append(cs);
    }
    return joiner.toString();
  }

  public static String join(Object... elements) {
    StringBuilder joiner = new StringBuilder();
    for (Object cs : elements) {
      joiner.append(cs);
    }
    return joiner.toString();
  }

  private Log() {
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void preInit(EnderIOLifecycleEvent.Config.Post event) {
    suppressDebugMessages = DiagnosticsConfig.debugSuppressDebugMessages.get();
    inDev |= DiagnosticsConfig.debugUpgradeDebugMessagesToInfo.get();
    if (LOGGER.isDebugEnabled()) {
      if (DiagnosticsConfig.debugComplainAboutForgeLogging.get()) {
        Logger temp = LogManager.getLogger("");
        temp.warn("========================================================");
        temp.warn("== Forge Debug Logging is ENABLED ======================");
        temp.warn("========================================================");
        temp.warn("== This WILL slow down the game, so we recommend you  ==");
        temp.warn("== disable it unless you need it. See:                ==");
        temp.warn("== https://github.com/MinecraftForge/MinecraftForge/issues/6271");
        temp.warn("========================================================");
      }
      if (suppressDebugMessages) {
        info("========================================================");
        info("== Ender IO Debug Logging is DISABLED ==================");
        info("========================================================");
        info("== This will not slow down the game, but you may miss ==");
        info("== out on information needed to diagnose issues. For  ==");
        info("== normal operation this is fine.                     ==");
        info("========================================================");
      } else {
        warn("========================================================");
        warn("== Ender IO Debug Logging is ENABLED ===================");
        warn("========================================================");
        warn("== This WILL slow down the game, so we recommend you  ==");
        warn("== disable it unless you need it.                     ==");
        warn("========================================================");
      }
    } else if (!suppressDebugMessages) {
      warn("========================================================");
      warn("== Forge Debug Logging is DISABLED but =================");
      warn("== Ender IO Debug Logging is ENABLED ===================");
      warn("========================================================");
      warn("== This means that Forge will throw away those log    ==");
      warn("== messages you asked Ender IO to generate.           ==");
      warn("========================================================");
    }
  }

  public static boolean isInDev() {
    return inDev;
  }

}
