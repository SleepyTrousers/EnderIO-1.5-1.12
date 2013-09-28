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

  public static double transceiverEnergyLoss = 0;

  public static double transceiverUpkeepCost = 0.25;

  public static double transceiverBucketTransmissionCost = 1;

  public static int transceiverMaxIO = 256;

  public static File configDirectory;

  public static void load(Configuration config, FMLPreInitializationEvent event) {

    for (ModObject e : ModObject.values()) {
      e.load(config);
    }

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
    configDirectory = event.getModConfigurationDirectory();
  }

  private Config() {
  }

}
