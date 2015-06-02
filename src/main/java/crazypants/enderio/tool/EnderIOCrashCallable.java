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
  private static boolean hasUnknownAE2 = false;

  public static void create() {

    if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
      hasOptifine = FMLClientHandler.instance().hasOptifine();
    }

    for (ModContainer modContainer : ModAPIManager.INSTANCE.getAPIList()) {
      if ("appliedenergistics2|API".equals(modContainer.getModId())) {
        if (!"rv2".equals(modContainer.getVersion())) {
          hasUnknownAE2 = true;
        }
      }
    }

    if (hasOptifine || hasUnknownAE2) {
      FMLCommonHandler.instance().registerCrashCallable(new EnderIOCrashCallable());
    }
  }

  @Override
  public String call() throws Exception {
    String msg = "Found the following problems with your installation: ";
    if (hasOptifine) {
      msg += "\"Optifine is installed. This is NOT supported.\" ";
    }
    if (hasUnknownAE2) {
      msg += "\"An unknown AE2 API is installed. Ender IO was build against API version rv2.\" ";
    }
    msg += "This may have caused the error. Try reproducing the crash WITHOUT this/these mods before reporting it.";
    return msg;
  }

  @Override
  public String getLabel() {
    return EnderIO.MODID;
  }

}
