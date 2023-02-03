package crazypants.enderio.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import com.enderio.core.common.event.ConfigFileChangedEvent;
import com.enderio.core.common.vecmath.VecmathUtil;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.network.PacketHandler;

public final class Config {

    public static class Section {

        public final String name;
        public final String lang;

        public Section(String name, String lang) {
            this.name = name;
            this.lang = lang;
            register();
        }

        private void register() {
            sections.add(this);
        }

        public String lc() {
            return name.toLowerCase(Locale.US);
        }
    }

    public static final List<Section> sections;

    static {
        sections = new ArrayList<Section>();
    }

    public static Configuration config;

    public static final Section sectionPower = new Section("Power Settings", "power");
    public static final Section sectionRecipe = new Section("Recipe Settings", "recipe");
    public static final Section sectionItems = new Section("Item Enabling", "item");
    public static final Section sectionEfficiency = new Section("Efficiency Settings", "efficiency");
    public static final Section sectionPersonal = new Section("Personal Settings", "personal");
    public static final Section sectionAnchor = new Section("Anchor Settings", "anchor");
    public static final Section sectionStaff = new Section("Staff Settings", "staff");
    public static final Section sectionDarkSteel = new Section("Dark Steel", "darksteel");
    public static final Section sectionFarm = new Section("Farm Settings", "farm");
    public static final Section sectionAesthetic = new Section("Aesthetic Settings", "aesthetic");
    public static final Section sectionAdvanced = new Section("Advanced Settings", "advanced");
    public static final Section sectionMagnet = new Section("Magnet Settings", "magnet");
    public static final Section sectionFluid = new Section("Fluid Settings", "fluid");
    public static final Section sectionSpawner = new Section("PoweredSpawner Settings", "spawner");
    public static final Section sectionKiller = new Section("Killer Joe Settings", "killerjoe");
    public static final Section sectionSoulBinder = new Section("Soul Binder Settings", "soulBinder");
    public static final Section sectionAttractor = new Section("Mob Attractor Settings", "attractor");
    public static final Section sectionLootConfig = new Section("Loot Config", "lootconfig");
    public static final Section sectionMobConfig = new Section("Mob Config", "mobconfig");
    public static final Section sectionRailConfig = new Section("Rail", "railconfig");
    public static final Section sectionEnchantments = new Section("Enchantments", "enchantments");
    public static final Section sectionWeather = new Section("Weather", "weather");
    public static final Section sectionTelepad = new Section("Telepad", "telepad");
    public static final Section sectionInventoryPanel = new Section("InventoryPanel", "inventorypanel");
    public static final Section sectionMisc = new Section("Misc", "misc");

    public static final double DEFAULT_CONDUIT_SCALE = 0.6;

    public static boolean reinforcedObsidianEnabled = true;
    public static boolean reinforcedObsidianUseDarkSteelBlocks = false;

    public static boolean useAlternateBinderRecipe = false;

    public static boolean useAlternateTesseractModel = false;

    public static boolean photovoltaicCellEnabled = true;

    public static boolean reservoirEnabled = true;

    public static double conduitScale = DEFAULT_CONDUIT_SCALE;

    public static int numConduitsPerRecipe = 8;

    public static boolean transceiverEnabled = true;
    public static double transceiverEnergyLoss = 0.1;
    public static int transceiverUpkeepCostRF = 10;
    public static int transceiverBucketTransmissionCostRF = 100;
    public static int transceiverMaxIoRF = 20480;
    public static boolean transceiverUseEasyRecipe = false;

    public static File configDirectory;

    public static boolean useHardRecipes = false;
    public static boolean addPeacefulRecipes = false;
    public static boolean allowExternalTickSpeedup = true;
    public static boolean crateSyntheticRecipes = true;

    public static boolean useSteelInChassi = false;

    public static boolean detailedPowerTrackingEnabled = false;

    public static boolean useSneakMouseWheelYetaWrench = true;
    public static boolean useSneakRightClickYetaWrench = false;
    public static int yetaWrenchOverlayMode = 0;

    public static boolean itemConduitUsePhyscialDistance = false;

    public static int stellarEnderFluidConduitExtractRate = 20_000;
    public static int stellarEnderFluidConduitMaxIoRate = 80_000;
    public static int melodicEnderFluidConduitExtractRate = 10_000;
    public static int melodicEnderFluidConduitMaxIoRate = 40_000;
    public static int crystallinePinkSlimeEnderFluidConduitExtractRate = 2_000;
    public static int crystallinePinkSlimeEnderFluidConduitMaxIoRate = 8_000;
    public static int crystallineEnderFluidConduitExtractRate = 1_000;
    public static int crystallineEnderFluidConduitMaxIoRate = 4_000;
    public static int enderFluidConduitExtractRate = 200;
    public static int enderFluidConduitMaxIoRate = 800;
    public static int advancedFluidConduitExtractRate = 100;
    public static int advancedFluidConduitMaxIoRate = 400;
    public static int fluidConduitExtractRate = 50;
    public static int fluidConduitMaxIoRate = 200;

    public static int gasConduitExtractRate = 200;
    public static int gasConduitMaxIoRate = 800;

    public static boolean updateLightingWhenHidingFacades = false;

    public static boolean travelAnchorEnabled = true;
    public static int travelAnchorMaxDistance = 48;
    public static int travelAnchorCooldown = 0;
    public static boolean travelAnchorSneak = true;
    public static boolean travelAnchorSkipWarning = true;

    public static int travelStaffMaxDistance = 128;
    public static float travelStaffPowerPerBlockRF = 250;

    public static int travelStaffMaxBlinkDistance = 16;
    public static int travelStaffBlinkPauseTicks = 10;

    public static boolean travelStaffEnabled = true;
    public static boolean travelStaffBlinkEnabled = true;
    public static boolean travelStaffBlinkThroughSolidBlocksEnabled = true;
    public static boolean travelStaffBlinkThroughClearBlocksEnabled = true;
    public static boolean travelStaffBlinkThroughUnbreakableBlocksEnabled = false;
    public static String[] travelStaffBlinkBlackList = new String[] { "minecraft:bedrock", "Thaumcraft:blockWarded" };
    public static float travelAnchorZoomScale = 0.2f;
    public static boolean travelStaffSearchOptimize = true;

    /** The max distance for travelling to a Travel Anchor. */
    public static int teleportStaffMaxDistance = 2048;
    /** The max distance for travelling to player look. */
    public static int teleportStaffMaxBlinkDistance = 512;
    /**
     * The distance travelled when no block is found within {@link #teleportStaffMaxBlinkDistance}.
     */
    public static int teleportStaffFailedBlinkDistance = 64;

    public static int enderIoRange = 8;
    public static boolean enderIoMeAccessEnabled = true;

    public static double[] darkSteelPowerDamgeAbsorptionRatios = { 0.5, 0.6, 0.7, 0.85, 0.95 };
    public static int darkSteelPowerStorageBase = 100000;
    public static int darkSteelPowerStorageLevelOne = 150000;
    public static int darkSteelPowerStorageLevelTwo = 250000;
    public static int darkSteelPowerStorageLevelThree = 1000000;
    public static int darkSteelPowerStorageLevelFour = 2500000;

    public static float darkSteelSpeedOneWalkModifier = 0.1f;
    public static float darkSteelSpeedTwoWalkMultiplier = 0.2f;
    public static float darkSteelSpeedThreeWalkMultiplier = 0.3f;

    public static float darkSteelSpeedOneSprintModifier = 0.1f;
    public static float darkSteelSpeedTwoSprintMultiplier = 0.3f;
    public static float darkSteelSpeedThreeSprintMultiplier = 0.5f;

    public static int darkSteelSpeedOneCost = 10;
    public static int darkSteelSpeedTwoCost = 15;
    public static int darkSteelSpeedThreeCost = 20;

    public static double darkSteelBootsJumpModifier = 1.5;
    public static int darkSteelJumpOneCost = 10;
    public static int darkSteelJumpTwoCost = 15;
    public static int darkSteelJumpThreeCost = 20;

    public static boolean slotZeroPlacesEight = true;

    public static int darkSteelWalkPowerCost = darkSteelPowerStorageLevelTwo / 3000;
    public static int darkSteelSprintPowerCost = darkSteelWalkPowerCost * 4;
    public static boolean darkSteelDrainPowerFromInventory = false;
    public static int darkSteelBootsJumpPowerCost = 150;
    public static int darkSteelFallDistanceCost = 75;

    public static float darkSteelSwordWitherSkullChance = 0.05f;
    public static float darkSteelSwordWitherSkullLootingModifier = 0.05f;
    public static float darkSteelSwordSkullChance = 0.1f;
    public static float darkSteelSwordSkullLootingModifier = 0.075f;
    public static float vanillaSwordSkullLootingModifier = 0.05f;
    public static float vanillaSwordSkullChance = 0.05f;
    public static float ticCleaverSkullDropChance = 0.1f;
    public static float ticBeheadingSkullModifier = 0.075f;
    public static float fakePlayerSkullChance = 0.5f;

    public static int darkSteelSwordPowerUsePerHit = 750;
    public static double darkSteelSwordEnderPearlDropChance = 1;
    public static double darkSteelSwordEnderPearlDropChancePerLooting = 0.5;

    public static int darkSteelPickEffeciencyObsidian = 50;
    public static int darkSteelPickPowerUseObsidian = 10000;
    public static float darkSteelPickApplyObsidianEffeciencyAtHardess = 40;
    public static int darkSteelPickPowerUsePerDamagePoint = 750;
    public static float darkSteelPickEffeciencyBoostWhenPowered = 2;
    public static boolean darkSteelPickMinesTiCArdite = true;

    public static int darkSteelAxePowerUsePerDamagePoint = 750;
    public static int darkSteelAxePowerUsePerDamagePointMultiHarvest = 1500;
    public static float darkSteelAxeEffeciencyBoostWhenPowered = 2;
    public static float darkSteelAxeSpeedPenaltyMultiHarvest = 4;

    public static int darkSteelShearsDurabilityFactor = 5;
    public static int darkSteelShearsPowerUsePerDamagePoint = 250;
    public static float darkSteelShearsEffeciencyBoostWhenPowered = 2.0f;
    public static int darkSteelShearsBlockAreaBoostWhenPowered = 2;
    public static float darkSteelShearsEntityAreaBoostWhenPowered = 3.0f;

    public static int darkSteelUpgradeVibrantCost = 10;
    public static int darkSteelUpgradePowerOneCost = 10;
    public static int darkSteelUpgradePowerTwoCost = 15;
    public static int darkSteelUpgradePowerThreeCost = 20;
    public static int darkSteelUpgradePowerFourCost = 25;

    public static int darkSteelGliderCost = 10;
    public static double darkSteelGliderHorizontalSpeed = 0.03;
    public static double darkSteelGliderVerticalSpeed = -0.05;
    public static double darkSteelGliderVerticalSpeedSprinting = -0.15;

    public static int darkSteelGogglesOfRevealingCost = 10;

    public static int darkSteelApiaristArmorCost = 10;

    public static int darkSteelSwimCost = 10;

    public static int darkSteelNightVisionCost = 10;

    public static int darkSteelSoundLocatorCost = 10;
    public static int darkSteelSoundLocatorRange = 40;
    public static int darkSteelSoundLocatorLifespan = 40;

    public static int darkSteelTravelCost = 30;
    public static int darkSteelSpoonCost = 10;

    public static int darkSteelSolarOneGen = 10;
    public static int darkSteelSolarOneCost = 15;
    public static int darkSteelSolarTwoGen = 40;
    public static int darkSteelSolarTwoCost = 30;
    public static int darkSteelSolarThreeGen = 160;
    public static int darkSteelSolarThreeCost = 40;

    public static boolean darkSteelSolarChargeOthers = true;

    public static float darkSteelAnvilDamageChance = 0.024f;

    public static float darkSteelLadderSpeedBoost = 0.06f;

    public static int hootchPowerPerCycleRF = 60;
    public static int hootchPowerTotalBurnTime = 6000;
    public static int rocketFuelPowerPerCycleRF = 160;
    public static int rocketFuelPowerTotalBurnTime = 7000;
    public static int fireWaterPowerPerCycleRF = 80;
    public static int fireWaterPowerTotalBurnTime = 15000;
    public static int vatPowerUserPerTickRF = 20;

    public static int maxPhotovoltaicOutputRF = 10;
    public static int maxPhotovoltaicAdvancedOutputRF = 40;
    public static int maxPhotovoltaicVibrantOutputRF = 160;

    public static int zombieGeneratorRfPerTick = 80;
    public static int zombieGeneratorTicksPerBucketFuel = 12000;

    public static int frankenzombieGeneratorRfPerTick = 120;
    public static int frankenzombieGeneratorTicksPerBucketFuel = 12000;

    public static int enderGeneratorRfPerTick = 360;
    public static int enderGeneratorTicksPerBucketFuel = 96000;

    public static double[] zombieGeneratorsEnergyMultipliers = { 1f, 2f, 3f, 5f, 8f, 13f, 21f };
    public static double[] zombieGeneratorsBurnTimeMultipliers = { 0.5f, 1f / 1.5f };

    public static int stirlingGeneratorBaseRfPerTick = 20;
    public static float stirlingGeneratorEnergyMultiplierT1 = 1f;
    public static float stirlingGeneratorEnergyMultiplierT2 = 2f;
    public static float stirlingGeneratorEnergyMultiplierT3 = 4f;
    public static float stirlingGeneratorBurnTimeMultiplierT1 = 1f / 2f;
    public static float stirlingGeneratorBurnTimeMultiplierT2 = 1f / 1.5f;
    public static float stirlingGeneratorBurnTimeMultiplierT3 = 1f / 1.5f;

    public static double[] stirlingGeneratorEnergyMultipliers = { 1f, 2f, 3f, 5f, 8f, 13f, 21f };
    public static double[] stirlingGeneratorBurnTimeMultipliers = { 0.5f, 1f / 1.5f };

    public static boolean combustionGeneratorUseOpaqueModel = true;

    public static boolean addFuelTooltipsToAllFluidContainers = true;
    public static boolean addFurnaceFuelTootip = true;
    public static boolean addDurabilityTootip = true;

    public static int farmContinuousEnergyUseRF = 40;
    public static int farmActionEnergyUseRF = 500;
    public static int farmAxeActionEnergyUseRF = 1000;
    public static int farmBonemealActionEnergyUseRF = 160;
    public static int farmBonemealTryEnergyUseRF = 80;

    public static int farmDefaultSize = 3;
    public static int farmBonusSize = 2;
    public static boolean farmAxeDamageOnLeafBreak = false;
    public static float farmToolTakeDamageChance = 1;
    public static boolean disableFarmNotification = false;
    public static boolean farmEssenceBerriesEnabled = true;
    public static boolean farmManaBeansEnabled = false;
    public static boolean farmHarvestJungleWhenCocoa = false;
    public static String[] hoeStrings = new String[] { "minecraft:wooden_hoe", "minecraft:stone_hoe",
            "minecraft:iron_hoe", "minecraft:diamond_hoe", "minecraft:golden_hoe", "MekanismTools:ObsidianHoe",
            "MekanismTools:LapisLazuliHoe", "MekanismTools:OsmiumHoe", "MekanismTools:BronzeHoe",
            "MekanismTools:GlowstoneHoe", "MekanismTools:SteelHoe", "Steamcraft:hoeBrass", "Steamcraft:hoeGildedGold",
            "Railcraft:tool.steel.hoe", "TConstruct:mattock", "appliedenergistics2:item.ToolCertusQuartzHoe",
            "appliedenergistics2:item.ToolNetherQuartzHoe", "ProjRed|Exploration:projectred.exploration.hoeruby",
            "ProjRed|Exploration:projectred.exploration.hoesapphire",
            "ProjRed|Exploration:projectred.exploration.hoeperidot", "magicalcrops:magicalcrops_AccioHoe",
            "magicalcrops:magicalcrops_CrucioHoe", "magicalcrops:magicalcrops_ImperioHoe",
            // disabled as it is currently not unbreaking as advertised "magicalcrops:magicalcrops_ZivicioHoe",
            "magicalcrops:magicalcropsarmor_AccioHoe", "magicalcrops:magicalcropsarmor_CrucioHoe",
            "magicalcrops:magicalcropsarmor_ImperioHoe", "BiomesOPlenty:hoeAmethyst", "BiomesOPlenty:hoeMud",
            "Eln:Eln.Copper Hoe", "Thaumcraft:ItemHoeThaumium", "Thaumcraft:ItemHoeElemental", "Thaumcraft:ItemHoeVoid",
            "ThermalFoundation:tool.hoeInvar", "ThermalFoundation:tool.hoeCopper", "ThermalFoundation:tool.hoeBronze",
            "ThermalFoundation:tool.hoeSilver", "ThermalFoundation:tool.hoeElectrum", "ThermalFoundation:tool.hoeTin",
            "ThermalFoundation:tool.hoeLead", "ThermalFoundation:tool.hoeNickel", "ThermalFoundation:tool.hoePlatinum",
            "TwilightForest:item.steeleafHoe", "TwilightForest:item.ironwoodHoe", "IC2:itemToolBronzeHoe" };
    public static List<ItemStack> farmHoes = new ArrayList<ItemStack>();
    public static int farmSaplingReserveAmount = 8;

    public static int magnetPowerUsePerSecondRF = 1;
    public static int magnetPowerCapacityRF = 100000;
    public static int magnetRange = 5;
    public static String[] magnetBlacklist = new String[] { "appliedenergistics2:item.ItemCrystalSeed",
            "Botania:livingrock", "Botania:manaTablet" };
    public static int magnetMaxItems = 20;

    public static boolean magnetAllowInMainInventory = false;
    public static boolean magnetAllowInBaublesSlot = true;
    public static boolean magnetAllowDeactivatedInBaublesSlot = false;
    public static boolean magnetAllowPowerExtraction = false;
    public static String magnetBaublesType = "AMULET";

    public static boolean useCombustionGenModel = false;

    public static int crafterRfPerCraft = 2500;

    public static int capacitorBankMaxIoRF = 5000;
    public static int capacitorBankMaxStorageRF = 5000000;

    public static int capacitorBankTierOneMaxIoRF = 1000;
    public static int capacitorBankTierOneMaxStorageRF = 1000000;

    public static int capacitorBankTierTwoMaxIoRF = 5000;
    public static int capacitorBankTierTwoMaxStorageRF = 5000000;

    public static int capacitorBankTierThreeMaxIoRF = 25000;
    public static int capacitorBankTierThreeMaxStorageRF = 25000000;

    public static int poweredSpawnerMinDelayTicks = 200;
    public static int poweredSpawnerMaxDelayTicks = 800;
    public static int poweredSpawnerLevelOnePowerPerTickRF = 160;
    public static int poweredSpawnerLevelTwoPowerPerTickRF = 500;
    public static int poweredSpawnerLevelThreePowerPerTickRF = 1500;
    public static int poweredSpawnerLevelFourPowerPerTickRF = 4500;
    public static int poweredSpawnerLevelFivePowerPerTickRF = 2000;
    public static int poweredSpawnerLevelSixPowerPerTickRF = 1000;
    public static int poweredSpawnerLevelSevenPowerPerTickRF = 500;
    public static int poweredSpawnerLevelEightPowerPerTickRF = 160;
    public static int poweredSpawnerLevelNinePowerPerTickRF = 500;
    public static int poweredSpawnerLevelTenPowerPerTickRF = 1500;
    public static int poweredSpawnerMaxPlayerDistance = 0;
    public static int poweredSpawnerDespawnTimeSeconds = 120;
    public static int poweredSpawnerSpawnCount = 4;
    public static int poweredSpawnerSpawnRange = 4;
    public static int poweredSpawnerMaxNearbyEntities = 6;
    public static int poweredSpawnerMaxSpawnTries = 3;
    public static boolean poweredSpawnerUseVanillaSpawChecks = false;
    public static double brokenSpawnerDropChance = 1;
    public static String[] brokenSpawnerToolBlacklist = new String[] { "RotaryCraft:rotarycraft_item_bedpick" };
    public static int powerSpawnerAddSpawnerCost = 30;

    public static int painterEnergyPerTaskRF = 2000;

    public static int vacuumChestRange = 6;

    public static boolean useModMetals = true;

    public static int wirelessChargerRange = 24;

    public static long nutrientFoodBoostDelay = 400;

    public static int enchanterBaseLevelCost = 4;

    public static boolean machineSoundsEnabled = true;

    public static float machineSoundVolume = 1.0f;

    public static int killerJoeNutrientUsePerAttackMb = 5;
    public static double killerJoeAttackHeight = 2;
    public static double killerJoeAttackWidth = 2;
    public static double killerJoeAttackLength = 4;
    public static double killerJoeHooverXpWidth = 5;
    public static double killerJoeHooverXpLength = 10;
    public static int killerJoeMaxXpLevel = Integer.MAX_VALUE;
    public static boolean killerJoeMustSee = false;
    public static boolean killerPvPoffDisablesSwing = false;
    public static boolean killerPvPoffIsIgnored = false;

    public static boolean allowTileEntitiesAsPaintSource = true;

    public static boolean isGasConduitEnabled = true;
    public static boolean enableMEConduits = true;
    public static boolean enableOCConduits = true;
    public static boolean enableOCConduitsAnimatedTexture = true;

    public static String[] soulVesselBlackList = new String[0];
    public static boolean soulVesselCapturesBosses = false;

    public static int soulBinderLevelOnePowerPerTickRF = 500;
    public static int soulBinderLevelTwoPowerPerTickRF = 1000;
    public static int soulBinderLevelThreePowerPerTickRF = 2000;
    public static int soulBinderLevelFourPowerPerTickRF = 4000;
    public static int soulBinderLevelFivePowerPerTickRF = 8000;
    public static int soulBinderLevelSixPowerPerTickRF = 16000;
    public static int soulBinderLevelSevenPowerPerTickRF = 32000;
    public static int soulBinderLevelEightPowerPerTickRF = 500;
    public static int soulBinderLevelNinePowerPerTickRF = 1000;
    public static int soulBinderLevelTenPowerPerTickRF = 2000;
    public static int soulBinderBrokenSpawnerRF = 2500000;
    public static int soulBinderBrokenSpawnerLevels = 15;
    public static int soulBinderReanimationRF = 100000;
    public static int soulBinderReanimationLevels = 10;
    public static int soulBinderEnderCystalRF = 100000;
    public static int soulBinderEnderCystalLevels = 10;
    public static int soulBinderPrecientCystalRF = 100000;
    public static int soulBinderPrecientCystalLevels = 10;
    public static int soulBinderAttractorCystalRF = 100000;
    public static int soulBinderAttractorCystalLevels = 10;
    public static int soulBinderEnderRailRF = 100000;
    public static int soulBinderEnderRailLevels = 10;
    public static int soulBinderMaxXpLevel = 40;

    public static boolean powerConduitCanDifferentTiersConnect = false;
    public static int powerConduitTierOneRF = 640;
    public static int powerConduitTierTwoRF = 5120;
    public static int powerConduitTierThreeRF = 20480;
    public static int[] powerConduitEndergyTiers = { 20, 40, 80, 160, 320, 1280, 2560, 10240, 40960, 81920, 327680,
            2000000000 };
    public static boolean powerConduitOutputMJ = true;

    public static int sliceAndSpliceLevelOnePowerPerTickRF = 80;
    public static int sliceAndSpliceLevelTwoPowerPerTickRF = 160;
    public static int sliceAndSpliceLevelThreePowerPerTickRF = 320;
    public static int sliceAndSpliceLevelFourPowerPerTickRF = 640;
    public static int sliceAndSpliceLevelFivePowerPerTickRF = 1280;
    public static int sliceAndSpliceLevelSixPowerPerTickRF = 2560;
    public static int sliceAndSpliceLevelSevenPowerPerTickRF = 5120;
    public static int sliceAndSpliceLevelEightPowerPerTickRF = 80;
    public static int sliceAndSpliceLevelNinePowerPerTickRF = 160;
    public static int sliceAndSpliceLevelTenPowerPerTickRF = 320;

    public static boolean soulBinderRequiresEndermanSkull = true;

    public static int attractorRangeLevelOne = 16;
    public static int attractorPowerPerTickLevelOne = 20;
    public static int attractorRangeLevelTwo = 32;
    public static int attractorPowerPerTickLevelTwo = 40;
    public static int attractorRangeLevelThree = 64;
    public static int attractorPowerPerTickLevelThree = 80;

    public static int spawnGuardRangeLevelOne = 64;
    public static int spawnGuardPowerPerTickLevelOne = 80;
    public static int spawnGuardRangeLevelTwo = 96;
    public static int spawnGuardPowerPerTickLevelTwo = 300;
    public static int spawnGuardRangeLevelThree = 160;
    public static int spawnGuardPowerPerTickLevelThree = 800;
    public static boolean spawnGuardStopAllSlimesDebug = false;
    public static boolean spawnGuardStopAllSquidSpawning = false;

    public static int weatherObeliskClearFluid = 2000;
    public static int weatherObeliskRainFluid = 500;
    public static int weatherObeliskThunderFluid = 1000;

    // Loot Defaults
    public static boolean lootDarkSteel = true;
    public static boolean lootItemConduitProbe = true;
    public static boolean lootQuartz = true;
    public static boolean lootNetherWart = true;
    public static boolean lootEnderPearl = true;
    public static boolean lootElectricSteel = true;
    public static boolean lootRedstoneAlloy = true;
    public static boolean lootPhasedIron = true;
    public static boolean lootPhasedGold = true;
    public static boolean lootTravelStaff = true;
    public static boolean lootTheEnder = true;
    public static boolean lootDarkSteelBoots = true;

    public static boolean dumpMobNames = false;

    public static boolean enderRailEnabled = true;
    public static int enderRailPowerRequireCrossDimensions = 10000;
    public static int enderRailPowerRequiredPerBlock = 10;
    public static boolean enderRailCapSameDimensionPowerAtCrossDimensionCost = true;
    public static int enderRailTicksBeforeForceSpawningLinkedCarts = 60;
    public static boolean enderRailTeleportPlayers = false;

    public static int xpObeliskMaxXpLevel = Integer.MAX_VALUE;
    public static String xpJuiceName = "xpjuice";

    public static boolean clearGlassSameTexture = false;
    public static boolean clearGlassConnectToFusedQuartz = false;

    public static int enchantmentSoulBoundId = -1;
    public static int enchantmentSoulBoundWeight = 1;
    public static boolean enchantmentSoulBoundEnabled = true;

    public static boolean replaceWitherSkeletons = true;

    public static boolean enableWaterFromBottles = true;

    public static boolean telepadLockDimension = true;
    public static boolean telepadLockCoords = true;
    public static int telepadPowerCoefficient = 100000;
    public static int telepadPowerInterdimensional = 100000;
    public static int telepadPowerPerTickRF = 1000;
    public static int telepadPowerStorageRF = 100000;

    public static boolean inventoryPanelFree = false;;
    public static float inventoryPanelPowerPerMB = 800.0f;
    public static float inventoryPanelScanCostPerSlot = 0.1f;
    public static float inventoryPanelExtractCostPerItem = 12.0f;
    public static float inventoryPanelExtractCostPerOperation = 32.0f;

    public static void load(FMLPreInitializationEvent event) {
        PacketHandler.INSTANCE
                .registerMessage(PacketConfigSync.class, PacketConfigSync.class, PacketHandler.nextID(), Side.CLIENT);

        FMLCommonHandler.instance().bus().register(new Config());
        configDirectory = new File(event.getModConfigurationDirectory(), EnderIO.DOMAIN);
        if (!configDirectory.exists()) {
            configDirectory.mkdir();
        }

        File configFile = new File(configDirectory, "EnderIO.cfg");
        config = new Configuration(configFile);
        syncConfig(false);
    }

    public static void syncConfig(boolean load) {
        try {
            if (load) {
                config.load();
            }
            Config.processConfig(config);
        } catch (Exception e) {
            Log.error("EnderIO has a problem loading it's configuration");
            e.printStackTrace();
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event) {
        if (event.modID.equals(EnderIO.MODID)) {
            Log.info("Updating config...");
            syncConfig(false);
            init();
            postInit();
        }
    }

    @SubscribeEvent
    public void onConfigFileChanged(ConfigFileChangedEvent event) {
        if (event.modID.equals(EnderIO.MODID)) {
            Log.info("Updating config...");
            syncConfig(true);
            event.setSuccessful();
            init();
            postInit();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggon(PlayerLoggedInEvent evt) {
        PacketHandler.INSTANCE.sendTo(new PacketConfigSync(), (EntityPlayerMP) evt.player);
    }

    public static void processConfig(Configuration config) {

        capacitorBankMaxIoRF = config.get(
                sectionPower.name,
                "capacitorBankMaxIoRF",
                capacitorBankMaxIoRF,
                "The maximum IO for a single capacitor in RF/t").getInt(capacitorBankMaxIoRF);
        capacitorBankMaxStorageRF = config.get(
                sectionPower.name,
                "capacitorBankMaxStorageRF",
                capacitorBankMaxStorageRF,
                "The maximum storage for a single capacitor in RF").getInt(capacitorBankMaxStorageRF);

        capacitorBankTierOneMaxIoRF = config.get(
                sectionPower.name,
                "capacitorBankTierOneMaxIoRF",
                capacitorBankTierOneMaxIoRF,
                "The maximum IO for a single tier one capacitor in RF/t").getInt(capacitorBankTierOneMaxIoRF);
        capacitorBankTierOneMaxStorageRF = config
                .get(
                        sectionPower.name,
                        "capacitorBankTierOneMaxStorageRF",
                        capacitorBankTierOneMaxStorageRF,
                        "The maximum storage for a single tier one capacitor in RF")
                .getInt(capacitorBankTierOneMaxStorageRF);

        capacitorBankTierTwoMaxIoRF = config.get(
                sectionPower.name,
                "capacitorBankTierTwoMaxIoRF",
                capacitorBankTierTwoMaxIoRF,
                "The maximum IO for a single tier two capacitor in RF/t").getInt(capacitorBankTierTwoMaxIoRF);
        capacitorBankTierTwoMaxStorageRF = config
                .get(
                        sectionPower.name,
                        "capacitorBankTierTwoMaxStorageRF",
                        capacitorBankTierTwoMaxStorageRF,
                        "The maximum storage for a single tier two capacitor in RF")
                .getInt(capacitorBankTierTwoMaxStorageRF);

        capacitorBankTierThreeMaxIoRF = config
                .get(
                        sectionPower.name,
                        "capacitorBankTierThreeMaxIoRF",
                        capacitorBankTierThreeMaxIoRF,
                        "The maximum IO for a single tier three capacitor in RF/t")
                .getInt(capacitorBankTierThreeMaxIoRF);
        capacitorBankTierThreeMaxStorageRF = config
                .get(
                        sectionPower.name,
                        "capacitorBankTierThreeMaxStorageRF",
                        capacitorBankTierThreeMaxStorageRF,
                        "The maximum storage for a single tier three capacitor in RF")
                .getInt(capacitorBankTierThreeMaxStorageRF);

        powerConduitTierOneRF = config.get(
                sectionPower.name,
                "powerConduitTierOneRF",
                powerConduitTierOneRF,
                "The maximum IO for the tier 1 power conduit").getInt(powerConduitTierOneRF);
        powerConduitTierTwoRF = config.get(
                sectionPower.name,
                "powerConduitTierTwoRF",
                powerConduitTierTwoRF,
                "The maximum IO for the tier 2 power conduit").getInt(powerConduitTierTwoRF);
        powerConduitTierThreeRF = config.get(
                sectionPower.name,
                "powerConduitTierThreeRF",
                powerConduitTierThreeRF,
                "The maximum IO for the tier 3 power conduit").getInt(powerConduitTierThreeRF);
        powerConduitEndergyTiers = config.get(
                sectionPower.name,
                "powerConduitTiersEndergy",
                powerConduitEndergyTiers,
                "The maximum IO for the endergy power conduit").getIntList();

        powerConduitCanDifferentTiersConnect = config.getBoolean(
                "powerConduitCanDifferentTiersConnect",
                sectionPower.name,
                powerConduitCanDifferentTiersConnect,
                "If set to false power conduits of different tiers cannot be connected. in this case a block such as a cap. bank is needed to bridge different tiered networks");
        powerConduitOutputMJ = config.getBoolean(
                "powerConduitOutputMJ",
                sectionPower.name,
                powerConduitOutputMJ,
                "When set to true power conduits will output MJ if RF is not supported");

        painterEnergyPerTaskRF = config.get(
                sectionPower.name,
                "painterEnergyPerTaskRF",
                painterEnergyPerTaskRF,
                "The total amount of RF required to paint one block").getInt(painterEnergyPerTaskRF);

        useHardRecipes = config.get(
                sectionRecipe.name,
                "useHardRecipes",
                useHardRecipes,
                "When enabled machines cost significantly more.").getBoolean(useHardRecipes);
        addPeacefulRecipes = config
                .get(
                        sectionRecipe.name,
                        "addPeacefulRecipes",
                        addPeacefulRecipes,
                        "When enabled peaceful recipes are added for soulbinder based crafting components.")
                .getBoolean(addPeacefulRecipes);
        soulBinderRequiresEndermanSkull = config.getBoolean(
                "soulBinderRequiresEndermanSkull",
                sectionRecipe.name,
                soulBinderRequiresEndermanSkull,
                "When true the Soul Binder requires an Enderman Skull to craft");
        allowTileEntitiesAsPaintSource = config
                .get(
                        sectionRecipe.name,
                        "allowTileEntitiesAsPaintSource",
                        allowTileEntitiesAsPaintSource,
                        "When enabled blocks with tile entities (e.g. machines) can be used as paint targets.")
                .getBoolean(allowTileEntitiesAsPaintSource);
        useSteelInChassi = config
                .get(
                        sectionRecipe.name,
                        "useSteelInChassi",
                        useSteelInChassi,
                        "When enabled machine chassis will require steel instead of iron.")
                .getBoolean(useSteelInChassi);
        numConduitsPerRecipe = config.get(
                sectionRecipe.name,
                "numConduitsPerRecipe",
                numConduitsPerRecipe,
                "The number of conduits crafted per recipe.").getInt(numConduitsPerRecipe);
        transceiverUseEasyRecipe = config.get(
                sectionRecipe.name,
                "transceiverUseEasyRecipe",
                transceiverUseEasyRecipe,
                "When enabled the dim trans. will use a cheaper recipe").getBoolean(useHardRecipes);
        crateSyntheticRecipes = config.get(
                sectionRecipe.name,
                "crateSyntheticRecipes",
                crateSyntheticRecipes,
                "Automatically create alloy smelter recipes with double and triple inputs and different slot allocations (1+1+1, 2+1, 1+2, 3 and 2) for single-input recipes.")
                .getBoolean(crateSyntheticRecipes);

        allowExternalTickSpeedup = config.get(
                sectionMisc.name,
                "allowExternalTickSpeedup",
                allowExternalTickSpeedup,
                "Allows machines to run faster if another mod speeds up the tickrate. Running at higher tickrates is "
                        + "unsupported. Disable this if you run into any kind of problem.")
                .getBoolean(allowExternalTickSpeedup);

        enchanterBaseLevelCost = config.get(
                sectionRecipe.name,
                "enchanterBaseLevelCost",
                enchanterBaseLevelCost,
                "Base level cost added to all recipes in the enchanter.").getInt(enchanterBaseLevelCost);

        photovoltaicCellEnabled = config
                .get(
                        sectionItems.name,
                        "photovoltaicCellEnabled",
                        photovoltaicCellEnabled,
                        "If set to false: Photovoltaic Cells will not be craftable.")
                .getBoolean(photovoltaicCellEnabled);

        reservoirEnabled = config.get(
                sectionItems.name,
                "reservoirEnabled",
                reservoirEnabled,
                "If set to false reservoirs will not be craftable.").getBoolean(reservoirEnabled);

        transceiverEnabled = config
                .get(
                        sectionItems.name,
                        "transceiverEnabled",
                        transceiverEnabled,
                        "If set to false: Dimensional Transceivers will not be craftable.")
                .getBoolean(transceiverEnabled);

        maxPhotovoltaicOutputRF = config.get(
                sectionPower.name,
                "maxPhotovoltaicOutputRF",
                maxPhotovoltaicOutputRF,
                "Maximum output in RF/t of the Photovoltaic Panels.").getInt(maxPhotovoltaicOutputRF);
        maxPhotovoltaicAdvancedOutputRF = config
                .get(
                        sectionPower.name,
                        "maxPhotovoltaicAdvancedOutputRF",
                        maxPhotovoltaicAdvancedOutputRF,
                        "Maximum output in RF/t of the Advanced Photovoltaic Panels.")
                .getInt(maxPhotovoltaicAdvancedOutputRF);
        maxPhotovoltaicVibrantOutputRF = config
                .get(
                        sectionPower.name,
                        "maxPhotovoltaicVibrantOutputRF",
                        maxPhotovoltaicVibrantOutputRF,
                        "Maximum output in RF/t of the Vibrant Photovoltaic Panels.")
                .getInt(maxPhotovoltaicVibrantOutputRF);

        useAlternateBinderRecipe = config
                .get(
                        sectionRecipe.name,
                        "useAlternateBinderRecipe",
                        false,
                        "Create conduit binder in crafting table instead of furnace")
                .getBoolean(useAlternateBinderRecipe);

        conduitScale = config
                .get(
                        sectionAesthetic.name,
                        "conduitScale",
                        DEFAULT_CONDUIT_SCALE,
                        "Valid values are between 0-1, smallest conduits at 0, largest at 1.\n"
                                + "In SMP, all clients must be using the same value as the server.")
                .getDouble(DEFAULT_CONDUIT_SCALE);
        conduitScale = VecmathUtil.clamp(conduitScale, 0, 1);

        wirelessChargerRange = config.get(
                sectionEfficiency.name,
                "wirelessChargerRange",
                wirelessChargerRange,
                "The range of the wireless charger").getInt(wirelessChargerRange);

        fluidConduitExtractRate = config
                .get(
                        sectionEfficiency.name,
                        "fluidConduitExtractRate",
                        fluidConduitExtractRate,
                        "Number of liters per tick extracted by a fluid conduits auto extracting")
                .getInt(fluidConduitExtractRate);

        fluidConduitMaxIoRate = config
                .get(
                        sectionEfficiency.name,
                        "fluidConduitMaxIoRate",
                        fluidConduitMaxIoRate,
                        "Number of liters per tick that can pass through a single connection to a fluid conduit.")
                .getInt(fluidConduitMaxIoRate);

        advancedFluidConduitExtractRate = config
                .get(
                        sectionEfficiency.name,
                        "advancedFluidConduitExtractRate",
                        advancedFluidConduitExtractRate,
                        "Number of liters per tick extracted by pressurized fluid conduits auto extracting")
                .getInt(advancedFluidConduitExtractRate);

        advancedFluidConduitMaxIoRate = config.get(
                sectionEfficiency.name,
                "advancedFluidConduitMaxIoRate",
                advancedFluidConduitMaxIoRate,
                "Number of liters per tick that can pass through a single connection to an pressurized fluid conduit.")
                .getInt(advancedFluidConduitMaxIoRate);

        enderFluidConduitExtractRate = config
                .get(
                        sectionEfficiency.name,
                        "enderFluidConduitExtractRate",
                        enderFluidConduitExtractRate,
                        "Number of liters per tick extracted by ender fluid conduits auto extracting")
                .getInt(enderFluidConduitExtractRate);

        enderFluidConduitMaxIoRate = config.get(
                sectionEfficiency.name,
                "enderFluidConduitMaxIoRate",
                enderFluidConduitMaxIoRate,
                "Number of liters per tick that can pass through a single connection to an ender fluid conduit.")
                .getInt(enderFluidConduitMaxIoRate);

        crystallineEnderFluidConduitExtractRate = config
                .get(
                        sectionEfficiency.name,
                        "crystallineEnderFluidConduitExtractRate",
                        crystallineEnderFluidConduitExtractRate,
                        "Number of liters per tick extracted by crystalline ender fluid conduits auto extracting")
                .getInt(crystallineEnderFluidConduitExtractRate);

        crystallineEnderFluidConduitMaxIoRate = config.get(
                sectionEfficiency.name,
                "crystallineEnderFluidConduitMaxIoRate",
                crystallineEnderFluidConduitMaxIoRate,
                "Number of liters per tick that can pass through a single connection to a crystalline ender fluid conduit.")
                .getInt(crystallineEnderFluidConduitMaxIoRate);

        crystallinePinkSlimeEnderFluidConduitExtractRate = config.get(
                sectionEfficiency.name,
                "crystallinePinkSlimeEnderFluidConduitExtractRate",
                crystallinePinkSlimeEnderFluidConduitExtractRate,
                "Number of liters per tick extracted by crystalline pink slime ender fluid conduits auto extracting")
                .getInt(crystallinePinkSlimeEnderFluidConduitExtractRate);

        crystallinePinkSlimeEnderFluidConduitMaxIoRate = config.get(
                sectionEfficiency.name,
                "crystallinePinkSlimeEnderFluidConduitMaxIoRate",
                crystallinePinkSlimeEnderFluidConduitMaxIoRate,
                "Number of liters per tick that can pass through a single connection to a crystalline pink slime ender fluid conduit.")
                .getInt(crystallinePinkSlimeEnderFluidConduitMaxIoRate);

        melodicEnderFluidConduitExtractRate = config
                .get(
                        sectionEfficiency.name,
                        "melodicEnderFluidConduitExtractRate",
                        melodicEnderFluidConduitExtractRate,
                        "Number of liters per tick extracted by melodic ender fluid conduits auto extracting")
                .getInt(melodicEnderFluidConduitExtractRate);

        melodicEnderFluidConduitMaxIoRate = config.get(
                sectionEfficiency.name,
                "melodicEnderFluidConduitMaxIoRate",
                melodicEnderFluidConduitMaxIoRate,
                "Number of liters per tick that can pass through a single connection to a melodic ender fluid conduit.")
                .getInt(melodicEnderFluidConduitMaxIoRate);

        stellarEnderFluidConduitExtractRate = config
                .get(
                        sectionEfficiency.name,
                        "stellarEnderFluidConduitExtractRate",
                        stellarEnderFluidConduitExtractRate,
                        "Number of liters per tick extracted by stellar ender fluid conduits auto extracting")
                .getInt(stellarEnderFluidConduitExtractRate);

        stellarEnderFluidConduitMaxIoRate = config.get(
                sectionEfficiency.name,
                "stellarEnderFluidConduitMaxIoRate",
                stellarEnderFluidConduitMaxIoRate,
                "Number of liters per tick that can pass through a single connection to a stellar ender fluid conduit.")
                .getInt(stellarEnderFluidConduitMaxIoRate);

        gasConduitExtractRate = config
                .get(
                        sectionEfficiency.name,
                        "gasConduitExtractRate",
                        gasConduitExtractRate,
                        "Amount of gas per tick extracted by gas conduits auto extracting")
                .getInt(gasConduitExtractRate);

        gasConduitMaxIoRate = config
                .get(
                        sectionEfficiency.name,
                        "gasConduitMaxIoRate",
                        gasConduitMaxIoRate,
                        "Amount of gas per tick that can pass through a single connection to a gas conduit.")
                .getInt(gasConduitMaxIoRate);

        useAlternateTesseractModel = config.get(
                sectionAesthetic.name,
                "useAlternateTransceiverModel",
                useAlternateTesseractModel,
                "Use TheKazador's alternative model for the Dimensional Transceiver").getBoolean(false);
        transceiverEnergyLoss = config.get(
                sectionPower.name,
                "transceiverEnergyLoss",
                transceiverEnergyLoss,
                "Amount of energy lost when transferred by Dimensional Transceiver; 0 is no loss, 1 is 100% loss")
                .getDouble(transceiverEnergyLoss);
        transceiverUpkeepCostRF = config
                .get(
                        sectionPower.name,
                        "transceiverUpkeepCostRF",
                        transceiverUpkeepCostRF,
                        "Number of RF/t required to keep a Dimensional Transceiver connection open")
                .getInt(transceiverUpkeepCostRF);
        transceiverMaxIoRF = config.get(
                sectionPower.name,
                "transceiverMaxIoRF",
                transceiverMaxIoRF,
                "Maximum RF/t sent and received by a Dimensional Transceiver per tick. Input and output limits are not cumulative")
                .getInt(transceiverMaxIoRF);
        transceiverBucketTransmissionCostRF = config
                .get(
                        sectionEfficiency.name,
                        "transceiverBucketTransmissionCostRF",
                        transceiverBucketTransmissionCostRF,
                        "The cost in RF of transporting a bucket of fluid via a Dimensional Transceiver.")
                .getInt(transceiverBucketTransmissionCostRF);

        vatPowerUserPerTickRF = config.get(
                sectionPower.name,
                "vatPowerUserPerTickRF",
                vatPowerUserPerTickRF,
                "Power use (RF/t) used by the vat.").getInt(vatPowerUserPerTickRF);

        detailedPowerTrackingEnabled = config.get(
                sectionAdvanced.name,
                "perInterfacePowerTrackingEnabled",
                detailedPowerTrackingEnabled,
                "Enable per tick sampling on individual power inputs and outputs. This allows slightly more detailed messages from the RF Reader but has a negative impact on server performance.")
                .getBoolean(detailedPowerTrackingEnabled);

        useSneakMouseWheelYetaWrench = config.get(
                sectionPersonal.name,
                "useSneakMouseWheelYetaWrench",
                useSneakMouseWheelYetaWrench,
                "If true, shift-mouse wheel will change the conduit display mode when the YetaWrench is equipped.")
                .getBoolean(useSneakMouseWheelYetaWrench);

        useSneakRightClickYetaWrench = config.get(
                sectionPersonal.name,
                "useSneakRightClickYetaWrench",
                useSneakRightClickYetaWrench,
                "If true, shift-clicking the YetaWrench on a null or non wrenchable object will change the conduit display mode.")
                .getBoolean(useSneakRightClickYetaWrench);

        yetaWrenchOverlayMode = config.getInt(
                "yetaWrenchOverlayMode",
                sectionPersonal.name,
                yetaWrenchOverlayMode,
                0,
                2,
                "What kind of overlay to use when holding the yeta wrench\n\n"
                        + "0 - Sideways scrolling in ceter of screen\n"
                        + "1 - Vertical icon bar in bottom right\n"
                        + "2 - Old-style group of icons in bottom right");

        machineSoundsEnabled = config.get(
                sectionPersonal.name,
                "useMachineSounds",
                machineSoundsEnabled,
                "If true, machines will make sounds.").getBoolean(machineSoundsEnabled);

        machineSoundVolume = (float) config
                .get(sectionPersonal.name, "machineSoundVolume", machineSoundVolume, "Volume of machine sounds.")
                .getDouble(machineSoundVolume);

        itemConduitUsePhyscialDistance = config.get(
                sectionEfficiency.name,
                "itemConduitUsePhyscialDistance",
                itemConduitUsePhyscialDistance,
                "If true, "
                        + "'line of sight' distance rather than conduit path distance is used to calculate priorities.")
                .getBoolean(itemConduitUsePhyscialDistance);

        vacuumChestRange = config
                .get(sectionEfficiency.name, "vacumChestRange", vacuumChestRange, "The range of the vacuum chest")
                .getInt(vacuumChestRange);

        reinforcedObsidianEnabled = config
                .get(
                        sectionItems.name,
                        "reinforcedObsidianEnabled",
                        reinforcedObsidianEnabled,
                        "When set to false reinforced obsidian is not craftable.")
                .getBoolean(reinforcedObsidianEnabled);
        reinforcedObsidianUseDarkSteelBlocks = config.get(
                sectionRecipe.name,
                "reinforcedObsidianUseDarkSteelBlocks",
                reinforcedObsidianUseDarkSteelBlocks,
                "When set to true four dark steel blocks are required instead of ingots when making reinforced obsidian.")
                .getBoolean(reinforcedObsidianUseDarkSteelBlocks);

        travelAnchorEnabled = config.get(
                sectionItems.name,
                "travelAnchorEnabled",
                travelAnchorEnabled,
                "When set to false: the travel anchor will not be craftable.").getBoolean(travelAnchorEnabled);

        travelAnchorMaxDistance = config
                .get(
                        sectionAnchor.name,
                        "travelAnchorMaxDistance",
                        travelAnchorMaxDistance,
                        "Maximum number of blocks that can be traveled from one travel anchor to another.")
                .getInt(travelAnchorMaxDistance);

        travelAnchorCooldown = config.get(
                sectionAnchor.name,
                "travelAnchorCooldown",
                travelAnchorCooldown,
                "Number of ticks cooldown between activations (1 sec = 20 ticks)").getInt(travelAnchorCooldown);

        travelAnchorSneak = config.get(
                sectionAnchor.name,
                "travelAnchorSneak",
                travelAnchorSneak,
                "Add sneak as an option to activate travel anchors").getBoolean(travelAnchorSneak);

        travelAnchorSkipWarning = config
                .get(
                        sectionAnchor.name,
                        "travelAnchorSkipWarning",
                        travelAnchorSkipWarning,
                        "Travel Anchors send a chat warning when skipping inaccessible anchors")
                .getBoolean(travelAnchorSkipWarning);

        travelStaffMaxDistance = config
                .get(
                        sectionStaff.name,
                        "travelStaffMaxDistance",
                        travelStaffMaxDistance,
                        "Maximum number of blocks that can be traveled using the Staff of Traveling.")
                .getInt(travelStaffMaxDistance);
        travelStaffPowerPerBlockRF = (float) config
                .get(
                        sectionStaff.name,
                        "travelStaffPowerPerBlockRF",
                        travelStaffPowerPerBlockRF,
                        "Number of RF required per block traveled using the Staff of Traveling.")
                .getDouble(travelStaffPowerPerBlockRF);

        travelStaffMaxBlinkDistance = config
                .get(
                        sectionStaff.name,
                        "travelStaffMaxBlinkDistance",
                        travelStaffMaxBlinkDistance,
                        "Max number of blocks teleported when shift clicking the staff.")
                .getInt(travelStaffMaxBlinkDistance);

        travelStaffBlinkPauseTicks = config.get(
                sectionStaff.name,
                "travelStaffBlinkPauseTicks",
                travelStaffBlinkPauseTicks,
                "Minimum number of ticks between 'blinks'. Values of 10 or less allow a limited sort of flight.")
                .getInt(travelStaffBlinkPauseTicks);

        travelStaffEnabled = config.get(
                sectionStaff.name,
                "travelStaffEnabled",
                travelAnchorEnabled,
                "If set to false: the travel staff will not be craftable.").getBoolean(travelStaffEnabled);
        travelStaffBlinkEnabled = config
                .get(
                        sectionStaff.name,
                        "travelStaffBlinkEnabled",
                        travelStaffBlinkEnabled,
                        "If set to false: the travel staff can not be used to shift-right click teleport, or blink.")
                .getBoolean(travelStaffBlinkEnabled);
        travelStaffBlinkThroughSolidBlocksEnabled = config
                .get(
                        sectionStaff.name,
                        "travelStaffBlinkThroughSolidBlocksEnabled",
                        travelStaffBlinkThroughSolidBlocksEnabled,
                        "If set to false: the travel staff can be used to blink through any block.")
                .getBoolean(travelStaffBlinkThroughSolidBlocksEnabled);
        travelStaffBlinkThroughClearBlocksEnabled = config.get(
                sectionItems.name,
                "travelStaffBlinkThroughClearBlocksEnabled",
                travelStaffBlinkThroughClearBlocksEnabled,
                "If travelStaffBlinkThroughSolidBlocksEnabled is set to false and this is true: the travel "
                        + "staff can only be used to blink through transparent or partial blocks (e.g. torches). "
                        + "If both are false: only air blocks may be teleported through.")
                .getBoolean(travelStaffBlinkThroughClearBlocksEnabled);
        travelStaffBlinkThroughUnbreakableBlocksEnabled = config.get(
                sectionItems.name,
                "travelStaffBlinkThroughUnbreakableBlocksEnabled",
                travelStaffBlinkThroughUnbreakableBlocksEnabled,
                "Allows the travel staff to blink through unbreakable blocks such as warded blocks and bedrock.")
                .getBoolean();
        travelStaffBlinkBlackList = config.getStringList(
                "travelStaffBlinkBlackList",
                sectionStaff.name,
                travelStaffBlinkBlackList,
                "Lists the blocks that cannot be teleported through in the form 'modID:blockName'");
        travelAnchorZoomScale = config.getFloat(
                "travelAnchorZoomScale",
                sectionStaff.name,
                travelAnchorZoomScale,
                0,
                1,
                "Set the max zoomed size of a travel anchor as an aprox. percentage of screen height");
        travelStaffSearchOptimize = config.get(
                sectionStaff.name,
                "travelStaffSearchOptimize",
                travelStaffSearchOptimize,
                "If set to true: blinking by travel staff has reduced search branch. "
                        + "You can now teleport onto the roof. "
                        + "This config is experimental, so if you encounter any strange behavior, please report to GTNH developer.")
                .getBoolean(travelStaffSearchOptimize);

        teleportStaffMaxDistance = config
                .get(
                        sectionStaff.name,
                        "teleportStaffMaxDistance",
                        teleportStaffMaxDistance,
                        "Max number of blocks teleported when travelling to a Travel Anchor.")
                .getInt(teleportStaffMaxDistance);

        teleportStaffMaxBlinkDistance = config
                .get(
                        sectionStaff.name,
                        "teleportStaffMaxBlinkDistance",
                        teleportStaffMaxBlinkDistance,
                        "Max number of blocks teleported when travelling to player look.")
                .getInt(teleportStaffMaxBlinkDistance);

        teleportStaffFailedBlinkDistance = config
                .get(
                        sectionStaff.name,
                        "teleportStaffFailedBlinkDistance",
                        teleportStaffFailedBlinkDistance,
                        "Number of blocks teleported when no block is being looked at.")
                .getInt(teleportStaffFailedBlinkDistance);

        enderIoRange = config.get(
                sectionEfficiency.name,
                "enderIoRange",
                enderIoRange,
                "Range accessible (in blocks) when using the Ender IO.").getInt(enderIoRange);

        enderIoMeAccessEnabled = config
                .get(
                        sectionPersonal.name,
                        "enderIoMeAccessEnabled",
                        enderIoMeAccessEnabled,
                        "If false: you will not be able to access a ME access or crafting terminal using the Ender IO.")
                .getBoolean(enderIoMeAccessEnabled);

        updateLightingWhenHidingFacades = config.get(
                sectionEfficiency.name,
                "updateLightingWhenHidingFacades",
                updateLightingWhenHidingFacades,
                "When true: correct lighting is recalculated (client side) for conduit bundles when transitioning to"
                        + " from being hidden behind a facade. This produces "
                        + "better quality rendering but can result in frame stutters when switching to/from a wrench.")
                .getBoolean(updateLightingWhenHidingFacades);

        darkSteelPowerDamgeAbsorptionRatios = config.get(
                sectionDarkSteel.name,
                "darkSteelPowerDamgeAbsorptionRatios",
                darkSteelPowerDamgeAbsorptionRatios,
                "A list of the amount of durability damage absorbed when items are powered. In order of upgrade level. 1=100% so items take no durability damage when powered.")
                .getDoubleList();
        darkSteelPowerStorageBase = config.get(
                sectionDarkSteel.name,
                "darkSteelPowerStorageBase",
                darkSteelPowerStorageBase,
                "Base amount of power stored by dark steel items.").getInt(darkSteelPowerStorageBase);
        darkSteelPowerStorageLevelOne = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelPowerStorageLevelOne",
                        darkSteelPowerStorageLevelOne,
                        "Amount of power stored by dark steel items with a level 1 upgrade.")
                .getInt(darkSteelPowerStorageLevelOne);
        darkSteelPowerStorageLevelTwo = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelPowerStorageLevelTwo",
                        darkSteelPowerStorageLevelTwo,
                        "Amount of power stored by dark steel items with a level 2 upgrade.")
                .getInt(darkSteelPowerStorageLevelTwo);
        darkSteelPowerStorageLevelThree = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelPowerStorageLevelThree",
                        darkSteelPowerStorageLevelThree,
                        "Amount of power stored by dark steel items with a level 3 upgrade.")
                .getInt(darkSteelPowerStorageLevelThree);
        darkSteelPowerStorageLevelFour = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelPowerStorageLevelFour",
                        darkSteelPowerStorageLevelFour,
                        "Amount of power stored by dark steel items with a level 4 upgrade.")
                .getInt(darkSteelPowerStorageLevelFour);

        darkSteelUpgradeVibrantCost = config.get(
                sectionDarkSteel.name,
                "darkSteelUpgradeVibrantCost",
                darkSteelUpgradeVibrantCost,
                "Number of levels required for the 'Empowered.").getInt(darkSteelUpgradeVibrantCost);
        darkSteelUpgradePowerOneCost = config.get(
                sectionDarkSteel.name,
                "darkSteelUpgradePowerOneCost",
                darkSteelUpgradePowerOneCost,
                "Number of levels required for the 'Power 1.").getInt(darkSteelUpgradePowerOneCost);
        darkSteelUpgradePowerTwoCost = config.get(
                sectionDarkSteel.name,
                "darkSteelUpgradePowerTwoCost",
                darkSteelUpgradePowerTwoCost,
                "Number of levels required for the 'Power 2.").getInt(darkSteelUpgradePowerTwoCost);
        darkSteelUpgradePowerThreeCost = config.get(
                sectionDarkSteel.name,
                "darkSteelUpgradePowerThreeCost",
                darkSteelUpgradePowerThreeCost,
                "Number of levels required for the 'Power 3' upgrade.").getInt(darkSteelUpgradePowerThreeCost);
        darkSteelUpgradePowerFourCost = config.get(
                sectionDarkSteel.name,
                "darkSteelUpgradePowerFourCost",
                darkSteelUpgradePowerFourCost,
                "Number of levels required for the 'Power 4' upgrade.").getInt(darkSteelUpgradePowerFourCost);

        darkSteelJumpOneCost = config.get(
                sectionDarkSteel.name,
                "darkSteelJumpOneCost",
                darkSteelJumpOneCost,
                "Number of levels required for the 'Jump 1' upgrade.").getInt(darkSteelJumpOneCost);
        darkSteelJumpTwoCost = config.get(
                sectionDarkSteel.name,
                "darkSteelJumpTwoCost",
                darkSteelJumpTwoCost,
                "Number of levels required for the 'Jump 2' upgrade.").getInt(darkSteelJumpTwoCost);
        darkSteelJumpThreeCost = config.get(
                sectionDarkSteel.name,
                "darkSteelJumpThreeCost",
                darkSteelJumpThreeCost,
                "Number of levels required for the 'Jump 3' upgrade.").getInt(darkSteelJumpThreeCost);

        darkSteelSpeedOneCost = config.get(
                sectionDarkSteel.name,
                "darkSteelSpeedOneCost",
                darkSteelSpeedOneCost,
                "Number of levels required for the 'Speed 1' upgrade.").getInt(darkSteelSpeedOneCost);
        darkSteelSpeedTwoCost = config.get(
                sectionDarkSteel.name,
                "darkSteelSpeedTwoCost",
                darkSteelSpeedTwoCost,
                "Number of levels required for the 'Speed 2' upgrade.").getInt(darkSteelSpeedTwoCost);
        darkSteelSpeedThreeCost = config.get(
                sectionDarkSteel.name,
                "darkSteelSpeedThreeCost",
                darkSteelSpeedThreeCost,
                "Number of levels required for the 'Speed 3' upgrade.").getInt(darkSteelSpeedThreeCost);

        slotZeroPlacesEight = config.get(
                sectionDarkSteel.name,
                "shouldSlotZeroWrap",
                slotZeroPlacesEight,
                "Should the dark steel placement, when in the first (0th) slot, place the item in the last slot. If false, will place what's in the second slot.")
                .getBoolean();

        darkSteelSpeedOneWalkModifier = (float) config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelSpeedOneWalkModifier",
                        darkSteelSpeedOneWalkModifier,
                        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.")
                .getDouble(darkSteelSpeedOneWalkModifier);
        darkSteelSpeedTwoWalkMultiplier = (float) config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelSpeedTwoWalkMultiplier",
                        darkSteelSpeedTwoWalkMultiplier,
                        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.")
                .getDouble(darkSteelSpeedTwoWalkMultiplier);
        darkSteelSpeedThreeWalkMultiplier = (float) config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelSpeedThreeWalkMultiplier",
                        darkSteelSpeedThreeWalkMultiplier,
                        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.")
                .getDouble(darkSteelSpeedThreeWalkMultiplier);

        darkSteelSpeedOneSprintModifier = (float) config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelSpeedOneSprintModifier",
                        darkSteelSpeedOneSprintModifier,
                        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.")
                .getDouble(darkSteelSpeedOneSprintModifier);
        darkSteelSpeedTwoSprintMultiplier = (float) config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelSpeedTwoSprintMultiplier",
                        darkSteelSpeedTwoSprintMultiplier,
                        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.")
                .getDouble(darkSteelSpeedTwoSprintMultiplier);
        darkSteelSpeedThreeSprintMultiplier = (float) config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelSpeedThreeSprintMultiplier",
                        darkSteelSpeedThreeSprintMultiplier,
                        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.")
                .getDouble(darkSteelSpeedThreeSprintMultiplier);

        darkSteelBootsJumpModifier = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelBootsJumpModifier",
                        darkSteelBootsJumpModifier,
                        "Jump height modifier applied when jumping with Dark Steel Boots equipped")
                .getDouble(darkSteelBootsJumpModifier);

        darkSteelPowerStorageBase = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelPowerStorage",
                        darkSteelPowerStorageBase,
                        "Amount of power stored (RF) per crystal in the armor items recipe.")
                .getInt(darkSteelPowerStorageBase);
        darkSteelWalkPowerCost = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelWalkPowerCost",
                        darkSteelWalkPowerCost,
                        "Amount of power stored (RF) per block walked when wearing the dark steel boots.")
                .getInt(darkSteelWalkPowerCost);
        darkSteelSprintPowerCost = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelSprintPowerCost",
                        darkSteelWalkPowerCost,
                        "Amount of power stored (RF) per block walked when wearing the dark steel boots.")
                .getInt(darkSteelSprintPowerCost);
        darkSteelDrainPowerFromInventory = config.get(
                sectionDarkSteel.name,
                "darkSteelDrainPowerFromInventory",
                darkSteelDrainPowerFromInventory,
                "If true, dark steel armor will drain power stored (RF) in power containers in the players inventory.")
                .getBoolean(darkSteelDrainPowerFromInventory);

        darkSteelBootsJumpPowerCost = config.get(
                sectionDarkSteel.name,
                "darkSteelBootsJumpPowerCost",
                darkSteelBootsJumpPowerCost,
                "Base amount of power used per jump (RF) dark steel boots. The second jump in a 'double jump' uses 2x this etc")
                .getInt(darkSteelBootsJumpPowerCost);

        darkSteelFallDistanceCost = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelFallDistanceCost",
                        darkSteelFallDistanceCost,
                        "Amount of power used (RF) per block height of fall distance damage negated.")
                .getInt(darkSteelFallDistanceCost);

        darkSteelSwimCost = config.get(
                sectionDarkSteel.name,
                "darkSteelSwimCost",
                darkSteelSwimCost,
                "Number of levels required for the 'Swim' upgrade.").getInt(darkSteelSwimCost);

        darkSteelNightVisionCost = config.get(
                sectionDarkSteel.name,
                "darkSteelNightVisionCost",
                darkSteelNightVisionCost,
                "Number of levels required for the 'Night Vision' upgrade.").getInt(darkSteelNightVisionCost);

        darkSteelGliderCost = config.get(
                sectionDarkSteel.name,
                "darkSteelGliderCost",
                darkSteelGliderCost,
                "Number of levels required for the 'Glider' upgrade.").getInt(darkSteelGliderCost);
        darkSteelGliderHorizontalSpeed = config.get(
                sectionDarkSteel.name,
                "darkSteelGliderHorizontalSpeed",
                darkSteelGliderHorizontalSpeed,
                "Horizontal movement speed modifier when gliding.").getDouble(darkSteelGliderHorizontalSpeed);
        darkSteelGliderVerticalSpeed = config.get(
                sectionDarkSteel.name,
                "darkSteelGliderVerticalSpeed",
                darkSteelGliderVerticalSpeed,
                "Rate of altitude loss when gliding.").getDouble(darkSteelGliderVerticalSpeed);
        darkSteelGliderVerticalSpeedSprinting = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelGliderVerticalSpeedSprinting",
                        darkSteelGliderVerticalSpeedSprinting,
                        "Rate of altitude loss when sprinting and gliding.")
                .getDouble(darkSteelGliderVerticalSpeedSprinting);

        darkSteelSoundLocatorCost = config.get(
                sectionDarkSteel.name,
                "darkSteelSoundLocatorCost",
                darkSteelSoundLocatorCost,
                "Number of levels required for the 'Sound Locator' upgrade.").getInt(darkSteelSoundLocatorCost);
        darkSteelSoundLocatorRange = config.get(
                sectionDarkSteel.name,
                "darkSteelSoundLocatorRange",
                darkSteelSoundLocatorRange,
                "Range of the 'Sound Locator' upgrade.").getInt(darkSteelSoundLocatorRange);
        darkSteelSoundLocatorLifespan = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelSoundLocatorLifespan",
                        darkSteelSoundLocatorLifespan,
                        "Number of ticks the 'Sound Locator' icons are displayed for.")
                .getInt(darkSteelSoundLocatorLifespan);

        darkSteelGogglesOfRevealingCost = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelGogglesOfRevealingCost",
                        darkSteelGogglesOfRevealingCost,
                        "Number of levels required for the Goggles of Revealing upgrade.")
                .getInt(darkSteelGogglesOfRevealingCost);

        darkSteelApiaristArmorCost = config.get(
                sectionDarkSteel.name,
                "darkSteelApiaristArmorCost",
                darkSteelApiaristArmorCost,
                "Number of levels required for the Apiarist Armor upgrade.").getInt(darkSteelApiaristArmorCost);

        darkSteelTravelCost = config.get(
                sectionDarkSteel.name,
                "darkSteelTravelCost",
                darkSteelTravelCost,
                "Number of levels required for the 'Travel' upgrade.").getInt(darkSteelTravelCost);

        darkSteelSpoonCost = config.get(
                sectionDarkSteel.name,
                "darkSteelSpoonCost",
                darkSteelSpoonCost,
                "Number of levels required for the 'Spoon' upgrade.").getInt(darkSteelSpoonCost);

        darkSteelSolarOneCost = config.get(
                sectionDarkSteel.name,
                "darkSteelSolarOneCost",
                darkSteelSolarOneCost,
                "Cost in XP levels of the Solar I upgrade.").getInt();
        darkSteelSolarOneGen = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelSolarOneGen",
                        darkSteelSolarOneGen,
                        "RF per SECOND generated by the Solar I upgrade. Split between all equipped DS armors.")
                .getInt();

        darkSteelSolarTwoCost = config.get(
                sectionDarkSteel.name,
                "darkSteelSolarTwoCost",
                darkSteelSolarTwoCost,
                "Cost in XP levels of the Solar II upgrade.").getInt();
        darkSteelSolarTwoGen = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelSolarTwoGen",
                        darkSteelSolarTwoGen,
                        "RF per SECOND generated by the Solar II upgrade. Split between all equipped DS armors.")
                .getInt();
        darkSteelSolarChargeOthers = config.get(
                sectionDarkSteel.name,
                "darkSteelSolarChargeOthers",
                darkSteelSolarChargeOthers,
                "If enabled allows the solar upgrade to charge non-darksteel armors that the player is wearing.")
                .getBoolean();

        darkSteelSwordSkullChance = (float) config.get(
                sectionDarkSteel.name,
                "darkSteelSwordSkullChance",
                darkSteelSwordSkullChance,
                "The base chance that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)")
                .getDouble(darkSteelSwordSkullChance);
        darkSteelSwordSkullLootingModifier = (float) config.get(
                sectionDarkSteel.name,
                "darkSteelSwordSkullLootingModifier",
                darkSteelSwordSkullLootingModifier,
                "The chance per looting level that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)")
                .getDouble(darkSteelSwordSkullLootingModifier);

        darkSteelSwordWitherSkullChance = (float) config.get(
                sectionDarkSteel.name,
                "darkSteelSwordWitherSkullChance",
                darkSteelSwordWitherSkullChance,
                "The base chance that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)")
                .getDouble(darkSteelSwordWitherSkullChance);
        darkSteelSwordWitherSkullLootingModifier = (float) config.get(
                sectionDarkSteel.name,
                "darkSteelSwordWitherSkullLootingModifie",
                darkSteelSwordWitherSkullLootingModifier,
                "The chance per looting level that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)")
                .getDouble(darkSteelSwordWitherSkullLootingModifier);

        vanillaSwordSkullChance = (float) config.get(
                sectionDarkSteel.name,
                "vanillaSwordSkullChance",
                vanillaSwordSkullChance,
                "The base chance that a skull will be dropped when using a non dark steel sword (0 = no chance, 1 = 100% chance)")
                .getDouble(vanillaSwordSkullChance);
        vanillaSwordSkullLootingModifier = (float) config.get(
                sectionPersonal.name,
                "vanillaSwordSkullLootingModifier",
                vanillaSwordSkullLootingModifier,
                "The chance per looting level that a skull will be dropped when using a non-dark steel sword (0 = no chance, 1 = 100% chance)")
                .getDouble(vanillaSwordSkullLootingModifier);

        ticCleaverSkullDropChance = (float) config
                .get(
                        sectionDarkSteel.name,
                        "ticCleaverSkullDropChance",
                        ticCleaverSkullDropChance,
                        "The base chance that an Enderman Skull will be dropped when using TiC Cleaver")
                .getDouble(ticCleaverSkullDropChance);
        ticBeheadingSkullModifier = (float) config
                .get(
                        sectionPersonal.name,
                        "ticBeheadingSkullModifier",
                        ticBeheadingSkullModifier,
                        "The chance per level of Beheading that a skull will be dropped when using a TiC weapon")
                .getDouble(ticBeheadingSkullModifier);

        fakePlayerSkullChance = (float) config.get(
                sectionDarkSteel.name,
                "fakePlayerSkullChance",
                fakePlayerSkullChance,
                "The ratio of skull drops when a mob is killed by a 'FakePlayer', such as Killer Joe. When set to 0 no skulls will drop, at 1 the rate of skull drops is not modified")
                .getDouble(fakePlayerSkullChance);

        darkSteelSwordPowerUsePerHit = config.get(
                sectionDarkSteel.name,
                "darkSteelSwordPowerUsePerHit",
                darkSteelSwordPowerUsePerHit,
                "The amount of power (RF) used per hit.").getInt(darkSteelSwordPowerUsePerHit);
        darkSteelSwordEnderPearlDropChance = config.get(
                sectionDarkSteel.name,
                "darkSteelSwordEnderPearlDropChance",
                darkSteelSwordEnderPearlDropChance,
                "The chance that an ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)")
                .getDouble(darkSteelSwordEnderPearlDropChance);
        darkSteelSwordEnderPearlDropChancePerLooting = config.get(
                sectionDarkSteel.name,
                "darkSteelSwordEnderPearlDropChancePerLooting",
                darkSteelSwordEnderPearlDropChancePerLooting,
                "The chance for each looting level that an additional ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)")
                .getDouble(darkSteelSwordEnderPearlDropChancePerLooting);

        darkSteelPickPowerUseObsidian = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelPickPowerUseObsidian",
                        darkSteelPickPowerUseObsidian,
                        "The amount of power (RF) used to break an obsidian block.")
                .getInt(darkSteelPickPowerUseObsidian);
        darkSteelPickEffeciencyObsidian = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelPickEffeciencyObsidian",
                        darkSteelPickEffeciencyObsidian,
                        "The efficiency when breaking obsidian with a powered Dark Pickaxe.")
                .getInt(darkSteelPickEffeciencyObsidian);
        darkSteelPickApplyObsidianEffeciencyAtHardess = (float) config.get(
                sectionDarkSteel.name,
                "darkSteelPickApplyObsidianEffeciencyAtHardess",
                darkSteelPickApplyObsidianEffeciencyAtHardess,
                "If set to a value > 0, the obsidian speed and power use will be used for all blocks with hardness >= to this value.")
                .getDouble(darkSteelPickApplyObsidianEffeciencyAtHardess);
        darkSteelPickPowerUsePerDamagePoint = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelPickPowerUsePerDamagePoint",
                        darkSteelPickPowerUsePerDamagePoint,
                        "Power use (RF) per damage/durability point avoided.")
                .getInt(darkSteelPickPowerUsePerDamagePoint);
        darkSteelPickEffeciencyBoostWhenPowered = (float) config.get(
                sectionDarkSteel.name,
                "darkSteelPickEffeciencyBoostWhenPowered",
                darkSteelPickEffeciencyBoostWhenPowered,
                "The increase in efficiency when powered.").getDouble(darkSteelPickEffeciencyBoostWhenPowered);
        darkSteelPickMinesTiCArdite = config.getBoolean(
                "darkSteelPickMinesTiCArdite",
                sectionDarkSteel.name,
                darkSteelPickMinesTiCArdite,
                "When true the dark steel pick will be able to mine TiC Ardite and Cobalt");

        darkSteelAxePowerUsePerDamagePoint = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelAxePowerUsePerDamagePoint",
                        darkSteelAxePowerUsePerDamagePoint,
                        "Power use (RF) per damage/durability point avoided.")
                .getInt(darkSteelAxePowerUsePerDamagePoint);
        darkSteelAxePowerUsePerDamagePointMultiHarvest = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelPickAxeUsePerDamagePointMultiHarvest",
                        darkSteelAxePowerUsePerDamagePointMultiHarvest,
                        "Power use (RF) per damage/durability point avoided when shift-harvesting multiple logs")
                .getInt(darkSteelAxePowerUsePerDamagePointMultiHarvest);
        darkSteelAxeSpeedPenaltyMultiHarvest = (float) config.get(
                sectionDarkSteel.name,
                "darkSteelAxeSpeedPenaltyMultiHarvest",
                darkSteelAxeSpeedPenaltyMultiHarvest,
                "How much slower shift-harvesting logs is.").getDouble(darkSteelAxeSpeedPenaltyMultiHarvest);
        darkSteelAxeEffeciencyBoostWhenPowered = (float) config.get(
                sectionDarkSteel.name,
                "darkSteelAxeEffeciencyBoostWhenPowered",
                darkSteelAxeEffeciencyBoostWhenPowered,
                "The increase in efficiency when powered.").getDouble(darkSteelAxeEffeciencyBoostWhenPowered);

        darkSteelShearsDurabilityFactor = config.get(
                sectionDarkSteel.name,
                "darkSteelShearsDurabilityFactor",
                darkSteelShearsDurabilityFactor,
                "How much more durable as vanilla shears they are.").getInt(darkSteelShearsDurabilityFactor);
        darkSteelShearsPowerUsePerDamagePoint = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelShearsPowerUsePerDamagePoint",
                        darkSteelShearsPowerUsePerDamagePoint,
                        "Power use (RF) per damage/durability point avoided.")
                .getInt(darkSteelShearsPowerUsePerDamagePoint);
        darkSteelShearsEffeciencyBoostWhenPowered = (float) config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelShearsEffeciencyBoostWhenPowered",
                        darkSteelShearsEffeciencyBoostWhenPowered,
                        "The increase in efficiency when powered.")
                .getDouble(darkSteelShearsEffeciencyBoostWhenPowered);
        darkSteelShearsBlockAreaBoostWhenPowered = config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelShearsBlockAreaBoostWhenPowered",
                        darkSteelShearsBlockAreaBoostWhenPowered,
                        "The increase in effected area (radius) when powered and used on blocks.")
                .getInt(darkSteelShearsBlockAreaBoostWhenPowered);
        darkSteelShearsEntityAreaBoostWhenPowered = (float) config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelShearsEntityAreaBoostWhenPowered",
                        darkSteelShearsEntityAreaBoostWhenPowered,
                        "The increase in effected area (radius) when powered and used on sheep.")
                .getDouble(darkSteelShearsEntityAreaBoostWhenPowered);

        darkSteelAnvilDamageChance = (float) config.get(
                sectionDarkSteel.name,
                "darkSteelAnvilDamageChance",
                darkSteelAnvilDamageChance,
                "Chance that the dark steel anvil will take damage after repairing something.").getDouble();

        darkSteelLadderSpeedBoost = (float) config
                .get(
                        sectionDarkSteel.name,
                        "darkSteelLadderSpeedBoost",
                        darkSteelLadderSpeedBoost,
                        "Speed boost, in blocks per tick, that the DS ladder gives over the vanilla ladder.")
                .getDouble();

        hootchPowerPerCycleRF = config
                .get(
                        sectionPower.name,
                        "hootchPowerPerCycleRF",
                        hootchPowerPerCycleRF,
                        "The amount of power generated per BC engine cycle. Examples: BC Oil = 30, BC Fuel = 60")
                .getInt(hootchPowerPerCycleRF);
        hootchPowerTotalBurnTime = config
                .get(
                        sectionPower.name,
                        "hootchPowerTotalBurnTime",
                        hootchPowerTotalBurnTime,
                        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000")
                .getInt(hootchPowerTotalBurnTime);

        rocketFuelPowerPerCycleRF = config
                .get(
                        sectionPower.name,
                        "rocketFuelPowerPerCycleRF",
                        rocketFuelPowerPerCycleRF,
                        "The amount of power generated per BC engine cycle. Examples: BC Oil = 3, BC Fuel = 6")
                .getInt(rocketFuelPowerPerCycleRF);
        rocketFuelPowerTotalBurnTime = config
                .get(
                        sectionPower.name,
                        "rocketFuelPowerTotalBurnTime",
                        rocketFuelPowerTotalBurnTime,
                        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000")
                .getInt(rocketFuelPowerTotalBurnTime);

        fireWaterPowerPerCycleRF = config
                .get(
                        sectionPower.name,
                        "fireWaterPowerPerCycleRF",
                        fireWaterPowerPerCycleRF,
                        "The amount of power generated per BC engine cycle. Examples: BC Oil = 30, BC Fuel = 60")
                .getInt(fireWaterPowerPerCycleRF);
        fireWaterPowerTotalBurnTime = config
                .get(
                        sectionPower.name,
                        "fireWaterPowerTotalBurnTime",
                        fireWaterPowerTotalBurnTime,
                        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000")
                .getInt(fireWaterPowerTotalBurnTime);

        zombieGeneratorRfPerTick = config.get(
                sectionPower.name,
                "zombieGeneratorRfPerTick",
                zombieGeneratorRfPerTick,
                "The amount of power generated per tick.").getInt(zombieGeneratorRfPerTick);
        zombieGeneratorTicksPerBucketFuel = config.get(
                sectionPower.name,
                "zombieGeneratorTicksPerMbFuel",
                zombieGeneratorTicksPerBucketFuel,
                "The number of ticks one bucket of fuel lasts.").getInt(zombieGeneratorTicksPerBucketFuel);

        frankenzombieGeneratorRfPerTick = config.get(
                sectionPower.name,
                "frankenzombieGeneratorRfPerTick",
                frankenzombieGeneratorRfPerTick,
                "The amount of power generated per tick.").getInt(frankenzombieGeneratorRfPerTick);
        frankenzombieGeneratorTicksPerBucketFuel = config
                .get(
                        sectionPower.name,
                        "frankenzombieGeneratorTicksPerMbFuel",
                        frankenzombieGeneratorTicksPerBucketFuel,
                        "The number of ticks one bucket of fuel lasts.")
                .getInt(frankenzombieGeneratorTicksPerBucketFuel);

        enderGeneratorRfPerTick = config.get(
                sectionPower.name,
                "enderGeneratorRfPerTick",
                enderGeneratorRfPerTick,
                "The amount of power generated per tick.").getInt(enderGeneratorRfPerTick);
        enderGeneratorTicksPerBucketFuel = config.get(
                sectionPower.name,
                "enderGeneratorTicksPerMbFuel",
                enderGeneratorTicksPerBucketFuel,
                "The number of ticks one bucket of fuel lasts.").getInt(enderGeneratorTicksPerBucketFuel);

        zombieGeneratorsEnergyMultipliers = config.get(
                sectionPower.name,
                "zombieGeneratorsEnergyMultipliers",
                zombieGeneratorsEnergyMultipliers,
                "Energy multipliers for the Zombie-Type Generators").getDoubleList();

        zombieGeneratorsBurnTimeMultipliers = config.get(
                sectionPower.name,
                "zombieGeneratorsBurnTimeMultipliers",
                zombieGeneratorsBurnTimeMultipliers,
                "Burn time multipliers for the Zombie-Type Generators").getDoubleList();

        stirlingGeneratorBaseRfPerTick = config.get(
                sectionPower.name,
                "stirlingGeneratorBaseRfPerTick",
                stirlingGeneratorBaseRfPerTick,
                "The amount of power generated per tick.").getInt(stirlingGeneratorBaseRfPerTick);

        stirlingGeneratorEnergyMultiplierT1 = (float) config.get(
                sectionPower.name,
                "stirlingGeneratorEnergyMultiplierT1",
                stirlingGeneratorEnergyMultiplierT1,
                "[Deprecated]Energy multiplier for the Stirling Generator, Tier 1 machine").getDouble();
        stirlingGeneratorEnergyMultiplierT2 = (float) config.get(
                sectionPower.name,
                "stirlingGeneratorEnergyMultiplierT2",
                stirlingGeneratorEnergyMultiplierT2,
                "[Deprecated]Energy multiplier for the Stirling Generator, Tier 2 machine").getDouble();
        stirlingGeneratorEnergyMultiplierT3 = (float) config.get(
                sectionPower.name,
                "stirlingGeneratorEnergyMultiplierT3",
                stirlingGeneratorEnergyMultiplierT3,
                "[Deprecated]Energy multiplier for the Stirling Generator, Tier 3 machine").getDouble();

        stirlingGeneratorEnergyMultipliers = config.get(
                sectionPower.name,
                "stirlingGeneratorEnergyMultipliers",
                stirlingGeneratorEnergyMultipliers,
                "Energy multipliers for the Stirling Generator").getDoubleList();

        stirlingGeneratorBurnTimeMultiplierT1 = (float) config.get(
                sectionPower.name,
                "stirlingGeneratorBurnTimeMultiplierT1",
                stirlingGeneratorBurnTimeMultiplierT1,
                "[Deprecated]Burn time multiplier for the Stirling Generator, Tier 1 machine").getDouble();
        stirlingGeneratorBurnTimeMultiplierT2 = (float) config.get(
                sectionPower.name,
                "stirlingGeneratorBurnTimeMultiplierT2",
                stirlingGeneratorBurnTimeMultiplierT2,
                "[Deprecated]Burn time multiplier for the Stirling Generator, Tier 2 machine").getDouble();
        stirlingGeneratorBurnTimeMultiplierT3 = (float) config.get(
                sectionPower.name,
                "stirlingGeneratorBurnTimeMultiplierT3",
                stirlingGeneratorBurnTimeMultiplierT3,
                "[Deprecated]Burn time multiplier for the Stirling Generator, Tier 3 machine").getDouble();

        stirlingGeneratorBurnTimeMultipliers = config.get(
                sectionPower.name,
                "stirlingGeneratorBurnTimeMultipliers",
                stirlingGeneratorBurnTimeMultipliers,
                "Burn time multipliers for the Stirling Generator").getDoubleList();

        addFuelTooltipsToAllFluidContainers = config.get(
                sectionPersonal.name,
                "addFuelTooltipsToAllFluidContainers",
                addFuelTooltipsToAllFluidContainers,
                "If true, the RF/t and burn time of the fuel will be displayed in all tooltips for fluid containers with fuel.")
                .getBoolean(addFuelTooltipsToAllFluidContainers);
        addDurabilityTootip = config.get(
                sectionPersonal.name,
                "addDurabilityTootip",
                addFuelTooltipsToAllFluidContainers,
                "If true, adds durability tooltips to tools and armor").getBoolean(addDurabilityTootip);
        addFurnaceFuelTootip = config.get(
                sectionPersonal.name,
                "addFurnaceFuelTootip",
                addFuelTooltipsToAllFluidContainers,
                "If true, adds burn duration tooltips to furnace fuels").getBoolean(addFurnaceFuelTootip);

        farmContinuousEnergyUseRF = config.get(
                sectionFarm.name,
                "farmContinuousEnergyUseRF",
                farmContinuousEnergyUseRF,
                "The amount of power used by a farm per tick ").getInt(farmContinuousEnergyUseRF);
        farmActionEnergyUseRF = config
                .get(
                        sectionFarm.name,
                        "farmActionEnergyUseRF",
                        farmActionEnergyUseRF,
                        "The amount of power used by a farm per action (eg plant, till, harvest) ")
                .getInt(farmActionEnergyUseRF);
        farmAxeActionEnergyUseRF = config.get(
                sectionFarm.name,
                "farmAxeActionEnergyUseRF",
                farmAxeActionEnergyUseRF,
                "The amount of power used by a farm per wood block 'chopped'").getInt(farmAxeActionEnergyUseRF);

        farmBonemealActionEnergyUseRF = config.get(
                sectionFarm.name,
                "farmBonemealActionEnergyUseRF",
                farmBonemealActionEnergyUseRF,
                "The amount of power used by a farm per bone meal used").getInt(farmBonemealActionEnergyUseRF);
        farmBonemealTryEnergyUseRF = config.get(
                sectionFarm.name,
                "farmBonemealTryEnergyUseRF",
                farmBonemealTryEnergyUseRF,
                "The amount of power used by a farm per bone meal try").getInt(farmBonemealTryEnergyUseRF);

        farmDefaultSize = config.get(
                sectionFarm.name,
                "farmDefaultSize",
                farmDefaultSize,
                "The number of blocks a farm will extend from its center").getInt(farmDefaultSize);
        farmBonusSize = config
                .get(
                        sectionFarm.name,
                        "farmBonusSize",
                        farmBonusSize,
                        "The extra number of blocks a farm will extend from its center per upgrade")
                .getInt(farmBonusSize);

        farmAxeDamageOnLeafBreak = config.get(
                sectionFarm.name,
                "farmAxeDamageOnLeafBreak",
                farmAxeDamageOnLeafBreak,
                "Should axes in a farm take damage when breaking leaves?").getBoolean(farmAxeDamageOnLeafBreak);
        farmToolTakeDamageChance = (float) config.get(
                sectionFarm.name,
                "farmToolTakeDamageChance",
                farmToolTakeDamageChance,
                "The chance that a tool in the farm will take damage.").getDouble(farmToolTakeDamageChance);

        disableFarmNotification = config.get(
                sectionFarm.name,
                "disableFarmNotifications",
                disableFarmNotification,
                "Disable the notification text above the farm block.").getBoolean();

        farmEssenceBerriesEnabled = config
                .get(
                        sectionFarm.name,
                        "farmEssenceBerriesEnabled",
                        farmEssenceBerriesEnabled,
                        "This setting controls whether essence berry bushes from TiC can be harvested by the farm.")
                .getBoolean();

        farmManaBeansEnabled = config
                .get(
                        sectionFarm.name,
                        "farmManaBeansEnabled",
                        farmManaBeansEnabled,
                        "This setting controls whether mana beans from Thaumcraft can be harvested by the farm.")
                .getBoolean();

        farmHarvestJungleWhenCocoa = config.get(
                sectionFarm.name,
                "farmHarvestJungleWhenCocoa",
                farmHarvestJungleWhenCocoa,
                "If this is enabled the farm will harvest jungle wood even if it has cocoa beans in its inventory.")
                .getBoolean();

        hoeStrings = config.get(
                sectionFarm.name,
                "farmHoes",
                hoeStrings,
                "Use this to specify items that can be hoes in the farming station. Use the registry name (eg. modid:name).")
                .getStringList();

        farmSaplingReserveAmount = config.get(
                sectionFarm.name,
                "farmSaplingReserveAmount",
                farmSaplingReserveAmount,
                "The amount of saplings the farm has to have in reserve to switch to shearing all leaves. If there are less "
                        + "saplings in store, it will only shear part the leaves and break the others for spalings. Set this to 0 to "
                        + "always shear all leaves.")
                .getInt(farmSaplingReserveAmount);

        combustionGeneratorUseOpaqueModel = config
                .get(
                        sectionAesthetic.name,
                        "combustionGeneratorUseOpaqueModel",
                        combustionGeneratorUseOpaqueModel,
                        "If set to true: fluid will not be shown in combustion generator tanks. Improves FPS. ")
                .getBoolean(combustionGeneratorUseOpaqueModel);

        magnetPowerUsePerSecondRF = config
                .get(
                        sectionMagnet.name,
                        "magnetPowerUsePerTickRF",
                        magnetPowerUsePerSecondRF,
                        "The amount of RF power used per tick when the magnet is active")
                .getInt(magnetPowerUsePerSecondRF);
        magnetPowerCapacityRF = config.get(
                sectionMagnet.name,
                "magnetPowerCapacityRF",
                magnetPowerCapacityRF,
                "Amount of RF power stored in a fully charged magnet").getInt(magnetPowerCapacityRF);
        magnetRange = config.get(sectionMagnet.name, "magnetRange", magnetRange, "Range of the magnet in blocks.")
                .getInt(magnetRange);
        magnetMaxItems = config
                .get(
                        sectionMagnet.name,
                        "magnetMaxItems",
                        magnetMaxItems,
                        "Maximum number of items the magnet can effect at a time. (-1 for unlimited)")
                .getInt(magnetMaxItems);

        magnetBlacklist = config.getStringList(
                "magnetBlacklist",
                sectionMagnet.name,
                magnetBlacklist,
                "These items will not be picked up by the magnet.");

        magnetAllowInMainInventory = config
                .get(
                        sectionMagnet.name,
                        "magnetAllowInMainInventory",
                        magnetAllowInMainInventory,
                        "If true the magnet will also work in the main inventory, not just the hotbar")
                .getBoolean(magnetAllowInMainInventory);

        magnetAllowInBaublesSlot = config.get(
                sectionMagnet.name,
                "magnetAllowInBaublesSlot",
                magnetAllowInBaublesSlot,
                "If true the magnet can be put into the 'amulet' Baubles slot (requires Baubles to be installed)")
                .getBoolean(magnetAllowInBaublesSlot);
        magnetAllowDeactivatedInBaublesSlot = config.get(
                sectionMagnet.name,
                "magnetAllowDeactivatedInBaublesSlot",
                magnetAllowDeactivatedInBaublesSlot,
                "If true the magnet can be put into the 'amulet' Baubles slot even if switched off (requires Baubles to be installed and magnetAllowInBaublesSlot to be on)")
                .getBoolean(magnetAllowDeactivatedInBaublesSlot);

        magnetAllowPowerExtraction = config.get(
                sectionMagnet.name,
                "magnetAllowPowerExtraction",
                magnetAllowPowerExtraction,
                "If true the magnet can be used as a battery.").getBoolean(magnetAllowPowerExtraction);

        magnetBaublesType = config.get(
                sectionMagnet.name,
                "magnetBaublesType",
                magnetBaublesType,
                "The BaublesType the magnet should be, 'AMULET', 'RING' or 'BELT' (requires Baubles to be installed and magnetAllowInBaublesSlot to be on)")
                .getString();

        useCombustionGenModel = config
                .get(
                        sectionAesthetic.name,
                        "useCombustionGenModel",
                        useCombustionGenModel,
                        "If set to true: WIP Combustion Generator model will be used")
                .getBoolean(useCombustionGenModel);

        crafterRfPerCraft = config
                .get("AutoCrafter Settings", "crafterRfPerCraft", crafterRfPerCraft, "RF used per autocrafted recipe")
                .getInt(crafterRfPerCraft);

        poweredSpawnerMinDelayTicks = config.get(
                sectionSpawner.name,
                "poweredSpawnerMinDelayTicks",
                poweredSpawnerMinDelayTicks,
                "Min tick delay between spawns for a non-upgraded spawner").getInt(poweredSpawnerMinDelayTicks);
        poweredSpawnerMaxDelayTicks = config.get(
                sectionSpawner.name,
                "poweredSpawnerMaxDelayTicks",
                poweredSpawnerMaxDelayTicks,
                "Min tick delay between spawns for a non-upgraded spawner").getInt(poweredSpawnerMaxDelayTicks);
        poweredSpawnerLevelOnePowerPerTickRF = config.get(
                sectionSpawner.name,
                "poweredSpawnerLevelOnePowerPerTickRF",
                poweredSpawnerLevelOnePowerPerTickRF,
                "RF per tick for a level 1 (non-upgraded) spawner. See PoweredSpanerConfig_Core.json for mob type multipliers")
                .getInt(poweredSpawnerLevelOnePowerPerTickRF);
        poweredSpawnerLevelTwoPowerPerTickRF = config.get(
                sectionSpawner.name,
                "poweredSpawnerLevelTwoPowerPerTickRF",
                poweredSpawnerLevelTwoPowerPerTickRF,
                "RF per tick for a level 2 spawner").getInt(poweredSpawnerLevelTwoPowerPerTickRF);
        poweredSpawnerLevelThreePowerPerTickRF = config.get(
                sectionSpawner.name,
                "poweredSpawnerLevelThreePowerPerTickRF",
                poweredSpawnerLevelThreePowerPerTickRF,
                "RF per tick for a level 3 spawner").getInt(poweredSpawnerLevelThreePowerPerTickRF);
        poweredSpawnerLevelFourPowerPerTickRF = config.get(
                sectionSpawner.name,
                "poweredSpawnerLevelFourPowerPerTickRF",
                poweredSpawnerLevelFourPowerPerTickRF,
                "RF per tick for a level 4 spawner").getInt(poweredSpawnerLevelFourPowerPerTickRF);
        poweredSpawnerLevelFivePowerPerTickRF = config.get(
                sectionSpawner.name,
                "poweredSpawnerLevelFivePowerPerTickRF",
                poweredSpawnerLevelFivePowerPerTickRF,
                "RF per tick for a level 5 spawner").getInt(poweredSpawnerLevelFivePowerPerTickRF);
        poweredSpawnerLevelSixPowerPerTickRF = config.get(
                sectionSpawner.name,
                "poweredSpawnerLevelSixPowerPerTickRF",
                poweredSpawnerLevelSixPowerPerTickRF,
                "RF per tick for a level 6 spawner").getInt(poweredSpawnerLevelSixPowerPerTickRF);
        poweredSpawnerLevelSevenPowerPerTickRF = config.get(
                sectionSpawner.name,
                "poweredSpawnerLevelSevenPowerPerTickRF",
                poweredSpawnerLevelSevenPowerPerTickRF,
                "RF per tick for a level 7 spawner").getInt(poweredSpawnerLevelSevenPowerPerTickRF);
        poweredSpawnerLevelEightPowerPerTickRF = config.get(
                sectionSpawner.name,
                "poweredSpawnerLevelEightPowerPerTickRF",
                poweredSpawnerLevelEightPowerPerTickRF,
                "RF per tick for a level 1 spawner").getInt(poweredSpawnerLevelEightPowerPerTickRF);
        poweredSpawnerLevelNinePowerPerTickRF = config.get(
                sectionSpawner.name,
                "poweredSpawnerLevelNinePowerPerTickRF",
                poweredSpawnerLevelNinePowerPerTickRF,
                "RF per tick for a level 2 spawner").getInt(poweredSpawnerLevelNinePowerPerTickRF);
        poweredSpawnerLevelTenPowerPerTickRF = config.get(
                sectionSpawner.name,
                "poweredSpawnerLevelTenPowerPerTickRF",
                poweredSpawnerLevelTenPowerPerTickRF,
                "RF per tick for a level 3 spawner").getInt(poweredSpawnerLevelTenPowerPerTickRF);
        poweredSpawnerMaxPlayerDistance = config.get(
                sectionSpawner.name,
                "poweredSpawnerMaxPlayerDistance",
                poweredSpawnerMaxPlayerDistance,
                "Max distance of the closest player for the spawner to be active. A zero value will remove the player check")
                .getInt(poweredSpawnerMaxPlayerDistance);
        poweredSpawnerDespawnTimeSeconds = config
                .get(
                        sectionSpawner.name,
                        "poweredSpawnerDespawnTimeSeconds",
                        poweredSpawnerDespawnTimeSeconds,
                        "Number of seconds in which spawned entities are protected from despawning")
                .getInt(poweredSpawnerDespawnTimeSeconds);
        poweredSpawnerSpawnCount = config.get(
                sectionSpawner.name,
                "poweredSpawnerSpawnCount",
                poweredSpawnerSpawnCount,
                "Number of entities to spawn each time").getInt(poweredSpawnerSpawnCount);
        poweredSpawnerSpawnRange = config
                .get(sectionSpawner.name, "poweredSpawnerSpawnRange", poweredSpawnerSpawnRange, "Spawning range in X/Z")
                .getInt(poweredSpawnerSpawnRange);
        poweredSpawnerMaxNearbyEntities = config.get(
                sectionSpawner.name,
                "poweredSpawnerMaxNearbyEntities",
                poweredSpawnerMaxNearbyEntities,
                "Max number of entities in the nearby area until no more are spawned. A zero value will remove this check")
                .getInt(poweredSpawnerMaxNearbyEntities);
        poweredSpawnerMaxSpawnTries = config.get(
                sectionSpawner.name,
                "poweredSpawnerMaxSpawnTries",
                poweredSpawnerMaxSpawnTries,
                "Number of tries to find a suitable spawning location").getInt(poweredSpawnerMaxSpawnTries);
        poweredSpawnerUseVanillaSpawChecks = config.get(
                sectionSpawner.name,
                "poweredSpawnerUseVanillaSpawChecks",
                poweredSpawnerUseVanillaSpawChecks,
                "If true, regular spawn checks such as lighting level and dimension will be made before spawning mobs")
                .getBoolean(poweredSpawnerUseVanillaSpawChecks);
        brokenSpawnerDropChance = (float) config.get(
                sectionSpawner.name,
                "brokenSpawnerDropChance",
                brokenSpawnerDropChance,
                "The chance a broken spawner will be dropped when a spawner is broken. 1 = 100% chance, 0 = 0% chance")
                .getDouble(brokenSpawnerDropChance);
        brokenSpawnerToolBlacklist = config.getStringList(
                "brokenSpawnerToolBlacklist",
                sectionSpawner.name,
                brokenSpawnerToolBlacklist,
                "When a spawner is broken with these tools they will not drop a broken spawner");

        powerSpawnerAddSpawnerCost = config.get(
                sectionSpawner.name,
                "powerSpawnerAddSpawnerCost",
                powerSpawnerAddSpawnerCost,
                "The number of levels it costs to add a broken spawner").getInt(powerSpawnerAddSpawnerCost);

        useModMetals = config
                .get(
                        sectionRecipe.name,
                        "useModMetals",
                        useModMetals,
                        "If true copper and tin will be used in recipes when registered in the ore dictionary")
                .getBoolean(useModMetals);

        nutrientFoodBoostDelay = config
                .get(
                        sectionFluid.name,
                        "nutrientFluidFoodBoostDelay",
                        nutrientFoodBoostDelay,
                        "The delay in ticks between when nutrient distillation boosts your food value.")
                .getInt((int) nutrientFoodBoostDelay);

        killerJoeNutrientUsePerAttackMb = config
                .get(
                        sectionKiller.name,
                        "killerJoeNutrientUsePerAttackMb",
                        killerJoeNutrientUsePerAttackMb,
                        "The number of millibuckets of nutrient fluid used per attack.")
                .getInt(killerJoeNutrientUsePerAttackMb);

        killerJoeAttackHeight = config.get(
                sectionKiller.name,
                "killerJoeAttackHeight",
                killerJoeAttackHeight,
                "The reach of attacks above and bellow Joe.").getDouble(killerJoeAttackHeight);
        killerJoeAttackWidth = config.get(
                sectionKiller.name,
                "killerJoeAttackWidth",
                killerJoeAttackWidth,
                "The reach of attacks to each side of Joe.").getDouble(killerJoeAttackWidth);
        killerJoeAttackLength = config.get(
                sectionKiller.name,
                "killerJoeAttackLength",
                killerJoeAttackLength,
                "The reach of attacks in front of Joe.").getDouble(killerJoeAttackLength);
        killerJoeHooverXpLength = config
                .get(
                        sectionKiller.name,
                        "killerJoeHooverXpLength",
                        killerJoeHooverXpLength,
                        "The distance from which XP will be gathered to each side of Joe.")
                .getDouble(killerJoeHooverXpLength);
        killerJoeHooverXpWidth = config
                .get(
                        sectionKiller.name,
                        "killerJoeHooverXpWidth",
                        killerJoeHooverXpWidth,
                        "The distance from which XP will be gathered in front of Joe.")
                .getDouble(killerJoeHooverXpWidth);
        killerJoeMaxXpLevel = config.get(
                sectionMisc.name,
                "killerJoeMaxXpLevel",
                killerJoeMaxXpLevel,
                "Maximum level of XP the killer joe can contain.").getInt();

        killerJoeMustSee = config.get(
                sectionKiller.name,
                "killerJoeMustSee",
                killerJoeMustSee,
                "Set whether the Killer Joe can attack through blocks.").getBoolean();
        killerPvPoffDisablesSwing = config.get(
                sectionKiller.name,
                "killerPvPoffDisablesSwing",
                killerPvPoffDisablesSwing,
                "Set whether the Killer Joe swings even if PvP is off (that swing will do nothing unless killerPvPoffIsIgnored is enabled).")
                .getBoolean();
        killerPvPoffIsIgnored = config.get(
                sectionKiller.name,
                "killerPvPoffIsIgnored",
                killerPvPoffIsIgnored,
                "Set whether the Killer Joe ignores PvP settings and always hits players (killerPvPoffDisablesSwing must be off for this to work).")
                .getBoolean();

        // Add deprecated comment
        config.getString(
                "isGasConduitEnabled",
                sectionItems.name,
                "auto",
                "Deprecated option. Use boolean \"gasConduitsEnabled\" below.");
        isGasConduitEnabled = config.getBoolean(
                "gasConduitEnabled",
                sectionItems.name,
                isGasConduitEnabled,
                "If true, gas conduits will be enabled if the Mekanism Gas API is found. False to forcibly disable.");
        enableMEConduits = config.getBoolean(
                "enableMEConduits",
                sectionItems.name,
                enableMEConduits,
                "Allows ME conduits. Only has an effect with AE2 installed.");
        enableOCConduits = config.getBoolean(
                "enableOCConduits",
                sectionItems.name,
                enableOCConduits,
                "Allows OC conduits. Only has an effect with OpenComputers installed.");
        enableOCConduitsAnimatedTexture = config.getBoolean(
                "enableOCConduitsAnimatedTexture",
                sectionItems.name,
                enableOCConduitsAnimatedTexture,
                "Use the animated texture for OC conduits.");

        soulVesselBlackList = config.getStringList(
                "soulVesselBlackList",
                sectionSoulBinder.name,
                soulVesselBlackList,
                "Entities listed here will can not be captured in a Soul Vial");

        soulVesselCapturesBosses = config.getBoolean(
                "soulVesselCapturesBosses",
                sectionSoulBinder.name,
                soulVesselCapturesBosses,
                "When set to false, any mob with a 'boss bar' won't be able to be captured in the Soul Vial");

        soulBinderLevelOnePowerPerTickRF = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderLevelOnePowerPerTickRF",
                        soulBinderLevelOnePowerPerTickRF,
                        "The number of RF/t consumed by an unupgraded soul binder.")
                .getInt(soulBinderLevelOnePowerPerTickRF);
        soulBinderLevelTwoPowerPerTickRF = config.get(
                sectionSoulBinder.name,
                "soulBinderLevelTwoPowerPerTickRF",
                soulBinderLevelTwoPowerPerTickRF,
                "The number of RF/t consumed by a soul binder with a double layer capacitor or a endergetic capacitor upgrade.")
                .getInt(soulBinderLevelTwoPowerPerTickRF);
        soulBinderLevelThreePowerPerTickRF = config.get(
                sectionSoulBinder.name,
                "soulBinderLevelThreePowerPerTickRF",
                soulBinderLevelThreePowerPerTickRF,
                "The number of RF/t consumed by a soul binder with an octadic capacitor or a endergised upgrade.")
                .getInt(soulBinderLevelThreePowerPerTickRF);
        soulBinderLevelFourPowerPerTickRF = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderLevelFourPowerPerTickRF",
                        soulBinderLevelFourPowerPerTickRF,
                        "The number of RF/t consumed by a soul binder with an crystalline capacitor upgrade.")
                .getInt(soulBinderLevelFourPowerPerTickRF);
        soulBinderLevelFivePowerPerTickRF = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderLevelFivePowerPerTickRF",
                        soulBinderLevelFivePowerPerTickRF,
                        "The number of RF/t consumed by a soul binder with an melodic capacitor upgrade.")
                .getInt(soulBinderLevelFivePowerPerTickRF);
        soulBinderLevelSixPowerPerTickRF = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderLevelSixPowerPerTickRF",
                        soulBinderLevelSixPowerPerTickRF,
                        "The number of RF/t consumed by a soul binder with an stellar capacitor upgrade.")
                .getInt(soulBinderLevelSixPowerPerTickRF);
        soulBinderLevelSevenPowerPerTickRF = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderLevelSevenPowerPerTickRF",
                        soulBinderLevelSevenPowerPerTickRF,
                        "The number of RF/t consumed by a soul binder with an totemic capacitor upgrade.")
                .getInt(soulBinderLevelSevenPowerPerTickRF);
        soulBinderLevelEightPowerPerTickRF = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderLevelEightPowerPerTickRF",
                        soulBinderLevelEightPowerPerTickRF,
                        "The number of RF/t consumed by a soul binder with an silver capacitor upgrade.")
                .getInt(soulBinderLevelEightPowerPerTickRF);
        soulBinderLevelNinePowerPerTickRF = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderLevelNinePowerPerTickRF",
                        soulBinderLevelNinePowerPerTickRF,
                        "The number of RF/t consumed by a soul binder with an endergetic capacitor upgrade.")
                .getInt(soulBinderLevelNinePowerPerTickRF);
        soulBinderLevelTenPowerPerTickRF = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderLevelTenPowerPerTickRF",
                        soulBinderLevelTenPowerPerTickRF,
                        "The number of RF/t consumed by a soul binder with an endergised capacitor upgrade.")
                .getInt(soulBinderLevelTenPowerPerTickRF);
        soulBinderBrokenSpawnerRF = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderBrokenSpawnerRF",
                        soulBinderBrokenSpawnerRF,
                        "The number of RF required to change the type of a broken spawner.")
                .getInt(soulBinderBrokenSpawnerRF);
        soulBinderReanimationRF = config.get(
                sectionSoulBinder.name,
                "soulBinderReanimationRF",
                soulBinderReanimationRF,
                "The number of RF required to to re-animated a mob head.").getInt(soulBinderReanimationRF);
        soulBinderEnderCystalRF = config.get(
                sectionSoulBinder.name,
                "soulBinderEnderCystalRF",
                soulBinderEnderCystalRF,
                "The number of RF required to create an ender crystal.").getInt(soulBinderEnderCystalRF);
        soulBinderPrecientCystalRF = config.get(
                sectionSoulBinder.name,
                "soulBinderPrecientCystalRF",
                soulBinderPrecientCystalRF,
                "The number of RF required to create an precient crystal.").getInt(soulBinderPrecientCystalRF);
        soulBinderAttractorCystalRF = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderAttractorCystalRF",
                        soulBinderAttractorCystalRF,
                        "The number of RF required to create an attractor crystal.")
                .getInt(soulBinderAttractorCystalRF);
        soulBinderEnderRailRF = config.get(
                sectionSoulBinder.name,
                "soulBinderEnderRailRF",
                soulBinderEnderRailRF,
                "The number of RF required to create an ender rail.").getInt(soulBinderEnderRailRF);

        soulBinderAttractorCystalLevels = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderAttractorCystalLevels",
                        soulBinderAttractorCystalLevels,
                        "The number of levels required to create an attractor crystal.")
                .getInt(soulBinderAttractorCystalLevels);
        soulBinderEnderCystalLevels = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderEnderCystalLevels",
                        soulBinderEnderCystalLevels,
                        "The number of levels required to create an ender crystal.")
                .getInt(soulBinderEnderCystalLevels);
        soulBinderPrecientCystalLevels = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderPrecientCystalLevels",
                        soulBinderPrecientCystalLevels,
                        "The number of levels required to create an precient crystal.")
                .getInt(soulBinderPrecientCystalLevels);
        soulBinderReanimationLevels = config.get(
                sectionSoulBinder.name,
                "soulBinderReanimationLevels",
                soulBinderReanimationLevels,
                "The number of levels required to re-animate a mob head.").getInt(soulBinderReanimationLevels);
        soulBinderBrokenSpawnerLevels = config
                .get(
                        sectionSoulBinder.name,
                        "soulBinderBrokenSpawnerLevels",
                        soulBinderBrokenSpawnerLevels,
                        "The number of levels required to change the type of a broken spawner.")
                .getInt(soulBinderBrokenSpawnerLevels);
        soulBinderEnderRailLevels = config.get(
                sectionSoulBinder.name,
                "soulBinderEnderRailLevels",
                soulBinderEnderRailLevels,
                "The number of levels required to create an ender rail.").getInt(soulBinderEnderRailLevels);

        soulBinderMaxXpLevel = config.get(
                sectionSoulBinder.name,
                "soulBinderMaxXPLevel",
                soulBinderMaxXpLevel,
                "Maximum level of XP the soul binder can contain.").getInt();

        sliceAndSpliceLevelOnePowerPerTickRF = config
                .get(
                        sectionPower.name,
                        "sliceAndSpliceLevelOnePowerPerTickRF",
                        sliceAndSpliceLevelOnePowerPerTickRF,
                        "The number of RF/t consumed by an unupgraded Slice'N'Splice")
                .getInt(sliceAndSpliceLevelOnePowerPerTickRF);
        sliceAndSpliceLevelTwoPowerPerTickRF = config
                .get(
                        sectionPower.name,
                        "sliceAndSpliceLevelTwoPowerPerTickRF",
                        sliceAndSpliceLevelTwoPowerPerTickRF,
                        "The number of RF/t consumed by a Slice'N'Splice with a double layer capacitor upgrade.")
                .getInt(sliceAndSpliceLevelTwoPowerPerTickRF);
        sliceAndSpliceLevelThreePowerPerTickRF = config
                .get(
                        sectionPower.name,
                        "sliceAndSpliceLevelThreePowerPerTickRF",
                        sliceAndSpliceLevelThreePowerPerTickRF,
                        "The number of RF/t consumed by a Slice'N'Splice with an octadic capacitor upgrade.")
                .getInt(sliceAndSpliceLevelThreePowerPerTickRF);
        sliceAndSpliceLevelFourPowerPerTickRF = config
                .get(
                        sectionPower.name,
                        "sliceAndSpliceLevelFourPowerPerTickRF",
                        sliceAndSpliceLevelFourPowerPerTickRF,
                        "The number of RF/t consumed by a Slice'N'Splice with an crystalline capacitor upgrade.")
                .getInt(sliceAndSpliceLevelFourPowerPerTickRF);
        sliceAndSpliceLevelFivePowerPerTickRF = config
                .get(
                        sectionPower.name,
                        "sliceAndSpliceLevelFivePowerPerTickRF",
                        sliceAndSpliceLevelFivePowerPerTickRF,
                        "The number of RF/t consumed by a Slice'N'Splice with an melodic capacitor upgrade.")
                .getInt(sliceAndSpliceLevelFivePowerPerTickRF);
        sliceAndSpliceLevelSixPowerPerTickRF = config
                .get(
                        sectionPower.name,
                        "sliceAndSpliceLevelSixPowerPerTickRF",
                        sliceAndSpliceLevelSixPowerPerTickRF,
                        "The number of RF/t consumed by a Slice'N'Splice with an stellar capacitor upgrade.")
                .getInt(sliceAndSpliceLevelSixPowerPerTickRF);
        sliceAndSpliceLevelSevenPowerPerTickRF = config
                .get(
                        sectionPower.name,
                        "sliceAndSpliceLevelSevenPowerPerTickRF",
                        sliceAndSpliceLevelSevenPowerPerTickRF,
                        "The number of RF/t consumed by a Slice'N'Splice with an totemic capacitor upgrade.")
                .getInt(sliceAndSpliceLevelSevenPowerPerTickRF);
        sliceAndSpliceLevelEightPowerPerTickRF = config
                .get(
                        sectionPower.name,
                        "sliceAndSpliceLevelEightPowerPerTickRF",
                        sliceAndSpliceLevelEightPowerPerTickRF,
                        "The number of RF/t consumed by a Slice'N'Splice with an silver capacitor upgrade.")
                .getInt(sliceAndSpliceLevelEightPowerPerTickRF);
        sliceAndSpliceLevelNinePowerPerTickRF = config
                .get(
                        sectionPower.name,
                        "sliceAndSpliceLevelNinePowerPerTickRF",
                        sliceAndSpliceLevelNinePowerPerTickRF,
                        "The number of RF/t consumed by a Slice'N'Splice with an endergetic capacitor upgrade.")
                .getInt(sliceAndSpliceLevelNinePowerPerTickRF);
        sliceAndSpliceLevelTenPowerPerTickRF = config
                .get(
                        sectionPower.name,
                        "sliceAndSpliceLevelTenPowerPerTickRF",
                        sliceAndSpliceLevelTenPowerPerTickRF,
                        "The number of RF/t consumed by a Slice'N'Splice with an endergised capacitor upgrade.")
                .getInt(sliceAndSpliceLevelTenPowerPerTickRF);

        attractorRangeLevelOne = config.get(
                sectionAttractor.name,
                "attractorRangeLevelOne",
                attractorRangeLevelOne,
                "The range of the mob attractor with no upgrades").getInt(attractorRangeLevelOne);
        attractorRangeLevelTwo = config
                .get(
                        sectionAttractor.name,
                        "attractorRangeLevelTwo",
                        attractorRangeLevelTwo,
                        "The range of the mob attractor with a double layer capacitor upgrade")
                .getInt(attractorRangeLevelTwo);
        attractorRangeLevelThree = config
                .get(
                        sectionAttractor.name,
                        "attractorRangeLevelThree",
                        attractorRangeLevelThree,
                        "The range of the mob attractor with an octadic capacitor upgrade")
                .getInt(attractorRangeLevelThree);
        attractorPowerPerTickLevelOne = config.get(
                sectionAttractor.name,
                "attractorPowerPerTickLevelOne",
                attractorPowerPerTickLevelOne,
                "The RF/t  power use of a levele 1 mob attractor").getInt(attractorPowerPerTickLevelOne);
        attractorPowerPerTickLevelTwo = config.get(
                sectionAttractor.name,
                "attractorPowerPerTickLevelTwo",
                attractorPowerPerTickLevelTwo,
                "The RF/t  power use of a levele 2 mob attractor").getInt(attractorPowerPerTickLevelTwo);
        attractorPowerPerTickLevelThree = config.get(
                sectionAttractor.name,
                "attractorPowerPerTickLevelThree",
                attractorPowerPerTickLevelThree,
                "The RF/t  power use of a levele 3 mob attractor").getInt(attractorPowerPerTickLevelThree);

        spawnGuardRangeLevelOne = config.get(
                sectionAttractor.name,
                "spawnGuardRangeLevelOne",
                spawnGuardRangeLevelOne,
                "The range of the spawn guard with no upgrades").getInt(spawnGuardRangeLevelOne);
        spawnGuardRangeLevelTwo = config
                .get(
                        sectionAttractor.name,
                        "spawnGuardRangeLevelTwo",
                        spawnGuardRangeLevelTwo,
                        "The range of the spawn guard with a double layer capacitor upgrade")
                .getInt(spawnGuardRangeLevelTwo);
        spawnGuardRangeLevelThree = config
                .get(
                        sectionAttractor.name,
                        "spawnGuardRangeLevelThree",
                        spawnGuardRangeLevelThree,
                        "The range of the spawn guard with an octadic capacitor upgrade")
                .getInt(spawnGuardRangeLevelThree);
        spawnGuardPowerPerTickLevelOne = config.get(
                sectionAttractor.name,
                "spawnGuardPowerPerTickLevelOne",
                spawnGuardPowerPerTickLevelOne,
                "The RF/t  power use of a levele 1 spawn guard").getInt(spawnGuardPowerPerTickLevelOne);
        spawnGuardPowerPerTickLevelTwo = config.get(
                sectionAttractor.name,
                "spawnGuardPowerPerTickLevelTwo",
                spawnGuardPowerPerTickLevelTwo,
                "The RF/t  power use of a levele 2 spawn guard").getInt(spawnGuardPowerPerTickLevelTwo);
        spawnGuardPowerPerTickLevelThree = config.get(
                sectionAttractor.name,
                "spawnGuardPowerPerTickLevelThree",
                spawnGuardPowerPerTickLevelThree,
                "The RF/t  power use of a levele 3 spawn guard").getInt(spawnGuardPowerPerTickLevelThree);
        spawnGuardStopAllSlimesDebug = config.getBoolean(
                "spawnGuardStopAllSlimesDebug",
                sectionAttractor.name,
                spawnGuardStopAllSlimesDebug,
                "When true slimes wont be allowed to spawn at all. Only added to aid testing in super flat worlds.");
        spawnGuardStopAllSquidSpawning = config.getBoolean(
                "spawnGuardStopAllSquidSpawning",
                sectionAttractor.name,
                spawnGuardStopAllSquidSpawning,
                "When true no squid will be spawned.");

        weatherObeliskClearFluid = config.get(
                sectionWeather.name,
                "weatherObeliskClearFluid",
                weatherObeliskClearFluid,
                "The fluid required (in mB) to set the world to clear weather").getInt();
        weatherObeliskRainFluid = config.get(
                sectionWeather.name,
                "weatherObeliskRainFluid",
                weatherObeliskRainFluid,
                "The fluid required (in mB) to set the world to rainy weather").getInt();
        weatherObeliskThunderFluid = config.get(
                sectionWeather.name,
                "weatherObeliskThunderFluid",
                weatherObeliskThunderFluid,
                "The fluid required (in mB) to set the world to thundering weather").getInt();

        // Loot Config
        lootDarkSteel = config.getBoolean(
                "lootDarkSteel",
                sectionLootConfig.name,
                lootDarkSteel,
                "Adds Darksteel Ingots to loot tables");
        lootItemConduitProbe = config.getBoolean(
                "lootItemConduitProbe",
                sectionLootConfig.name,
                lootItemConduitProbe,
                "Adds ItemConduitProbe to loot tables");
        lootQuartz = config.getBoolean("lootQuartz", sectionLootConfig.name, lootQuartz, "Adds quartz to loot tables");
        lootNetherWart = config.getBoolean(
                "lootNetherWart",
                sectionLootConfig.name,
                lootNetherWart,
                "Adds nether wart to loot tables");
        lootEnderPearl = config.getBoolean(
                "lootEnderPearl",
                sectionLootConfig.name,
                lootEnderPearl,
                "Adds ender pearls to loot tables");
        lootElectricSteel = config.getBoolean(
                "lootElectricSteel",
                sectionLootConfig.name,
                lootElectricSteel,
                "Adds Electric Steel Ingots to loot tables");
        lootRedstoneAlloy = config.getBoolean(
                "lootRedstoneAlloy",
                sectionLootConfig.name,
                lootRedstoneAlloy,
                "Adds Redstone Alloy Ingots to loot tables");
        lootPhasedIron = config.getBoolean(
                "lootPhasedIron",
                sectionLootConfig.name,
                lootPhasedIron,
                "Adds Phased Iron Ingots to loot tables");
        lootPhasedGold = config.getBoolean(
                "lootPhasedGold",
                sectionLootConfig.name,
                lootPhasedGold,
                "Adds Phased Gold Ingots to loot tables");
        lootTravelStaff = config.getBoolean(
                "lootTravelStaff",
                sectionLootConfig.name,
                lootTravelStaff,
                "Adds Travel Staff to loot tables");
        lootTheEnder = config
                .getBoolean("lootTheEnder", sectionLootConfig.name, lootTheEnder, "Adds The Ender to loot tables");
        lootDarkSteelBoots = config.getBoolean(
                "lootDarkSteelBoots",
                sectionLootConfig.name,
                lootDarkSteelBoots,
                "Adds Darksteel Boots to loot tables");

        enderRailEnabled = config.getBoolean(
                "enderRailEnabled",
                sectionRailConfig.name,
                enderRailEnabled,
                "Whether Ender Rails are enabled");
        enderRailPowerRequireCrossDimensions = config
                .get(
                        sectionRailConfig.name,
                        "enderRailPowerRequireCrossDimensions",
                        enderRailPowerRequireCrossDimensions,
                        "The amount of power required to transport a cart across dimensions")
                .getInt(enderRailPowerRequireCrossDimensions);
        enderRailPowerRequiredPerBlock = config
                .get(
                        sectionRailConfig.name,
                        "enderRailPowerRequiredPerBlock",
                        enderRailPowerRequiredPerBlock,
                        "The amount of power required to teleport a cart per block in the same dimension")
                .getInt(enderRailPowerRequiredPerBlock);
        enderRailCapSameDimensionPowerAtCrossDimensionCost = config.getBoolean(
                "enderRailCapSameDimensionPowerAtCrossDimensionCost",
                sectionRailConfig.name,
                enderRailCapSameDimensionPowerAtCrossDimensionCost,
                "When set to true the RF cost of sending a cart within the same dimension will be capped to the cross dimension cost");
        enderRailTicksBeforeForceSpawningLinkedCarts = config.get(
                sectionRailConfig.name,
                "enderRailTicksBeforeForceSpawningLinkedCarts",
                enderRailTicksBeforeForceSpawningLinkedCarts,
                "The number of ticks to wait for the track to clear before force spawning the next cart in a (RailCraft) linked set")
                .getInt(enderRailTicksBeforeForceSpawningLinkedCarts);
        enderRailTeleportPlayers = config.getBoolean(
                "enderRailTeleportPlayers",
                sectionRailConfig.name,
                enderRailTeleportPlayers,
                "If true player in minecarts will be teleported. WARN: WIP, seems to cause a memory leak.");

        dumpMobNames = config.getBoolean(
                "dumpMobNames",
                sectionMobConfig.name,
                dumpMobNames,
                "When set to true a list of all registered mobs will be dumped to config/enderio/mobTypes.txt The names are in the format required by EIOs mob blacklists.");

        xpObeliskMaxXpLevel = config.get(
                sectionMisc.name,
                "xpObeliskMaxXpLevel",
                xpObeliskMaxXpLevel,
                "Maximum level of XP the xp obelisk can contain.").getInt();
        xpJuiceName = config.getString(
                "xpJuiceName",
                sectionMisc.name,
                xpJuiceName,
                "Id of liquid XP fluid (WARNING: only for users who know what they are doing - changing this id can break worlds) - this should match with OpenBlocks when installed");

        clearGlassSameTexture = config.getBoolean(
                "clearGlassSameTexture",
                sectionMisc.name,
                clearGlassSameTexture,
                "If true, quite clear glass will use the fused quartz border texture for the block instead of the white border.");
        clearGlassConnectToFusedQuartz = config.getBoolean(
                "clearGlassConnectToFusedQuartz",
                sectionMisc.name,
                clearGlassConnectToFusedQuartz,
                "If true, quite clear glass will connect textures with fused quartz.");

        enchantmentSoulBoundEnabled = config.getBoolean(
                "enchantmentSoulBoundEnabled",
                sectionEnchantments.name,
                enchantmentSoulBoundEnabled,
                "If false the soul bound enchantment will not be available");
        enchantmentSoulBoundId = config
                .get(
                        sectionEnchantments.name,
                        "enchantmentSoulBoundId",
                        enchantmentSoulBoundId,
                        "The id of the enchantment. If set to -1 the lowest unassigned id will be used.")
                .getInt(enchantmentSoulBoundId);
        enchantmentSoulBoundWeight = config
                .get(
                        sectionEnchantments.name,
                        "enchantmentSoulBoundWeight",
                        enchantmentSoulBoundWeight,
                        "The chance of getting this enchantment in the enchantment table")
                .getInt(enchantmentSoulBoundWeight);

        replaceWitherSkeletons = config.get(
                sectionMisc.name,
                "replaceWitherSkeletons",
                replaceWitherSkeletons,
                "Separates wither and normal skeletons into different entities, enables the powered spawner to treat them differently [EXPERIMENTAL - MAY CAUSE ISSUES WITH OTHER MODS]")
                .getBoolean();

        enableWaterFromBottles = config.get(
                sectionMisc.name,
                "enableWaterFromBottles",
                enableWaterFromBottles,
                "Enables emptying vanilla water bottles without breaking the bottle. In combination with a water source block this allows duping of water without cost.")
                .getBoolean();

        telepadLockDimension = config
                .get(
                        sectionTelepad.name,
                        "lockDimension",
                        telepadLockDimension,
                        "If true, the dimension cannot be set via the GUI, the coord selector must be used.")
                .getBoolean();
        telepadLockCoords = config
                .get(
                        sectionTelepad.name,
                        "lockCoords",
                        telepadLockCoords,
                        "If true, the coordinates cannot be set via the GUI, the coord selector must be used.")
                .getBoolean();
        telepadPowerCoefficient = config.get(
                sectionTelepad.name,
                "powerCoefficient",
                telepadPowerCoefficient,
                "Power for a teleport is calculated by the formula:\npower = [this value] * ln(0.005*distance + 1)")
                .getInt();
        telepadPowerInterdimensional = config.get(
                sectionTelepad.name,
                "powerInterdimensional",
                telepadPowerInterdimensional,
                "The amount of RF required for an interdimensional teleport.").getInt();
        telepadPowerPerTickRF = config.get(
                sectionTelepad.name,
                "telepadPowerPerTickRF",
                telepadPowerPerTickRF,
                "The number of RF/t consumed by a telepad.").getInt();
        telepadPowerStorageRF = config.get(
                sectionTelepad.name,
                "telepadPowerStorageRF",
                telepadPowerStorageRF,
                "The amount of RF a telepad can store.").getInt();

        inventoryPanelFree = config.getBoolean(
                "inventoryPanelFree",
                sectionInventoryPanel.name,
                inventoryPanelFree,
                "If true, the inv panel will not accept fluids and will be active permanently.");
        inventoryPanelPowerPerMB = config.getFloat(
                "powerPerMB",
                sectionInventoryPanel.name,
                inventoryPanelPowerPerMB,
                1.0f,
                10000.0f,
                "Internal power generated per mB. The default of 800/mB matches the RF generation of the Zombie generator. A panel tries to refill only once every second - setting this value too low slows down the scanning speed.");
        inventoryPanelScanCostPerSlot = config.getFloat(
                "scanCostPerSlot",
                sectionInventoryPanel.name,
                inventoryPanelScanCostPerSlot,
                0.0f,
                10.0f,
                "Internal power used for scanning a slot");
        inventoryPanelExtractCostPerItem = config.getFloat(
                "extractCostPerItem",
                sectionInventoryPanel.name,
                inventoryPanelExtractCostPerItem,
                0.0f,
                10.0f,
                "Internal power used per item extracted (not a stack of items)");
        inventoryPanelExtractCostPerOperation = config.getFloat(
                "extractCostPerOperation",
                sectionInventoryPanel.name,
                inventoryPanelExtractCostPerOperation,
                0.0f,
                10000.0f,
                "Internal power used per extract operation (independent of stack size)");
    }

    public static void checkYetaAccess() {
        if (!useSneakMouseWheelYetaWrench && !useSneakRightClickYetaWrench) {
            Log.warn(
                    "Both useSneakMouseWheelYetaWrench and useSneakRightClickYetaWrench are set to false. Enabling right click.");
            useSneakRightClickYetaWrench = true;
        }
    }

    public static void init() {}

    public static void postInit() {
        for (String s : hoeStrings) {
            ItemStack hoe = getStackForString(s);
            if (hoe != null) {
                farmHoes.add(hoe);
            }
        }
    }

    public static ItemStack getStackForString(String s) {
        String[] nameAndMeta = s.split(";");
        int meta = nameAndMeta.length == 1 ? 0 : Integer.parseInt(nameAndMeta[1]);
        String[] data = nameAndMeta[0].split(":");
        ItemStack stack = GameRegistry.findItemStack(data[0], data[1], 1);
        if (stack == null) {
            return null;
        }
        stack.setItemDamage(meta);
        return stack;
    }

    private Config() {}
}
