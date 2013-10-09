package crazypants.enderio;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.Configuration;

import org.apache.commons.io.FileUtils;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import crazypants.vecmath.VecmathUtil;

public final class Config {

  static int BID = 700;
  static int IID = 8524;

  public static final double DEFAULT_CONDUIT_SCALE = 0.2;

  public static boolean useAlternateBinderRecipe;

  public static boolean useAlternateTesseractModel;

  public static boolean photovoltaicCellEnabled = true;

  public static double conduitScale = DEFAULT_CONDUIT_SCALE;

  public static int numConduitsPerRecipe = 4;

  public static double transceiverEnergyLoss = 0.1;

  public static double transceiverUpkeepCost = 0.25;

  public static double transceiverBucketTransmissionCost = 1;

  public static int transceiverMaxIO = 256;

  public static File configDirectory;

  public static boolean useHardRecipes = false;

  public static void load(FMLPreInitializationEvent event) {
    configDirectory = new File(event.getModConfigurationDirectory(), "enderio");
    if(!configDirectory.exists()) {
      configDirectory.mkdir();
    }

    File deprecatedFile = event.getSuggestedConfigurationFile();

    File configFile = new File(configDirectory, "EnderIO.cfg");
    if(deprecatedFile.exists()) {
      try {
        FileUtils.moveFile(deprecatedFile, configFile);
      } catch (IOException e) {
        Log.error("Could not move old config file to new directory: " + e);
        e.printStackTrace();
        throw new RuntimeException("Could not move old config file to new directory.", e);
      }
    }

    Configuration cfg = new Configuration(configFile);
    try {
      cfg.load();
      Config.processConfig(cfg);
    } catch (Exception e) {
      Log.error("EnderIO has a problem loading it's configuration");
    } finally {
      if(cfg.hasChanged()) {
        cfg.save();
      }
    }
  }

  public static void processConfig(Configuration config) {

    for (ModObject e : ModObject.values()) {
      e.load(config);
    }

    useHardRecipes = config.get("Settings", "useHardRecipes", useHardRecipes, "When enabled machines cost significantly more.")
        .getBoolean(useHardRecipes);

    numConduitsPerRecipe = config.get("Settings", "numConduitsPerRecipe", numConduitsPerRecipe,
        "The number of conduits crafted per recipe.").getInt(numConduitsPerRecipe);

    photovoltaicCellEnabled = config.get("Settings", "photovoltaicCellEnabled", photovoltaicCellEnabled,
        "If set to false Photovoltaic Cells will not be craftable.")
        .getBoolean(photovoltaicCellEnabled);

    useAlternateBinderRecipe = config.get("Settings", "useAlternateBinderRecipe", false, "Create conduit binder in crafting table instead of furnace")
        .getBoolean(false);

    conduitScale = config.get("Settings", "conduitScale", DEFAULT_CONDUIT_SCALE,
        "Valid values are between 0-1, smallest conduits at 0, largest at 1.\n" +
            "In SMP, all clients must be using the same value as the server.").getDouble(DEFAULT_CONDUIT_SCALE);
    conduitScale = VecmathUtil.clamp(conduitScale, 0, 1);

    useAlternateTesseractModel = config.get("Settings", "useAlternateTransceiverModel", false,
        "Use TheKazador's alternatice model for the Dimensional Transceiver")
        .getBoolean(false);
    transceiverEnergyLoss = config.get("Settings", "transceiverEnergyLoss", transceiverEnergyLoss,
        "Amount of energy lost when transfered by Dimensional Transceiver 0 is no loss, 1 is 100% loss").getDouble(transceiverEnergyLoss);
    transceiverUpkeepCost = config.get("Settings", "transceiverUpkeepCost", transceiverUpkeepCost,
        "Number of MJ/t required to keep a Dimensional Transceiver connection open").getDouble(transceiverUpkeepCost);
    transceiverMaxIO = config.get("Settings", "transceiverMaxIO", transceiverMaxIO,
        "Maximum MJ/t sent and recieved by a Dimensional Transceiver per tick. Input and output limites are no cumulative").getInt(transceiverMaxIO);
    transceiverBucketTransmissionCost = config.get("Settings", "transceiverBucketTransmissionCost", transceiverBucketTransmissionCost,
        "The cost in MJ of trasporting a bucket of fluid via a Dimensional Transceiver.").getDouble(transceiverBucketTransmissionCost);

  }

  private Config() {
  }

}
