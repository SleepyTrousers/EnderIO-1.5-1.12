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

  public static int numConduitsPerRecipe = 8;

  public static double transceiverEnergyLoss = 0.1;

  public static double transceiverUpkeepCost = 0.25;

  public static double transceiverBucketTransmissionCost = 1;

  public static int transceiverMaxIO = 256;

  public static File configDirectory;

  public static boolean useHardRecipes = false;

  public static boolean detailedPowerTrackingEnabled = false;

  public static double maxPhotovoltaicOutput = 1.0;

  public static boolean useSneakMouseWheelYetaWrench = true;

  public static boolean useSneakRightClickYetaWrench = false;

  public static boolean useRfAsDefault = true;

  public static boolean itemConduitUsePhyscialDistance = false;

  public static int advancedFluidConduitExtractRate = 100;

  public static int advancedFluidConduitMaxIoRate = 400;

  public static int fluidConduitExtractRate = 50;

  public static int fluidConduitMaxIoRate = 200;

  public static boolean renderCapBankGauge = true;

  public static boolean renderCapBankGaugeBackground = true;

  public static boolean renderCapBankGaugeLevel = true;

  public static boolean updateLightingWhenHidingFacades = false;

  public static boolean travelAnchorEnabled = true;
  public static int travelAnchorMaxDistance = 48;

  public static int travelStaffMaxDistance = 96;
  public static float travelStaffPowerPerBlock = 10;
  public static int travelStaffMaxStoredPower = 25000;
  public static int travelStaffMaxPowerIo = 500;
  public static int travelStaffMaxBlinkDistance = 8;
  public static int travelStaffBlinkPauseTicks = 10;

  public static boolean travelStaffEnabled = true;
  public static boolean travelStaffBlinkEnabled = true;
  public static boolean travelStaffBlinkThroughSolidBlocksEnabled = true;
  public static boolean travelStaffBlinkThroughClearBlocksEnabled = true;

  public static int enderIoRange = 8;
  public static boolean enderIoMeAccessEnabled = true;

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
        Log.warn("Could not move old config file to new directory: " + e);
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

    useRfAsDefault = config.get("Settings", "displayPowerAsRedstoneFlux", useRfAsDefault, "If true, all power is displayed in RF, otherwise MJ is used.")
        .getBoolean(useRfAsDefault);

    useHardRecipes = config.get("Settings", "useHardRecipes", useHardRecipes, "When enabled machines cost significantly more.")
        .getBoolean(useHardRecipes);

    numConduitsPerRecipe = config.get("Settings", "numConduitsPerRecipe", numConduitsPerRecipe,
        "The number of conduits crafted per recipe.").getInt(numConduitsPerRecipe);

    photovoltaicCellEnabled = config.get("Settings", "photovoltaicCellEnabled", photovoltaicCellEnabled,
        "If set to false Photovoltaic Cells will not be craftable.")
        .getBoolean(photovoltaicCellEnabled);

    maxPhotovoltaicOutput = config.get("Settings", "maxPhotovoltaicOutput", maxPhotovoltaicOutput,
        "Maximum output in MJ/t of the Photovoltaic Panels.").getDouble(maxPhotovoltaicOutput);

    useAlternateBinderRecipe = config.get("Settings", "useAlternateBinderRecipe", false, "Create conduit binder in crafting table instead of furnace")
        .getBoolean(useAlternateBinderRecipe);

    conduitScale = config.get("Settings", "conduitScale", DEFAULT_CONDUIT_SCALE,
        "Valid values are between 0-1, smallest conduits at 0, largest at 1.\n" +
            "In SMP, all clients must be using the same value as the server.").getDouble(DEFAULT_CONDUIT_SCALE);
    conduitScale = VecmathUtil.clamp(conduitScale, 0, 1);

    fluidConduitExtractRate = config.get("Settings", "fluidConduitExtractRate", fluidConduitExtractRate,
        "Number of millibuckects per tick extract by a fluid conduits auto extract..").getInt(fluidConduitExtractRate);

    fluidConduitMaxIoRate = config.get("Settings", "fluidConduitMaxIoRate", fluidConduitMaxIoRate,
        "Number of millibuckects per tick that can pass through a single connection to a fluid conduit.").getInt(fluidConduitMaxIoRate);

    advancedFluidConduitExtractRate = config.get("Settings", "advancedFluidConduitExtractRate", advancedFluidConduitExtractRate,
        "Number of millibuckects per tick extract by advanced fluid conduits auto extract..").getInt(advancedFluidConduitExtractRate);

    advancedFluidConduitMaxIoRate = config.get("Settings", "advancedFluidConduitMaxIoRate", advancedFluidConduitMaxIoRate,
        "Number of millibuckects per tick that can pass through a single connection to an advanced fluid conduit.").getInt(advancedFluidConduitMaxIoRate);

    useAlternateTesseractModel = config.get("Settings", "useAlternateTransceiverModel", useAlternateTesseractModel,
        "Use TheKazador's alternatice model for the Dimensional Transceiver")
        .getBoolean(false);
    transceiverEnergyLoss = config.get("Settings", "transceiverEnergyLoss", transceiverEnergyLoss,
        "Amount of energy lost when transfered by Dimensional Transceiver 0 is no loss, 1 is 100% loss").getDouble(transceiverEnergyLoss);
    transceiverUpkeepCost = config.get("Settings", "transceiverUpkeepCost", transceiverUpkeepCost,
        "Number of MJ/t required to keep a Dimensional Transceiver connection open").getDouble(transceiverUpkeepCost);
    transceiverMaxIO = config.get("Settings", "transceiverMaxIO", transceiverMaxIO,
        "Maximum MJ/t sent and recieved by a Dimensional Transceiver per tick. Input and output limits are not cumulative").getInt(transceiverMaxIO);
    transceiverBucketTransmissionCost = config.get("Settings", "transceiverBucketTransmissionCost", transceiverBucketTransmissionCost,
        "The cost in MJ of trasporting a bucket of fluid via a Dimensional Transceiver.").getDouble(transceiverBucketTransmissionCost);

    detailedPowerTrackingEnabled = config
        .get(
            "Settings",
            "perInterfacePowerTrackingEnabled",
            detailedPowerTrackingEnabled,
            "Enable per tick sampling on individual power inputs and outputs. This allows slightly more detailed messages from the MJ Reader but has a negative impact on server performance.")
        .getBoolean(detailedPowerTrackingEnabled);

    useSneakMouseWheelYetaWrench = config.get("Settings", "useSneakMouseWheelYetaWrench", useSneakMouseWheelYetaWrench,
        "If true, shift-mouse wheel will change the conduit display mode when the YetaWrench is eqipped.")
        .getBoolean(useSneakMouseWheelYetaWrench);

    useSneakRightClickYetaWrench = config.get("Settings", "useSneakRightClickYetaWrench", useSneakRightClickYetaWrench,
        "If true, shift-clicking the YetaWrench on a null or non wrenchable object will change the conduit display mode.")
        .getBoolean(useSneakRightClickYetaWrench);

    itemConduitUsePhyscialDistance = config.get("Settings", "itemConduitUsePhyscialDistance", itemConduitUsePhyscialDistance, "If true, " +
        "'line of sight' distance rather than conduit path distance is used to calculate priorities.")
        .getBoolean(itemConduitUsePhyscialDistance);

    itemConduitUsePhyscialDistance = config.get("Settings", "itemConduitUsePhyscialDistance", itemConduitUsePhyscialDistance, "If true, " +
        "'line of sight' distance rather than conduit path distance is used to calculate priorities.")
        .getBoolean(itemConduitUsePhyscialDistance);

    if(!useSneakMouseWheelYetaWrench && !useSneakRightClickYetaWrench) {
      Log.warn("Both useSneakMouseWheelYetaWrench and useSneakRightClickYetaWrench are set to false. Enabling mouse wheel.");
      useSneakMouseWheelYetaWrench = true;
    }

    travelAnchorEnabled = config.get("Settings", "travelAnchorEnabled", travelAnchorEnabled,
        "When set to false the travel anchor will not be craftable.").getBoolean(travelAnchorEnabled);

    travelAnchorMaxDistance = config.get("Settings", "travelAnchorMaxDistance", travelAnchorMaxDistance,
        "Maximum number of blocks that can be traveled from one travel anchor to another.").getInt(travelAnchorMaxDistance);

    travelStaffMaxDistance = config.get("Settings", "travelStaffMaxDistance", travelStaffMaxDistance,
        "Maximum number of blocks that can be traveled using the Staff of the Traveling.").getInt(travelStaffMaxDistance);
    travelStaffPowerPerBlock = (float) config.get("Settings", "travelStaffPowerPerBlock", travelStaffPowerPerBlock,
        "Number of MJ required per block travelled using the Staff of the Traveling.").getDouble(travelStaffPowerPerBlock);

    travelStaffMaxStoredPower = config.get("Settings", "travelStaffMaxStoredPower", travelStaffMaxStoredPower,
        "Maximum number of MJ that can be stored using in the Staff of the Traveling.").getInt(travelStaffMaxStoredPower);

    travelStaffMaxPowerIo = config.get("Settings", "travelStaffMaxPowerIo", travelStaffMaxPowerIo,
        "Maximum number of MJ that the Staff of the Traveling can be charged per tick.").getInt(travelStaffMaxPowerIo);

    travelStaffMaxBlinkDistance = config.get("Settings", "travelStaffMaxBlinkDistance", travelStaffMaxBlinkDistance,
        "Max number of blocks teleported when shift clicking the staff.").getInt(travelStaffMaxBlinkDistance);

    travelStaffBlinkPauseTicks = config.get("Settings", "travelStaffBlinkPauseTicks", travelStaffBlinkPauseTicks,
        "Minimum number of ticks between 'blinks'. Values of 10 or less allow a limited sort of flight.").getInt(travelStaffBlinkPauseTicks);

    travelStaffEnabled = config.get("Settings", "travelStaffEnabled", travelAnchorEnabled,
        "If set to false the travel staff will not be craftable.").getBoolean(travelStaffEnabled);
    travelStaffBlinkEnabled = config.get("Settings", "travelStaffBlinkEnabled", travelStaffBlinkEnabled,
        "If set to false the travel staff can not be used to shift-right click teleport, or blink.").getBoolean(travelStaffBlinkEnabled);
    travelStaffBlinkThroughSolidBlocksEnabled = config.get("Settings", "travelStaffBlinkThroughSolidBlocksEnabled", travelStaffBlinkThroughSolidBlocksEnabled,
        "If set to false the travel staff can be used to blink through any block.").getBoolean(travelStaffBlinkThroughSolidBlocksEnabled);
    travelStaffBlinkThroughClearBlocksEnabled = config
        .get("Settings", "travelStaffBlinkThroughClearBlocksEnabled", travelStaffBlinkThroughClearBlocksEnabled,
            "If travelStaffBlinkThroughSolidBlocksEnabled is set to false and this is true, the travel " +
                "staff can only be used to blink through transparent or partial blocks (e.g. torches). " +
                "If both are false, only air blocks may be teleported through.")
        .getBoolean(travelStaffBlinkThroughClearBlocksEnabled);

    enderIoRange = config.get("Settings", "enderIoRange", enderIoRange,
        "Range accessable (in blocks) when using the Ender IO.").getInt(enderIoRange);

    enderIoMeAccessEnabled = config.get("Settings", "enderIoMeAccessEnabled", enderIoMeAccessEnabled,
        "If fasle, you will not be able to access a ME acess or crafting terminal using the Ender IO.").getBoolean(enderIoMeAccessEnabled);

    updateLightingWhenHidingFacades = config.get("Settings", "updateLightingWhenHidingFacades", updateLightingWhenHidingFacades,
        "When true, correct lighting is recalculated (client side) for conduit bundles when transitioning to"
            + " from being hidden behind a facade. This produces "
            + "better quality rendering but can result in frame stutters when switching to/from a wrench.")
        .getBoolean(updateLightingWhenHidingFacades);

    //TODO: Debug
    renderCapBankGauge = config.get("Debug", "renderCapBankGauge", renderCapBankGauge, "If not true capacitor banks will not render the level gauge at all.")
        .getBoolean(renderCapBankGauge);
    renderCapBankGaugeBackground = config.get("Debug", "renderCapBankGaugeBackground", renderCapBankGaugeBackground,
        "If not true capacitor banks will not render the level gauge background.")
        .getBoolean(renderCapBankGaugeBackground);
    renderCapBankGaugeLevel = config.get("Debug", "renderCapBankGaugeLevel", renderCapBankGaugeLevel,
        "If not true capacitor banks will not render the level on the gauge.")
        .getBoolean(renderCapBankGaugeLevel);

  }

  private Config() {
  }

}
