package crazypants.enderio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public final class Log {

//  private static final boolean inDev = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
  private static final boolean inDev = System.getProperty("INDEV") != null;
  // private static final boolean inDev = false;

  public static final Logger LOGGER = LogManager.getLogger(EnderIO.MODID);

  public static void warn(String msg) {
    LOGGER.warn(msg);
  }

  public static void error(String msg) {
    LOGGER.error(msg);
  }

  public static void info(String msg) {
    if (inDev) {
      LOGGER.info(msg);
    } else {
      LOGGER.debug(msg);
    }
  }

  public static void debug(String msg) {
    if (inDev) {
      LOGGER.info("INDEV: " + msg);
    } else {
      LOGGER.debug(msg);
    }
  }

  private Log() {
  }

}
