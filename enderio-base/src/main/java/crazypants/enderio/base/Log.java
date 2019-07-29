package crazypants.enderio.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import crazypants.enderio.base.config.config.DiagnosticsConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public final class Log {

  // private static final boolean inDev = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
  private static boolean inDev = System.getProperty("INDEV") != null;
  // private static final boolean inDev = false;

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
    } else if (LOGGER.isDebugEnabled()) {
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

  @SubscribeEvent
  public static void preInit(EnderIOLifecycleEvent.Config.Pre event) {
    if (LOGGER.isDebugEnabled()) {
      warn("========================================================");
      warn("== Debug Logging is ENABLED ============================");
      warn("======================================================== ");
      warn("== This WILL slow down the game, so we recommend you  ==");
      warn("== disable it unless you need it. A log level of INFO ==");
      warn("== is enough for normal operation.                    ==");
      warn("========================================================  ");
    }
  }

  @SubscribeEvent
  public static void preInit2(EnderIOLifecycleEvent.Config.Post event) {
    inDev |= DiagnosticsConfig.debugUpgradeDebugMessagesToInfo.get();
  }

  public static boolean isInDev() {
    return inDev;
  }

}
