package crazypants.enderio.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.vecmath.VecmathUtil;

public final class Config {

  public static Configuration config;
  
  public static final String sectionPower = "Power Settings";
  public static final String sectionRecipe = "Recipe Settings";
  public static final String sectionItems = "Item Enabling";
  public static final String sectionEfficiency = "Efficiency Settings";
  public static final String sectionPersonal = "Personal Settings";
  public static final String sectionAnchor = "Anchor Settings";
  public static final String sectionStaff = "Staff Settings";
  public static final String sectionDarkSteel = "Dark Steel";
  public static final String sectionFarm = "Farm Settings";
  public static final String sectionAesthetic = "Aesthetic Settings";
  public static final String sectionAdvanced = "Advanced Settings";
  public static final String sectionMagnet = "Magnet Settings";
  public static final String sectionSpawner = "PoweredSpawner Settings";
  
  static int BID = 700;
  static int IID = 8524;

  public static final double DEFAULT_CONDUIT_SCALE = 0.5;

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

  public static boolean useSteelInChassi = false;

  public static boolean detailedPowerTrackingEnabled = false;

  public static boolean useSneakMouseWheelYetaWrench = true;
  public static boolean useSneakRightClickYetaWrench = false;

  public static boolean useRfAsDefault = true;

  public static boolean itemConduitUsePhyscialDistance = false;

  public static int enderFluidConduitExtractRate = 200;
  public static int enderFluidConduitMaxIoRate = 800;
  public static int advancedFluidConduitExtractRate = 100;
  public static int advancedFluidConduitMaxIoRate = 400;
  public static int fluidConduitExtractRate = 50;
  public static int fluidConduitMaxIoRate = 200;

  public static boolean updateLightingWhenHidingFacades = false;

  public static boolean travelAnchorEnabled = true;
  public static int travelAnchorMaxDistance = 48;

  public static int travelStaffMaxDistance = 96;
  public static float travelStaffPowerPerBlockRF = 250;

  public static int travelStaffMaxBlinkDistance = 16;
  public static int travelStaffBlinkPauseTicks = 10;

  public static boolean travelStaffEnabled = true;
  public static boolean travelStaffBlinkEnabled = true;
  public static boolean travelStaffBlinkThroughSolidBlocksEnabled = true;
  public static boolean travelStaffBlinkThroughClearBlocksEnabled = true;

  public static int enderIoRange = 8;
  public static boolean enderIoMeAccessEnabled = true;

  public static int darkSteelPowerStorageBase = 100000;
  public static int darkSteelPowerStorageLevelOne = 150000;
  public static int darkSteelPowerStorageLevelTwo = 250000;
  public static int darkSteelPowerStorageLevelThree = 1000000;

  public static float darkSteelSpeedOneWalkModifier = 0.1f;
  public static float darkSteelSpeedTwoWalkMultiplier = 0.2f;
  public static float darkSteelSpeedThreeWalkMultiplier = 0.3f;

  public static float darkSteelSpeedOneSprintModifier = 0.1f;
  public static float darkSteelSpeedTwoSprintMultiplier = 0.3f;
  public static float darkSteelSpeedThreeSprintMultiplier = 0.5f;

  public static double darkSteelBootsJumpModifier = 1.5;

  public static int darkSteelWalkPowerCost = darkSteelPowerStorageLevelTwo / 3000;
  public static int darkSteelSprintPowerCost = darkSteelWalkPowerCost * 4;
  public static boolean darkSteelDrainPowerFromInventory = false;
  public static int darkSteelBootsJumpPowerCost = 250;

  public static float darkSteelSwordWitherSkullChance = 0.05f;
  public static float darkSteelSwordWitherSkullLootingModifier = 0.167f / 3f; //at looting 3, have a 1 in 6 chance of getting a skull
  public static float darkSteelSwordSkullChance = 0.2f;
  public static float darkSteelSwordSkullLootingModifier = 0.15f;
  public static float vanillaSwordSkullLootingModifier = 0.1f;
  public static int darkSteelSwordPowerUsePerHit = 750;
  public static double darkSteelSwordEnderPearlDropChance = 1;
  public static double darkSteelSwordEnderPearlDropChancePerLooting = 0.5;

  public static int darkSteelPickPowerUseObsidian = 10000;
  public static int darkSteelPickEffeciencyObsidian = 50;
  public static int darkSteelPickPowerUsePerDamagePoint = 750;
  public static float darkSteelPickEffeciencyBoostWhenPowered = 2;

  public static int darkSteelAxePowerUsePerDamagePoint = 750;
  public static int darkSteelAxePowerUsePerDamagePointMultiHarvest = 1500;
  public static float darkSteelAxeEffeciencyBoostWhenPowered = 2;
  public static float darkSteelAxeSpeedPenaltyMultiHarvest = 8;

  public static int hootchPowerPerCycle = 6;
  public static int hootchPowerTotalBurnTime = 6000;
  public static int rocketFuelPowerPerCycle = 16;
  public static int rocketFuelPowerTotalBurnTime = 7000;
  public static int fireWaterPowerPerCycle = 8;
  public static int fireWaterPowerTotalBurnTime = 15000;
  public static float vatPowerUserPerTick = 2;

  public static double maxPhotovoltaicOutput = 1.0;
  public static double maxPhotovoltaicAdvancedOutput = 4.0;

  public static double zombieGeneratorMjPerTick = 8.0;
  public static int zombieGeneratorTicksPerBucketFuel = 10000;

  public static boolean combustionGeneratorUseOpaqueModel = true;

  public static boolean addFuelTooltipsToAllFluidContainers = true;
  public static boolean addFurnaceFuelTootip = true;
  public static boolean addDurabilityTootip = true;

  public static int darkSteelUpgradeVibrantCost = 20;
  public static int darkSteelUpgradePowerOneCost = 10;
  public static int darkSteelUpgradePowerTwoCost = 20;
  public static int darkSteelUpgradePowerThreeCost = 30;

  public static float farmContinuousEnergyUse = 4;
  public static float farmActionEnergyUse = 50;
  public static int farmDefaultSize = 3;
  public static boolean farmAxeDamageOnLeafBreak = false;
  public static float farmToolTakeDamageChance = 1;

  public static int magnetPowerUsePerSecondRF = 1;
  public static int magnetPowerCapacityRF = 100000;
  public static int magnetRange = 5;

  public static boolean useCombustionGenModel = false;

  public static int crafterMjPerCraft = 250;

  public static int capacitorBankMaxIoMJ = 100;
  public static int capacitorBankMaxStorageMJ = 500000;

  public static int poweredSpawnerMinDelayTicks = 200;
  public static int poweredSpawnerMaxDelayTicks = 800;
  public static float poweredSpawnerLevelOnePowerPerTick = 16;
  public static float poweredSpawnerLevelTwoPowerPerTick = 48;
  public static float poweredSpawnerLevelThreePowerPerTick = 96;
  public static int poweredSpawnerMaxPlayerDistance = 0;
  public static boolean poweredSpawnerUseVanillaSpawChecks = false;
  public static double brokenSpawnerDropChance = 1;
  public static int powerSpawnerAddSpawnerCost = 30;

  public static void load(FMLPreInitializationEvent event) {
    configDirectory = new File(event.getModConfigurationDirectory(), EnderIO.MODID.toLowerCase());
    if(!configDirectory.exists()) {
      configDirectory.mkdir();
    }

    File configFile = new File(configDirectory, "EnderIO.cfg");
    config = new Configuration(configFile);
    syncConfig();
  }
  
  public static void syncConfig()
  {
    try {
      Config.processConfig(config);
    } catch (Exception e) {
      Log.error("EnderIO has a problem loading it's configuration");
    } finally {
      if(config.hasChanged()) {
        config.save();
      }
    }
  }

  public static void processConfig(Configuration config) {
    useRfAsDefault = config.get(sectionPower, "displayPowerAsRedstoneFlux", useRfAsDefault, "If true, all power is displayed in RF, otherwise MJ is used.")
        .getBoolean(useRfAsDefault);

    capacitorBankMaxIoMJ = config.get(sectionPower, "capacitorBankMaxIoMJ", capacitorBankMaxIoMJ, "The maximum IO for a single capacitor in MJ/t")
        .getInt(capacitorBankMaxIoMJ);
    capacitorBankMaxStorageMJ = config.get(sectionPower, "capacitorBankMaxStorageMJ", capacitorBankMaxStorageMJ,
        "The maximum storage for a single capacitor in MJ")
        .getInt(capacitorBankMaxStorageMJ);

    useHardRecipes = config.get(sectionRecipe, "useHardRecipes", useHardRecipes, "When enabled machines cost significantly more.")
        .getBoolean(useHardRecipes);

    useSteelInChassi = config.get(sectionRecipe, "useSteelInChassi", useSteelInChassi, "When enabled machine chassis will require steel instead of iron.")
        .getBoolean(useSteelInChassi);

    numConduitsPerRecipe = config.get(sectionRecipe, "numConduitsPerRecipe", numConduitsPerRecipe,
        "The number of conduits crafted per recipe.").getInt(numConduitsPerRecipe);

    photovoltaicCellEnabled = config.get(sectionItems, "photovoltaicCellEnabled", photovoltaicCellEnabled,
        "If set to false: Photovoltaic Cells will not be craftable.")
        .getBoolean(photovoltaicCellEnabled);

    maxPhotovoltaicOutput = config.get(sectionPower, "maxPhotovoltaicOutput", maxPhotovoltaicOutput,
        "Maximum output in MJ/t of the Photovoltaic Panels.").getDouble(maxPhotovoltaicOutput);
    maxPhotovoltaicAdvancedOutput = config.get(sectionPower, "maxPhotovoltaicAdvancedOutput", maxPhotovoltaicAdvancedOutput,
        "Maximum output in MJ/t of the Advanced Photovoltaic Panels.").getDouble(maxPhotovoltaicAdvancedOutput);

    useAlternateBinderRecipe = config.get(sectionRecipe, "useAlternateBinderRecipe", false, "Create conduit binder in crafting table instead of furnace")
        .getBoolean(useAlternateBinderRecipe);

    conduitScale = config.get(sectionAesthetic, "conduitScale", DEFAULT_CONDUIT_SCALE,
        "Valid values are between 0-1, smallest conduits at 0, largest at 1.\n" +
            "In SMP, all clients must be using the same value as the server.").getDouble(DEFAULT_CONDUIT_SCALE);
    conduitScale = VecmathUtil.clamp(conduitScale, 0, 1);

    fluidConduitExtractRate = config.get(sectionEfficiency, "fluidConduitExtractRate", fluidConduitExtractRate,
        "Number of millibuckects per tick extracted by a fluid conduits auto extracting").getInt(fluidConduitExtractRate);

    fluidConduitMaxIoRate = config.get(sectionEfficiency, "fluidConduitMaxIoRate", fluidConduitMaxIoRate,
        "Number of millibuckects per tick that can pass through a single connection to a fluid conduit.").getInt(fluidConduitMaxIoRate);

    advancedFluidConduitExtractRate = config.get(sectionEfficiency, "advancedFluidConduitExtractRate", advancedFluidConduitExtractRate,
        "Number of millibuckects per tick extracted by pressurised fluid conduits auto extracting").getInt(advancedFluidConduitExtractRate);

    advancedFluidConduitMaxIoRate = config.get(sectionEfficiency, "advancedFluidConduitMaxIoRate", advancedFluidConduitMaxIoRate,
        "Number of millibuckects per tick that can pass through a single connection to an pressurised fluid conduit.").getInt(advancedFluidConduitMaxIoRate);

    enderFluidConduitExtractRate = config.get(sectionEfficiency, "enderFluidConduitExtractRate", enderFluidConduitExtractRate,
        "Number of millibuckects per tick extracted by ender fluid conduits auto extracting").getInt(enderFluidConduitExtractRate);

    enderFluidConduitMaxIoRate = config.get(sectionEfficiency, "enderFluidConduitMaxIoRate", enderFluidConduitMaxIoRate,
        "Number of millibuckects per tick that can pass through a single connection to an ender fluid conduit.").getInt(enderFluidConduitMaxIoRate);

    useAlternateTesseractModel = config.get(sectionAesthetic, "useAlternateTransceiverModel", useAlternateTesseractModel,
        "Use TheKazador's alternatice model for the Dimensional Transceiver")
        .getBoolean(false);
    transceiverEnergyLoss = config.get(sectionPower, "transceiverEnergyLoss", transceiverEnergyLoss,
        "Amount of energy lost when transfered by Dimensional Transceiver; 0 is no loss, 1 is 100% loss").getDouble(transceiverEnergyLoss);
    transceiverUpkeepCost = config.get(sectionPower, "transceiverUpkeepCost", transceiverUpkeepCost,
        "Number of MJ/t required to keep a Dimensional Transceiver connection open").getDouble(transceiverUpkeepCost);
    transceiverMaxIO = config.get(sectionPower, "transceiverMaxIO", transceiverMaxIO,
        "Maximum MJ/t sent and recieved by a Dimensional Transceiver per tick. Input and output limits are not cumulative").getInt(transceiverMaxIO);
    transceiverBucketTransmissionCost = config.get(sectionEfficiency, "transceiverBucketTransmissionCost", transceiverBucketTransmissionCost,
        "The cost in MJ of transporting a bucket of fluid via a Dimensional Transceiver.").getDouble(transceiverBucketTransmissionCost);

    vatPowerUserPerTick = (float) config.get(sectionPower, "vatPowerUserPerTick", vatPowerUserPerTick,
        "Power use (MJ/t) used by the vat.").getDouble(vatPowerUserPerTick);

    detailedPowerTrackingEnabled = config
        .get(
            sectionAdvanced,
            "perInterfacePowerTrackingEnabled",
            detailedPowerTrackingEnabled,
            "Enable per tick sampling on individual power inputs and outputs. This allows slightly more detailed messages from the MJ Reader but has a negative impact on server performance.")
        .getBoolean(detailedPowerTrackingEnabled);

    useSneakMouseWheelYetaWrench = config.get(sectionPersonal, "useSneakMouseWheelYetaWrench", useSneakMouseWheelYetaWrench,
        "If true, shift-mouse wheel will change the conduit display mode when the YetaWrench is eqipped.")
        .getBoolean(useSneakMouseWheelYetaWrench);

    useSneakRightClickYetaWrench = config.get(sectionPersonal, "useSneakRightClickYetaWrench", useSneakRightClickYetaWrench,
        "If true, shift-clicking the YetaWrench on a null or non wrenchable object will change the conduit display mode.")
        .getBoolean(useSneakRightClickYetaWrench);

    itemConduitUsePhyscialDistance = config.get(sectionEfficiency, "itemConduitUsePhyscialDistance", itemConduitUsePhyscialDistance, "If true, " +
        "'line of sight' distance rather than conduit path distance is used to calculate priorities.")
        .getBoolean(itemConduitUsePhyscialDistance);

    if(!useSneakMouseWheelYetaWrench && !useSneakRightClickYetaWrench) {
      Log.warn("Both useSneakMouseWheelYetaWrench and useSneakRightClickYetaWrench are set to false. Enabling mouse wheel.");
      useSneakMouseWheelYetaWrench = true;
    }

    travelAnchorEnabled = config.get(sectionItems, "travelAnchorEnabled", travelAnchorEnabled,
        "When set to false: the travel anchor will not be craftable.").getBoolean(travelAnchorEnabled);

    travelAnchorMaxDistance = config.get(sectionAnchor, "travelAnchorMaxDistance", travelAnchorMaxDistance,
        "Maximum number of blocks that can be traveled from one travel anchor to another.").getInt(travelAnchorMaxDistance);

    travelStaffMaxDistance = config.get(sectionStaff, "travelStaffMaxDistance", travelStaffMaxDistance,
        "Maximum number of blocks that can be traveled using the Staff of the Traveling.").getInt(travelStaffMaxDistance);
    travelStaffPowerPerBlockRF = (float) config.get(sectionStaff, "travelStaffPowerPerBlockRF", travelStaffPowerPerBlockRF,
        "Number of MJ required per block travelled using the Staff of the Traveling.").getDouble(travelStaffPowerPerBlockRF);

    travelStaffMaxBlinkDistance = config.get(sectionStaff, "travelStaffMaxBlinkDistance", travelStaffMaxBlinkDistance,
        "Max number of blocks teleported when shift clicking the staff.").getInt(travelStaffMaxBlinkDistance);

    travelStaffBlinkPauseTicks = config.get(sectionStaff, "travelStaffBlinkPauseTicks", travelStaffBlinkPauseTicks,
        "Minimum number of ticks between 'blinks'. Values of 10 or less allow a limited sort of flight.").getInt(travelStaffBlinkPauseTicks);

    travelStaffEnabled = config.get(sectionItems, "travelStaffEnabled", travelAnchorEnabled,
        "If set to false: the travel staff will not be craftable.").getBoolean(travelStaffEnabled);
    travelStaffBlinkEnabled = config.get(sectionItems, "travelStaffBlinkEnabled", travelStaffBlinkEnabled,
        "If set to false: the travel staff can not be used to shift-right click teleport, or blink.").getBoolean(travelStaffBlinkEnabled);
    travelStaffBlinkThroughSolidBlocksEnabled = config.get(sectionItems, "travelStaffBlinkThroughSolidBlocksEnabled",
        travelStaffBlinkThroughSolidBlocksEnabled,
        "If set to false: the travel staff can be used to blink through any block.").getBoolean(travelStaffBlinkThroughSolidBlocksEnabled);
    travelStaffBlinkThroughClearBlocksEnabled = config
        .get(sectionItems, "travelStaffBlinkThroughClearBlocksEnabled", travelStaffBlinkThroughClearBlocksEnabled,
            "If travelStaffBlinkThroughSolidBlocksEnabled is set to false and this is true: the travel " +
                "staff can only be used to blink through transparent or partial blocks (e.g. torches). " +
                "If both are false: only air blocks may be teleported through.")
        .getBoolean(travelStaffBlinkThroughClearBlocksEnabled);

    enderIoRange = config.get(sectionEfficiency, "enderIoRange", enderIoRange,
        "Range accessable (in blocks) when using the Ender IO.").getInt(enderIoRange);

    enderIoMeAccessEnabled = config.get(sectionPersonal, "enderIoMeAccessEnabled", enderIoMeAccessEnabled,
        "If false: you will not be able to access a ME acess or crafting terminal using the Ender IO.").getBoolean(enderIoMeAccessEnabled);

    updateLightingWhenHidingFacades = config.get(sectionEfficiency, "updateLightingWhenHidingFacades", updateLightingWhenHidingFacades,
        "When true: correct lighting is recalculated (client side) for conduit bundles when transitioning to"
            + " from being hidden behind a facade. This produces "
            + "better quality rendering but can result in frame stutters when switching to/from a wrench.")
        .getBoolean(updateLightingWhenHidingFacades);

    darkSteelPowerStorageBase = config.get(sectionDarkSteel, "darkSteelPowerStorageBase", darkSteelPowerStorageBase,
        "Base amount of power stored by dark steel items.").getInt(darkSteelPowerStorageBase);
    darkSteelPowerStorageLevelOne = config.get(sectionDarkSteel, "darkSteelPowerStorageLevelOne", darkSteelPowerStorageLevelOne,
        "Amount of power stored by dark steel items with a level 1 upgrade.").getInt(darkSteelPowerStorageLevelOne);
    darkSteelPowerStorageLevelTwo = config.get(sectionDarkSteel, "darkSteelPowerStorageLevelTwo", darkSteelPowerStorageLevelTwo,
        "Amount of power stored by dark steel items with a level 2 upgrade.").getInt(darkSteelPowerStorageLevelTwo);
    darkSteelPowerStorageLevelThree = config.get(sectionDarkSteel, "darkSteelPowerStorageLevelThree", darkSteelPowerStorageLevelThree,
        "Amount of power stored by dark steel items with a level 3 upgrade.").getInt(darkSteelPowerStorageLevelThree);

    darkSteelUpgradeVibrantCost = config.get(sectionDarkSteel, "darkSteelUpgradeVibrantCost", darkSteelUpgradeVibrantCost,
        "Number of levels required for the 'Vibrant' upgrade.").getInt(darkSteelUpgradeVibrantCost);
    darkSteelUpgradePowerOneCost = config.get(sectionDarkSteel, "darkSteelUpgradePowerOneCost", darkSteelUpgradePowerOneCost,
        "Number of levels required for the 'Vibrant' upgrade.").getInt(darkSteelUpgradePowerOneCost);
    darkSteelUpgradePowerTwoCost = config.get(sectionDarkSteel, "darkSteelUpgradePowerTwoCost", darkSteelUpgradePowerTwoCost,
        "Number of levels required for the 'Vibrant' upgrade.").getInt(darkSteelUpgradePowerTwoCost);
    darkSteelUpgradePowerThreeCost = config.get(sectionDarkSteel, "darkSteelUpgradePowerThreeCost", darkSteelUpgradePowerThreeCost,
        "Number of levels required for the 'Vibrant' upgrade.").getInt(darkSteelUpgradePowerThreeCost);

    darkSteelSpeedOneWalkModifier = (float) config.get(sectionDarkSteel, "darkSteelSpeedOneWalkModifier", darkSteelSpeedOneWalkModifier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedOneWalkModifier);
    darkSteelSpeedTwoWalkMultiplier = (float) config.get(sectionDarkSteel, "darkSteelSpeedTwoWalkMultiplier", darkSteelSpeedTwoWalkMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedTwoWalkMultiplier);
    darkSteelSpeedThreeWalkMultiplier = (float) config.get(sectionDarkSteel, "darkSteelSpeedThreeWalkMultiplier", darkSteelSpeedThreeWalkMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedThreeWalkMultiplier);

    darkSteelSpeedOneSprintModifier = (float) config.get(sectionDarkSteel, "darkSteelSpeedOneSprintModifier", darkSteelSpeedOneSprintModifier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedOneSprintModifier);
    darkSteelSpeedTwoSprintMultiplier = (float) config.get(sectionDarkSteel, "darkSteelSpeedTwoSprintMultiplier", darkSteelSpeedTwoSprintMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedTwoSprintMultiplier);
    darkSteelSpeedThreeSprintMultiplier = (float) config.get(sectionDarkSteel, "darkSteelSpeedThreeSprintMultiplier", darkSteelSpeedThreeSprintMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedThreeSprintMultiplier);

    darkSteelBootsJumpModifier = config.get(sectionDarkSteel, "darkSteelBootsJumpModifier", darkSteelBootsJumpModifier,
        "Jump height modifier applied when jumping with Dark Steel Boots equipped").getDouble(darkSteelBootsJumpModifier);

    darkSteelPowerStorageBase = config.get(sectionDarkSteel, "darkSteelPowerStorage", darkSteelPowerStorageBase,
        "Amount of power stored (RF) per crystal in the armor items recipe.").getInt(darkSteelPowerStorageBase);
    darkSteelWalkPowerCost = config.get(sectionDarkSteel, "darkSteelWalkPowerCost", darkSteelWalkPowerCost,
        "Amount of power stored (RF) per block walked when wearing the dark steel boots.").getInt(darkSteelWalkPowerCost);
    darkSteelSprintPowerCost = config.get(sectionDarkSteel, "darkSteelSprintPowerCost", darkSteelWalkPowerCost,
        "Amount of power stored (RF) per block walked when wearing the dark stell boots.").getInt(darkSteelSprintPowerCost);
    darkSteelDrainPowerFromInventory = config.get(sectionDarkSteel, "darkSteelDrainPowerFromInventory", darkSteelDrainPowerFromInventory,
        "If true, dark steel armor will drain power stored (RF) in power containers in the players invenotry.").getBoolean(darkSteelDrainPowerFromInventory);

    darkSteelBootsJumpPowerCost = config.get(sectionDarkSteel, "darkSteelBootsJumpPowerCost", darkSteelBootsJumpPowerCost,
        "Base amount of power used per jump (RF) dark steel boots. The second jump in a 'double jump' uses 2x this etc").getInt(darkSteelBootsJumpPowerCost);

    darkSteelSwordSkullChance = (float) config.get(sectionDarkSteel, "darkSteelSwordSkullChance", darkSteelSwordSkullChance,
        "The base chance that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordSkullChance);
    darkSteelSwordSkullLootingModifier = (float) config.get(sectionDarkSteel, "darkSteelSwordSkullLootingModifier", darkSteelSwordSkullLootingModifier,
        "The chance per looting level that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordSkullLootingModifier);
    darkSteelSwordWitherSkullChance = (float) config.get(sectionDarkSteel, "darkSteelSwordWitherSkullChance", darkSteelSwordWitherSkullChance,
        "The base chance that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordWitherSkullChance);
    darkSteelSwordWitherSkullLootingModifier = (float) config.get(sectionDarkSteel, "darkSteelSwordWitherSkullLootingModifie",
        darkSteelSwordWitherSkullLootingModifier,
        "The chance per looting level that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordWitherSkullLootingModifier);
    vanillaSwordSkullLootingModifier = (float) config.get(sectionPersonal, "vanillaSwordSkullLootingModifier", vanillaSwordSkullLootingModifier,
        "The chance per looting level that a skull will be dropped when using a non-dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        vanillaSwordSkullLootingModifier);
    darkSteelSwordPowerUsePerHit = config.get(sectionDarkSteel, "darkSteelSwordPowerUsePerHit", darkSteelSwordPowerUsePerHit,
        "The amount of power (RF) used per hit.").getInt(darkSteelSwordPowerUsePerHit);
    darkSteelSwordEnderPearlDropChance = config.get(sectionDarkSteel, "darkSteelSwordEnderPearlDropChance", darkSteelSwordEnderPearlDropChance,
        "The chance that an ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordEnderPearlDropChance);
    darkSteelSwordEnderPearlDropChancePerLooting = config.get(sectionDarkSteel, "darkSteelSwordEnderPearlDropChancePerLooting",
        darkSteelSwordEnderPearlDropChancePerLooting,
        "The chance for each looting level that an additional ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(
            darkSteelSwordEnderPearlDropChancePerLooting);

    darkSteelPickPowerUseObsidian = config.get(sectionDarkSteel, "darkSteelPickPowerUseObsidian", darkSteelPickPowerUseObsidian,
        "The amount of power (RF) used to break an obsidian block.").getInt(darkSteelPickPowerUseObsidian);
    darkSteelPickEffeciencyObsidian = config.get(sectionDarkSteel, "darkSteelPickEffeciencyObsidian", darkSteelPickEffeciencyObsidian,
        "The effeciency when breaking obsidian with a powered  Dark Pickaxe.").getInt(darkSteelPickEffeciencyObsidian);
    darkSteelPickPowerUsePerDamagePoint = config.get(sectionDarkSteel, "darkSteelPickPowerUsePerDamagePoint", darkSteelPickPowerUsePerDamagePoint,
        "Power use (RF) per damage/durability point avoided.").getInt(darkSteelPickPowerUsePerDamagePoint);
    darkSteelPickEffeciencyBoostWhenPowered = (float) config.get(sectionDarkSteel, "darkSteelPickEffeciencyBoostWhenPowered",
        darkSteelPickEffeciencyBoostWhenPowered, "The increase in effciency when powered.").getDouble(darkSteelPickEffeciencyBoostWhenPowered);

    darkSteelAxePowerUsePerDamagePoint = config.get(sectionDarkSteel, "darkSteelAxePowerUsePerDamagePoint", darkSteelAxePowerUsePerDamagePoint,
        "Power use (RF) per damage/durability point avoided.").getInt(darkSteelAxePowerUsePerDamagePoint);
    darkSteelAxePowerUsePerDamagePointMultiHarvest = config.get(sectionDarkSteel, "darkSteelPickAxeUsePerDamagePointMultiHarvest",
        darkSteelAxePowerUsePerDamagePointMultiHarvest,
        "Power use (RF) per damage/durability point avoided when shift-harvesting multiple logs").getInt(darkSteelAxePowerUsePerDamagePointMultiHarvest);
    darkSteelAxeSpeedPenaltyMultiHarvest = (float) config.get(sectionDarkSteel, "darkSteelAxeSpeedPenaltyMultiHarvest", darkSteelAxeSpeedPenaltyMultiHarvest,
        "How much slower shift-harvesting logs is.").getDouble(darkSteelAxeSpeedPenaltyMultiHarvest);
    darkSteelAxeEffeciencyBoostWhenPowered = (float) config.get(sectionDarkSteel, "darkSteelAxeEffeciencyBoostWhenPowered",
        darkSteelAxeEffeciencyBoostWhenPowered, "The increase in effciency when powered.").getDouble(darkSteelAxeEffeciencyBoostWhenPowered);

    hootchPowerPerCycle = config.get(sectionPower, "hootchPowerPerCycle", hootchPowerPerCycle,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 3, BC Fuel = 6").getInt(hootchPowerPerCycle);
    hootchPowerTotalBurnTime = config.get(sectionPower, "hootchPowerTotalBurnTime", hootchPowerTotalBurnTime,
        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000").getInt(hootchPowerTotalBurnTime);

    rocketFuelPowerPerCycle = config.get(sectionPower, "rocketFuelPowerPerCycle", rocketFuelPowerPerCycle,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 3, BC Fuel = 6").getInt(rocketFuelPowerPerCycle);
    rocketFuelPowerTotalBurnTime = config.get(sectionPower, "rocketFuelPowerTotalBurnTime", rocketFuelPowerTotalBurnTime,
        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000").getInt(rocketFuelPowerTotalBurnTime);

    fireWaterPowerPerCycle = config.get(sectionPower, "fireWaterPowerPerCycle", fireWaterPowerPerCycle,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 3, BC Fuel = 6").getInt(fireWaterPowerPerCycle);
    fireWaterPowerTotalBurnTime = config.get(sectionPower, "fireWaterPowerTotalBurnTime", fireWaterPowerTotalBurnTime,
        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000").getInt(fireWaterPowerTotalBurnTime);

    zombieGeneratorMjPerTick = config.get(sectionPower, "zombieGeneratorMjPerTick", zombieGeneratorMjPerTick,
        "The amount of power generated per tick.").getDouble(zombieGeneratorMjPerTick);
    zombieGeneratorTicksPerBucketFuel = config.get(sectionPower, "zombieGeneratorTicksPerMbFuel", zombieGeneratorTicksPerBucketFuel,
        "The number of ticks one bucket of fuel lasts.").getInt(zombieGeneratorTicksPerBucketFuel);

    addFuelTooltipsToAllFluidContainers = config.get(sectionPersonal, "addFuelTooltipsToAllFluidContainers", addFuelTooltipsToAllFluidContainers,
        "If true, the MJ/t and burn time of the fuel will be displayed in all tooltips for fluid containers with fuel.").getBoolean(
        addFuelTooltipsToAllFluidContainers);
    addDurabilityTootip = config.get(sectionPersonal, "addDurabilityTootip", addFuelTooltipsToAllFluidContainers,
        "If true, adds durability tooltips to tools and armor").getBoolean(
        addDurabilityTootip);
    addFurnaceFuelTootip = config.get(sectionPersonal, "addFurnaceFuelTootip", addFuelTooltipsToAllFluidContainers,
        "If true, adds burn duration tooltips to furnace fuels").getBoolean(addFurnaceFuelTootip);

    farmContinuousEnergyUse = (float) config.get(sectionFarm, "farmContinuousEnergyUse", farmContinuousEnergyUse,
        "The amount of power used by a farm per tick ").getDouble(farmContinuousEnergyUse);
    farmActionEnergyUse = (float) config.get(sectionFarm, "farmActionEnergyUse", farmActionEnergyUse,
        "The amount of power used by a farm per action (eg plant, till, harvest) ").getDouble(farmActionEnergyUse);
    farmDefaultSize = config.get(sectionFarm, "farmDefaultSize", farmDefaultSize,
        "The number of blocks a farm will extend from its center").getInt(farmDefaultSize);
    farmAxeDamageOnLeafBreak = config.get(sectionFarm, "farmAxeDamageOnLeafBreak", farmAxeDamageOnLeafBreak, 
	"Should axes in a farm take damage when breaking leaves?").getBoolean(farmAxeDamageOnLeafBreak);
    farmToolTakeDamageChance = (float) config.get(sectionFarm, "farmToolTakeDamageChance", farmToolTakeDamageChance, 
	"The chance that a tool in the farm will take damage.").getDouble(farmToolTakeDamageChance);

    combustionGeneratorUseOpaqueModel = config.get(sectionAesthetic, "combustionGeneratorUseOpaqueModel", combustionGeneratorUseOpaqueModel,
        "If set to true: fluid will not be shown in combustion generator tanks. Improves FPS. ").getBoolean(combustionGeneratorUseOpaqueModel);

    magnetPowerUsePerSecondRF = config.get(sectionMagnet, "magnetPowerUsePerTickRF", magnetPowerUsePerSecondRF,
        "The amount of RF power used per tick when the magnet is active").getInt(magnetPowerUsePerSecondRF);
    magnetPowerCapacityRF = config.get(sectionMagnet, "magnetPowerCapacityRF", magnetPowerCapacityRF,
        "Amount of RF power stored in a fully charged magnet").getInt(magnetPowerCapacityRF);
    magnetRange = config.get(sectionMagnet, "magnetRange", magnetRange,
        "Range of the magnet in blocks.").getInt(magnetRange);

    useCombustionGenModel = config.get(sectionAesthetic, "useCombustionGenModel", useCombustionGenModel,
        "If set to true: WIP Combustion Generator model will be used").getBoolean(useCombustionGenModel);

    crafterMjPerCraft = config.get("AutoCrafter Settings", "crafterMjPerCraft", crafterMjPerCraft,
        "MJ used per autocrafted recipe").getInt(crafterMjPerCraft);

    poweredSpawnerMinDelayTicks = config.get(sectionSpawner, "poweredSpawnerMinDelayTicks", poweredSpawnerMinDelayTicks,
        "Min tick delay between spawns for a non-upgraded spawner").getInt(poweredSpawnerMinDelayTicks);    
    poweredSpawnerMaxDelayTicks = config.get(sectionSpawner, "poweredSpawnerMaxDelayTicks", poweredSpawnerMaxDelayTicks,
        "Min tick delay between spawns for a non-upgraded spawner").getInt(poweredSpawnerMaxDelayTicks);    
    poweredSpawnerLevelOnePowerPerTick = (float)config.get(sectionSpawner, "poweredSpawnerLevelOnePowerPerTick", poweredSpawnerLevelOnePowerPerTick,
        "MJ per tick for a level 1 (non-upgraded) spawner").getDouble(poweredSpawnerLevelOnePowerPerTick);
    poweredSpawnerLevelTwoPowerPerTick = (float)config.get(sectionSpawner, "poweredSpawnerLevelTwoPowerPerTick", poweredSpawnerLevelTwoPowerPerTick,
        "MJ per tick for a level 2 spawner").getDouble(poweredSpawnerLevelTwoPowerPerTick);
    poweredSpawnerLevelThreePowerPerTick = (float)config.get(sectionSpawner, "poweredSpawnerLevelThreePowerPerTick", poweredSpawnerLevelThreePowerPerTick,
        "MJ per tick for a level 3 spawner").getDouble(poweredSpawnerLevelThreePowerPerTick);
    poweredSpawnerMaxPlayerDistance = config.get(sectionSpawner, "poweredSpawnerMaxPlayerDistance", poweredSpawnerMaxPlayerDistance,
        "Max distance of the closest player for the spawner to be active. A zero value will remove the player check").getInt(poweredSpawnerMaxPlayerDistance);
    poweredSpawnerUseVanillaSpawChecks = config.get(sectionSpawner, "poweredSpawnerUseVanillaSpawChecks", poweredSpawnerUseVanillaSpawChecks,
        "If true, regular spawn checks such as lighting level and dimension will be made before spawning mobs").getBoolean(poweredSpawnerUseVanillaSpawChecks);
    brokenSpawnerDropChance = (float)config.get(sectionSpawner, "brokenSpawnerDropChance", brokenSpawnerDropChance,
        "The chance a brokne spawner will be dropped when a spawner is broken. 1 = 100% chance, 0 = 0% chance").getDouble(brokenSpawnerDropChance);
    powerSpawnerAddSpawnerCost = config.get(sectionSpawner, "powerSpawnerAddSpawnerCost", powerSpawnerAddSpawnerCost,
        "The number of levels it costs to add a broken spawner").getInt(powerSpawnerAddSpawnerCost);
    
  }

  private Config() {
  }

}
