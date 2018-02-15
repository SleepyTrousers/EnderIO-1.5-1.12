package crazypants.enderio.base.config;

import java.io.File;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.enderio.core.common.event.ConfigFileChangedEvent;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;
import com.enderio.core.common.vecmath.VecmathUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.config.config.BaseConfig;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class Config {

  public static class Section {
    public final @Nonnull String name;

    public Section(String name, @Nonnull String lang) {
      this.name = lang;
    }

  }

  public static Configuration config;

  public static final @Nonnull Section sectionPower = new Section("Power Settings", "power");
  public static final @Nonnull Section sectionRecipe = new Section("Recipe Settings", "recipe");
  public static final @Nonnull Section sectionItems = new Section("Item Enabling", "item");
  public static final @Nonnull Section sectionEfficiency = new Section("Efficiency Settings", "efficiency");
  public static final @Nonnull Section sectionPersonal = new Section("Personal Settings", "personal");
  public static final @Nonnull Section sectionAnchor = new Section("Anchor Settings", "anchor");
  public static final @Nonnull Section sectionStaff = new Section("Staff Settings", "staff");
  public static final @Nonnull Section sectionRod = new Section("Rod of Return Settings", "rod");
  public static final @Nonnull Section sectionDarkSteel = new Section("Dark Steel", "darksteel");
  public static final @Nonnull Section sectionAesthetic = new Section("Aesthetic Settings", "aesthetic");
  public static final @Nonnull Section sectionAdvanced = new Section("Advanced Settings", "advanced");
  public static final @Nonnull Section sectionMagnet = new Section("Magnet Settings", "magnet");
  public static final @Nonnull Section sectionFluid = new Section("Fluid Settings", "fluid");
  public static final @Nonnull Section sectionKiller = new Section("Killer Joe Settings", "killerjoe");
  public static final @Nonnull Section sectionSoulBinder = new Section("Soul Binder Settings", "soulBinder");
  public static final @Nonnull Section sectionSoulVial = new Section("", "soulvial");
  public static final @Nonnull Section sectionAttractor = new Section("Mob Attractor Settings", "attractor");
  public static final @Nonnull Section sectionEnchantments = new Section("Enchantments", "enchantments");
  public static final @Nonnull Section sectionMisc = new Section("Misc", "misc");
  public static final @Nonnull Section sectionCapacitor = new Section("Capacitor Values", "capacitor");
  public static final @Nonnull Section sectionTOP = new Section("The One Probe integration", "top");
  public static final @Nonnull Section sectionHoes = new Section("Farm Settings.Hoes", "hoes");

  public static final double DEFAULT_CONDUIT_SCALE = 0.6;

  public static final float EXPLOSION_RESISTANT = 2000f * 3.0f / 5.0f; // obsidian

  public static double conduitScale = DEFAULT_CONDUIT_SCALE;

  public static double transceiverEnergyLoss = 0.1;
  public static int transceiverBucketTransmissionCostRF = 100;

  public static File configDirectory;

  public static @Nonnull File getConfigDirectory() {
    return NullHelper.notnull(configDirectory, "trying to access config before preInit");
  }

  public static int recipeLevel = 2;
  public static boolean addPeacefulRecipes = false;
  public static boolean createSyntheticRecipes = true;

  public static boolean detailedPowerTrackingEnabled = false;

  public static boolean useSneakMouseWheelYetaWrench = true;
  public static boolean useSneakRightClickYetaWrench = false;
  public static int yetaWrenchOverlayMode = 0;

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
  public static boolean transparentFacadesLetThroughBeaconBeam = true;

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
  public static String[] travelStaffBlinkBlackList = new String[] { "minecraft:bedrock", "Thaumcraft:blockWarded" };
  public static boolean travelStaffOffhandBlinkEnabled = true;
  public static boolean travelStaffOffhandTravelEnabled = true;
  public static boolean travelStaffOffhandShowsTravelTargets = true;

  public static float travelAnchorZoomScale = 0.2f;

  public static boolean darkSteelRightClickPlaceEnabled = true;

  public static double[] darkSteelPowerDamgeAbsorptionRatios = { 0.5, 0.6, 0.7, 0.85 };
  public static int darkSteelPowerStorageBase = 100000;
  public static int darkSteelPowerStorageLevelOne = 150000;
  public static int darkSteelPowerStorageLevelTwo = 250000;
  public static int darkSteelPowerStorageLevelThree = 1000000;

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

  public static float darkSteelBowDamageBonus = 0f;
  public static double[] darkSteelBowForceMultipliers = { 1.1f, 1.2f, 1.3f, 1.4f, 1.5f };
  public static int[] darkSteelBowDrawSpeeds = { 30, 20, 18, 16, 14 };
  public static double[] darkSteelBowFovMultipliers = { 0.25, 0.3, 0.35, 0.4, 0.45 };
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

  public static boolean addFuelTooltipsToAllFluidContainers = true;
  public static boolean addFurnaceFuelTootip = true;
  public static boolean addDurabilityTootip = true;

  public static String[] hoeStrings = new String[] { "minecraft:wooden_hoe", "minecraft:stone_hoe", "minecraft:iron_hoe", "minecraft:diamond_hoe",
      "minecraft:golden_hoe", "MekanismTools:ObsidianHoe", "MekanismTools:LapisLazuliHoe", "MekanismTools:OsmiumHoe", "MekanismTools:BronzeHoe",
      "MekanismTools:GlowstoneHoe", "MekanismTools:SteelHoe", "Steamcraft:hoeBrass", "Steamcraft:hoeGildedGold", "TConstruct:mattock",
      "ProjRed|Exploration:projectred.exploration.hoeruby", "ProjRed|Exploration:projectred.exploration.hoesapphire",
      "ProjRed|Exploration:projectred.exploration.hoeperidot", "magicalcrops:magicalcrops_AccioHoe", "magicalcrops:magicalcrops_CrucioHoe",
      "magicalcrops:magicalcrops_ImperioHoe",
      // disabled as it is currently not unbreaking as advertised "magicalcrops:magicalcrops_ZivicioHoe",
      "magicalcrops:magicalcropsarmor_AccioHoe", "magicalcrops:magicalcropsarmor_CrucioHoe", "magicalcrops:magicalcropsarmor_ImperioHoe",
      "BiomesOPlenty:hoeAmethyst", "BiomesOPlenty:hoeMud", "Eln:Eln.Copper Hoe", "Thaumcraft:ItemHoeThaumium", "Thaumcraft:ItemHoeElemental",
      "Thaumcraft:ItemHoeVoid", "thermalfoundation:tool.hoeInvar", "thermalfoundation:tool.hoeCopper", "thermalfoundation:tool.hoeBronze",
      "thermalfoundation:tool.hoeSilver", "thermalfoundation:tool.hoeElectrum", "thermalfoundation:tool.hoeTin", "thermalfoundation:tool.hoeLead",
      "thermalfoundation:tool.hoeNickel", "thermalfoundation:tool.hoePlatinum", "TwilightForest:item.steeleafHoe", "TwilightForest:item.ironwoodHoe",
      "IC2:itemToolBronzeHoe", "techreborn:bronzeHoe", "techreborn:rubyHoe", "techreborn:sapphireHoe", "techreborn:peridotHoe", "basemetals:adamantine_hoe",
      "basemetals:aquarium_hoe", "basemetals:brass_hoe", "basemetals:bronze_hoe", "basemetals:coldiron_hoe", "basemetals:copper_hoe",
      "basemetals:cupronickel_hoe", "basemetals:electrum_hoe", "basemetals:invar_hoe", "basemetals:lead_hoe", "basemetals:mithril_hoe", "basemetals:nickel_hoe",
      "basemetals:platinum_hoe", "basemetals:silver_hoe", "basemetals:starsteel_hoe", "basemetals:steel_hoe", "basemetals:tin_hoe",
      "actuallyadditions:itemHoeQuartz", "actuallyadditions:itemHoeEmerald", "actuallyadditions:itemHoeObsidian", "actuallyadditions:itemHoeCrystalRed",
      "actuallyadditions:itemHoeCrystalBlue", "actuallyadditions:itemHoeCrystalLightBlue", "actuallyadditions:itemHoeCrystalBlack",
      "actuallyadditions:itemHoeCrystalGreen", "actuallyadditions:itemHoeCrystalWhite", "silentgems:Hoe", "ic2:bronze_hoe", // IC2exp 1.10
      "appliedenergistics2:nether_quartz_hoe", "appliedenergistics2:certus_quartz_hoe", // AE2 1.10
      "railcraft:tool_hoe_steel", // Railcraft 1.10
      // new in 1.10:
      "calculator:ReinforcedHoe", "calculator:EnrichedGoldHoe", "calculator:ReinforcedIronHoe", "calculator:RedstoneHoe", "calculator:WeakenedDiamondHoe",
      "calculator:FlawlessDiamondHoe", "calculator:FireDiamondHoe", "calculator:ElectricHoe", "embers:hoeDawnstone", "embers:hoeCopper", "embers:hoeSilver",
      "embers:hoeLead", "roots:livingHoe", "mysticalagriculture:inferium_hoe", "mysticalagriculture:prudentium_hoe", "mysticalagriculture:intermedium_hoe",
      "mysticalagriculture:superium_hoe", "mysticalagriculture:supremium_hoe" };
  public static @Nonnull Things farmHoes = new Things();

  public static int magnetPowerUsePerSecondRF = 1;
  public static int magnetPowerCapacityRF = 100000;
  public static int magnetRange = 5;
  public static String[] magnetBlacklist = new String[] { "appliedenergistics2:item.ItemCrystalSeed", "Botania:livingrock", "Botania:manaTablet" };
  public static int magnetMaxItems = 20;

  public static boolean magnetAllowInMainInventory = false;
  public static boolean magnetAllowInBaublesSlot = true;
  public static boolean magnetAllowDeactivatedInBaublesSlot = false;
  public static String magnetBaublesType = "AMULET";

  // public static int crafterRfPerCraft = 2500;

  public static int capacitorBankMaxIoRF = 5000;
  public static int capacitorBankMaxStorageRF = 5000000;

  public static int capacitorBankTierOneMaxIoRF = 1000;
  public static int capacitorBankTierOneMaxStorageRF = 1000000;

  public static int capacitorBankTierTwoMaxIoRF = 5000;
  public static int capacitorBankTierTwoMaxStorageRF = 5000000;

  public static int capacitorBankTierThreeMaxIoRF = 25000;
  public static int capacitorBankTierThreeMaxStorageRF = 25000000;

  public static boolean capacitorBankRenderPowerOverlayOnItem = false;

  public static int painterEnergyPerTaskRF = 2000;

  public static long nutrientFoodBoostDelay = 400;
  public static boolean rocketFuelIsExplosive = true;

  public static boolean machineSoundsEnabled = true;

  public static float machineSoundVolume = 1.0f;

  public static boolean killerProvokesCreeperExpolosions = false;

  public static double xpVacuumRange = 10;

  public static boolean allowTileEntitiesAsPaintSource = true;

  public static boolean enableMEConduits = true;
  public static boolean enableOCConduits = true;
  public static boolean enableOCConduitsAnimatedTexture = true;

  public static NNList<ResourceLocation> soulVesselBlackList = new NNList<ResourceLocation>();
  public static NNList<ResourceLocation> soulVesselUnspawnableList = new NNList<ResourceLocation>();
  static {
    // TODO 1.11 move to integrations, find correct RL
    // soulVesselUnspawnableList.add("chickens.ChickensChicken");
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

  public static float slicenspliceToolDamageChance = 0.01f;

  public static boolean powerConduitCanDifferentTiersConnect = false;
  public static int powerConduitTierOneRF = 640;
  public static int powerConduitTierTwoRF = 5120;
  public static int powerConduitTierThreeRF = 20480;

  public static boolean spawnGuardStopAllSlimesDebug = false;
  public static boolean spawnGuardStopAllSquidSpawning = false;

  public static int xpObeliskMaxXpLevel = Integer.MAX_VALUE;

  public static boolean clearGlassConnectToFusedQuartz = false;
  public static boolean glassConnectToTheirVariants = true;
  public static boolean glassConnectToTheirColorVariants = true;

  public static Rarity enchantmentSoulBoundRarity = Rarity.VERY_RARE;
  public static boolean enchantmentSoulBoundEnabled = true;

  public static boolean rodOfReturnCanTargetAnywhere = false;
  public static int rodOfReturnTicksToActivate = 50;
  public static int rodOfReturnPowerStorage = 2000000;
  public static int rodOfReturnMinTicksToRecharge = 100;
  public static int rodOfReturnRfPerTick = 35000;
  public static int rodOfReturnFluidUsePerTeleport = 200;
  public static int rodOfReturnFluidStorage = 200;
  public static String rodOfReturnFluidType = "ender_distillation";

  public static String coldFireIgniterFluidType = "vapor_of_levity";
  public static int coldFireIgniterMbPerUse = 10;

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

  public static int maxMobsAttracted = 20;

  public static boolean debugTraceNBTActivityExtremelyDetailed = false;
  public static boolean debugTraceTELivecycleExtremelyDetailed = false;
  public static boolean debugTraceCapLimitsExtremelyDetailed = false;

  public static double teleportEffectProbability = 0.03f;

  public static void init(FMLPreInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(new Config());
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

  public static final Things TRAVEL_BLACKLIST = new Things(travelStaffBlinkBlackList);

  @SubscribeEvent
  public void onConfigChanged(OnConfigChangedEvent event) {
    if (event.getModID().equals(EnderIO.MODID)) {
      Log.info("Updating config...");
      syncConfig(false);
      init((FMLPostInitializationEvent) null);
    }
  }

  @SubscribeEvent
  public void onConfigFileChanged(ConfigFileChangedEvent event) {
    if (event.getModID().equals(EnderIO.MODID)) {
      Log.info("Updating config...");
      syncConfig(true);
      event.setSuccessful();
      init((FMLPostInitializationEvent) null);
    }
  }

  @SubscribeEvent
  public void onPlayerLoggon(PlayerLoggedInEvent evt) {
    PacketHandler.INSTANCE.sendTo(new PacketConfigSync(), (EntityPlayerMP) evt.player);
    if (EnderIO.VERSION.contains("-") || EnderIO.VERSION.contains("@")) { // e.g. 1.2.3-nightly
      evt.player.sendMessage(new TextComponentString(
          TextFormatting.DARK_RED + "This is an " + TextFormatting.BLACK + "Ender IO " + TextFormatting.DARK_RED + "development build!"));
      evt.player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "It may trash your world at any time!"));
      evt.player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "Do not use it for anything but testing!"));
      evt.player.sendMessage(new TextComponentString("You have been warned..."));
    }
  }

  public static void processConfig(@SuppressWarnings("hiding") Configuration config) {

    capacitorBankMaxIoRF = config.get(sectionPower.name, "capacitorBankMaxIoRF", capacitorBankMaxIoRF, "The maximum IO for a single capacitor in RF/t")
        .getInt(capacitorBankMaxIoRF);
    capacitorBankMaxStorageRF = config
        .get(sectionPower.name, "capacitorBankMaxStorageRF", capacitorBankMaxStorageRF, "The maximum storage for a single capacitor in RF")
        .getInt(capacitorBankMaxStorageRF);

    capacitorBankTierOneMaxIoRF = config
        .get(sectionPower.name, "capacitorBankTierOneMaxIoRF", capacitorBankTierOneMaxIoRF, "The maximum IO for a single tier one capacitor in RF/t")
        .getInt(capacitorBankTierOneMaxIoRF);
    capacitorBankTierOneMaxStorageRF = config.get(sectionPower.name, "capacitorBankTierOneMaxStorageRF", capacitorBankTierOneMaxStorageRF,
        "The maximum storage for a single tier one capacitor in RF").getInt(capacitorBankTierOneMaxStorageRF);

    capacitorBankTierTwoMaxIoRF = config
        .get(sectionPower.name, "capacitorBankTierTwoMaxIoRF", capacitorBankTierTwoMaxIoRF, "The maximum IO for a single tier two capacitor in RF/t")
        .getInt(capacitorBankTierTwoMaxIoRF);
    capacitorBankTierTwoMaxStorageRF = config.get(sectionPower.name, "capacitorBankTierTwoMaxStorageRF", capacitorBankTierTwoMaxStorageRF,
        "The maximum storage for a single tier two capacitor in RF").getInt(capacitorBankTierTwoMaxStorageRF);

    capacitorBankTierThreeMaxIoRF = config
        .get(sectionPower.name, "capacitorBankTierThreeMaxIoRF", capacitorBankTierThreeMaxIoRF, "The maximum IO for a single tier three capacitor in RF/t")
        .getInt(capacitorBankTierThreeMaxIoRF);
    capacitorBankTierThreeMaxStorageRF = config.get(sectionPower.name, "capacitorBankTierThreeMaxStorageRF", capacitorBankTierThreeMaxStorageRF,
        "The maximum storage for a single tier three capacitor in RF").getInt(capacitorBankTierThreeMaxStorageRF);

    capacitorBankRenderPowerOverlayOnItem = config.getBoolean("capacitorBankRenderPowerOverlayOnItem", sectionAesthetic.name,
        capacitorBankRenderPowerOverlayOnItem, "When true the capacitor bank item will get a power bar in addition to the gauge on the bank");

    powerConduitTierOneRF = config.get(sectionPower.name, "powerConduitTierOneRF", powerConduitTierOneRF, "The maximum IO for the tier 1 power conduit")
        .getInt(powerConduitTierOneRF);
    powerConduitTierTwoRF = config.get(sectionPower.name, "powerConduitTierTwoRF", powerConduitTierTwoRF, "The maximum IO for the tier 2 power conduit")
        .getInt(powerConduitTierTwoRF);
    powerConduitTierThreeRF = config.get(sectionPower.name, "powerConduitTierThreeRF", powerConduitTierThreeRF, "The maximum IO for the tier 3 power conduit")
        .getInt(powerConduitTierThreeRF);
    powerConduitCanDifferentTiersConnect = config.getBoolean("powerConduitCanDifferentTiersConnect", sectionPower.name, powerConduitCanDifferentTiersConnect,
        "If set to false power conduits of different tiers cannot be connected. in this case a block such as a cap. bank is needed to bridge different tiered networks");

    painterEnergyPerTaskRF = config
        .get(sectionPower.name, "painterEnergyPerTaskRF", painterEnergyPerTaskRF, "The total amount of RF required to paint one block")
        .getInt(painterEnergyPerTaskRF);

    recipeLevel = config
        .get(sectionRecipe.name, "recipeLevel", recipeLevel, "How expensive should the crafting recipes be? 0=cheapest, 1=cheaper, 2=normal, 3=expensive")
        .getInt(recipeLevel);

    addPeacefulRecipes = config
        .get(sectionRecipe.name, "addPeacefulRecipes", addPeacefulRecipes, "When enabled peaceful recipes are added for soulbinder based crafting components.")
        .getBoolean(addPeacefulRecipes);
    allowTileEntitiesAsPaintSource = config.get(sectionRecipe.name, "allowTileEntitiesAsPaintSource", allowTileEntitiesAsPaintSource,
        "When enabled blocks with tile entities (e.g. machines) can be used as paint targets.").getBoolean(allowTileEntitiesAsPaintSource);
    createSyntheticRecipes = config.get(sectionRecipe.name, "createSyntheticRecipes", createSyntheticRecipes,
        "Automatically create alloy smelter recipes with double and triple inputs and different slot allocations (1+1+1, 2+1, 1+2, 3 and 2) for single-input recipes.")
        .getBoolean(createSyntheticRecipes);

    redstoneConduitsShowState = config
        .get(sectionMisc.name, "redstoneConduitsShowState", redstoneConduitsShowState,
            "If set to false redstone conduits will look the same whether they are receiving a signal or not. This can help with performance.")
        .getBoolean(redstoneConduitsShowState);

    conduitScale = config
        .get(sectionAesthetic.name, "conduitScale", DEFAULT_CONDUIT_SCALE,
            "Valid values are between 0-1, smallest conduits at 0, largest at 1.\n" + "In SMP, all clients must be using the same value as the server.")
        .getDouble(DEFAULT_CONDUIT_SCALE);
    conduitScale = VecmathUtil.clamp(conduitScale, 0, 1);

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

    detailedPowerTrackingEnabled = config.get(sectionAdvanced.name, "perInterfacePowerTrackingEnabled", detailedPowerTrackingEnabled,
        "Enable per tick sampling on individual power inputs and outputs. This allows slightly more detailed messages from the RF Reader but has a negative impact on server performance.")
        .getBoolean(detailedPowerTrackingEnabled);

    useSneakMouseWheelYetaWrench = config.get(sectionPersonal.name, "useSneakMouseWheelYetaWrench", useSneakMouseWheelYetaWrench,
        "If true, shift-mouse wheel will change the conduit display mode when the YetaWrench is equipped.").getBoolean(useSneakMouseWheelYetaWrench);

    useSneakRightClickYetaWrench = config
        .get(sectionPersonal.name, "useSneakRightClickYetaWrench", useSneakRightClickYetaWrench,
            "If true, shift-clicking the YetaWrench on a null or non wrenchable object will change the conduit display mode.")
        .getBoolean(useSneakRightClickYetaWrench);

    yetaWrenchOverlayMode = config.getInt("yetaWrenchOverlayMode", sectionPersonal.name, yetaWrenchOverlayMode, 0, 2,
        "What kind of overlay to use when holding the yeta wrench\n\n" + "0 - Sideways scrolling in ceter of screen\n"
            + "1 - Vertical icon bar in bottom right\n" + "2 - Old-style group of icons in bottom right");

    machineSoundsEnabled = config.get(sectionPersonal.name, "useMachineSounds", machineSoundsEnabled, "If true, machines will make sounds.")
        .getBoolean(machineSoundsEnabled);

    machineSoundVolume = (float) config.get(sectionPersonal.name, "machineSoundVolume", machineSoundVolume, "Volume of machine sounds.")
        .getDouble(machineSoundVolume);

    itemConduitUsePhyscialDistance = config
        .get(sectionEfficiency.name, "itemConduitUsePhyscialDistance", itemConduitUsePhyscialDistance,
            "If true, " + "'line of sight' distance rather than conduit path distance is used to calculate priorities.")
        .getBoolean(itemConduitUsePhyscialDistance);

    travelAnchorMaximumDistance = config.get(sectionAnchor.name, "travelAnchorMaxDistance", travelAnchorMaximumDistance,
        "Maximum number of blocks that can be traveled from one travel anchor to another.").getInt(travelAnchorMaximumDistance);

    travelAnchorCooldown = config
        .get(sectionAnchor.name, "travelAnchorCooldown", travelAnchorCooldown, "Number of ticks cooldown between activations (1 sec = 20 ticks)")
        .getInt(travelAnchorCooldown);

    travelAnchorSneak = config.get(sectionAnchor.name, "travelAnchorSneak", travelAnchorSneak, "Add sneak as an option to activate travel anchors")
        .getBoolean(travelAnchorSneak);

    travelAnchorSkipWarning = config
        .get(sectionAnchor.name, "travelAnchorSkipWarning", travelAnchorSkipWarning, "Travel Anchors send a chat warning when skipping inaccessible anchors")
        .getBoolean(travelAnchorSkipWarning);

    travelStaffMaximumDistance = config.get(sectionStaff.name, "travelStaffMaxDistance", travelStaffMaximumDistance,
        "Maximum number of blocks that can be traveled using the Staff of Traveling.").getInt(travelStaffMaximumDistance);
    travelStaffPowerPerBlockRF = (float) config.get(sectionStaff.name, "travelStaffPowerPerBlockRF", travelStaffPowerPerBlockRF,
        "Number of RF required per block traveled using the Staff of Traveling.").getDouble(travelStaffPowerPerBlockRF);

    travelStaffMaxBlinkDistance = config
        .get(sectionStaff.name, "travelStaffMaxBlinkDistance", travelStaffMaxBlinkDistance, "Max number of blocks teleported when shift clicking the staff.")
        .getInt(travelStaffMaxBlinkDistance);

    travelStaffBlinkPauseTicks = config.get(sectionStaff.name, "travelStaffBlinkPauseTicks", travelStaffBlinkPauseTicks,
        "Minimum number of ticks between 'blinks'. Values of 10 or less allow a limited sort of flight.").getInt(travelStaffBlinkPauseTicks);

    travelStaffBlinkEnabled = config.get(sectionStaff.name, "travelStaffBlinkEnabled", travelStaffBlinkEnabled,
        "If set to false: the travel staff can not be used to shift-right click teleport, or blink.").getBoolean(travelStaffBlinkEnabled);
    travelStaffBlinkThroughSolidBlocksEnabled = config.get(sectionStaff.name, "travelStaffBlinkThroughSolidBlocksEnabled",
        travelStaffBlinkThroughSolidBlocksEnabled, "If set to false: the travel staff can be used to blink through any block.")
        .getBoolean(travelStaffBlinkThroughSolidBlocksEnabled);
    travelStaffBlinkThroughClearBlocksEnabled = config
        .get(sectionItems.name, "travelStaffBlinkThroughClearBlocksEnabled", travelStaffBlinkThroughClearBlocksEnabled,
            "If travelStaffBlinkThroughSolidBlocksEnabled is set to false and this is true: the travel "
                + "staff can only be used to blink through transparent or partial blocks (e.g. torches). "
                + "If both are false: only air blocks may be teleported through.")
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
    travelStaffOffhandShowsTravelTargets = config
        .get(sectionStaff.name, "travelStaffOffhandShowsTravelTargets", travelStaffOffhandShowsTravelTargets,
            "If set to false: Teleportation targets will not be highlighted for travel items held in the off-hand.")
        .getBoolean(travelStaffOffhandShowsTravelTargets);

    rodOfReturnCanTargetAnywhere = config
        .get(sectionRod.name, "rodOfReturnCanTargetAnywhere", rodOfReturnCanTargetAnywhere, "If set to false the rod of return can only target a telepad.")
        .getBoolean(rodOfReturnCanTargetAnywhere);
    rodOfReturnTicksToActivate = config
        .get(sectionRod.name, "rodOfReturnTicksToActivate", rodOfReturnTicksToActivate, "Number of ticks the rod must be used before teleporting")
        .getInt(rodOfReturnTicksToActivate);
    rodOfReturnPowerStorage = config.get(sectionRod.name, "rodOfReturnPowerStorage", rodOfReturnPowerStorage, "Internal RF buffer for rod")
        .getInt(rodOfReturnPowerStorage);
    rodOfReturnRfPerTick = config.get(sectionRod.name, "rodOfReturnRfPerTick", rodOfReturnRfPerTick, "RF used per tick").getInt(rodOfReturnRfPerTick);
    rodOfReturnMinTicksToRecharge = config
        .get(sectionRod.name, "rodOfReturnMinTicksToRecharge", rodOfReturnMinTicksToRecharge, "Min number of ticks required to recharge the internal RF buffer")
        .getInt(rodOfReturnMinTicksToRecharge);
    rodOfReturnFluidStorage = config.get(sectionRod.name, "rodOfReturnFluidStorage", rodOfReturnFluidStorage, "How much fluid the rod can store")
        .getInt(rodOfReturnFluidStorage);
    rodOfReturnFluidUsePerTeleport = config
        .get(sectionRod.name, "rodOfReturnFluidUsePerTeleport", rodOfReturnFluidUsePerTeleport, "How much fluid is used per teleport")
        .getInt(rodOfReturnFluidUsePerTeleport);
    rodOfReturnFluidType = config.getString("rodOfReturnFluidType", sectionRod.name, rodOfReturnFluidType, "The type of fluid used by the rod.");

    updateLightingWhenHidingFacades = config.get(sectionEfficiency.name, "updateLightingWhenHidingFacades", updateLightingWhenHidingFacades,
        "When true: correct lighting is recalculated (client side) for conduit bundles when transitioning to"
            + " from being hidden behind a facade. This produces "
            + "better quality rendering but can result in frame stutters when switching to/from a wrench.")
        .getBoolean(updateLightingWhenHidingFacades);

    transparentFacadesLetThroughBeaconBeam = config
        .get(sectionAdvanced.name, "transparentFacadesLetThroughBeaconBeam", transparentFacadesLetThroughBeaconBeam,
            "If true, transparent facades will not block the Beacon's beam. As side effect they will also let through a tiny amount of light.")
        .getBoolean(transparentFacadesLetThroughBeaconBeam);

    darkSteelRightClickPlaceEnabled = config.get(sectionDarkSteel.name, "darkSteelRightClickPlaceEnabled", darkSteelRightClickPlaceEnabled,
        "Enable / disable right click to place block using dark steel tools.").getBoolean(darkSteelRightClickPlaceEnabled);

    darkSteelPowerDamgeAbsorptionRatios = config.get(sectionDarkSteel.name, "darkSteelPowerDamgeAbsorptionRatios", darkSteelPowerDamgeAbsorptionRatios,
        "A list of the amount of durability damage absorbed when items are powered. In order of upgrade level. 1=100% so items take no durability damage when powered.")
        .getDoubleList();
    darkSteelPowerStorageBase = config
        .get(sectionDarkSteel.name, "darkSteelPowerStorageBase", darkSteelPowerStorageBase, "Base amount of power stored by dark steel items.")
        .getInt(darkSteelPowerStorageBase);
    darkSteelPowerStorageLevelOne = config.get(sectionDarkSteel.name, "darkSteelPowerStorageLevelOne", darkSteelPowerStorageLevelOne,
        "Amount of power stored by dark steel items with a level 1 upgrade.").getInt(darkSteelPowerStorageLevelOne);
    darkSteelPowerStorageLevelTwo = config.get(sectionDarkSteel.name, "darkSteelPowerStorageLevelTwo", darkSteelPowerStorageLevelTwo,
        "Amount of power stored by dark steel items with a level 2 upgrade.").getInt(darkSteelPowerStorageLevelTwo);
    darkSteelPowerStorageLevelThree = config.get(sectionDarkSteel.name, "darkSteelPowerStorageLevelThree", darkSteelPowerStorageLevelThree,
        "Amount of power stored by dark steel items with a level 3 upgrade.").getInt(darkSteelPowerStorageLevelThree);

    darkSteelUpgradeVibrantCost = config
        .get(sectionDarkSteel.name, "darkSteelUpgradeVibrantCost", darkSteelUpgradeVibrantCost, "Number of levels required for the 'Empowered.")
        .getInt(darkSteelUpgradeVibrantCost);
    darkSteelUpgradePowerOneCost = config
        .get(sectionDarkSteel.name, "darkSteelUpgradePowerOneCost", darkSteelUpgradePowerOneCost, "Number of levels required for the 'Power 1.")
        .getInt(darkSteelUpgradePowerOneCost);
    darkSteelUpgradePowerTwoCost = config
        .get(sectionDarkSteel.name, "darkSteelUpgradePowerTwoCost", darkSteelUpgradePowerTwoCost, "Number of levels required for the 'Power 2.")
        .getInt(darkSteelUpgradePowerTwoCost);
    darkSteelUpgradePowerThreeCost = config
        .get(sectionDarkSteel.name, "darkSteelUpgradePowerThreeCost", darkSteelUpgradePowerThreeCost, "Number of levels required for the 'Power 3' upgrade.")
        .getInt(darkSteelUpgradePowerThreeCost);

    darkSteelJumpOneCost = config
        .get(sectionDarkSteel.name, "darkSteelJumpOneCost", darkSteelJumpOneCost, "Number of levels required for the 'Jump 1' upgrade.")
        .getInt(darkSteelJumpOneCost);
    darkSteelJumpTwoCost = config
        .get(sectionDarkSteel.name, "darkSteelJumpTwoCost", darkSteelJumpTwoCost, "Number of levels required for the 'Jump 2' upgrade.")
        .getInt(darkSteelJumpTwoCost);
    darkSteelJumpThreeCost = config
        .get(sectionDarkSteel.name, "darkSteelJumpThreeCost", darkSteelJumpThreeCost, "Number of levels required for the 'Jump 3' upgrade.")
        .getInt(darkSteelJumpThreeCost);

    darkSteelSpeedOneCost = config
        .get(sectionDarkSteel.name, "darkSteelSpeedOneCost", darkSteelSpeedOneCost, "Number of levels required for the 'Speed 1' upgrade.")
        .getInt(darkSteelSpeedOneCost);
    darkSteelSpeedTwoCost = config
        .get(sectionDarkSteel.name, "darkSteelSpeedTwoCost", darkSteelSpeedTwoCost, "Number of levels required for the 'Speed 2' upgrade.")
        .getInt(darkSteelSpeedTwoCost);
    darkSteelSpeedThreeCost = config
        .get(sectionDarkSteel.name, "darkSteelSpeedThreeCost", darkSteelSpeedThreeCost, "Number of levels required for the 'Speed 3' upgrade.")
        .getInt(darkSteelSpeedThreeCost);

    slotZeroPlacesEight = config
        .get(sectionDarkSteel.name, "shouldSlotZeroWrap", slotZeroPlacesEight,
            "Should the dark steel placement, when in the first (0th) slot, place the item in the last slot. If false, will place what's in the second slot.")
        .getBoolean();

    darkSteelBootsJumpModifier = config.get(sectionDarkSteel.name, "darkSteelBootsJumpModifier", darkSteelBootsJumpModifier,
        "Jump height modifier applied when jumping with Dark Steel Boots equipped").getDouble(darkSteelBootsJumpModifier);

    darkSteelPowerStorageBase = config
        .get(sectionDarkSteel.name, "darkSteelPowerStorage", darkSteelPowerStorageBase, "Amount of power stored (RF) per crystal in the armor items recipe.")
        .getInt(darkSteelPowerStorageBase);
    darkSteelWalkPowerCost = config.get(sectionDarkSteel.name, "darkSteelWalkPowerCost", darkSteelWalkPowerCost,
        "Amount of power stored (RF) per block walked when wearing the dark steel boots.").getInt(darkSteelWalkPowerCost);
    darkSteelSprintPowerCost = config.get(sectionDarkSteel.name, "darkSteelSprintPowerCost", darkSteelWalkPowerCost,
        "Amount of power stored (RF) per block walked when wearing the dark steel boots.").getInt(darkSteelSprintPowerCost);
    darkSteelDrainPowerFromInventory = config
        .get(sectionDarkSteel.name, "darkSteelDrainPowerFromInventory", darkSteelDrainPowerFromInventory,
            "If true, dark steel armor will drain power stored (RF) in power containers in the players inventory.")
        .getBoolean(darkSteelDrainPowerFromInventory);

    darkSteelBootsJumpPowerCost = config
        .get(sectionDarkSteel.name, "darkSteelBootsJumpPowerCost", darkSteelBootsJumpPowerCost,
            "Base amount of power used per jump (RF) dark steel boots. The second jump in a 'double jump' uses 2x this etc")
        .getInt(darkSteelBootsJumpPowerCost);

    darkSteelFallDistanceCost = config.get(sectionDarkSteel.name, "darkSteelFallDistanceCost", darkSteelFallDistanceCost,
        "Amount of power used (RF) per block height of fall distance damage negated.").getInt(darkSteelFallDistanceCost);

    darkSteelSwimCost = config.get(sectionDarkSteel.name, "darkSteelSwimCost", darkSteelSwimCost, "Number of levels required for the 'Swim' upgrade.")
        .getInt(darkSteelSwimCost);

    darkSteelNightVisionCost = config
        .get(sectionDarkSteel.name, "darkSteelNightVisionCost", darkSteelNightVisionCost, "Number of levels required for the 'Night Vision' upgrade.")
        .getInt(darkSteelNightVisionCost);

    darkSteelTOPCost = config.get(sectionDarkSteel.name, "darkSteelTOPCost", darkSteelTOPCost, "Number of levels required for the 'The One Probe' upgrade.")
        .getInt(darkSteelTOPCost);

    darkSteelGliderCost = config.get(sectionDarkSteel.name, "darkSteelGliderCost", darkSteelGliderCost, "Number of levels required for the 'Glider' upgrade.")
        .getInt(darkSteelGliderCost);
    darkSteelGliderHorizontalSpeed = config
        .get(sectionDarkSteel.name, "darkSteelGliderHorizontalSpeed", darkSteelGliderHorizontalSpeed, "Horizontal movement speed modifier when gliding.")
        .getDouble(darkSteelGliderHorizontalSpeed);
    darkSteelGliderVerticalSpeed = config
        .get(sectionDarkSteel.name, "darkSteelGliderVerticalSpeed", darkSteelGliderVerticalSpeed, "Rate of altitude loss when gliding.")
        .getDouble(darkSteelGliderVerticalSpeed);
    darkSteelGliderVerticalSpeedSprinting = config.get(sectionDarkSteel.name, "darkSteelGliderVerticalSpeedSprinting", darkSteelGliderVerticalSpeedSprinting,
        "Rate of altitude loss when sprinting and gliding.").getDouble(darkSteelGliderVerticalSpeedSprinting);

    darkSteelElytraCost = config.get(sectionDarkSteel.name, "darkSteelElytraCost", darkSteelElytraCost, "Number of levels required for the 'Elytra' upgrade.")
        .getInt(darkSteelElytraCost);

    darkSteelSoundLocatorCost = config
        .get(sectionDarkSteel.name, "darkSteelSoundLocatorCost", darkSteelSoundLocatorCost, "Number of levels required for the 'Sound Locator' upgrade.")
        .getInt(darkSteelSoundLocatorCost);
    darkSteelSoundLocatorRange = config
        .get(sectionDarkSteel.name, "darkSteelSoundLocatorRange", darkSteelSoundLocatorRange, "Range of the 'Sound Locator' upgrade.")
        .getInt(darkSteelSoundLocatorRange);
    darkSteelSoundLocatorLifespan = config.get(sectionDarkSteel.name, "darkSteelSoundLocatorLifespan", darkSteelSoundLocatorLifespan,
        "Number of ticks the 'Sound Locator' icons are displayed for.").getInt(darkSteelSoundLocatorLifespan);

    darkSteelGogglesOfRevealingCost = config.get(sectionDarkSteel.name, "darkSteelGogglesOfRevealingCost", darkSteelGogglesOfRevealingCost,
        "Number of levels required for the Goggles of Revealing upgrade.").getInt(darkSteelGogglesOfRevealingCost);

    darkSteelApiaristArmorCost = config
        .get(sectionDarkSteel.name, "darkSteelApiaristArmorCost", darkSteelApiaristArmorCost, "Number of levels required for the Apiarist Armor upgrade.")
        .getInt(darkSteelApiaristArmorCost);

    darkSteelTravelCost = config.get(sectionDarkSteel.name, "darkSteelTravelCost", darkSteelTravelCost, "Number of levels required for the 'Travel' upgrade.")
        .getInt(darkSteelTravelCost);

    darkSteelSpoonCost = config.get(sectionDarkSteel.name, "darkSteelSpoonCost", darkSteelSpoonCost, "Number of levels required for the 'Spoon' upgrade.")
        .getInt(darkSteelSpoonCost);

    darkSteelSolarChargeOthers = config.get(sectionDarkSteel.name, "darkSteelSolarChargeOthers", darkSteelSolarChargeOthers,
        "If enabled allows the solar upgrade to charge non-darksteel armors that the player is wearing.").getBoolean();

    darkSteelSwordSkullChance = (float) config
        .get(sectionDarkSteel.name, "darkSteelSwordSkullChance", darkSteelSwordSkullChance,
            "The base chance that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(darkSteelSwordSkullChance);
    darkSteelSwordSkullLootingModifier = (float) config
        .get(sectionDarkSteel.name, "darkSteelSwordSkullLootingModifier", darkSteelSwordSkullLootingModifier,
            "The chance per looting level that a skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(darkSteelSwordSkullLootingModifier);

    darkSteelSwordWitherSkullChance = (float) config
        .get(sectionDarkSteel.name, "darkSteelSwordWitherSkullChance", darkSteelSwordWitherSkullChance,
            "The base chance that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(darkSteelSwordWitherSkullChance);
    darkSteelSwordWitherSkullLootingModifier = (float) config
        .get(sectionDarkSteel.name, "darkSteelSwordWitherSkullLootingModifie", darkSteelSwordWitherSkullLootingModifier,
            "The chance per looting level that a wither skull will be dropped when using a powered dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(darkSteelSwordWitherSkullLootingModifier);

    vanillaSwordSkullChance = (float) config
        .get(sectionDarkSteel.name, "vanillaSwordSkullChance", vanillaSwordSkullChance,
            "The base chance that a skull will be dropped when using a non dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(vanillaSwordSkullChance);
    vanillaSwordSkullLootingModifier = (float) config
        .get(sectionPersonal.name, "vanillaSwordSkullLootingModifier", vanillaSwordSkullLootingModifier,
            "The chance per looting level that a skull will be dropped when using a non-dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(vanillaSwordSkullLootingModifier);

    ticCleaverSkullDropChance = (float) config.get(sectionDarkSteel.name, "ticCleaverSkullDropChance", ticCleaverSkullDropChance,
        "The base chance that an Enderman Skull will be dropped when using TiC Cleaver").getDouble(ticCleaverSkullDropChance);
    ticBeheadingSkullModifier = (float) config.get(sectionPersonal.name, "ticBeheadingSkullModifier", ticBeheadingSkullModifier,
        "The chance per level of Beheading that a skull will be dropped when using a TiC weapon").getDouble(ticBeheadingSkullModifier);

    fakePlayerSkullChance = (float) config.get(sectionDarkSteel.name, "fakePlayerSkullChance", fakePlayerSkullChance,
        "The ratio of skull drops when a mob is killed by a 'FakePlayer', such as Killer Joe. When set to 0 no skulls will drop, at 1 the rate of skull drops is not modified")
        .getDouble(fakePlayerSkullChance);

    darkSteelSwordPowerUsePerHit = config
        .get(sectionDarkSteel.name, "darkSteelSwordPowerUsePerHit", darkSteelSwordPowerUsePerHit, "The amount of power (RF) used per hit.")
        .getInt(darkSteelSwordPowerUsePerHit);
    darkSteelSwordEnderPearlDropChance = config
        .get(sectionDarkSteel.name, "darkSteelSwordEnderPearlDropChance", darkSteelSwordEnderPearlDropChance,
            "The chance that an ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(darkSteelSwordEnderPearlDropChance);
    darkSteelSwordEnderPearlDropChancePerLooting = config
        .get(sectionDarkSteel.name, "darkSteelSwordEnderPearlDropChancePerLooting", darkSteelSwordEnderPearlDropChancePerLooting,
            "The chance for each looting level that an additional ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(darkSteelSwordEnderPearlDropChancePerLooting);

    darkSteelBowDamageBonus = (float) config
        .get(sectionDarkSteel.name, "darkSteelBowDamageBonus", darkSteelBowDamageBonus, "The damage bonus applied to arrows fire from the bow.")
        .getDouble(darkSteelBowDamageBonus);
    darkSteelBowForceMultipliers = config.get(sectionDarkSteel.name, "darkSteelBowForceMultipliers", darkSteelBowForceMultipliers,
        "Multiplier that effects the speed with which arrows leave the bow.").getDoubleList();
    darkSteelBowFovMultipliers = config.get(sectionDarkSteel.name, "darkSteelBowFovMultiplier", darkSteelBowFovMultipliers,
        "The reduction in FOV when the bow is fullen drawn (the zoom level). A 'vanilla' bow has a value of 0.15").getDoubleList();
    darkSteelBowPowerUsePerDamagePoint = config
        .get(sectionDarkSteel.name, "darkSteelBowPowerUsePerDamagePoint", darkSteelBowPowerUsePerDamagePoint, "The amount of power (RF) used per hit.")
        .getInt(darkSteelBowPowerUsePerDamagePoint);
    darkSteelBowDrawSpeeds = config.get(sectionDarkSteel.name, "darkSteelBowDrawSpeeds", darkSteelBowDrawSpeeds,
        "A list of the amount of draw speeds at the different upgrade levels. A vanilla bow draw speed is 20").getIntList();
    darkSteelBowPowerUsePerDraw = config
        .get(sectionDarkSteel.name, "darkSteelBowPowerUsePerDraw", darkSteelBowPowerUsePerDraw, "The power used to fully draw the bow")
        .getInt(darkSteelBowPowerUsePerDraw);
    darkSteelBowPowerUsePerTickDrawn = config
        .get(sectionDarkSteel.name, "darkSteelBowPowerUsePerTickDrawn", darkSteelBowPowerUsePerTickDrawn, "The power used per tick to hold the boy fully drawn")
        .getInt(darkSteelBowPowerUsePerTickDrawn);

    darkSteelPickPowerUseObsidian = config
        .get(sectionDarkSteel.name, "darkSteelPickPowerUseObsidian", darkSteelPickPowerUseObsidian, "The amount of power (RF) used to break an obsidian block.")
        .getInt(darkSteelPickPowerUseObsidian);
    darkSteelPickEffeciencyObsidian = config.get(sectionDarkSteel.name, "darkSteelPickEffeciencyObsidian", darkSteelPickEffeciencyObsidian,
        "The efficiency when breaking obsidian with a powered Dark Pickaxe.").getInt(darkSteelPickEffeciencyObsidian);
    darkSteelPickApplyObsidianEffeciencyAtHardess = (float) config
        .get(sectionDarkSteel.name, "darkSteelPickApplyObsidianEffeciencyAtHardess", darkSteelPickApplyObsidianEffeciencyAtHardess,
            "If set to a value > 0, the obsidian speed and power use will be used for all blocks with hardness >= to this value.")
        .getDouble(darkSteelPickApplyObsidianEffeciencyAtHardess);
    darkSteelPickPowerUsePerDamagePoint = config.get(sectionDarkSteel.name, "darkSteelPickPowerUsePerDamagePoint", darkSteelPickPowerUsePerDamagePoint,
        "Power use (RF) per damage/durability point avoided.").getInt(darkSteelPickPowerUsePerDamagePoint);
    darkSteelPickEffeciencyBoostWhenPowered = (float) config.get(sectionDarkSteel.name, "darkSteelPickEffeciencyBoostWhenPowered",
        darkSteelPickEffeciencyBoostWhenPowered, "The increase in efficiency when powered.").getDouble(darkSteelPickEffeciencyBoostWhenPowered);
    darkSteelPickMinesTiCArdite = config.getBoolean("darkSteelPickMinesTiCArdite", sectionDarkSteel.name, darkSteelPickMinesTiCArdite,
        "When true the dark steel pick will be able to mine TiC Ardite and Cobalt");

    darkSteelAxePowerUsePerDamagePoint = config.get(sectionDarkSteel.name, "darkSteelAxePowerUsePerDamagePoint", darkSteelAxePowerUsePerDamagePoint,
        "Power use (RF) per damage/durability point avoided.").getInt(darkSteelAxePowerUsePerDamagePoint);
    darkSteelAxePowerUsePerDamagePointMultiHarvest = config.get(sectionDarkSteel.name, "darkSteelPickAxeUsePerDamagePointMultiHarvest",
        darkSteelAxePowerUsePerDamagePointMultiHarvest, "Power use (RF) per damage/durability point avoided when shift-harvesting multiple logs")
        .getInt(darkSteelAxePowerUsePerDamagePointMultiHarvest);
    darkSteelAxeSpeedPenaltyMultiHarvest = (float) config
        .get(sectionDarkSteel.name, "darkSteelAxeSpeedPenaltyMultiHarvest", darkSteelAxeSpeedPenaltyMultiHarvest, "How much slower shift-harvesting logs is.")
        .getDouble(darkSteelAxeSpeedPenaltyMultiHarvest);
    darkSteelAxeEffeciencyBoostWhenPowered = (float) config.get(sectionDarkSteel.name, "darkSteelAxeEffeciencyBoostWhenPowered",
        darkSteelAxeEffeciencyBoostWhenPowered, "The increase in efficiency when powered.").getDouble(darkSteelAxeEffeciencyBoostWhenPowered);

    darkSteelShearsDurabilityFactor = config
        .get(sectionDarkSteel.name, "darkSteelShearsDurabilityFactor", darkSteelShearsDurabilityFactor, "How much more durable as vanilla shears they are.")
        .getInt(darkSteelShearsDurabilityFactor);
    darkSteelShearsPowerUsePerDamagePoint = config.get(sectionDarkSteel.name, "darkSteelShearsPowerUsePerDamagePoint", darkSteelShearsPowerUsePerDamagePoint,
        "Power use (RF) per damage/durability point avoided.").getInt(darkSteelShearsPowerUsePerDamagePoint);
    darkSteelShearsEffeciencyBoostWhenPowered = (float) config.get(sectionDarkSteel.name, "darkSteelShearsEffeciencyBoostWhenPowered",
        darkSteelShearsEffeciencyBoostWhenPowered, "The increase in efficiency when powered.").getDouble(darkSteelShearsEffeciencyBoostWhenPowered);
    darkSteelShearsBlockAreaBoostWhenPowered = config.get(sectionDarkSteel.name, "darkSteelShearsBlockAreaBoostWhenPowered",
        darkSteelShearsBlockAreaBoostWhenPowered, "The increase in effected area (radius) when powered and used on blocks.")
        .getInt(darkSteelShearsBlockAreaBoostWhenPowered);
    darkSteelShearsEntityAreaBoostWhenPowered = (float) config.get(sectionDarkSteel.name, "darkSteelShearsEntityAreaBoostWhenPowered",
        darkSteelShearsEntityAreaBoostWhenPowered, "The increase in effected area (radius) when powered and used on sheep.")
        .getDouble(darkSteelShearsEntityAreaBoostWhenPowered);

    darkSteelAnvilDamageChance = (float) config.get(sectionDarkSteel.name, "darkSteelAnvilDamageChance", darkSteelAnvilDamageChance,
        "Chance that the dark steel anvil will take damage after repairing something.").getDouble();

    darkSteelAnvilMaxLevel = config
        .get(sectionDarkSteel.name, "darkSteelAnvilMaxLevel", darkSteelAnvilMaxLevel, "Max cost operation the anvil can perform. Vanilla limit is 40.")
        .getInt();

    darkSteelLadderSpeedBoost = (float) config.get(sectionDarkSteel.name, "darkSteelLadderSpeedBoost", darkSteelLadderSpeedBoost,
        "Speed boost, in blocks per tick, that the DS ladder gives over the vanilla ladder.").getDouble();

    hootchPowerPerCycleRF = config.get(sectionPower.name, "hootchPowerPerCycleRF", hootchPowerPerCycleRF,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 30, BC Fuel = 60").getInt(hootchPowerPerCycleRF);
    hootchPowerTotalBurnTime = config
        .get(sectionPower.name, "hootchPowerTotalBurnTime", hootchPowerTotalBurnTime, "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000")
        .getInt(hootchPowerTotalBurnTime);

    rocketFuelPowerPerCycleRF = config.get(sectionPower.name, "rocketFuelPowerPerCycleRF", rocketFuelPowerPerCycleRF,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 3, BC Fuel = 6").getInt(rocketFuelPowerPerCycleRF);
    rocketFuelPowerTotalBurnTime = config
        .get(sectionPower.name, "rocketFuelPowerTotalBurnTime", rocketFuelPowerTotalBurnTime, "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000")
        .getInt(rocketFuelPowerTotalBurnTime);

    fireWaterPowerPerCycleRF = config.get(sectionPower.name, "fireWaterPowerPerCycleRF", fireWaterPowerPerCycleRF,
        "The amount of power generated per BC engine cycle. Examples: BC Oil = 30, BC Fuel = 60").getInt(fireWaterPowerPerCycleRF);
    fireWaterPowerTotalBurnTime = config
        .get(sectionPower.name, "fireWaterPowerTotalBurnTime", fireWaterPowerTotalBurnTime, "The total burn time. Examples: BC Oil = 5000, BC Fuel = 25000")
        .getInt(fireWaterPowerTotalBurnTime);

    addFuelTooltipsToAllFluidContainers = config
        .get(sectionPersonal.name, "addFuelTooltipsToAllFluidContainers", addFuelTooltipsToAllFluidContainers,
            "If true, the RF/t and burn time of the fuel will be displayed in all tooltips for fluid containers with fuel.")
        .getBoolean(addFuelTooltipsToAllFluidContainers);
    addDurabilityTootip = config
        .get(sectionPersonal.name, "addDurabilityTootip", addFuelTooltipsToAllFluidContainers, "If true, adds durability tooltips to tools and armor")
        .getBoolean(addDurabilityTootip);
    addFurnaceFuelTootip = config
        .get(sectionPersonal.name, "addFurnaceFuelTootip", addFuelTooltipsToAllFluidContainers, "If true, adds burn duration tooltips to furnace fuels")
        .getBoolean(addFurnaceFuelTootip);

    debugTraceNBTActivityExtremelyDetailed = config
        .get(sectionAdvanced.name, "debugTraceNBTActivityExtremelyDetailed", debugTraceNBTActivityExtremelyDetailed,
            "This will flood your logfile with gigabytes of data filling up your harddisk very fast. DO NOT enable unless asked by an Ender IO developer!")
        .getBoolean(debugTraceNBTActivityExtremelyDetailed);

    debugTraceTELivecycleExtremelyDetailed = config
        .get(sectionAdvanced.name, "debugTraceTELivecycleExtremelyDetailed", debugTraceTELivecycleExtremelyDetailed,
            "This will flood your logfile with gigabytes of data filling up your harddisk very fast. DO NOT enable unless asked by an Ender IO developer!")
        .getBoolean(debugTraceTELivecycleExtremelyDetailed);

    debugTraceCapLimitsExtremelyDetailed = config
        .get(sectionAdvanced.name, "debugTraceCapLimitsExtremelyDetailed", debugTraceCapLimitsExtremelyDetailed,
            "This will flood your logfile with gigabytes of data filling up your harddisk very fast. DO NOT enable unless asked by an Ender IO developer!")
        .getBoolean(debugTraceCapLimitsExtremelyDetailed);

    // START Hoes

    ConfigCategory hoes = config.getCategory(sectionHoes.name);
    hoes.setComment("Each value of this category is an item that could be a hoe. You can add more values.");

    for (String hoe : hoeStrings) {
      config.get(sectionHoes.name, hoe, true, "Is this item a hoe that can be used in the farming station?");
    }

    farmHoes = new Things();
    for (Entry<String, Property> entry : hoes.entrySet()) {
      if (entry.getValue().getBoolean()) {
        farmHoes.add(entry.getKey());
      }
    }

    // END Hoes

    magnetPowerUsePerSecondRF = config
        .get(sectionMagnet.name, "magnetPowerUsePerTickRF", magnetPowerUsePerSecondRF, "The amount of RF power used per tick when the magnet is active")
        .getInt(magnetPowerUsePerSecondRF);
    magnetPowerCapacityRF = config
        .get(sectionMagnet.name, "magnetPowerCapacityRF", magnetPowerCapacityRF, "Amount of RF power stored in a fully charged magnet")
        .getInt(magnetPowerCapacityRF);
    magnetRange = config.get(sectionMagnet.name, "magnetRange", magnetRange, "Range of the magnet in blocks.").getInt(magnetRange);
    magnetMaxItems = config
        .get(sectionMagnet.name, "magnetMaxItems", magnetMaxItems, "Maximum number of items the magnet can effect at a time. (-1 for unlimited)")
        .getInt(magnetMaxItems);

    magnetBlacklist = config.getStringList("magnetBlacklist", sectionMagnet.name, magnetBlacklist, "These items will not be picked up by the magnet.");

    magnetAllowInMainInventory = config.get(sectionMagnet.name, "magnetAllowInMainInventory", magnetAllowInMainInventory,
        "If true the magnet will also work in the main inventory, not just the hotbar").getBoolean(magnetAllowInMainInventory);

    magnetAllowInBaublesSlot = config.get(sectionMagnet.name, "magnetAllowInBaublesSlot", magnetAllowInBaublesSlot,
        "If true the magnet can be put into the 'amulet' Baubles slot (requires Baubles to be installed)").getBoolean(magnetAllowInBaublesSlot);
    magnetAllowDeactivatedInBaublesSlot = config.get(sectionMagnet.name, "magnetAllowDeactivatedInBaublesSlot", magnetAllowDeactivatedInBaublesSlot,
        "If true the magnet can be put into the 'amulet' Baubles slot even if switched off (requires Baubles to be installed and magnetAllowInBaublesSlot to be on)")
        .getBoolean(magnetAllowDeactivatedInBaublesSlot);

    magnetBaublesType = config
        .get(sectionMagnet.name, "magnetBaublesType", magnetBaublesType,
            "The BaublesType the magnet should be, 'AMULET', 'RING' or 'BELT' (requires Baubles to be installed and magnetAllowInBaublesSlot to be on)")
        .getString();

    // crafterRfPerCraft = config.get("AutoCrafter Settings", "crafterRfPerCraft", crafterRfPerCraft, "RF used per autocrafted
    // recipe").getInt(crafterRfPerCraft);

    nutrientFoodBoostDelay = config.get(sectionFluid.name, "nutrientFluidFoodBoostDelay", nutrientFoodBoostDelay,
        "The delay in ticks between when nutrient distillation boosts your food value.").getInt((int) nutrientFoodBoostDelay);
    rocketFuelIsExplosive = config
        .get(sectionFluid.name, "rocketFuelIsExplosive", rocketFuelIsExplosive, "If enabled, Rocket Fuel will explode when in contact with fire.").getBoolean();

    killerProvokesCreeperExpolosions = config.get(sectionKiller.name, "killerProvokesCreeperExpolosions", killerProvokesCreeperExpolosions,
        "If enabled, Creepers will explode for the Killer Joe just like for any player.").getBoolean();

    xpVacuumRange = config.get(sectionAdvanced.name, "xpVacuumRange", xpVacuumRange, "The distance from which XP will be gathered by the XP vacuum.")
        .getDouble(xpVacuumRange);

    // Add deprecated comment
    enableMEConduits = config.getBoolean("enableMEConduits", sectionItems.name, enableMEConduits, "Allows ME conduits. Only has an effect with AE2 installed.");
    enableOCConduits = config.getBoolean("enableOCConduits", sectionItems.name, enableOCConduits,
        "Allows OC conduits. Only has an effect with OpenComputers installed.");
    enableOCConduitsAnimatedTexture = config.getBoolean("enableOCConduitsAnimatedTexture", sectionItems.name, enableOCConduitsAnimatedTexture,
        "Use the animated texture for OC conduits.");

    final NNList<String> temp = new NNList<>();
    soulVesselBlackList.apply(new Callback<ResourceLocation>() {
      @Override
      public void apply(@Nonnull ResourceLocation rl) {
        temp.add(rl.toString());
      }
    });
    String[] list = config.getStringList("soulVesselBlackList", sectionSoulVial.name, temp.toArray(new String[0]),
        "Entities listed here will can not be captured in a Soul Vial");
    soulVesselBlackList.clear();
    for (String string : list) {
      if (string != null) {
        soulVesselBlackList.add(new ResourceLocation(string));
      }
    }

    temp.clear();
    soulVesselUnspawnableList.apply(new Callback<ResourceLocation>() {
      @Override
      public void apply(@Nonnull ResourceLocation rl) {
        temp.add(rl.toString());
      }
    });
    list = config.getStringList("soulVesselUnspawnableList", sectionSoulVial.name, temp.toArray(new String[0]),
        "Entities listed here cannot be spawned and must be cloned from a captured entity instead (Attention: Possibility of item duping!)");
    soulVesselUnspawnableList.clear();
    for (String string : list) {
      if (string != null) {
        soulVesselUnspawnableList.add(new ResourceLocation(string));
      }
    }

    soulVesselCapturesBosses = config.getBoolean("soulVesselCapturesBosses", sectionSoulVial.name, soulVesselCapturesBosses,
        "When set to false, any mob with a 'boss bar' won't be able to be captured in the Soul Vial. Note: The Ender Dragon can not "
            + "be captured, even with this enabled. This is a limitation of the dragon, not the Soul Vial.");

    soulBinderBrokenSpawnerRF = config.get(sectionSoulBinder.name, "soulBinderBrokenSpawnerRF", soulBinderBrokenSpawnerRF,
        "The number of RF required to change the type of a broken spawner.").getInt(soulBinderBrokenSpawnerRF);
    soulBinderReanimationRF = config
        .get(sectionSoulBinder.name, "soulBinderReanimationRF", soulBinderReanimationRF, "The number of RF required to to re-animated a mob head.")
        .getInt(soulBinderReanimationRF);
    soulBinderEnderCystalRF = config
        .get(sectionSoulBinder.name, "soulBinderEnderCystalRF", soulBinderEnderCystalRF, "The number of RF required to create an ender crystal.")
        .getInt(soulBinderEnderCystalRF);
    soulBinderAttractorCystalRF = config
        .get(sectionSoulBinder.name, "soulBinderAttractorCystalRF", soulBinderAttractorCystalRF, "The number of RF required to create an attractor crystal.")
        .getInt(soulBinderAttractorCystalRF);
    soulBinderTunedPressurePlateRF = config
        .get(sectionSoulBinder.name, "soulBinderTunedPressurePlateRF", soulBinderTunedPressurePlateRF, "The number of RF required to tune a pressure plate.")
        .getInt(soulBinderTunedPressurePlateRF);
    soulBinderPrecientCystalRF = config
        .get(sectionSoulBinder.name, "soulBinderPrecientCystalRF", soulBinderPrecientCystalRF, "The number of RF required to create a precient crystal.")
        .getInt(soulBinderPrecientCystalRF);

    soulBinderAttractorCystalLevels = config.get(sectionSoulBinder.name, "soulBinderAttractorCystalLevels", soulBinderAttractorCystalLevels,
        "The number of levels required to create an attractor crystal.").getInt(soulBinderAttractorCystalLevels);
    soulBinderEnderCystalLevels = config
        .get(sectionSoulBinder.name, "soulBinderEnderCystalLevels", soulBinderEnderCystalLevels, "The number of levels required to create an ender crystal.")
        .getInt(soulBinderEnderCystalLevels);
    soulBinderPrecientCystalLevels = config.get(sectionSoulBinder.name, "soulBinderPrecientCystalLevels", soulBinderPrecientCystalLevels,
        "The number of levels required to create a precient crystal.").getInt(soulBinderPrecientCystalLevels);
    soulBinderReanimationLevels = config
        .get(sectionSoulBinder.name, "soulBinderReanimationLevels", soulBinderReanimationLevels, "The number of levels required to re-animate a mob head.")
        .getInt(soulBinderReanimationLevels);
    soulBinderBrokenSpawnerLevels = config.get(sectionSoulBinder.name, "soulBinderBrokenSpawnerLevels", soulBinderBrokenSpawnerLevels,
        "The number of levels required to change the type of a broken spawner.").getInt(soulBinderBrokenSpawnerLevels);
    soulBinderTunedPressurePlateLevels = config.get(sectionSoulBinder.name, "soulBinderTunedPressurePlateLevels", soulBinderTunedPressurePlateLevels,
        "The number of levels required to tune a pressure plate.").getInt(soulBinderTunedPressurePlateLevels);

    slicenspliceToolDamageChance = (float) config.get(sectionAdvanced.name, "slicenspliceToolDamageChance", slicenspliceToolDamageChance,
        "The chance that a tool will take damage each tick while the Slice'n'Splice is running (0 = no chance, 1 = 100% chance). "
            + "Tools will always take damage when the crafting is finished.")
        .getDouble(slicenspliceToolDamageChance);

    spawnGuardStopAllSlimesDebug = config.getBoolean("spawnGuardStopAllSlimesDebug", sectionAttractor.name, spawnGuardStopAllSlimesDebug,
        "When true slimes wont be allowed to spawn at all. Only added to aid testing in super flat worlds.");
    spawnGuardStopAllSquidSpawning = config.getBoolean("spawnGuardStopAllSquidSpawning", sectionAttractor.name, spawnGuardStopAllSquidSpawning,
        "When true no squid will be spawned.");

    xpObeliskMaxXpLevel = config.get(sectionMisc.name, "xpObeliskMaxXpLevel", xpObeliskMaxXpLevel, "Maximum level of XP the xp obelisk can contain.").getInt();

    maxMobsAttracted = config
        .get(sectionMisc.name, "maxMobsAttracted", maxMobsAttracted, "Maximum number of mobs any Attraction Obelisk can attract at any time.").getInt();

    glassConnectToTheirVariants = config.getBoolean("glassConnectToTheirVariants", sectionMisc.name, glassConnectToTheirVariants,
        "If true, quite clear glass and fused quartz will connect textures with their respective enlightened and darkened variants.");
    clearGlassConnectToFusedQuartz = config.getBoolean("clearGlassConnectToFusedQuartz", sectionMisc.name, clearGlassConnectToFusedQuartz,
        "If true, quite clear glass will connect textures with fused quartz.");
    glassConnectToTheirColorVariants = config.getBoolean("glassConnectToTheirColorVariants", sectionMisc.name, glassConnectToTheirColorVariants,
        "If true, quite clear glass and fused quartz of different colors will connect textures.");

    paintedGlowstoneRequireSilkTouch = config.getBoolean("paintedGlowstoneRequireSilkTouch", sectionMisc.name, paintedGlowstoneRequireSilkTouch,
        "If true, painted glowstone will drop dust unless broken with silk touch");

    enableBaublesIntegration = config.getBoolean("enableBaublesIntegration", sectionMisc.name, enableBaublesIntegration,
        "If false baubles intergation will be disabled even if Baubles is installed");

    enchantmentSoulBoundEnabled = config.getBoolean("enchantmentSoulBoundEnabled", sectionEnchantments.name, enchantmentSoulBoundEnabled,
        "If false the soul bound enchantment will not be available");

    teleportEffectProbability = config
        .get(sectionAdvanced.name, "teleportEffectProbability", teleportEffectProbability, "The probability that Enderios do what they promise.")
        .getDouble(teleportEffectProbability);

    String rareStr = config.get(sectionEnchantments.name, "enchantmentSoulBoundWeight", enchantmentSoulBoundRarity.toString(),
        "The rarity of the enchantment. COMMON, UNCOMMON, RARE, VERY_RARE ").getString();
    try {
      enchantmentSoulBoundRarity = Rarity.valueOf(NullHelper.notnull(rareStr, "invalid config value"));
    } catch (Exception e) {
      Log.warn("Could not set value config entry enchantmentWitherArrowRarity Specified value " + rareStr);
      e.printStackTrace();
    }

    coldFireIgniterFluidType = config.getString("coldFireIgniterFluidType", sectionDarkSteel.name, coldFireIgniterFluidType,
        "The type of fluid required to ignite cold fire");
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

    CapacitorKey.processConfig(config);
    BaseConfig.load();
    BaseConfig.F.setConfig(config);
  }

  public static void checkYetaAccess() {
    if (!useSneakMouseWheelYetaWrench && !useSneakRightClickYetaWrench) {
      Log.warn("Both useSneakMouseWheelYetaWrench and useSneakRightClickYetaWrench are set to false. Enabling right click.");
      useSneakRightClickYetaWrench = true;
    }
  }

  public static void init(FMLPostInitializationEvent event) {
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
  }

  private Config() {
  }
}
