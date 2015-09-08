package crazypants.enderio.tool;

import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.EnderIO;

public class EnderIOCrashCallable implements ICrashCallable {

  private static boolean hasOptifine = false;
  private static String hasUnknownAE2 = null;
  private static String hasOldAE2 = null;
  private static String hasOldRF = null;

  public static void create() {

    boolean register = false;

    if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
      register = hasOptifine = FMLClientHandler.instance().hasOptifine();
    }

    for (ModContainer modContainer : ModAPIManager.INSTANCE.getAPIList()) {
      if ("appliedenergistics2|API".equals(modContainer.getModId())) {
        if ("rv1".equals(modContainer.getVersion())) {
          hasOldAE2 = modContainer.getVersion() + " from " + modContainer.getSource().getName();
          register = true;
        } else if (!"rv2".equals(modContainer.getVersion())) {
          hasUnknownAE2 = modContainer.getVersion() + " from " + modContainer.getSource().getName();
          register = true;
        }
      } else if ("CoFHAPI|energy".equals(modContainer.getModId())) {
        if ("1.7.10R1.0.0".equals(modContainer.getVersion()) || "1.7.10R1.0.1".equals(modContainer.getVersion())) {
          hasOldRF = modContainer.getVersion() + " from " + modContainer.getSource().getName();
          register = true;
        }
      }
    }

    if (register) {
      FMLCommonHandler.instance().registerCrashCallable(new EnderIOCrashCallable());
    }
  }

  @Override
  public String call() throws Exception {
    String msg = "Found the following problem(s) with your installation: ";
    if (hasOptifine) {
      msg += "\"Optifine is installed. This is NOT supported.\" ";
    }
    if (hasOldRF != null) {
      msg += "\"An unsupportted old RF API is installed (" + hasOldRF
          + "). Ender IO needs at least 1.7.10R1.0.2 and will NOT work with older versions.\" ";
    }
    if (hasOldAE2 != null) {
      msg += "\"An unsupportted old AE2 API is installed (" + hasOldAE2
          + "). Ender IO was build against API version rv2 and will NOT work with older versions.\" ";
    }
    if (hasUnknownAE2 != null) {
      msg += "\"An unknown AE2 API is installed (" + hasUnknownAE2
          + "). Ender IO was build against API version rv2.\" ";
    }
    msg += "This may have caused the error. Try reproducing the crash WITHOUT this/these mod(s) before reporting it.";
    return msg;
  }

  @Override
  public String getLabel() {
    return EnderIO.MODID;
  }

}
