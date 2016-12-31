package crazypants.enderio.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import com.enderio.core.common.event.ConfigFileChangedEvent;
import com.enderio.core.common.vecmath.VecmathUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.capacitor.CapacitorKey;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.Things;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;

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
  public static final Section sectionRod = new Section("Rod of Return Settings", "rod");
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
  // public static final Section sectionRailConfig = new Section("Rail", "railconfig");
  public static final Section sectionEnchantments = new Section("Enchantments", "enchantments");
  public static final Section sectionWeather = new Section("Weather", "weather");
  public static final Section sectionTelepad = new Section("Telepad", "telepad");
  public static final Section sectionInventoryPanel = new Section("InventoryPanel", "inventorypanel");
  public static final Section sectionMisc = new Section("Misc", "misc");
  public static final Section sectionCapacitor = new Section("Capacitor Values", "capacitor");
  public static final Section sectionTOP = new Section("The One Probe integration", "top");
  public static final Section sectionHoes = new Section("Farm Settings.Hoes", "hoes");

  public static final double DEFAULT_CONDUIT_SCALE = 0.6;

  public static final float EXPLOSION_RESISTANT = 2000f * 3.0f / 5.0f; // obsidian

  public static boolean registerRecipes = true;

  public static boolean allowFovControlsInSurvivalMode = false;

  public static boolean jeiUseShortenedPainterRecipes = true;

  public static boolean reinforcedObsidianEnabled = true;

  public static boolean photovoltaicCellEnabled = true;

  public static boolean reservoirEnabled = true;

  public static double conduitScale = DEFAULT_CONDUIT_SCALE;

  public static boolean transceiverEnabled = true;
  public static double transceiverEnergyLoss = 0.1;
  public static int transceiverBucketTransmissionCostRF = 100;

  public static File configDirectory;

  public static int recipeLevel = 2;
  public static boolean addPeacefulRecipes = false;
  public static boolean createSyntheticRecipes = true;

  public static boolean detailedPowerTrackingEnabled = false;

  public static boolean useSneakMouseWheelYetaWrench = true;
  public static boolean useSneakRightClickYetaWrench = false;
  public static int     yetaWrenchOverlayMode = 0;
  
  public static boolean itemConduitUsePhyscialDistance = false;
  
  public static boolean redstoneConduitsShowState = true;

  public static int enderFluidConduitExtractRate = 200;
  public static int enderFluidConduitMaxIoRate = 800;
  public static int advancedFluidConduitExtractRate = 100;
  public static int advancedFluidConduitMaxIoRate = 400;
  public static int fluidConduitExtractRate = 50;
  public static int fluidConduitMaxIoRate = 200;

  public static boolean fluidConduitDynamicLighting = true;
  
  public static boolean updateLightingWhenHidingFacades = false;
  public static boolean transparentFacesLetThroughBeaconBeam = true;

  public static boolean travelAnchorEnabled = true;
  public static int travelAnchorMaximumDistance = 96;
  public static int travelAnchorCooldown = 0;
  public static boolean travelAnchorSneak = true;
  public static boolean travelAnchorSkipWarning = true;

  public static int travelStaffMaximumDistance = 256;
  public static float travelStaffPowerPerBlockRF = 250;

  public static int travelStaffMaxBlinkDistance = 16;
  public static int travelStaffBlinkPauseTicks = 10;

  public static boolean travelStaffBlinkEnabled = true;
  public static boolean travelStaffBlinkThroughSolidBlocksEnabled = true;
  public static boolean travelStaffBlinkThroughClearBlocksEnabled = true;
  public static boolean travelStaffBlinkThroughUnbreakableBlocksEnabled = false;
  public static String[] travelStaffBlinkBlackList = new String[] {
      "minecraft:bedrock",
      "Thaumcraft:blockWarded"
  };
  public static boolean travelStaffOffhandBlinkEnabled = true;
  public static boolean travelStaffOffhandTravelEnabled = true;
  public static boolean travelStaffOffhandEnderIOEnabled = true;
  public static boolean travelStaffOffhandShowsTravelTargets = true;

  public static float travelAnchorZoomScale = 0.2f;

  public static boolean darkSteelRightClickPlaceEnabled = true;
  
  public static double[] darkSteelPowerDamgeAbsorptionRatios = {0.5, 0.6, 0.7, 0.85};
  public static int darkSteelPowerStorageBase = 100000;
  public static int darkSteelPowerStorageLevelOne = 150000;
  public static int darkSteelPowerStorageLevelTwo = 250000;
  public static int darkSteelPowerStorageLevelThree = 1000000;

  public static float darkSteelSpeedOneWalkModifier = 0.15f;
  public static float darkSteelSpeedTwoWalkMultiplier = 0.3f;
  public static float darkSteelSpeedThreeWalkMultiplier = 0.45f;

  public static float darkSteelSpeedOneSprintModifier = 0.1f;
  public static float darkSteelSpeedTwoSprintMultiplier = 0.3f;
  public static float darkSteelSpeedThreeSprintMultiplier = 0.5f;

  public static int darkSteelSpeedOneCost = 4;
  public static int darkSteelSpeedTwoCost = 6;
  public static int darkSteelSpeedThreeCost = 8;
  
  public static double darkSteelBootsJumpModifier = 1.5;
  public static int darkSteelJumpOneCost = 4;
  public static int darkSteelJumpTwoCost = 6;
  public static int darkSteelJumpThreeCost = 8;

  public static boolean slotZeroPlacesEight = true;

  public static int darkSteelWalkPowerCost = darkSteelPowerStorageLevelTwo / 3000;
  public static int darkSteelSprintPowerCost = darkSteelWalkPowerCost * 4;
  public static boolean darkSteelDrainPowerFromInventory = false;
  public static int darkSteelBootsJumpPowerCost = 150;
  public static int darkSteelFallDistanceCost = 75;

  public static float darkSteelSwordPoweredDamageBonus = 1.0f;
  public static float darkSteelSwordPoweredSpeedBonus = 0.4f;
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
  public static double darkSteelSwordEnderPearlDropChance = 1.05;
  public static double darkSteelSwordEnderPearlDropChancePerLooting = 0.5;

  public static boolean darkSteelBowEnabled = true;
  public static float darkSteelBowDamageBonus = 0f;
  public static double[] darkSteelBowForceMultipliers = {1.1f, 1.2f, 1.3f, 1.4f, 1.5f};
  public static int[] darkSteelBowDrawSpeeds = {30, 20, 18, 16, 14};
  public static double[] darkSteelBowFovMultipliers = {0.25, 0.3, 0.35, 0.4, 0.45};
  public static int darkSteelBowPowerUsePerDamagePoint = 1000;
  public static int darkSteelBowPowerUsePerDraw = 750;
  public static int darkSteelBowPowerUsePerTickDrawn = 5;

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
  public static int darkSteelShearsBlockAreaBoostWhenPowered = 4;
  public static float darkSteelShearsEntityAreaBoostWhenPowered = 5.0f;

  public static int darkSteelUpgradeVibrantCost = 4;
  public static int darkSteelUpgradePowerOneCost = 6;
  public static int darkSteelUpgradePowerTwoCost = 8;
  public static int darkSteelUpgradePowerThreeCost = 12;

  public static int darkSteelGliderCost = 4;
  public static double darkSteelGliderHorizontalSpeed = 0.03;
  public static double darkSteelGliderVerticalSpeed = -0.05;
  public static double darkSteelGliderVerticalSpeedSprinting = -0.15;

  public static int darkSteelElytraCost = 10;

  public static int darkSteelGogglesOfRevealingCost = 4;

  public static int darkSteelApiaristArmorCost = 4;

  public static int darkSteelSwimCost = 4;

  public static int darkSteelNightVisionCost = 4;

  public static int darkSteelTOPCost = 4;

  public static int darkSteelSoundLocatorCost = 4;
  public static int darkSteelSoundLocatorRange = 40;
  public static int darkSteelSoundLocatorLifespan = 40;

  public static int darkSteelTravelCost = 16;
  public static int darkSteelSpoonCost = 4;

  public static int darkSteelSolarOneGen = 10;
  public static int darkSteelSolarOneCost = 4;
  public static int darkSteelSolarTwoGen = 40;
  public static int darkSteelSolarTwoCost = 8;
  public static int darkSteelSolarThreeGen = 80;
  public static int darkSteelSolarThreeCost = 24;
  public static boolean darkSteelSolarChargeOthers = true;

  public static float darkSteelAnvilDamageChance = 0.024f;
  public static int darkSteelAnvilMaxLevel = 80;

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
  public static int zombieGeneratorTicksPerBucketFuel = 10000;

  public static boolean addFuelTooltipsToAllFluidContainers = true;
  public static boolean addFurnaceFuelTootip = true;
  public static boolean addDurabilityTootip = true;

  public static int farmActionEnergyUseRF = 500;
  public static int farmAxeActionEnergyUseRF = 1000;
  public static int farmBonemealActionEnergyUseRF = 160;
  public static int farmBonemealTryEnergyUseRF = 80;

  public static boolean farmAxeDamageOnLeafBreak = false;
  public static float farmToolTakeDamageChance = 1;
  public static boolean disableFarmNotification = false;
  public static boolean farmEssenceBerriesEnabled = true;
  public static boolean farmManaBeansEnabled = false;
  public static boolean farmHarvestJungleWhenCocoa = false;
  public static String[] hoeStrings = new String[] {
      "minecraft:wooden_hoe", "minecraft:stone_hoe", "minecraft:iron_hoe", "minecraft:diamond_hoe", "minecraft:golden_hoe",
      "MekanismTools:ObsidianHoe", "MekanismTools:LapisLazuliHoe", "MekanismTools:OsmiumHoe", "MekanismTools:BronzeHoe", "MekanismTools:GlowstoneHoe",
      "MekanismTools:SteelHoe",
      "Steamcraft:hoeBrass", "Steamcraft:hoeGildedGold",
      "TConstruct:mattock",
      "ProjRed|Exploration:projectred.exploration.hoeruby", "ProjRed|Exploration:projectred.exploration.hoesapphire",
      "ProjRed|Exploration:projectred.exploration.hoeperidot",
      "magicalcrops:magicalcrops_AccioHoe", "magicalcrops:magicalcrops_CrucioHoe", "magicalcrops:magicalcrops_ImperioHoe",
      // disabled as it is currently not unbreaking as advertised "magicalcrops:magicalcrops_ZivicioHoe",
      "magicalcrops:magicalcropsarmor_AccioHoe", "magicalcrops:magicalcropsarmor_CrucioHoe", "magicalcrops:magicalcropsarmor_ImperioHoe",
      "BiomesOPlenty:hoeAmethyst", "BiomesOPlenty:hoeMud",
      "Eln:Eln.Copper Hoe",
      "Thaumcraft:ItemHoeThaumium", "Thaumcraft:ItemHoeElemental", "Thaumcraft:ItemHoeVoid",
      "ThermalFoundation:tool.hoeInvar", "ThermalFoundation:tool.hoeCopper", "ThermalFoundation:tool.hoeBronze", "ThermalFoundation:tool.hoeSilver",
      "ThermalFoundation:tool.hoeElectrum", "ThermalFoundation:tool.hoeTin", "ThermalFoundation:tool.hoeLead", "ThermalFoundation:tool.hoeNickel",
      "ThermalFoundation:tool.hoePlatinum",
      "TwilightForest:item.steeleafHoe", "TwilightForest:item.ironwoodHoe",
      "IC2:itemToolBronzeHoe", "techreborn:bronzeHoe", "techreborn:rubyHoe", "techreborn:sapphireHoe", "techreborn:peridotHoe", "basemetals:adamantine_hoe",
      "basemetals:aquarium_hoe", "basemetals:brass_hoe", "basemetals:bronze_hoe", "basemetals:coldiron_hoe", "basemetals:copper_hoe",
      "basemetals:cupronickel_hoe", "basemetals:electrum_hoe", "basemetals:invar_hoe", "basemetals:lead_hoe", "basemetals:mithril_hoe", "basemetals:nickel_hoe",
      "basemetals:platinum_hoe", "basemetals:silver_hoe", "basemetals:starsteel_hoe", "basemetals:steel_hoe", "basemetals:tin_hoe",
      "actuallyadditions:itemHoeQuartz", "actuallyadditions:itemHoeEmerald", "actuallyadditions:itemHoeObsidian",
      "actuallyadditions:itemHoeCrystalRed", "actuallyadditions:itemHoeCrystalBlue", "actuallyadditions:itemHoeCrystalLightBlue",
      "actuallyadditions:itemHoeCrystalBlack", "actuallyadditions:itemHoeCrystalGreen", "actuallyadditions:itemHoeCrystalWhite", "silentgems:Hoe",
      "ic2:bronze_hoe", // IC2exp 1.10
      "appliedenergistics2:nether_quartz_hoe", "appliedenergistics2:certus_quartz_hoe", // AE2 1.10
      "railcraft:tool_hoe_steel", // Railcraft 1.10
      // new in 1.10:
      "calculator:ReinforcedHoe", "calculator:EnrichedGoldHoe", "calculator:ReinforcedIronHoe", "calculator:RedstoneHoe", "calculator:WeakenedDiamondHoe",
      "calculator:FlawlessDiamondHoe", "calculator:FireDiamondHoe", "calculator:ElectricHoe", "embers:hoeDawnstone", "embers:hoeCopper", "embers:hoeSilver",
      "embers:hoeLead", "roots:livingHoe", "mysticalagriculture:inferium_hoe", "mysticalagriculture:prudentium_hoe", "mysticalagriculture:intermedium_hoe",
      "mysticalagriculture:superium_hoe", "mysticalagriculture:supremium_hoe"
  };
  public static Things farmHoes = new Things();
  public static int farmSaplingReserveAmount = 8;
  public static boolean farmStopOnNoOutputSlots = true;
  public static boolean farmEvictEmptyRFTools = true;

  public static int magnetPowerUsePerSecondRF = 1;
  public static int magnetPowerCapacityRF = 100000;
  public static int magnetRange = 5;
  public static String[] magnetBlacklist = new String[] { "appliedenergistics2:item.ItemCrystalSeed", "Botania:livingrock",
      "Botania:manaTablet" };
  public static int magnetMaxItems = 20;

  public static boolean magnetAllowInMainInventory = false;
  public static boolean magnetAllowInBaublesSlot = true;
  public static boolean magnetAllowDeactivatedInBaublesSlot = false;
  public static String  magnetBaublesType = "AMULET";
  
  public static int crafterRfPerCraft = 2500;

  public static int capacitorBankMaxIoRF = 5000;
  public static int capacitorBankMaxStorageRF = 5000000;

  public static int capacitorBankTierOneMaxIoRF = 1000;
  public static int capacitorBankTierOneMaxStorageRF = 1000000;

  public static int capacitorBankTierTwoMaxIoRF = 5000;
  public static int capacitorBankTierTwoMaxStorageRF = 5000000;

  public static int capacitorBankTierThreeMaxIoRF = 25000;
  public static int capacitorBankTierThreeMaxStorageRF = 25000000;

  public static boolean capacitorBankRenderPowerOverlayOnItem = false;

  public static int poweredSpawnerMinDelayTicks = 200;
  public static int poweredSpawnerMaxDelayTicks = 800;
  public static int poweredSpawnerMaxPlayerDistance = 0;
  public static int poweredSpawnerDespawnTimeSeconds = 120;
  public static int poweredSpawnerSpawnCount = 4;
  public static int poweredSpawnerSpawnRange = 4;
  public static int poweredSpawnerMaxNearbyEntities = 6;
  public static int poweredSpawnerMaxSpawnTries = 3;
  public static boolean poweredSpawnerUseVanillaSpawChecks = false;
  public static double brokenSpawnerDropChance = 1;
  public static String[] brokenSpawnerToolBlacklist = new String[] {
    "RotaryCraft:rotarycraft_item_bedpick"
  };
  public static int powerSpawnerAddSpawnerCost = 16;

  public static int painterEnergyPerTaskRF = 2000;

  public static int vacuumChestRange = 6;

  public static int wirelessChargerRange = 24;

  public static long nutrientFoodBoostDelay = 400;
  public static boolean rocketFuelIsExplosive = true;

  public static int enchanterBaseLevelCost = 2;
  public static double enchanterLevelCostFactor = 0.75;
  public static double enchanterLapisCostFactor = 3;

  public static boolean machineSoundsEnabled = true;

  public static float machineSoundVolume = 1.0f;

  public static int killerJoeNutrientUsePerAttackMb = 5;
  public static double killerJoeAttackHeight = 2;
  public static double killerJoeAttackWidth = 2;
  public static double killerJoeAttackLength = 4;
  public static double killerJoeHooverXpWidth = 5;
  public static double killerJoeHooverXpLength = 10;
  public static boolean killerJoeMustSee = false;
  public static boolean killerPvPoffDisablesSwing = false;
  public static boolean killerPvPoffIsIgnored = false;
  public static boolean killerMendingEnabled = true;
  public static boolean killerProvokesCreeperExpolosions = false;

  public static double xpVacuumRange = 10;

  public static boolean allowTileEntitiesAsPaintSource = true;

  public static boolean enableMEConduits = true;
  public static boolean enableOCConduits = true;
  public static boolean enableOCConduitsAnimatedTexture = true;

  public static List<String> soulVesselBlackList = Collections.<String> emptyList();
  public static List<String> soulVesselUnspawnableList = new ArrayList<String>();
  static {
    soulVesselUnspawnableList.add("chickens.ChickensChicken");
  }
  public static boolean soulVesselCapturesBosses = false;

  public static int soulBinderBrokenSpawnerRF = 2500000;
  public static int soulBinderBrokenSpawnerLevels = 8;
  public static int soulBinderReanimationRF = 100000;
  public static int soulBinderReanimationLevels = 4;
  public static int soulBinderEnderCystalRF = 150000;
  public static int soulBinderEnderCystalLevels = 6;
  public static int soulBinderPrecientCystalRF = 200000;
  public static int soulBinderPrecientCystalLevels = 8;
  public static int soulBinderAttractorCystalRF = 100000;
  public static int soulBinderAttractorCystalLevels = 4;
  public static int soulBinderTunedPressurePlateLevels = 2;
  public static int soulBinderTunedPressurePlateRF = 250000;
  public static int soulBinderMaxXpLevel = 40;

  public static boolean powerConduitCanDifferentTiersConnect = false;
  public static int powerConduitTierOneRF = 640;
  public static int powerConduitTierTwoRF = 5120;
  public static int powerConduitTierThreeRF = 20480;

  public static boolean spawnGuardStopAllSlimesDebug = false;
  public static boolean spawnGuardStopAllSquidSpawning = false;

  public static int weatherObeliskClearFluid = 2000;
  public static int weatherObeliskRainFluid = 500;
  public static int weatherObeliskThunderFluid = 1000;

  //Loot Defaults
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

  public static int xpObeliskMaxXpLevel = Integer.MAX_VALUE;
  public static String xpJuiceName = "xpjuice";

  public static boolean clearGlassConnectToFusedQuartz = false;
  public static boolean glassConnectToTheirVariants = true;
  public static boolean glassConnectToTheirColorVariants = true;
  
  public static Rarity enchantmentSoulBoundRarity = Rarity.VERY_RARE;
  public static boolean enchantmentSoulBoundEnabled = true;

  public static boolean telepadLockDimension = true;
  public static boolean telepadLockCoords = true;
  public static int telepadPowerCoefficient = 100000;
  public static int telepadPowerInterdimensional = 100000;
  public static boolean telepadShrinkEffect = true;
  public static boolean telepadIsTravelAnchor = true;
  public static int telepadEnergyBufferRF = 100000;
  public static int telepadEnergyUsePerTickRF = 4000;
  public static String telepadFluidType = "ender_distillation";
  public static int telepadFluidUse = 50;
  
  public static boolean rodOfReturnCanTargetAnywhere = false;
  public static int rodOfReturnTicksToActivate = 50;
  public static int rodOfReturnPowerStorage = 2000000;
  public static int rodOfReturnMinTicksToRecharge = 100;
  public static int rodOfReturnRfPerTick = 35000;
  public static int rodOfReturnFluidUsePerTeleport = 200;
  public static int rodOfReturnFluidStorage = 200;
  public static String rodOfReturnFluidType = "ender_distillation";
  
  public static boolean inventoryPanelFree = false;
  public static float inventoryPanelPowerPerMB = 800.0f;
  public static float inventoryPanelScanCostPerSlot = 0.1f;
  public static float inventoryPanelExtractCostPerItem = 12.0f;
  public static float inventoryPanelExtractCostPerOperation = 32.0f;
  public static boolean inventoryPanelScaleText = true;

  public static int[] remoteInventoryMBPerOpen = { 100, 25, 15 };
  public static int[] remoteInventoryRFPerTick = {4, 6, 8};
  public static int[] remoteInventoryMBCapacity = {2000, 1000, 1500};
  public static int[] remoteInventoryRFCapacity = {60000, 120000, 150000};
  public static String[] remoteInventoryFluidTypes = {"nutrient_distillation", "ender_distillation", "vapor_of_levity"};

  public static String coldFireIgniterFluidType = "vapor_of_levity";
  public static int coldFireIgniterMbPerUse = 10;

  public static boolean photovoltaicCanTypesJoins = true;
  public static int photovoltaicRecalcSunTick = 100;

  public static boolean debugUpdatePackets = false;

  public static boolean topEnabled = true;
  public static boolean topShowProgressByDefault = true;
  public static boolean topShowPowerByDefault = true;
  public static boolean topShowRedstoneByDefault = false;
  public static boolean topShowSideConfigByDefault = false;
  public static boolean topShowRangeByDefault = false;
  public static boolean topShowMobsByDefault = true;
  public static boolean topShowTanksByDefault = true;
  public static boolean topShowXPByDefault = true;
  public static boolean topShowItemCountDefault = true;
  
  public static boolean paintedGlowstoneRequireSilkTouch = false;
  
  public static boolean enableBaublesIntegration = true;

  public static String leversEnabled = "10,30,60,300";

  public static void preInit(FMLPreInitializationEvent event) {
    PacketHandler.INSTANCE.registerMessage(PacketConfigSync.class, PacketConfigSync.class, PacketHandler.nextID(), Side.CLIENT);
    MinecraftForge.EVENT_BUS.register(new Config());
    configDirectory = new File(event.getModConfigurationDirectory(), EnderIO.DOMAIN);
    if(!configDirectory.exists()) {
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
      if(config.hasChanged()) {
        config.save();
      }
    }
  }

  @SubscribeEvent
  public void onConfigChanged(OnConfigChangedEvent event) {
    if (event.getModID().equals(EnderIO.MODID)) {
      Log.info("Updating config...");
      syncConfig(false);
      init(null);
      postInit();
    }
  }

  @SubscribeEvent
  public void onConfigFileChanged(ConfigFileChangedEvent event) {
    if (event.getModID().equals(EnderIO.MODID)) {
      Log.info("Updating config...");
      syncConfig(true);
      event.setSuccessful();
      init(null);
      postInit();
    }
  }

  @SubscribeEvent
  public void onPlayerLoggon(PlayerLoggedInEvent evt) {
    PacketHandler.INSTANCE.sendTo(new PacketConfigSync(), (EntityPlayerMP) evt.player);
  }

  
  public static void processConfig(Configuration config) {

    capacitorBankMaxIoRF = config.get(sectionPower.name, "capacitorBankMaxIoRF", capacitorBankMaxIoRF, "The maximum IO for a single capacitor in RF/t")
        .getInt(capacitorBankMaxIoRF);
    capacitorBankMaxStorageRF = config.get(sectionPower.name, "capacitorBankMaxStorageRF", capacitorBankMaxStorageRF,
        "The maximum storage for a single capacitor in RF")
        .getInt(capacitorBankMaxStorageRF);

    capacitorBankTierOneMaxIoRF = config.get(sectionPower.name, "capacitorBankTierOneMaxIoRF", capacitorBankTierOneMaxIoRF,
        "The maximum IO for a single tier one capacitor in RF/t")
        .getInt(capacitorBankTierOneMaxIoRF);
    capacitorBankTierOneMaxStorageRF = config.get(sectionPower.name, "capacitorBankTierOneMaxStorageRF", capacitorBankTierOneMaxStorageRF,
        "The maximum storage for a single tier one capacitor in RF")
        .getInt(capacitorBankTierOneMaxStorageRF);

    capacitorBankTierTwoMaxIoRF = config.get(sectionPower.name, "capacitorBankTierTwoMaxIoRF", capacitorBankTierTwoMaxIoRF,
        "The maximum IO for a single tier two capacitor in RF/t")
        .getInt(capacitorBankTierTwoMaxIoRF);
    capacitorBankTierTwoMaxStorageRF = config.get(sectionPower.name, "capacitorBankTierTwoMaxStorageRF", capacitorBankTierTwoMaxStorageRF,
        "The maximum storage for a single tier two capacitor in RF")
        .getInt(capacitorBankTierTwoMaxStorageRF);

    capacitorBankTierThreeMaxIoRF = config.get(sectionPower.name, "capacitorBankTierThreeMaxIoRF", capacitorBankTierThreeMaxIoRF,
        "The maximum IO for a single tier three capacitor in RF/t")
        .getInt(capacitorBankTierThreeMaxIoRF);
    capacitorBankTierThreeMaxStorageRF = config.get(sectionPower.name, "capacitorBankTierThreeMaxStorageRF", capacitorBankTierThreeMaxStorageRF,
        "The maximum storage for a single tier three capacitor in RF")
        .getInt(capacitorBankTierThreeMaxStorageRF);

    capacitorBankRenderPowerOverlayOnItem = config.getBoolean("capacitorBankRenderPowerOverlayOnItem", sectionAesthetic.name,
        capacitorBankRenderPowerOverlayOnItem, "When true the the capacitor bank item wil get a power bar in addition to the gauge on the bank");

    powerConduitTierOneRF = config.get(sectionPower.name, "powerConduitTierOneRF", powerConduitTierOneRF, "The maximum IO for the tier 1 power conduit")
        .getInt(powerConduitTierOneRF);
    powerConduitTierTwoRF = config.get(sectionPower.name, "powerConduitTierTwoRF", powerConduitTierTwoRF, "The maximum IO for the tier 2 power conduit")
        .getInt(powerConduitTierTwoRF);
    powerConduitTierThreeRF = config.get(sectionPower.name, "powerConduitTierThreeRF", powerConduitTierThreeRF, "The maximum IO for the tier 3 power conduit")
        .getInt(powerConduitTierThreeRF);
    powerConduitCanDifferentTiersConnect = config
        .getBoolean("powerConduitCanDifferentTiersConnect", sectionPower.name, powerConduitCanDifferentTiersConnect,
            "If set to false power conduits of different tiers cannot be connected. in this case a block such as a cap. bank is needed to bridge different tiered networks");


    painterEnergyPerTaskRF = config.get(sectionPower.name, "painterEnergyPerTaskRF", painterEnergyPerTaskRF,
        "The total amount of RF required to paint one block")
        .getInt(painterEnergyPerTaskRF);

    recipeLevel = config.get(sectionRecipe.name, "recipeLevel", recipeLevel,
        "How expensive should the crafting recipes be? 0=cheapest, 1=cheaper, 2=normal, 3=expensive").getInt(
        recipeLevel);

    registerRecipes = config
        .get(sectionRecipe.name, "registerRecipes", registerRecipes,
            "If set to false: No crafting recipes (crafting table and furnace) will be registered. You need to use Creative mode or something like minetweaker to add them yourself.")
        .getBoolean(registerRecipes);

    addPeacefulRecipes = config.get(sectionRecipe.name, "addPeacefulRecipes", addPeacefulRecipes, "When enabled peaceful recipes are added for soulbinder based crafting components.")
        .getBoolean(addPeacefulRecipes);
    allowTileEntitiesAsPaintSource = config.get(sectionRecipe.name, "allowTileEntitiesAsPaintSource", allowTileEntitiesAsPaintSource,
        "When enabled blocks with tile entities (e.g. machines) can be used as paint targets.")
        .getBoolean(allowTileEntitiesAsPaintSource);
    createSyntheticRecipes = config
        .get(
            sectionRecipe.name,
            "createSyntheticRecipes",
            createSyntheticRecipes,
            "Automatically create alloy smelter recipes with double and triple inputs and different slot allocations (1+1+1, 2+1, 1+2, 3 and 2) for single-input recipes.")
        .getBoolean(createSyntheticRecipes);

    redstoneConduitsShowState = config.get(sectionMisc.name, "redstoneConduitsShowState", redstoneConduitsShowState,
        "If set to false redstone conduits will look the same whether they are recieving a signal or not. This can help with performance.")
        .getBoolean(redstoneConduitsShowState);

    enchanterBaseLevelCost = config.get(sectionRecipe.name, "enchanterBaseLevelCost", enchanterBaseLevelCost,
        "Base level cost added to all recipes in the enchanter.").getInt(enchanterBaseLevelCost);
    enchanterLevelCostFactor = config.get(sectionRecipe.name, "enchanterLevelCostFactor", enchanterLevelCostFactor,
        "The final XP cost for an enchantment is multiplied by this value. To halve costs set to 0.5, to double them set it to 2").getDouble(enchanterLevelCostFactor);
    enchanterLapisCostFactor = config.get(sectionRecipe.name, "enchanterLapisCostFactor", enchanterLapisCostFactor,
        "The lapis cost is enchant level multiplied by this value").getDouble(enchanterLapisCostFactor);
    
    photovoltaicCellEnabled = config.get(sectionItems.name, "photovoltaicCellEnabled", photovoltaicCellEnabled,
        "If set to false: Photovoltaic Cells will not be craftable.")
        .getBoolean(photovoltaicCellEnabled);

    reservoirEnabled= config.get(sectionItems.name, "reservoirEnabled", reservoirEnabled,
        "If set to false reservoirs will not be craftable.")
        .getBoolean(reservoirEnabled);

    transceiverEnabled = config.get(sectionItems.name, "transceiverEnabled", transceiverEnabled,
        "If set to false: Dimensional Transceivers will not be craftable.")
        .getBoolean(transceiverEnabled);

    maxPhotovoltaicOutputRF = config.get(sectionPower.name, "maxPhotovoltaicOutputRF", maxPhotovoltaicOutputRF,
        "Maximum output in RF/t of the Photovoltaic Panels.").getInt(maxPhotovoltaicOutputRF);
    maxPhotovoltaicAdvancedOutputRF = config.get(sectionPower.name, "maxPhotovoltaicAdvancedOutputRF", maxPhotovoltaicAdvancedOutputRF,
        "Maximum output in RF/t of the Advanced Photovoltaic Panels.").getInt(maxPhotovoltaicAdvancedOutputRF);
    maxPhotovoltaicVibrantOutputRF = config.get(sectionPower.name, "maxPhotovoltaicVibrantOutputRF", maxPhotovoltaicVibrantOutputRF,
        "Maximum output in RF/t of the Vibrant Photovoltaic Panels.").getInt(maxPhotovoltaicVibrantOutputRF);

    photovoltaicCanTypesJoins = config.get(sectionPower.name, "photovoltaicCanTypesJoins", photovoltaicCanTypesJoins,
        "When enabled Photovoltaic Panels of different kinds can join together as a multi-block").getBoolean(photovoltaicCanTypesJoins);
    photovoltaicRecalcSunTick = config.get(sectionPower.name, "photovoltaicRecalcSunTick", photovoltaicRecalcSunTick,
        "How often (in ticks) the Photovoltaic Panels should check the sun's angle.").getInt(photovoltaicRecalcSunTick);

    conduitScale = config.get(sectionAesthetic.name, "conduitScale", DEFAULT_CONDUIT_SCALE,
        "Valid values are between 0-1, smallest conduits at 0, largest at 1.\n" +
            "In SMP, all clients must be using the same value as the server.").getDouble(DEFAULT_CONDUIT_SCALE);
    conduitScale = VecmathUtil.clamp(conduitScale, 0, 1);

    wirelessChargerRange = config.get(sectionEfficiency.name, "wirelessChargerRange", wirelessChargerRange,
        "The range of the wireless charger").getInt(wirelessChargerRange);

    fluidConduitExtractRate = config.get(sectionEfficiency.name, "fluidConduitExtractRate", fluidConduitExtractRate,
        "Number of millibuckets per tick extracted by a fluid conduits auto extracting").getInt(fluidConduitExtractRate);

    fluidConduitDynamicLighting = config.get(sectionEfficiency.name, "fluidConduitDynamicLighting", fluidConduitDynamicLighting,
        "If enabled, conduits will change their light levels based on their contents.").getBoolean(false);
    
    fluidConduitMaxIoRate = config.get(sectionEfficiency.name, "fluidConduitMaxIoRate", fluidConduitMaxIoRate,
        "Number of millibuckets per tick that can pass through a single connection to a fluid conduit.").getInt(fluidConduitMaxIoRate);

    advancedFluidConduitExtractRate = config.get(sectionEfficiency.name, "advancedFluidConduitExtractRate", advancedFluidConduitExtractRate,
        "Number of millibuckets per tick extracted by pressurized fluid conduits auto extracting").getInt(advancedFluidConduitExtractRate);

    advancedFluidConduitMaxIoRate = config.get(sectionEfficiency.name, "advancedFluidConduitMaxIoRate", advancedFluidConduitMaxIoRate,
        "Number of millibuckets per tick that can pass through a single connection to an pressurized fluid conduit.").getInt(advancedFluidConduitMaxIoRate);

    enderFluidConduitExtractRate = config.get(sectionEfficiency.name, "enderFluidConduitExtractRate", enderFluidConduitExtractRate,
        "Number of millibuckets per tick extracted by ender fluid conduits auto extracting").getInt(enderFluidConduitExtractRate);

    enderFluidConduitMaxIoRate = config.get(sectionEfficiency.name, "enderFluidConduitMaxIoRate", enderFluidConduitMaxIoRate,
        "Number of millibuckets per tick that can pass through a single connection to an ender fluid conduit.").getInt(enderFluidConduitMaxIoRate);

    transceiverEnergyLoss = config.get(sectionPower.name, "transceiverEnergyLoss", transceiverEnergyLoss,
        "Amount of energy lost when transfered by Dimensional Transceiver; 0 is no loss, 1 is 100% loss").getDouble(transceiverEnergyLoss);
    transceiverBucketTransmissionCostRF = config.get(sectionEfficiency.name, "transceiverBucketTransmissionCostRF", transceiverBucketTransmissionCostRF,
        "The cost in RF of transporting a bucket of fluid via a Dimensional Transceiver.").getInt(transceiverBucketTransmissionCostRF);

    vatPowerUserPerTickRF = config.get(sectionPower.name, "vatPowerUserPerTickRF", vatPowerUserPerTickRF,
        "Power use (RF/t) used by the vat.").getInt(vatPowerUserPerTickRF);

    detailedPowerTrackingEnabled = config
        .get(
            sectionAdvanced.name,
            "perInterfacePowerTrackingEnabled",
            detailedPowerTrackingEnabled,
            "Enable per tick sampling on individual power inputs and outputs. This allows slightly more detailed messages from the RF Reader but has a negative impact on server performance.")
        .getBoolean(detailedPowerTrackingEnabled);

    jeiUseShortenedPainterRecipes = config
        .get(sectionPersonal.name, "jeiUseShortenedPainterRecipes", jeiUseShortenedPainterRecipes,
            "If true, only a handful of sample painter recipes will be shown in JEI. Enable this if you have timing problems starting a world or logging into a server.")
        .getBoolean(jeiUseShortenedPainterRecipes);

    useSneakMouseWheelYetaWrench = config.get(sectionPersonal.name, "useSneakMouseWheelYetaWrench", useSneakMouseWheelYetaWrench,
        "If true, shift-mouse wheel will change the conduit display mode when the YetaWrench is equipped.")
        .getBoolean(useSneakMouseWheelYetaWrench);

    useSneakRightClickYetaWrench = config.get(sectionPersonal.name, "useSneakRightClickYetaWrench", useSneakRightClickYetaWrench,
        "If true, shift-clicking the YetaWrench on a null or non wrenchable object will change the conduit display mode.").getBoolean(
        useSneakRightClickYetaWrench);

    yetaWrenchOverlayMode = config.getInt("yetaWrenchOverlayMode",sectionPersonal.name, yetaWrenchOverlayMode, 0, 2,
            "What kind of overlay to use when holding the yeta wrench\n\n"
            + "0 - Sideways scrolling in ceter of screen\n"
            + "1 - Vertical icon bar in bottom right\n"
            + "2 - Old-style group of icons in bottom right");

    machineSoundsEnabled = config.get(sectionPersonal.name, "useMachineSounds", machineSoundsEnabled, "If true, machines will make sounds.").getBoolean(
        machineSoundsEnabled);

    machineSoundVolume = (float) config.get(sectionPersonal.name, "machineSoundVolume", machineSoundVolume, "Volume of machine sounds.").getDouble(
        machineSoundVolume);

    itemConduitUsePhyscialDistance = config.get(sectionEfficiency.name, "itemConduitUsePhyscialDistance", itemConduitUsePhyscialDistance, "If true, " +
        "'line of sight' distance rather than conduit path distance is used to calculate priorities.")
        .getBoolean(itemConduitUsePhyscialDistance);

    vacuumChestRange = config.get(sectionEfficiency.name, "vacumChestRange", vacuumChestRange, "The range of the vacuum chest").getInt(vacuumChestRange);

    reinforcedObsidianEnabled = config.get(sectionItems.name, "reinforcedObsidianEnabled", reinforcedObsidianEnabled,
        "When set to false reinforced obsidian is not craftable.").getBoolean(reinforcedObsidianEnabled);

    travelAnchorEnabled = config.get(sectionItems.name, "travelAnchorEnabled", travelAnchorEnabled,
        "When set to false: the travel anchor will not be craftable.").getBoolean(travelAnchorEnabled);

    travelAnchorMaximumDistance = config.get(sectionAnchor.name, "travelAnchorMaxDistance", travelAnchorMaximumDistance,
        "Maximum number of blocks that can be traveled from one travel anchor to another.").getInt(travelAnchorMaximumDistance);

    travelAnchorCooldown = config.get(sectionAnchor.name, "travelAnchorCooldown", travelAnchorCooldown,
        "Number of ticks cooldown between activations (1 sec = 20 ticks)").getInt(travelAnchorCooldown);

    travelAnchorSneak = config.get(sectionAnchor.name, "travelAnchorSneak", travelAnchorSneak,
        "Add sneak as an option to activate travel anchors").getBoolean(travelAnchorSneak);

    travelAnchorSkipWarning = config.get(sectionAnchor.name, "travelAnchorSkipWarning", travelAnchorSkipWarning,
        "Travel Anchors send a chat warning when skipping inaccessible anchors").getBoolean(travelAnchorSkipWarning);

    travelStaffMaximumDistance = config.get(sectionStaff.name, "travelStaffMaxDistance", travelStaffMaximumDistance,
        "Maximum number of blocks that can be traveled using the Staff of Traveling.").getInt(travelStaffMaximumDistance);
    travelStaffPowerPerBlockRF = (float) config.get(sectionStaff.name, "travelStaffPowerPerBlockRF", travelStaffPowerPerBlockRF,
        "Number of RF required per block traveled using the Staff of Traveling.").getDouble(travelStaffPowerPerBlockRF);

    travelStaffMaxBlinkDistance = config.get(sectionStaff.name, "travelStaffMaxBlinkDistance", travelStaffMaxBlinkDistance,
        "Max number of blocks teleported when shift clicking the staff.").getInt(travelStaffMaxBlinkDistance);

    travelStaffBlinkPauseTicks = config.get(sectionStaff.name, "travelStaffBlinkPauseTicks", travelStaffBlinkPauseTicks,
        "Minimum number of ticks between 'blinks'. Values of 10 or less allow a limited sort of flight.").getInt(travelStaffBlinkPauseTicks);

    travelStaffBlinkEnabled = config.get(sectionStaff.name, "travelStaffBlinkEnabled", travelStaffBlinkEnabled,
        "If set to false: the travel staff can not be used to shift-right click teleport, or blink.").getBoolean(travelStaffBlinkEnabled);
    travelStaffBlinkThroughSolidBlocksEnabled = config.get(sectionStaff.name, "travelStaffBlinkThroughSolidBlocksEnabled",
        travelStaffBlinkThroughSolidBlocksEnabled,
        "If set to false: the travel staff can be used to blink through any block.").getBoolean(travelStaffBlinkThroughSolidBlocksEnabled);
    travelStaffBlinkThroughClearBlocksEnabled = config
        .get(sectionItems.name, "travelStaffBlinkThroughClearBlocksEnabled", travelStaffBlinkThroughClearBlocksEnabled,
            "If travelStaffBlinkThroughSolidBlocksEnabled is set to false and this is true: the travel " +
                "staff can only be used to blink through transparent or partial blocks (e.g. torches). " +
                "If both are false: only air blocks may be teleported through.")
        .getBoolean(travelStaffBlinkThroughClearBlocksEnabled);
    travelStaffBlinkThroughUnbreakableBlocksEnabled = config.get(sectionItems.name, "travelStaffBlinkThroughUnbreakableBlocksEnabled",
        travelStaffBlinkThroughUnbreakableBlocksEnabled, "Allows the travel staff to blink through unbreakable blocks such as warded blocks and bedrock.")
        .getBoolean();
    travelStaffBlinkBlackList = config.getStringList("travelStaffBlinkBlackList", sectionStaff.name, travelStaffBlinkBlackList,
        "Lists the blocks that cannot be teleported through in the form 'modID:blockName'");
    travelAnchorZoomScale = config.getFloat("travelAnchorZoomScale", sectionStaff.name, travelAnchorZoomScale, 0, 1,
        "Set the max zoomed size of a travel anchor as an aprox. percentage of screen height");

    travelStaffOffhandBlinkEnabled = config
        .get(sectionStaff.name, "travelStaffOffhandBlinkEnabled", travelStaffOffhandBlinkEnabled,
            "If set to false: the travel staff can not be used to shift-right click teleport, or blink, when held in the off-hand.")
        .getBoolean(travelStaffOffhandBlinkEnabled);
    travelStaffOffhandTravelEnabled = config
        .get(sectionStaff.name, "travelStaffOffhandTravelEnabled", travelStaffOffhandTravelEnabled,
            "If set to false: the travel staff can not be used to click teleport to Travel Anchors, when held in the off-hand.")
        .getBoolean(travelStaffOffhandTravelEnabled);
    travelStaffOffhandEnderIOEnabled = config
        .get(sectionStaff.name, "travelStaffOffhandEnderIOEnabled", travelStaffOffhandEnderIOEnabled,
            "If set to false: the travel staff can not be used to activate the Ender IO, when held in the off-hand.")
        .getBoolean(travelStaffOffhandEnderIOEnabled);
    travelStaffOffhandShowsTravelTargets = config
        .get(sectionStaff.name, "travelStaffOffhandShowsTravelTargets", travelStaffOffhandShowsTravelTargets,
            "If set to false: Teleportation targets will not be highlighted for travel items held in the off-hand.")
        .getBoolean(travelStaffOffhandShowsTravelTargets);

    rodOfReturnCanTargetAnywhere = config.get(sectionRod.name, "rodOfReturnCanTargetAnywhere", rodOfReturnCanTargetAnywhere,
        "If set to false the rod of return can only target a telepad.").getBoolean(rodOfReturnCanTargetAnywhere);
    rodOfReturnTicksToActivate = config.get(sectionRod.name, "rodOfReturnTicksToActivate", rodOfReturnTicksToActivate,
        "Number of ticks the rod must be used before teleporting").getInt(rodOfReturnTicksToActivate);
    rodOfReturnPowerStorage = config.get(sectionRod.name, "rodOfReturnPowerStorage", rodOfReturnPowerStorage,
        "Internal RF buffer for rod").getInt(rodOfReturnPowerStorage);
    rodOfReturnRfPerTick = config.get(sectionRod.name, "rodOfReturnRfPerTick", rodOfReturnRfPerTick,
        "RF used per tick").getInt(rodOfReturnRfPerTick);
    rodOfReturnMinTicksToRecharge = config.get(sectionRod.name, "rodOfReturnMinTicksToRecharge", rodOfReturnMinTicksToRecharge,
        "Min number of ticks required to recharge the internal RF buffer").getInt(rodOfReturnMinTicksToRecharge);
    rodOfReturnFluidStorage = config.get(sectionRod.name, "rodOfReturnFluidStorage", rodOfReturnFluidStorage,
        "How much fluid the rod can store").getInt(rodOfReturnFluidStorage);
    rodOfReturnFluidUsePerTeleport = config.get(sectionRod.name, "rodOfReturnFluidUsePerTeleport", rodOfReturnFluidUsePerTeleport,
        "How much fluid is used per teleport").getInt(rodOfReturnFluidUsePerTeleport);
    rodOfReturnFluidType = config.getString("rodOfReturnFluidType", sectionRod.name, rodOfReturnFluidType, "The type of fluid used by the rod.");


    updateLightingWhenHidingFacades = config.get(sectionEfficiency.name, "updateLightingWhenHidingFacades", updateLightingWhenHidingFacades,
        "When true: correct lighting is recalculated (client side) for conduit bundles when transitioning to"
            + " from being hidden behind a facade. This produces "
            + "better quality rendering but can result in frame stutters when switching to/from a wrench.")
        .getBoolean(updateLightingWhenHidingFacades);

    transparentFacesLetThroughBeaconBeam = config
        .get(sectionAdvanced.name, "transparentFacesLetThroughBeaconBeam", transparentFacesLetThroughBeaconBeam,
            "If true, transparent facades will not block the Beacon's beam. As side effect they will also let through a tiny amount of light.")
        .getBoolean(transparentFacesLetThroughBeaconBeam);
    
    darkSteelRightClickPlaceEnabled = config.get(sectionDarkSteel.name, "darkSteelRightClickPlaceEnabled", darkSteelRightClickPlaceEnabled,
        "Enable / disable right click to place block using dark steel tools.").getBoolean(darkSteelRightClickPlaceEnabled);
    
    darkSteelPowerDamgeAbsorptionRatios = config
        .get(sectionDarkSteel.name, "darkSteelPowerDamgeAbsorptionRatios", darkSteelPowerDamgeAbsorptionRatios,
            "A list of the amount of durability damage absorbed when items are powered. In order of upgrade level. 1=100% so items take no durability damage when powered.")
        .getDoubleList();
    darkSteelPowerStorageBase = config.get(sectionDarkSteel.name, "darkSteelPowerStorageBase", darkSteelPowerStorageBase,
        "Base amount of power stored by dark steel items.").getInt(darkSteelPowerStorageBase);
    darkSteelPowerStorageLevelOne = config.get(sectionDarkSteel.name, "darkSteelPowerStorageLevelOne", darkSteelPowerStorageLevelOne,
        "Amount of power stored by dark steel items with a level 1 upgrade.").getInt(darkSteelPowerStorageLevelOne);
    darkSteelPowerStorageLevelTwo = config.get(sectionDarkSteel.name, "darkSteelPowerStorageLevelTwo", darkSteelPowerStorageLevelTwo,
        "Amount of power stored by dark steel items with a level 2 upgrade.").getInt(darkSteelPowerStorageLevelTwo);
    darkSteelPowerStorageLevelThree = config.get(sectionDarkSteel.name, "darkSteelPowerStorageLevelThree", darkSteelPowerStorageLevelThree,
        "Amount of power stored by dark steel items with a level 3 upgrade.").getInt(darkSteelPowerStorageLevelThree);

    darkSteelUpgradeVibrantCost = config.get(sectionDarkSteel.name, "darkSteelUpgradeVibrantCost", darkSteelUpgradeVibrantCost,
        "Number of levels required for the 'Empowered.").getInt(darkSteelUpgradeVibrantCost);
    darkSteelUpgradePowerOneCost = config.get(sectionDarkSteel.name, "darkSteelUpgradePowerOneCost", darkSteelUpgradePowerOneCost,
        "Number of levels required for the 'Power 1.").getInt(darkSteelUpgradePowerOneCost);
    darkSteelUpgradePowerTwoCost = config.get(sectionDarkSteel.name, "darkSteelUpgradePowerTwoCost", darkSteelUpgradePowerTwoCost,
        "Number of levels required for the 'Power 2.").getInt(darkSteelUpgradePowerTwoCost);
    darkSteelUpgradePowerThreeCost = config.get(sectionDarkSteel.name, "darkSteelUpgradePowerThreeCost", darkSteelUpgradePowerThreeCost,
        "Number of levels required for the 'Power 3' upgrade.").getInt(darkSteelUpgradePowerThreeCost);

    darkSteelJumpOneCost = config.get(sectionDarkSteel.name, "darkSteelJumpOneCost", darkSteelJumpOneCost,
        "Number of levels required for the 'Jump 1' upgrade.").getInt(darkSteelJumpOneCost);
    darkSteelJumpTwoCost = config.get(sectionDarkSteel.name, "darkSteelJumpTwoCost", darkSteelJumpTwoCost,
        "Number of levels required for the 'Jump 2' upgrade.").getInt(darkSteelJumpTwoCost);
    darkSteelJumpThreeCost = config.get(sectionDarkSteel.name, "darkSteelJumpThreeCost", darkSteelJumpThreeCost,
        "Number of levels required for the 'Jump 3' upgrade.").getInt(darkSteelJumpThreeCost);

    darkSteelSpeedOneCost = config.get(sectionDarkSteel.name, "darkSteelSpeedOneCost", darkSteelSpeedOneCost,
        "Number of levels required for the 'Speed 1' upgrade.").getInt(darkSteelSpeedOneCost);
    darkSteelSpeedTwoCost = config.get(sectionDarkSteel.name, "darkSteelSpeedTwoCost", darkSteelSpeedTwoCost,
        "Number of levels required for the 'Speed 2' upgrade.").getInt(darkSteelSpeedTwoCost);
    darkSteelSpeedThreeCost = config.get(sectionDarkSteel.name, "darkSteelSpeedThreeCost", darkSteelSpeedThreeCost,
        "Number of levels required for the 'Speed 3' upgrade.").getInt(darkSteelSpeedThreeCost);

    slotZeroPlacesEight = config.get(sectionDarkSteel.name, "shouldSlotZeroWrap", slotZeroPlacesEight, "Should the dark steel placement, when in the first (0th) slot, place the item in the last slot. If false, will place what's in the second slot.").getBoolean();

    darkSteelSpeedOneWalkModifier = (float) config.get(sectionDarkSteel.name, "darkSteelSpeedOneWalkModifier", darkSteelSpeedOneWalkModifier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedOneWalkModifier);
    darkSteelSpeedTwoWalkMultiplier = (float) config.get(sectionDarkSteel.name, "darkSteelSpeedTwoWalkMultiplier", darkSteelSpeedTwoWalkMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedTwoWalkMultiplier);
    darkSteelSpeedThreeWalkMultiplier = (float) config.get(sectionDarkSteel.name, "darkSteelSpeedThreeWalkMultiplier", darkSteelSpeedThreeWalkMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedThreeWalkMultiplier);

    darkSteelSpeedOneSprintModifier = (float) config.get(sectionDarkSteel.name, "darkSteelSpeedOneSprintModifier", darkSteelSpeedOneSprintModifier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedOneSprintModifier);
    darkSteelSpeedTwoSprintMultiplier = (float) config.get(sectionDarkSteel.name, "darkSteelSpeedTwoSprintMultiplier", darkSteelSpeedTwoSprintMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedTwoSprintMultiplier);
    darkSteelSpeedThreeSprintMultiplier = (float) config.get(sectionDarkSteel.name, "darkSteelSpeedThreeSprintMultiplier", darkSteelSpeedThreeSprintMultiplier,
        "Speed modifier applied when walking in the Dark Steel Boots with Speed I.").getDouble(darkSteelSpeedThreeSprintMultiplier);

    darkSteelBootsJumpModifier = config.get(sectionDarkSteel.name, "darkSteelBootsJumpModifier", darkSteelBootsJumpModifier,
        "Jump height modifier applied when jumping with Dark Steel Boots equipped").getDouble(darkSteelBootsJumpModifier);

    darkSteelPowerStorageBase = config.get(sectionDarkSteel.name, "darkSteelPowerStorage", darkSteelPowerStorageBase,
        "Amount of power stored (RF) per crystal in the armor items recipe.").getInt(darkSteelPowerStorageBase);
    darkSteelWalkPowerCost = config.get(sectionDarkSteel.name, "darkSteelWalkPowerCost", darkSteelWalkPowerCost,
        "Amount of power stored (RF) per block walked when wearing the dark steel boots.").getInt(darkSteelWalkPowerCost);
    darkSteelSprintPowerCost = config.get(sectionDarkSteel.name, "darkSteelSprintPowerCost", darkSteelWalkPowerCost,
        "Amount of power stored (RF) per block walked when wearing the dark steel boots.").getInt(darkSteelSprintPowerCost);
    darkSteelDrainPowerFromInventory = config.get(sectionDarkSteel.name, "darkSteelDrainPowerFromInventory", darkSteelDrainPowerFromInventory,
        "If true, dark steel armor will drain power stored (RF) in power containers in the players inventory.").getBoolean(darkSteelDrainPowerFromInventory);

    darkSteelBootsJumpPowerCost = config.get(sectionDarkSteel.name, "darkSteelBootsJumpPowerCost", darkSteelBootsJumpPowerCost,
        "Base amount of power used per jump (RF) dark steel boots. The second jump in a 'double jump' uses 2x this etc").getInt(darkSteelBootsJumpPowerCost);

    darkSteelFallDistanceCost = config.get(sectionDarkSteel.name, "darkSteelFallDistanceCost", darkSteelFallDistanceCost,
        "Amount of power used (RF) per block height of fall distance damage negated.").getInt(darkSteelFallDistanceCost);

    darkSteelSwimCost = config.get(sectionDarkSteel.name, "darkSteelSwimCost", darkSteelSwimCost,
        "Number of levels required for the 'Swim' upgrade.").getInt(darkSteelSwimCost);

    darkSteelNightVisionCost = config.get(sectionDarkSteel.name, "darkSteelNightVisionCost", darkSteelNightVisionCost,
        "Number of levels required for the 'Night Vision' upgrade.").getInt(darkSteelNightVisionCost);

    darkSteelTOPCost = config.get(sectionDarkSteel.name, "darkSteelTOPCost", darkSteelTOPCost, "Number of levels required for the 'The One Probe' upgrade.")
        .getInt(darkSteelTOPCost);

    darkSteelGliderCost = config.get(sectionDarkSteel.name, "darkSteelGliderCost", darkSteelGliderCost,
        "Number of levels required for the 'Glider' upgrade.").getInt(darkSteelGliderCost);
    darkSteelGliderHorizontalSpeed = config.get(sectionDarkSteel.name, "darkSteelGliderHorizontalSpeed", darkSteelGliderHorizontalSpeed,
        "Horizontal movement speed modifier when gliding.").getDouble(darkSteelGliderHorizontalSpeed);
    darkSteelGliderVerticalSpeed = config.get(sectionDarkSteel.name, "darkSteelGliderVerticalSpeed", darkSteelGliderVerticalSpeed,
        "Rate of altitude loss when gliding.").getDouble(darkSteelGliderVerticalSpeed);
    darkSteelGliderVerticalSpeedSprinting = config.get(sectionDarkSteel.name, "darkSteelGliderVerticalSpeedSprinting", darkSteelGliderVerticalSpeedSprinting,
        "Rate of altitude loss when sprinting and gliding.").getDouble(darkSteelGliderVerticalSpeedSprinting);

    darkSteelElytraCost = config.get(sectionDarkSteel.name, "darkSteelElytraCost", darkSteelElytraCost, "Number of levels required for the 'Elytra' upgrade.")
        .getInt(darkSteelElytraCost);

    darkSteelSoundLocatorCost = config.get(sectionDarkSteel.name, "darkSteelSoundLocatorCost", darkSteelSoundLocatorCost,
        "Number of levels required for the 'Sound Locator' upgrade.").getInt(darkSteelSoundLocatorCost);
    darkSteelSoundLocatorRange = config.get(sectionDarkSteel.name, "darkSteelSoundLocatorRange", darkSteelSoundLocatorRange,
        "Range of the 'Sound Locator' upgrade.").getInt(darkSteelSoundLocatorRange);
    darkSteelSoundLocatorLifespan = config.get(sectionDarkSteel.name, "darkSteelSoundLocatorLifespan", darkSteelSoundLocatorLifespan,
        "Number of ticks the 'Sound Locator' icons are displayed for.").getInt(darkSteelSoundLocatorLifespan);

    darkSteelGogglesOfRevealingCost= config.get(sectionDarkSteel.name, "darkSteelGogglesOfRevealingCost", darkSteelGogglesOfRevealingCost,
        "Number of levels required for the Goggles of Revealing upgrade.").getInt(darkSteelGogglesOfRevealingCost);

    darkSteelApiaristArmorCost= config.get(sectionDarkSteel.name, "darkSteelApiaristArmorCost", darkSteelApiaristArmorCost,
        "Number of levels required for the Apiarist Armor upgrade.").getInt(darkSteelApiaristArmorCost);

    darkSteelTravelCost = config.get(sectionDarkSteel.name, "darkSteelTravelCost", darkSteelTravelCost,
        "Number of levels required for the 'Travel' upgrade.").getInt(darkSteelTravelCost);

    darkSteelSpoonCost = config.get(sectionDarkSteel.name, "darkSteelSpoonCost", darkSteelSpoonCost,
        "Number of levels required for the 'Spoon' upgrade.").getInt(darkSteelSpoonCost);

    darkSteelSolarOneCost = config.get(sectionDarkSteel.name, "darkSteelSolarOneCost", darkSteelSolarOneCost,
        "Cost in XP levels of the Solar I upgrade.").getInt();
    darkSteelSolarOneGen = config.get(sectionDarkSteel.name, "darkSteelSolarOneGen", darkSteelSolarOneGen,
        "RF per SECOND generated by the Solar I upgrade. Split between all equipped DS armors.").getInt();

    darkSteelSolarTwoCost = config.get(sectionDarkSteel.name, "darkSteelSolarTwoCost", darkSteelSolarTwoCost,
        "Cost in XP levels of the Solar II upgrade.").getInt();
    darkSteelSolarTwoGen = config.get(sectionDarkSteel.name, "darkSteelSolarTwoGen", darkSteelSolarTwoGen,
        "RF per SECOND generated by the Solar II upgrade. Split between all equipped DS armors.").getInt();

    darkSteelSolarThreeCost = config.get(sectionDarkSteel.name, "darkSteelSolarThreeCost", darkSteelSolarThreeCost,
        "Cost in XP levels of the Solar III upgrade.").getInt();
    darkSteelSolarThreeGen = config.get(sectionDarkSteel.name, "darkSteelSolarThreeGen", darkSteelSolarThreeGen,
        "RF per SECOND generated by the Solar III upgrade. Split between all equipped DS armors.").getInt();

    darkSteelSolarChargeOthers = config.get(sectionDarkSteel.name, "darkSteelSolarChargeOthers", darkSteelSolarChargeOthers,
        "If enabled allows the solar upgrade to charge non-darksteel armors that the player is wearing.").getBoolean();

    darkSteelSwordSkullChance = (float) config.get(sectionDarkSteel.name, "darkSteelSwordSkullChance", darkSteelSwordSkullChance,
        "The base chance that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordSkullChance);
    darkSteelSwordSkullLootingModifier = (float) config.get(sectionDarkSteel.name, "darkSteelSwordSkullLootingModifier", darkSteelSwordSkullLootingModifier,
        "The chance per looting level that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordSkullLootingModifier);

    darkSteelSwordPoweredDamageBonus = (float) config.get(sectionDarkSteel.name, "darkSteelSwordPoweredDamageBonus", darkSteelSwordPoweredDamageBonus,
        "The extra damage dealt when the sword is powered").getDouble(
            darkSteelSwordPoweredDamageBonus);
    darkSteelSwordPoweredSpeedBonus = (float) config.get(sectionDarkSteel.name, "darkSteelSwordPoweredSpeedBonus", darkSteelSwordPoweredSpeedBonus,
        "The increase in attack speed when powered").getDouble(
            darkSteelSwordPoweredSpeedBonus);
    
    darkSteelSwordWitherSkullChance = (float) config.get(sectionDarkSteel.name, "darkSteelSwordWitherSkullChance", darkSteelSwordWitherSkullChance,
        "The base chance that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordWitherSkullChance);
    darkSteelSwordWitherSkullLootingModifier = (float) config.get(sectionDarkSteel.name, "darkSteelSwordWitherSkullLootingModifie",
        darkSteelSwordWitherSkullLootingModifier,
        "The chance per looting level that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordWitherSkullLootingModifier);

    vanillaSwordSkullChance = (float) config.get(sectionDarkSteel.name, "vanillaSwordSkullChance", vanillaSwordSkullChance,
        "The base chance that a skull will be dropped when using a non dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        vanillaSwordSkullChance);
    vanillaSwordSkullLootingModifier = (float) config.get(sectionPersonal.name, "vanillaSwordSkullLootingModifier", vanillaSwordSkullLootingModifier,
        "The chance per looting level that a skull will be dropped when using a non-dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        vanillaSwordSkullLootingModifier);

    ticCleaverSkullDropChance = (float) config.get(sectionDarkSteel.name, "ticCleaverSkullDropChance", ticCleaverSkullDropChance,
        "The base chance that an Enderman Skull will be dropped when using TiC Cleaver").getDouble(
        ticCleaverSkullDropChance);
    ticBeheadingSkullModifier = (float) config.get(sectionPersonal.name, "ticBeheadingSkullModifier", ticBeheadingSkullModifier,
        "The chance per level of Beheading that a skull will be dropped when using a TiC weapon").getDouble(
        ticBeheadingSkullModifier);

    fakePlayerSkullChance = (float) config
        .get(
            sectionDarkSteel.name,
            "fakePlayerSkullChance",
            fakePlayerSkullChance,
            "The ratio of skull drops when a mob is killed by a 'FakePlayer', such as Killer Joe. When set to 0 no skulls will drop, at 1 the rate of skull drops is not modified")
        .getDouble(
            fakePlayerSkullChance);

    darkSteelSwordPowerUsePerHit = config.get(sectionDarkSteel.name, "darkSteelSwordPowerUsePerHit", darkSteelSwordPowerUsePerHit,
        "The amount of power (RF) used per hit.").getInt(darkSteelSwordPowerUsePerHit);
    darkSteelSwordEnderPearlDropChance = config.get(sectionDarkSteel.name, "darkSteelSwordEnderPearlDropChance", darkSteelSwordEnderPearlDropChance,
        "The chance that an ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)").getDouble(
        darkSteelSwordEnderPearlDropChance);
    darkSteelSwordEnderPearlDropChancePerLooting = config.get(sectionDarkSteel.name, "darkSteelSwordEnderPearlDropChancePerLooting",
        darkSteelSwordEnderPearlDropChancePerLooting,
        "The chance for each looting level that an additional ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(
            darkSteelSwordEnderPearlDropChancePerLooting);


    darkSteelBowEnabled = config.getBoolean("darkSteelBowEnabled", sectionItems.name, darkSteelBowEnabled, "If false the  Dark Steel Bow will be disabled");
    darkSteelBowDamageBonus = (float) config
        .get(sectionDarkSteel.name, "darkSteelBowDamageBonus", darkSteelBowDamageBonus, "The damage bonus applied to arrows fire from the bow.")
        .getDouble(darkSteelBowDamageBonus);
    darkSteelBowForceMultipliers = config.get(sectionDarkSteel.name, "darkSteelBowForceMultipliers", darkSteelBowForceMultipliers,
        "Multiplier that effects the speed with which arrows leave the bow.").getDoubleList();
    darkSteelBowFovMultipliers = config.get(sectionDarkSteel.name, "darkSteelBowFovMultiplier", darkSteelBowFovMultipliers,
        "The reduction in FOV when the bow is fullen drawn (the zoom level). A 'vanilla' bow has a value of 0.15").getDoubleList();
    darkSteelBowPowerUsePerDamagePoint = config.get(sectionDarkSteel.name, "darkSteelBowPowerUsePerDamagePoint", darkSteelBowPowerUsePerDamagePoint,
        "The amount of power (RF) used per hit.").getInt(darkSteelBowPowerUsePerDamagePoint);
    darkSteelBowDrawSpeeds = config
        .get(sectionDarkSteel.name, "darkSteelBowDrawSpeeds", darkSteelBowDrawSpeeds,
            "A list of the amount of draw speeds at the different upgrade levels. A vanilla bow draw speed is 20")
        .getIntList();
    darkSteelBowPowerUsePerDraw = config.get(sectionDarkSteel.name, "darkSteelBowPowerUsePerDraw", darkSteelBowPowerUsePerDraw,
        "The power used to fully draw the bow").getInt(darkSteelBowPowerUsePerDraw);
    darkSteelBowPowerUsePerTickDrawn = config.get(sectionDarkSteel.name, "darkSteelBowPowerUsePerTickDrawn", darkSteelBowPowerUsePerTickDrawn,
        "The power used per tick to hold the boy fully drawn").getInt(darkSteelBowPowerUsePerTickDrawn);


    darkSteelPickPowerUseObsidian = config.get(sectionDarkSteel.name, "darkSteelPickPowerUseObsidian", darkSteelPickPowerUseObsidian,
        "The amount of power (RF) used to break an obsidian block.").getInt(darkSteelPickPowerUseObsidian);
    darkSteelPickEffeciencyObsidian = config.get(sectionDarkSteel.name, "darkSteelPickEffeciencyObsidian", darkSteelPickEffeciencyObsidian,
        "The efficiency when breaking obsidian with a powered Dark Pickaxe.").getInt(darkSteelPickEffeciencyObsidian);
    darkSteelPickApplyObsidianEffeciencyAtHardess = (float) config.get(sectionDarkSteel.name, "darkSteelPickApplyObsidianEffeciencyAtHardess",
        darkSteelPickApplyObsidianEffeciencyAtHardess,
        "If set to a value > 0, the obsidian speed and power use will be used for all blocks with hardness >= to this value.").getDouble(
        darkSteelPickApplyObsidianEffeciencyAtHardess);
    darkSteelPickPowerUsePerDamagePoint = config.get(sectionDarkSteel.name, "darkSteelPickPowerUsePerDamagePoint", darkSteelPickPowerUsePerDamagePoint,
        "Power use (RF) per damage/durability point avoided.").getInt(darkSteelPickPowerUsePerDamagePoint);
    darkSteelPickEffeciencyBoostWhenPowered = (float) config.get(sectionDarkSteel.name, "darkSteelPickEffeciencyBoostWhenPowered",
        darkSteelPickEffeciencyBoostWhenPowered, "The increase in efficiency when powered.").getDouble(darkSteelPickEffeciencyBoostWhenPowered);
    darkSteelPickMinesTiCArdite = config.getBoolean("darkSteelPickMinesTiCArdite", sectionDarkSteel.name, darkSteelPickMinesTiCArdite,
        "When true the dark steel pick will be able to mine TiC Ardite and Cobalt");

    darkSteelAxePowerUsePerDamagePoint = config.get(sectionDarkSteel.name, "darkSteelAxePowerUsePerDamagePoint", darkSteelAxePowerUsePerDamagePoint,
        "Power use (RF) per damage/durability point avoided.").getInt(darkSteelAxePowerUsePerDamagePoint);
    darkSteelAxePowerUsePerDamagePointMultiHarvest = config.get(sectionDarkSteel.name, "darkSteelPickAxeUsePerDamagePointMultiHarvest",
        darkSteelAxePowerUsePerDamagePointMultiHarvest,
        "Power use (RF) per damage/durability point avoided when shift-harvesting multiple logs").getInt(darkSteelAxePowerUsePerDamagePointMultiHarvest);
    darkSteelAxeSpeedPenaltyMultiHarvest = (float) config.get(sectionDarkSteel.name, "darkSteelAxeSpeedPenaltyMultiHarvest",
        darkSteelAxeSpeedPenaltyMultiHarvest,
        "How much slower shift-harvesting logs is.").getDouble(darkSteelAxeSpeedPenaltyMultiHarvest);
    darkSteelAxeEffeciencyBoostWhenPowered = (float) config.get(sectionDarkSteel.name, "darkSteelAxeEffeciencyBoostWhenPowered",
        darkSteelAxeEffeciencyBoostWhenPowered, "The increase in efficiency when powered.").getDouble(darkSteelAxeEffeciencyBoostWhenPowered);

    darkSteelShearsDurabilityFactor = config.get(sectionDarkSteel.name, "darkSteelShearsDurabilityFactor", darkSteelShearsDurabilityFactor,
        "How much more durable as vanilla shears they are.").getInt(darkSteelShearsDurabilityFactor);
    darkSteelShearsPowerUsePerDamagePoint = config.get(sectionDarkSteel.name, "darkSteelShearsPowerUsePerDamagePoint", darkSteelShearsPowerUsePerDamagePoint,
        "Power use (RF) per damage/durability point avoided.").getInt(darkSteelShearsPowerUsePerDamagePoint);
    darkSteelShearsEffeciencyBoostWhenPowered = (float) config.get(sectionDarkSteel.name, "darkSteelShearsEffeciencyBoostWhenPowered",
        darkSteelShearsEffeciencyBoostWhenPowered, "The increase in efficiency when powered.").getDouble(darkSteelShearsEffeciencyBoostWhenPowered);
    darkSteelShearsBlockAreaBoostWhenPowered = config.get(sectionDarkSteel.name, "darkSteelShearsBlockAreaBoostWhenPowered", darkSteelShearsBlockAreaBoostWhenPowered,
        "The increase in effected area (radius) when powered and used on blocks.").getInt(darkSteelShearsBlockAreaBoostWhenPowered);
    darkSteelShearsEntityAreaBoostWhenPowered = (float) config.get(sectionDarkSteel.name, "darkSteelShearsEntityAreaBoostWhenPowered",
        darkSteelShearsEntityAreaBoostWhenPowered, "The increase in effected area (radius) when powered and used on sheep.").getDouble(darkSteelShearsEntityAreaBoostWhenPowered);

    darkSteelAnvilDamageChance = (float) config.get(sectionDarkSteel.name, "darkSteelAnvilDamageChance", darkSteelAnvilDamageChance, "Chance that the dark steel anvil will take damage after repairing something.").getDouble();

    darkSteelAnvilMaxLevel = config.get(sectionDarkSteel.name, "darkSteelAnvilMaxLevel", darkSteelAnvilMaxLevel, "Max cost operation the anvil can perform. Vanilla limit is 40.").getInt();
    
    darkSteelLadderSpeedBoost = (float) config.get(sectionDarkSteel.name, "darkSteelLadderSpeedBoost", darkSteelLadderSpeedBoost, "Speed boost, in blocks per tick, that the DS ladder gives over the vanilla ladder.").getDouble();

    hootchPowerPerCycleRF = config.get(sectionPower.name, "hootchPowerPerCycleRF", hootchPowerPerCycleRF,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 30, BC Fuel = 60").getInt(hootchPowerPerCycleRF);
    hootchPowerTotalBurnTime = config.get(sectionPower.name, "hootchPowerTotalBurnTime", hootchPowerTotalBurnTime,
        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000").getInt(hootchPowerTotalBurnTime);

    rocketFuelPowerPerCycleRF = config.get(sectionPower.name, "rocketFuelPowerPerCycleRF", rocketFuelPowerPerCycleRF,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 3, BC Fuel = 6").getInt(rocketFuelPowerPerCycleRF);
    rocketFuelPowerTotalBurnTime = config.get(sectionPower.name, "rocketFuelPowerTotalBurnTime", rocketFuelPowerTotalBurnTime,
        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000").getInt(rocketFuelPowerTotalBurnTime);

    fireWaterPowerPerCycleRF = config.get(sectionPower.name, "fireWaterPowerPerCycleRF", fireWaterPowerPerCycleRF,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 30, BC Fuel = 60").getInt(fireWaterPowerPerCycleRF);
    fireWaterPowerTotalBurnTime = config.get(sectionPower.name, "fireWaterPowerTotalBurnTime", fireWaterPowerTotalBurnTime,
        "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000").getInt(fireWaterPowerTotalBurnTime);

    zombieGeneratorRfPerTick = config.get(sectionPower.name, "zombieGeneratorRfPerTick", zombieGeneratorRfPerTick,
        "The amount of power generated per tick.").getInt(zombieGeneratorRfPerTick);
    zombieGeneratorTicksPerBucketFuel = config.get(sectionPower.name, "zombieGeneratorTicksPerMbFuel", zombieGeneratorTicksPerBucketFuel,
        "The number of ticks one bucket of fuel lasts.").getInt(zombieGeneratorTicksPerBucketFuel);

    addFuelTooltipsToAllFluidContainers = config.get(sectionPersonal.name, "addFuelTooltipsToAllFluidContainers", addFuelTooltipsToAllFluidContainers,
        "If true, the RF/t and burn time of the fuel will be displayed in all tooltips for fluid containers with fuel.").getBoolean(
        addFuelTooltipsToAllFluidContainers);
    addDurabilityTootip = config.get(sectionPersonal.name, "addDurabilityTootip", addFuelTooltipsToAllFluidContainers,
        "If true, adds durability tooltips to tools and armor").getBoolean(
        addDurabilityTootip);
    addFurnaceFuelTootip = config.get(sectionPersonal.name, "addFurnaceFuelTootip", addFuelTooltipsToAllFluidContainers,
        "If true, adds burn duration tooltips to furnace fuels").getBoolean(addFurnaceFuelTootip);

    farmActionEnergyUseRF = config.get(sectionFarm.name, "farmActionEnergyUseRF", farmActionEnergyUseRF,
        "The amount of power used by a farm per action (eg plant, till, harvest) ").getInt(farmActionEnergyUseRF);
    farmAxeActionEnergyUseRF = config.get(sectionFarm.name, "farmAxeActionEnergyUseRF", farmAxeActionEnergyUseRF,
        "The amount of power used by a farm per wood block 'chopped'").getInt(farmAxeActionEnergyUseRF);

    farmBonemealActionEnergyUseRF = config.get(sectionFarm.name, "farmBonemealActionEnergyUseRF", farmBonemealActionEnergyUseRF,
        "The amount of power used by a farm per bone meal used").getInt(farmBonemealActionEnergyUseRF);
    farmBonemealTryEnergyUseRF = config.get(sectionFarm.name, "farmBonemealTryEnergyUseRF", farmBonemealTryEnergyUseRF,
        "The amount of power used by a farm per bone meal try").getInt(farmBonemealTryEnergyUseRF);

    farmAxeDamageOnLeafBreak = config.get(sectionFarm.name, "farmAxeDamageOnLeafBreak", farmAxeDamageOnLeafBreak,
        "Should axes in a farm take damage when breaking leaves?").getBoolean(farmAxeDamageOnLeafBreak);
    farmToolTakeDamageChance = (float) config.get(sectionFarm.name, "farmToolTakeDamageChance", farmToolTakeDamageChance,
        "The chance that a tool in the farm will take damage.").getDouble(farmToolTakeDamageChance);

    disableFarmNotification = config.get(sectionFarm.name, "disableFarmNotifications", disableFarmNotification,
        "Disable the notification text above the farm block.").getBoolean();

    farmEssenceBerriesEnabled = config.get(sectionFarm.name, "farmEssenceBerriesEnabled", farmEssenceBerriesEnabled,
        "This setting controls whether essence berry bushes from TiC can be harvested by the farm.").getBoolean();

    farmManaBeansEnabled = config.get(sectionFarm.name, "farmManaBeansEnabled", farmManaBeansEnabled,
            "This setting controls whether mana beans from Thaumcraft can be harvested by the farm.").getBoolean();

    farmHarvestJungleWhenCocoa = config.get(sectionFarm.name, "farmHarvestJungleWhenCocoa", farmHarvestJungleWhenCocoa,
        "If this is enabled the farm will harvest jungle wood even if it has cocoa beans in its inventory.").getBoolean();

    // START Hoes

    ConfigCategory hoes = config.getCategory(sectionHoes.name);
    hoes.setComment("Each value of this category is an item that could be a hoe. You can add more values.");

    for (String hoe : hoeStrings) {
      config.get(sectionHoes.name, hoe, true, "Is this item a hoe that can be used in the farming station?");
    }

    final Property hoeProp = config.get(sectionFarm.name, "farmHoes", new String[0],
        "Use this to add items that can be hoes in the farming station. They will be moved to the proper config section. Use the registry name (eg. modid:name).");
    for (String hoe : hoeProp.getStringList()) {
      if (!config.hasKey(sectionHoes.name, hoe)) {
        config.get(sectionHoes.name, hoe, true, "Is this item a hoe that can be used in the farming station? (user added value)");
      }
    }
    hoeProp.set(new String[0]);

    farmHoes = new Things();
    for (Entry<String, Property> entry : hoes.entrySet()) {
      if (entry.getValue().getBoolean()) {
        farmHoes.add(entry.getKey());
      }
    }

    // END Hoes

    farmSaplingReserveAmount = config.get(sectionFarm.name, "farmSaplingReserveAmount", farmSaplingReserveAmount,
        "The amount of saplings the farm has to have in reserve to switch to shearing all leaves. If there are less " +
        "saplings in store, it will only shear part the leaves and break the others for spalings. Set this to 0 to " +
        "always shear all leaves.").getInt(farmSaplingReserveAmount);
    
    farmStopOnNoOutputSlots = config.get(sectionFarm.name, "farmStopOnNoOutputSlots", farmStopOnNoOutputSlots,
        "If this is enabled the farm will stop if there is not at least one empty output slot. Otherwise it will only stop if all output slots are full.")
        .getBoolean();

    farmEvictEmptyRFTools = config.get(sectionFarm.name, "farmEvictEmptyRFTools", farmEvictEmptyRFTools,
        "If this is enabled the farm will move tools that can store RF and are empty to the output slots instead of using them.").getBoolean();

    magnetPowerUsePerSecondRF = config.get(sectionMagnet.name, "magnetPowerUsePerTickRF", magnetPowerUsePerSecondRF,
        "The amount of RF power used per tick when the magnet is active").getInt(magnetPowerUsePerSecondRF);
    magnetPowerCapacityRF = config.get(sectionMagnet.name, "magnetPowerCapacityRF", magnetPowerCapacityRF,
        "Amount of RF power stored in a fully charged magnet").getInt(magnetPowerCapacityRF);
    magnetRange = config.get(sectionMagnet.name, "magnetRange", magnetRange,
        "Range of the magnet in blocks.").getInt(magnetRange);
    magnetMaxItems = config.get(sectionMagnet.name, "magnetMaxItems", magnetMaxItems,
        "Maximum number of items the magnet can effect at a time. (-1 for unlimited)").getInt(magnetMaxItems);

    magnetBlacklist = config.getStringList("magnetBlacklist", sectionMagnet.name, magnetBlacklist,
        "These items will not be picked up by the magnet.");

    magnetAllowInMainInventory = config.get(sectionMagnet.name, "magnetAllowInMainInventory", magnetAllowInMainInventory,
        "If true the magnet will also work in the main inventory, not just the hotbar").getBoolean(magnetAllowInMainInventory);
    
    magnetAllowInBaublesSlot = config.get(sectionMagnet.name, "magnetAllowInBaublesSlot", magnetAllowInBaublesSlot,
        "If true the magnet can be put into the 'amulet' Baubles slot (requires Baubles to be installed)").getBoolean(magnetAllowInBaublesSlot);
    magnetAllowDeactivatedInBaublesSlot = config.get(sectionMagnet.name, "magnetAllowDeactivatedInBaublesSlot", magnetAllowDeactivatedInBaublesSlot,
        "If true the magnet can be put into the 'amulet' Baubles slot even if switched off (requires Baubles to be installed and magnetAllowInBaublesSlot to be on)").getBoolean(magnetAllowDeactivatedInBaublesSlot);
    
    magnetBaublesType = config.get(sectionMagnet.name, "magnetBaublesType", magnetBaublesType,
        "The BaublesType the magnet should be, 'AMULET', 'RING' or 'BELT' (requires Baubles to be installed and magnetAllowInBaublesSlot to be on)").getString();
    
    crafterRfPerCraft = config.get("AutoCrafter Settings", "crafterRfPerCraft", crafterRfPerCraft,
        "RF used per autocrafted recipe").getInt(crafterRfPerCraft);

    poweredSpawnerMinDelayTicks = config.get(sectionSpawner.name, "poweredSpawnerMinDelayTicks", poweredSpawnerMinDelayTicks,
        "Min tick delay between spawns for a non-upgraded spawner").getInt(poweredSpawnerMinDelayTicks);
    poweredSpawnerMaxDelayTicks = config.get(sectionSpawner.name, "poweredSpawnerMaxDelayTicks", poweredSpawnerMaxDelayTicks,
        "Min tick delay between spawns for a non-upgraded spawner").getInt(poweredSpawnerMaxDelayTicks);
    poweredSpawnerMaxPlayerDistance = config.get(sectionSpawner.name, "poweredSpawnerMaxPlayerDistance", poweredSpawnerMaxPlayerDistance,
        "Max distance of the closest player for the spawner to be active. A zero value will remove the player check").getInt(poweredSpawnerMaxPlayerDistance);
    poweredSpawnerDespawnTimeSeconds = config.get(sectionSpawner.name, "poweredSpawnerDespawnTimeSeconds" , poweredSpawnerDespawnTimeSeconds,
        "Number of seconds in which spawned entities are protected from despawning").getInt(poweredSpawnerDespawnTimeSeconds);
    poweredSpawnerSpawnCount = config.get(sectionSpawner.name, "poweredSpawnerSpawnCount" , poweredSpawnerSpawnCount,
        "Number of entities to spawn each time").getInt(poweredSpawnerSpawnCount);
    poweredSpawnerSpawnRange = config.get(sectionSpawner.name, "poweredSpawnerSpawnRange" , poweredSpawnerSpawnRange,
        "Spawning range in X/Z").getInt(poweredSpawnerSpawnRange);
    poweredSpawnerMaxNearbyEntities = config.get(sectionSpawner.name, "poweredSpawnerMaxNearbyEntities" , poweredSpawnerMaxNearbyEntities,
        "Max number of entities in the nearby area until no more are spawned. A zero value will remove this check").getInt(poweredSpawnerMaxNearbyEntities);
    poweredSpawnerMaxSpawnTries = config.get(sectionSpawner.name, "poweredSpawnerMaxSpawnTries" , poweredSpawnerMaxSpawnTries,
        "Number of tries to find a suitable spawning location").getInt(poweredSpawnerMaxSpawnTries);
    poweredSpawnerUseVanillaSpawChecks = config.get(sectionSpawner.name, "poweredSpawnerUseVanillaSpawChecks", poweredSpawnerUseVanillaSpawChecks,
        "If true, regular spawn checks such as lighting level and dimension will be made before spawning mobs").getBoolean(poweredSpawnerUseVanillaSpawChecks);
    brokenSpawnerDropChance = (float) config.get(sectionSpawner.name, "brokenSpawnerDropChance", brokenSpawnerDropChance,
        "The chance a broken spawner will be dropped when a spawner is broken. 1 = 100% chance, 0 = 0% chance").getDouble(brokenSpawnerDropChance);
    brokenSpawnerToolBlacklist = config.getStringList("brokenSpawnerToolBlacklist", sectionSpawner.name, brokenSpawnerToolBlacklist,
        "When a spawner is broken with these tools they will not drop a broken spawner");

    powerSpawnerAddSpawnerCost = config.get(sectionSpawner.name, "powerSpawnerAddSpawnerCost", powerSpawnerAddSpawnerCost,
        "The number of levels it costs to add a broken spawner").getInt(powerSpawnerAddSpawnerCost);

    nutrientFoodBoostDelay = config.get(sectionFluid.name, "nutrientFluidFoodBoostDelay", nutrientFoodBoostDelay,
        "The delay in ticks between when nutrient distillation boosts your food value.").getInt((int) nutrientFoodBoostDelay);
    rocketFuelIsExplosive = config
        .get(sectionFluid.name, "rocketFuelIsExplosive", rocketFuelIsExplosive, "If enabled, Rocket Fuel will explode when in contact with fire.").getBoolean();

    killerJoeNutrientUsePerAttackMb = config.get(sectionKiller.name, "killerJoeNutrientUsePerAttackMb", killerJoeNutrientUsePerAttackMb,
        "The number of millibuckets of nutrient fluid used per attack.").getInt(killerJoeNutrientUsePerAttackMb);

    killerJoeAttackHeight = config.get(sectionKiller.name, "killerJoeAttackHeight", killerJoeAttackHeight,
        "The reach of attacks above and bellow Joe.").getDouble(killerJoeAttackHeight);
    killerJoeAttackWidth = config.get(sectionKiller.name, "killerJoeAttackWidth", killerJoeAttackWidth,
        "The reach of attacks to each side of Joe.").getDouble(killerJoeAttackWidth);
    killerJoeAttackLength = config.get(sectionKiller.name, "killerJoeAttackLength", killerJoeAttackLength,
        "The reach of attacks in front of Joe.").getDouble(killerJoeAttackLength);
    killerJoeHooverXpLength = config.get(sectionKiller.name, "killerJoeHooverXpLength", killerJoeHooverXpLength,
        "The distance from which XP will be gathered to each side of Joe.").getDouble(killerJoeHooverXpLength);
    killerJoeHooverXpWidth = config.get(sectionKiller.name, "killerJoeHooverXpWidth", killerJoeHooverXpWidth,
        "The distance from which XP will be gathered in front of Joe.").getDouble(killerJoeHooverXpWidth);

    killerJoeMustSee = config.get(sectionKiller.name, "killerJoeMustSee", killerJoeMustSee, "Set whether the Killer Joe can attack through blocks.").getBoolean();
    killerPvPoffDisablesSwing = config
        .get(sectionKiller.name, "killerPvPoffDisablesSwing", killerPvPoffDisablesSwing,
            "Set whether the Killer Joe swings even if PvP is off (that swing will do nothing unless killerPvPoffIsIgnored is enabled).")
        .getBoolean();
    killerPvPoffIsIgnored = config
        .get(sectionKiller.name, "killerPvPoffIsIgnored", killerPvPoffIsIgnored,
            "Set whether the Killer Joe ignores PvP settings and always hits players (killerPvPoffDisablesSwing must be off for this to work).")
        .getBoolean();
    killerMendingEnabled = config
        .get(sectionKiller.name, "killerMending", killerMendingEnabled, "If enabled, picked up XP will be used for the enchantement 'Mending' on the weapon.")
        .getBoolean();

    killerProvokesCreeperExpolosions = config.get(sectionKiller.name, "killerProvokesCreeperExpolosions", killerProvokesCreeperExpolosions,
        "If enabled, Creepers will explode for the Killer Joe just like for any player.").getBoolean();

    xpVacuumRange = config.get(sectionAdvanced.name, "xpVacuumRange", xpVacuumRange, "The distance from which XP will be gathered by the XP vacuum.")
        .getDouble(xpVacuumRange);

    // Add deprecated comment
    enableMEConduits = config.getBoolean("enableMEConduits", sectionItems.name, enableMEConduits, "Allows ME conduits. Only has an effect with AE2 installed.");
    enableOCConduits = config.getBoolean("enableOCConduits", sectionItems.name, enableOCConduits,
        "Allows OC conduits. Only has an effect with OpenComputers installed.");
    enableOCConduitsAnimatedTexture = config.getBoolean("enableOCConduitsAnimatedTexture", sectionItems.name,
        enableOCConduitsAnimatedTexture, "Use the animated texture for OC conduits.");

    soulVesselBlackList = Arrays.asList(config.getStringList("soulVesselBlackList", sectionSoulBinder.name, soulVesselBlackList.toArray(new String[0]),
        "Entities listed here will can not be captured in a Soul Vial"));
    soulVesselUnspawnableList = Arrays
        .asList(config.getStringList("soulVesselUnspawnableList", sectionSpawner.name, soulVesselUnspawnableList.toArray(new String[0]),
            "Entities listed here cannot be spawned and must be cloned from a captured entity instead (Attention: Possibility of item duping!)"));

    soulVesselCapturesBosses = config.getBoolean("soulVesselCapturesBosses", sectionSoulBinder.name, soulVesselCapturesBosses,
        "When set to false, any mob with a 'boss bar' won't be able to be captured in the Soul Vial. Note: The Ender Dragon can not "
            + "be captured, even with this enabled. This is a limitation of the dragon, not the Soul Vial.");

    soulBinderBrokenSpawnerRF = config.get(sectionSoulBinder.name, "soulBinderBrokenSpawnerRF", soulBinderBrokenSpawnerRF,
        "The number of RF required to change the type of a broken spawner.").getInt(soulBinderBrokenSpawnerRF);
    soulBinderReanimationRF = config.get(sectionSoulBinder.name, "soulBinderReanimationRF", soulBinderReanimationRF,
        "The number of RF required to to re-animated a mob head.").getInt(soulBinderReanimationRF);
    soulBinderEnderCystalRF = config.get(sectionSoulBinder.name, "soulBinderEnderCystalRF", soulBinderEnderCystalRF,
        "The number of RF required to create an ender crystal.").getInt(soulBinderEnderCystalRF);
    soulBinderAttractorCystalRF = config.get(sectionSoulBinder.name, "soulBinderAttractorCystalRF", soulBinderAttractorCystalRF,
        "The number of RF required to create an attractor crystal.").getInt(soulBinderAttractorCystalRF);
    // soulBinderEnderRailRF = config.get(sectionSoulBinder.name, "soulBinderEnderRailRF", soulBinderEnderRailRF,
    // "The number of RF required to create an ender rail.").getInt(soulBinderEnderRailRF);
    soulBinderTunedPressurePlateRF = config.get(sectionSoulBinder.name, "soulBinderTunedPressurePlateRF", soulBinderTunedPressurePlateRF,
        "The number of RF required to tune a pressure plate.").getInt(soulBinderTunedPressurePlateRF);
    soulBinderPrecientCystalRF = config.get(sectionSoulBinder.name, "soulBinderPrecientCystalRF", soulBinderPrecientCystalRF,
        "The number of RF required to create a precient crystal.").getInt(soulBinderPrecientCystalRF);

    soulBinderAttractorCystalLevels = config.get(sectionSoulBinder.name, "soulBinderAttractorCystalLevels", soulBinderAttractorCystalLevels,
        "The number of levels required to create an attractor crystal.").getInt(soulBinderAttractorCystalLevels);
    soulBinderEnderCystalLevels = config.get(sectionSoulBinder.name, "soulBinderEnderCystalLevels", soulBinderEnderCystalLevels,
        "The number of levels required to create an ender crystal.").getInt(soulBinderEnderCystalLevels);
    soulBinderPrecientCystalLevels = config.get(sectionSoulBinder.name, "soulBinderPrecientCystalLevels", soulBinderPrecientCystalLevels,
        "The number of levels required to create a precient crystal.").getInt(soulBinderPrecientCystalLevels);
    soulBinderReanimationLevels = config.get(sectionSoulBinder.name, "soulBinderReanimationLevels", soulBinderReanimationLevels,
        "The number of levels required to re-animate a mob head.").getInt(soulBinderReanimationLevels);
    soulBinderBrokenSpawnerLevels = config.get(sectionSoulBinder.name, "soulBinderBrokenSpawnerLevels", soulBinderBrokenSpawnerLevels,
        "The number of levels required to change the type of a broken spawner.").getInt(soulBinderBrokenSpawnerLevels);
    // soulBinderEnderRailLevels = config.get(sectionSoulBinder.name, "soulBinderEnderRailLevels", soulBinderEnderRailLevels,
    // "The number of levels required to create an ender rail.").getInt(soulBinderEnderRailLevels);
    soulBinderTunedPressurePlateLevels = config.get(sectionSoulBinder.name, "soulBinderTunedPressurePlateLevels", soulBinderTunedPressurePlateLevels,
        "The number of levels required to tune a pressure plate.").getInt(soulBinderTunedPressurePlateLevels);

    soulBinderMaxXpLevel = config.get(sectionSoulBinder.name, "soulBinderMaxXPLevel", soulBinderMaxXpLevel, "Maximum level of XP the soul binder can contain.").getInt();

    spawnGuardStopAllSlimesDebug = config.getBoolean("spawnGuardStopAllSlimesDebug", sectionAttractor.name, spawnGuardStopAllSlimesDebug,
        "When true slimes wont be allowed to spawn at all. Only added to aid testing in super flat worlds.");
    spawnGuardStopAllSquidSpawning = config.getBoolean("spawnGuardStopAllSquidSpawning", sectionAttractor.name, spawnGuardStopAllSquidSpawning,
        "When true no squid will be spawned.");

    weatherObeliskClearFluid = config.get(sectionWeather.name, "weatherObeliskClearFluid", weatherObeliskClearFluid,
        "The fluid required (in mB) to set the world to clear weather").getInt();
    weatherObeliskRainFluid = config.get(sectionWeather.name, "weatherObeliskRainFluid", weatherObeliskRainFluid,
        "The fluid required (in mB) to set the world to rainy weather").getInt();
    weatherObeliskThunderFluid = config.get(sectionWeather.name, "weatherObeliskThunderFluid", weatherObeliskThunderFluid,
        "The fluid required (in mB) to set the world to thundering weather").getInt();

    // Loot Config
    lootDarkSteel = config.getBoolean("lootDarkSteel", sectionLootConfig.name, lootDarkSteel, "Adds Darksteel Ingots to loot tables");
    lootItemConduitProbe = config.getBoolean("lootItemConduitProbe", sectionLootConfig.name, lootItemConduitProbe, "Adds ItemConduitProbe to loot tables");
    lootQuartz = config.getBoolean("lootQuartz", sectionLootConfig.name, lootQuartz, "Adds quartz to loot tables");
    lootNetherWart = config.getBoolean("lootNetherWart", sectionLootConfig.name, lootNetherWart, "Adds nether wart to loot tables");
    lootEnderPearl = config.getBoolean("lootEnderPearl", sectionLootConfig.name, lootEnderPearl, "Adds ender pearls to loot tables");
    lootElectricSteel = config.getBoolean("lootElectricSteel", sectionLootConfig.name, lootElectricSteel, "Adds Electric Steel Ingots to loot tables");
    lootRedstoneAlloy = config.getBoolean("lootRedstoneAlloy", sectionLootConfig.name, lootRedstoneAlloy, "Adds Redstone Alloy Ingots to loot tables");
    lootPhasedIron = config.getBoolean("lootPhasedIron", sectionLootConfig.name, lootPhasedIron, "Adds Phased Iron Ingots to loot tables");
    lootPhasedGold = config.getBoolean("lootPhasedGold", sectionLootConfig.name, lootPhasedGold, "Adds Phased Gold Ingots to loot tables");
    lootTravelStaff = config.getBoolean("lootTravelStaff", sectionLootConfig.name, lootTravelStaff, "Adds Travel Staff to loot tables");
    lootTheEnder = config.getBoolean("lootTheEnder", sectionLootConfig.name, lootTheEnder, "Adds The Ender to loot tables");
    lootDarkSteelBoots = config.getBoolean("lootDarkSteelBoots", sectionLootConfig.name, lootDarkSteelBoots, "Adds Darksteel Boots to loot tables");

    // enderRailEnabled = config.getBoolean("enderRailEnabled", sectionRailConfig.name, enderRailEnabled, "Whether Ender Rails are enabled");
    // enderRailPowerRequireCrossDimensions = config.get(sectionRailConfig.name, "enderRailPowerRequireCrossDimensions", enderRailPowerRequireCrossDimensions,
    // "The amount of power required to transport a cart across dimensions").getInt(enderRailPowerRequireCrossDimensions);
    // enderRailPowerRequiredPerBlock = config.get(sectionRailConfig.name, "enderRailPowerRequiredPerBlock", enderRailPowerRequiredPerBlock,
    // "The amount of power required to teleport a cart per block in the same dimension").getInt(enderRailPowerRequiredPerBlock);
    // enderRailCapSameDimensionPowerAtCrossDimensionCost = config.getBoolean("enderRailCapSameDimensionPowerAtCrossDimensionCost", sectionRailConfig.name,
    // enderRailCapSameDimensionPowerAtCrossDimensionCost,
    // "When set to true the RF cost of sending a cart within the same dimension will be capped to the cross dimension cost");
    // enderRailTicksBeforeForceSpawningLinkedCarts = config.get(sectionRailConfig.name, "enderRailTicksBeforeForceSpawningLinkedCarts",
    // enderRailTicksBeforeForceSpawningLinkedCarts,
    // "The number of ticks to wait for the track to clear before force spawning the next cart in a (RailCraft) linked
    // set").getInt(enderRailTicksBeforeForceSpawningLinkedCarts);
    // enderRailTeleportPlayers = config.getBoolean("enderRailTeleportPlayers", sectionRailConfig.name, enderRailTeleportPlayers, "If true player in minecarts
    // will be teleported. WARN: WIP, seems to cause a memory leak.");

    dumpMobNames = config.getBoolean("dumpMobNames", sectionMobConfig.name, dumpMobNames,
        "When set to true a list of all registered mobs will be dumped to config/enderio/mobTypes.txt The names are in the format required by EIOs mob blacklists.");

    xpObeliskMaxXpLevel = config.get(sectionMisc.name, "xpObeliskMaxXpLevel", xpObeliskMaxXpLevel, "Maximum level of XP the xp obelisk can contain.").getInt();
    xpJuiceName = config.getString("xpJuiceName", sectionMisc.name, xpJuiceName, "Id of liquid XP fluid (WARNING: only for users who know what they are doing - changing this id can break worlds) - this should match with OpenBlocks when installed");

    glassConnectToTheirVariants = config.getBoolean("glassConnectToTheirVariants", sectionMisc.name, glassConnectToTheirVariants,
        "If true, quite clear glass and fused quartz will connect textures with their respective enlightened and darkened variants.");
    clearGlassConnectToFusedQuartz = config.getBoolean("clearGlassConnectToFusedQuartz", sectionMisc.name, clearGlassConnectToFusedQuartz, "If true, quite clear glass will connect textures with fused quartz.");
    glassConnectToTheirColorVariants = config.getBoolean("glassConnectToTheirColorVariants", sectionMisc.name, glassConnectToTheirColorVariants,
        "If true, quite clear glass and fused quartz of different colors will connect textures.");

    paintedGlowstoneRequireSilkTouch = config.getBoolean("paintedGlowstoneRequireSilkTouch", sectionMisc.name, paintedGlowstoneRequireSilkTouch, "If true, painted glowstone will drop dust unless broken with silk touch");
    
    
    enableBaublesIntegration = config.getBoolean("enableBaublesIntegration", sectionMisc.name, enableBaublesIntegration, "If false baubles intergation will be disabled even if Baubles is installed");
    
    enchantmentSoulBoundEnabled = config.getBoolean("enchantmentSoulBoundEnabled", sectionEnchantments.name, enchantmentSoulBoundEnabled,
        "If false the soul bound enchantment will not be available");
    
    
    String rareStr = config.get(sectionEnchantments.name, "enchantmentSoulBoundWeight", enchantmentSoulBoundRarity.toString(),
        "The rarity of the enchantment. COMMON, UNCOMMON, RARE, VERY_RARE ").getString();
    try {
      enchantmentSoulBoundRarity = Rarity.valueOf(rareStr);
    } catch (Exception e) {
      Log.warn("Could not set value config entry enchantmentWitherArrowRarity Specified value " + rareStr);
      e.printStackTrace();
    }

    telepadLockDimension = config.get(sectionTelepad.name, "lockDimension", telepadLockDimension,
        "If true, the dimension cannot be set via the GUI, the coord selector must be used.").getBoolean();
    telepadLockCoords = config.get(sectionTelepad.name, "lockCoords", telepadLockCoords,
        "If true, the coordinates cannot be set via the GUI, the coord selector must be used.").getBoolean();
    telepadPowerCoefficient = config.get(sectionTelepad.name, "powerCoefficient", telepadPowerCoefficient,
        "Power for a teleport is calculated by the formula:\npower = [this value] * ln(0.005*distance + 1)").getInt();
    telepadPowerInterdimensional = config.get(sectionTelepad.name, "powerInterdimensional", telepadPowerInterdimensional,
        "The amount of RF required for an interdimensional teleport.").getInt();
    telepadEnergyBufferRF = config.get(sectionTelepad.name, "telepadEnergyBufferRF", telepadEnergyBufferRF,
        "The amount of RF in the internal buffer.").getInt();
    telepadEnergyUsePerTickRF = config.get(sectionTelepad.name, "telepadEnergyUsePerTickRF", telepadEnergyUsePerTickRF,
        "The max amount of RF that can be used per tick. Higher values allow faster teleporting.").getInt();
    
    telepadFluidType = config.getString("telepadFluidType", sectionTelepad.name, telepadFluidType, "The type of fluid required to teleport entities");
    telepadFluidUse = config.get(sectionTelepad.name, "telepadFluidUse", telepadFluidUse,
        "The max amount of fluid in mb used per teleport. If set to <= 0 fluid use will be disabled").getInt();
    
    telepadIsTravelAnchor = config
        .get(sectionTelepad.name, "telepadIsTravelAnchor", telepadIsTravelAnchor, "If true, TelePads will also act as normal Travel Anchors.").getBoolean();
    telepadShrinkEffect = config.get(sectionPersonal.name, "telepadShrinkEffect", telepadShrinkEffect,
        "Can be used to disable the 'shrinking' effect of the telepad in case of conflicts with other mods.").getBoolean();

    inventoryPanelFree = config.getBoolean("inventoryPanelFree", sectionInventoryPanel.name, inventoryPanelFree, "If true, the inv panel will not accept fluids and will be active permanently.");
    inventoryPanelPowerPerMB = config.getFloat("powerPerMB", sectionInventoryPanel.name, inventoryPanelPowerPerMB, 1.0f, 10000.0f,
        "Internal power generated per mB. The default of 800/mB matches the RF generation of the Zombie generator. A panel tries to refill only once every second - setting this value too low slows down the scanning speed.");
    inventoryPanelScanCostPerSlot = config.getFloat("scanCostPerSlot", sectionInventoryPanel.name, inventoryPanelScanCostPerSlot, 0.0f, 10.0f,
        "Internal power used for scanning a slot");
    inventoryPanelExtractCostPerItem = config.getFloat("extractCostPerItem", sectionInventoryPanel.name, inventoryPanelExtractCostPerItem, 0.0f, 10.0f,
        "Internal power used per item extracted (not a stack of items)");
    inventoryPanelExtractCostPerOperation = config.getFloat("extractCostPerOperation", sectionInventoryPanel.name, inventoryPanelExtractCostPerOperation, 0.0f,
        10000.0f, "Internal power used per extract operation (independent of stack size)");
    inventoryPanelScaleText= config.getBoolean("inventoryPanelScaleText", sectionInventoryPanel.name, inventoryPanelScaleText,
        "If true stack sizes will be drawn at a smaller size with a little more detail.");
        
    remoteInventoryMBPerOpen = config.get(sectionInventoryPanel.name, "remoteInventoryMBPerOpen", remoteInventoryMBPerOpen, "MB required to open the panel").getIntList();
    remoteInventoryRFPerTick = config.get(sectionInventoryPanel.name, "remoteInventoryRFPerTick", remoteInventoryRFPerTick, "RF used per tick when the panel is open").getIntList();
    remoteInventoryMBCapacity = config.get(sectionInventoryPanel.name, "remoteInventoryMBCapacity", remoteInventoryMBCapacity, "Capacity of the intrenal tank in MB").getIntList();
    remoteInventoryRFCapacity = config.get(sectionInventoryPanel.name, "remoteInventoryRFCapacity", remoteInventoryRFCapacity, "Capacity of the intrenal energy storage in RF").getIntList();
    remoteInventoryFluidTypes = config.getStringList("remoteInventoryFluidTypes", sectionInventoryPanel.name, remoteInventoryFluidTypes, "The type of fluid reqquired");


    coldFireIgniterFluidType = config.getString("coldFireIgniterFluidType", sectionDarkSteel.name, coldFireIgniterFluidType, "The type of fluid required to ignite cold fire");
    coldFireIgniterMbPerUse = config.get(sectionDarkSteel.name, "coldFireIgniterMbPerUse", coldFireIgniterMbPerUse,
        "The amount of fluid in mb used per usage. If set to <= 0 fluid use will be disabled").getInt();
        
    debugUpdatePackets = config.getBoolean("debugUpdatePackets", sectionPersonal.name, debugUpdatePackets,
        "DEBUG: If true, TEs will flash when they recieve an update packet.");

    topEnabled = config.getBoolean("topEnabled", sectionTOP.name, topEnabled, "If true, 'The One Probe' by McJty will be supported");

    topShowProgressByDefault = config.getBoolean("topShowProgressByDefault", sectionTOP.name, topShowProgressByDefault,
        "If true, the progress will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed)");
    topShowPowerByDefault = config.getBoolean("topShowPowerByDefault", sectionTOP.name, topShowPowerByDefault,
        "If true, the power level will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed)");
    topShowRedstoneByDefault = config.getBoolean("topShowRedstoneByDefault", sectionTOP.name, topShowRedstoneByDefault,
        "If true, the resdstone status will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed)");
    topShowSideConfigByDefault = config.getBoolean("topShowSideConfigByDefault", sectionTOP.name, topShowSideConfigByDefault,
        "If true, the side config will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed)");
    topShowRangeByDefault = config.getBoolean("topShowRangeByDefault", sectionTOP.name, topShowRangeByDefault,
        "If true, the range will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed)");
    topShowMobsByDefault = config.getBoolean("topShowMobsByDefault", sectionTOP.name, topShowMobsByDefault,
        "If true, the mob list will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed)");
    topShowTanksByDefault = config.getBoolean("topShowTanksByDefault", sectionTOP.name, topShowTanksByDefault,
        "If true, the tank content will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed)");
    topShowXPByDefault = config.getBoolean("topShowXPByDefault", sectionTOP.name, topShowXPByDefault,
        "If true, the XP level will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed)");
    topShowItemCountDefault = config.getBoolean("topShowItemCountDefault", sectionTOP.name, topShowItemCountDefault,
        "If true, the item count will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed)");

    allowFovControlsInSurvivalMode = config.getBoolean("allowFovControlsInSurvivalMode", sectionMisc.name, allowFovControlsInSurvivalMode,
        "If true, the FOV keyboard controls can be used in survival and advanture mode. Otherwise they are limited to create and spectator modes.");

    leversEnabled = config.getString("leversEnabled", sectionRecipe.name, leversEnabled,
        "A comma-seperated list of durations in seconds. For these, self-reseting levers will be created. Set to 0 to disable the lever. Please note that you also need to supply a resource pack with matching blockstates and a language file for this to work.");

    CapacitorKey.processConfig(config);
  }

  public static void checkYetaAccess() {
    if(!useSneakMouseWheelYetaWrench && !useSneakRightClickYetaWrench) {
      Log.warn("Both useSneakMouseWheelYetaWrench and useSneakRightClickYetaWrench are set to false. Enabling right click.");
      useSneakRightClickYetaWrench = true;
    }
  }

  public static void init(FMLInitializationEvent event) {
  }

  public static void postInit() {
    if (darkSteelPowerDamgeAbsorptionRatios == null || darkSteelPowerDamgeAbsorptionRatios.length != 4) {
      throw new IllegalArgumentException("Ender IO config value darkSteelPowerDamgeAbsorptionRatios must have exactly 4 values");
    }
    if (darkSteelBowForceMultipliers == null || darkSteelBowForceMultipliers.length != 5) {
      throw new IllegalArgumentException("Ender IO config value darkSteelBowForceMultipliers must have exactly 5 values");
    }
    if (darkSteelBowDrawSpeeds == null || darkSteelBowDrawSpeeds.length != 5) {
      throw new IllegalArgumentException("Ender IO config value darkSteelBowDrawSpeeds must have exactly 5 values");
    }
    if (darkSteelBowFovMultipliers == null || darkSteelBowFovMultipliers.length != 5) {
      throw new IllegalArgumentException("Ender IO config value darkSteelBowFovMultipliers must have exactly 5 values");
    }
    if (remoteInventoryMBPerOpen == null || remoteInventoryMBPerOpen.length != 3) {
      throw new IllegalArgumentException("Ender IO config value remoteInventoryMBPerOpen must have exactly 3 values");
    }
    if (remoteInventoryRFPerTick == null || remoteInventoryRFPerTick.length != 3) {
      throw new IllegalArgumentException("Ender IO config value remoteInventoryRFPerTick must have exactly 3 values");
    }
    if (remoteInventoryMBCapacity == null || remoteInventoryMBCapacity.length != 3) {
      throw new IllegalArgumentException("Ender IO config value remoteInventoryMBCapacity must have exactly 3 values");
    }
    if (remoteInventoryRFCapacity == null || remoteInventoryRFCapacity.length != 3) {
      throw new IllegalArgumentException("Ender IO config value remoteInventoryRFCapacity must have exactly 3 values");
    }
    if (remoteInventoryFluidTypes == null || remoteInventoryFluidTypes.length != 3) {
      throw new IllegalArgumentException("Ender IO config value remoteInventoryFluidTypes must have exactly 3 values");
    }
  }

  public static ItemStack getStackForString(String s) {
    String[] nameAndMeta = s.split(";");
    int meta = nameAndMeta.length == 1 ? 0 : Integer.parseInt(nameAndMeta[1]);
    String[] data = nameAndMeta[0].split(":");
    Item item = Item.REGISTRY.getObject(new ResourceLocation(data[0], data[1]));
    if(item == null) {
      return null;
    }
    return new ItemStack(item, 1, meta);
  }

  private Config() {
  }
}
