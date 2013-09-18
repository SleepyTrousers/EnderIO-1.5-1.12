package crazypants.enderio;

import java.io.File;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import crazypants.vecmath.VecmathUtil;

public final class Config {

  static int BID = 700;
  static int IID = 8524;

  public static final double DEFAULT_CONDUIT_SCALE = 0.2;

  public static boolean useAlternateBinderRecipe;

  public static boolean useAlternateTesseractModel;

  public static double conduitScale = DEFAULT_CONDUIT_SCALE;

  public static File configDirectory;

  public static void load(Configuration config, FMLPreInitializationEvent event) {

    for (ModObject e : ModObject.values()) {
      e.load(config);
    }
    useAlternateBinderRecipe = config.get("Settings", "useAlternateBinderRecipe", false).getBoolean(false);
    useAlternateTesseractModel = config.get("Settings", "useAlternateTesseractModel", false).getBoolean(false);
    conduitScale = config.get("Settings", "conduitScale", DEFAULT_CONDUIT_SCALE,
        "Valid values are between 0-1, smallest conduits at 0, largest at 1.\n" +
            "In SMP, all clients must be using the same value as the server.").getDouble(DEFAULT_CONDUIT_SCALE);
    conduitScale = VecmathUtil.clamp(conduitScale, 0, 1);

    configDirectory = event.getModConfigurationDirectory();
  }

  private Config() {
  }

}
