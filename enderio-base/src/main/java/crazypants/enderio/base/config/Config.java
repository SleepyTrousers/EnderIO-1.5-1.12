package crazypants.enderio.base.config;

import java.io.File;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.enderio.core.common.event.ConfigFileChangedEvent;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.BaseConfig;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
  public static final @Nonnull Section sectionItems = new Section("Item Enabling", "item");
  public static final @Nonnull Section sectionAnchor = new Section("Anchor Settings", "anchor");
  public static final @Nonnull Section sectionStaff = new Section("Staff Settings", "staff");
  public static final @Nonnull Section sectionRod = new Section("Rod of Return Settings", "rod");
  public static final @Nonnull Section sectionDarkSteel = new Section("Dark Steel", "darksteel");
  public static final @Nonnull Section sectionAdvanced = new Section("Advanced Settings", "advanced");
  public static final @Nonnull Section sectionFluid = new Section("Fluid Settings", "fluid");
  public static final @Nonnull Section sectionSoulBinder = new Section("Soul Binder Settings", "soulBinder");
  public static final @Nonnull Section sectionSoulVial = new Section("", "soulvial");
  public static final @Nonnull Section sectionMisc = new Section("Misc", "misc");
  public static final @Nonnull Section sectionHoes = new Section("Farm Settings.Hoes", "hoes");

  public static final int DEFAULT_CONDUIT_PIXELS = 3;

  public static final float EXPLOSION_RESISTANT = 2000f * 3.0f / 5.0f; // obsidian

  public static int conduitPixels = DEFAULT_CONDUIT_PIXELS;

  public static File configDirectory;

  public static @Nonnull File getConfigDirectory() {
    return NullHelper.notnull(configDirectory, "trying to access config before preInit");
  }

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

  public static int darkSteelSpeedOneCost = 4;
  public static int darkSteelSpeedTwoCost = 6;
  public static int darkSteelSpeedThreeCost = 8;

  public static double darkSteelBootsJumpModifier = 1.5;
  public static int darkSteelJumpOneCost = 4;
  public static int darkSteelJumpTwoCost = 6;
  public static int darkSteelJumpThreeCost = 8;

  public static boolean slotZeroPlacesEight = true;

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

  public static int darkSteelPickEffeciencyObsidian = 50;
  public static int darkSteelPickPowerUseObsidian = 10000;
  public static float darkSteelPickApplyObsidianEffeciencyAtHardess = 40;
  public static int darkSteelPickPowerUsePerDamagePoint = 750;
  public static float darkSteelPickEffeciencyBoostWhenPowered = 2;
  public static boolean darkSteelPickMinesTiCArdite = true;
  public static boolean endSteelPickMinesTiCArdite = true;

  public static int darkSteelAxePowerUsePerDamagePoint = 750;
  public static int darkSteelAxePowerUsePerDamagePointMultiHarvest = 1500;
  public static float darkSteelAxeEffeciencyBoostWhenPowered = 2;
  public static float darkSteelAxeSpeedPenaltyMultiHarvest = 4;

  public static int darkSteelShearsDurabilityFactor = 5;
  public static int darkSteelShearsPowerUsePerDamagePoint = 250;
  public static float darkSteelShearsEffeciencyBoostWhenPowered = 2.0f;
  public static int darkSteelShearsBlockAreaBoostWhenPowered = 4;
  public static float darkSteelShearsEntityAreaBoostWhenPowered = 5.0f;

  public static int darkSteelGliderCost = 4;
  public static double darkSteelGliderHorizontalSpeed = 0.03;
  public static double darkSteelGliderVerticalSpeed = -0.05;
  public static double darkSteelGliderVerticalSpeedSprinting = -0.15;

  public static int darkSteelElytraCost = 10;

  public static int darkSteelGogglesOfRevealingCost = 4;
  public static int darkSteelThaumaturgeRobeCost = 4;

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

  public static float darkSteelLadderSpeedBoost = 0.06f;

  public static int hootchPowerPerCycleRF = 60;
  public static int hootchPowerTotalBurnTime = 6000;
  public static int rocketFuelPowerPerCycleRF = 160;
  public static int rocketFuelPowerTotalBurnTime = 7000;
  public static int fireWaterPowerPerCycleRF = 80;
  public static int fireWaterPowerTotalBurnTime = 15000;

  /**
   * Note: If someone asks you to include a hoe in this list, the correct answer is "No. Get the other mod author to oredict their hoe(s) as 'toolHoe'"
   */
  public static String[] hoeStrings = new String[] { "tconstruct:mattock", "thermalfoundation:tool.hoe_invar", "thermalfoundation:tool.hoe_copper",
      "thermalfoundation:tool.hoe_bronze", "thermalfoundation:tool.hoe_silver", "thermalfoundation:tool.hoe_electrum", "thermalfoundation:tool.hoe_tin",
      "thermalfoundation:tool.hoe_lead", "thermalfoundation:tool.hoe_nickel", "thermalfoundation:tool.hoe_platinum", "thermalfoundation:tool.hoe_aluminum",
      "thermalfoundation:tool.hoe_steel", "thermalfoundation:tool.hoe_constantan", "basemetals:adamantine_hoe", "basemetals:aquarium_hoe",
      "basemetals:brass_hoe", "basemetals:bronze_hoe", "basemetals:coldiron_hoe", "basemetals:copper_hoe", "basemetals:cupronickel_hoe",
      "basemetals:electrum_hoe", "basemetals:invar_hoe", "basemetals:lead_hoe", "basemetals:mithril_hoe", "basemetals:nickel_hoe", "basemetals:platinum_hoe",
      "basemetals:silver_hoe", "basemetals:starsteel_hoe", "basemetals:steel_hoe", "basemetals:tin_hoe", "actuallyadditions:item_hoe_quartz",
      "actuallyadditions:item_hoe_emerald", "actuallyadditions:item_hoe_obsidian", "actuallyadditions:item_hoe_crystal_red",
      "actuallyadditions:item_hoe_crystal_blue", "actuallyadditions:item_hoe_crystal_light_blue", "actuallyadditions:item_hoe_crystal_black",
      "actuallyadditions:item_hoe_crystal_green", "actuallyadditions:item_hoe_crystal_white", "ic2:bronze_hoe" /* IC2exp 1.10 */,
      "appliedenergistics2:nether_quartz_hoe", "appliedenergistics2:certus_quartz_hoe" /* AE2 1.10 */, "railcraft:tool_hoe_steel" /* Railcraft 1.10 */,
      "mysticalagriculture:inferium_hoe", "mysticalagriculture:prudentium_hoe", "mysticalagriculture:intermedium_hoe", "mysticalagriculture:superium_hoe",
      "mysticalagriculture:supremium_hoe" };
  public static @Nonnull Things farmHoes = new Things();

  public static long nutrientFoodBoostDelay = 400;
  public static boolean rocketFuelIsExplosive = true;

  public static double xpVacuumRange = 10;

  public static NNList<ResourceLocation> soulVesselBlackList = new NNList<ResourceLocation>();
  public static NNList<ResourceLocation> soulVesselUnspawnableList = new NNList<ResourceLocation>();
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
  public static int soulBinderVibrantCystalRF = 200000;
  public static int soulBinderVibrantCystalLevels = 8;

  public static float slicenspliceToolDamageChance = 0.01f;

  public static int xpObeliskMaxXpLevel = Integer.MAX_VALUE;

  public static boolean clearGlassConnectToFusedQuartz = false;
  public static boolean glassConnectToTheirVariants = true;
  public static boolean glassConnectToTheirColorVariants = true;

  public static boolean rodOfReturnCanTargetAnywhere = false;
  public static int rodOfReturnTicksToActivate = 50;
  public static int rodOfReturnPowerStorage = 2000000;
  public static int rodOfReturnMinTicksToRecharge = 100;
  public static int rodOfReturnRfPerTick = 35000;
  public static int rodOfReturnFluidUsePerTeleport = 200;
  public static int rodOfReturnFluidStorage = 200;
  public static String rodOfReturnFluidType = "ender_distillation";

  public static int staffOfLevityFluidUsePerTeleport = 100;
  public static int staffOfLevityFluidStorage = 8000;
  public static int staffOfLevityTicksBetweenActivation = 10;
  public static String staffOfLevityFluidType = "vapor_of_levity";

  public static boolean paintedGlowstoneRequireSilkTouch = false;

  public static boolean enableBaublesIntegration = true;

  public static int maxMobsAttracted = 20;

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

    // TODO change geometry to be re-baked after server join
    conduitPixels = config
        .get(sectionMisc.name, "conduitPixels", DEFAULT_CONDUIT_PIXELS,
            "Valid values are between 2-5, smallest conduits at 2, largest at 5.\n" + "In SMP, all clients must be using the same value as the server.")
        .getInt(DEFAULT_CONDUIT_PIXELS);
    conduitPixels = MathHelper.clamp(conduitPixels, 2, 5);

    travelAnchorMaximumDistance = config.get(sectionAnchor.name, "travelAnchorMaxDistance", travelAnchorMaximumDistance,
        "Maximum number of blocks that can be traveled from one travel anchor to another.").getInt(travelAnchorMaximumDistance);

    travelAnchorCooldown = config
        .get(sectionAnchor.name, "travelAnchorCooldown", travelAnchorCooldown, "Number of ticks in cooldown between activations (1 sec = 20 ticks)")
        .getInt(travelAnchorCooldown);

    travelAnchorSneak = config.get(sectionAnchor.name, "travelAnchorSneak", travelAnchorSneak, "Add sneak as an option to activate travel anchors")
        .getBoolean(travelAnchorSneak);

    travelAnchorSkipWarning = config
        .get(sectionAnchor.name, "travelAnchorSkipWarning", travelAnchorSkipWarning, "Travel Anchors send a chat warning when skipping inaccessible anchors")
        .getBoolean(travelAnchorSkipWarning);

    travelStaffMaximumDistance = config.get(sectionStaff.name, "travelStaffMaxDistance", travelStaffMaximumDistance,
        "Maximum number of blocks that can be traveled using the Staff of Traveling.").getInt(travelStaffMaximumDistance);
    travelStaffPowerPerBlockRF = (float) config.get(sectionStaff.name, "travelStaffPowerPerBlockRF", travelStaffPowerPerBlockRF,
        "Amount of energy required per block traveled using the Staff of Traveling.").getDouble(travelStaffPowerPerBlockRF);

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
    rodOfReturnPowerStorage = config.get(sectionRod.name, "rodOfReturnPowerStorage", rodOfReturnPowerStorage, "Internal energy buffer for rod")
        .getInt(rodOfReturnPowerStorage);
    rodOfReturnRfPerTick = config.get(sectionRod.name, "rodOfReturnRfPerTick", rodOfReturnRfPerTick, "energy used per tick").getInt(rodOfReturnRfPerTick);
    rodOfReturnMinTicksToRecharge = config.get(sectionRod.name, "rodOfReturnMinTicksToRecharge", rodOfReturnMinTicksToRecharge,
        "Min number of ticks required to recharge the internal energy buffer").getInt(rodOfReturnMinTicksToRecharge);
    rodOfReturnFluidStorage = config.get(sectionRod.name, "rodOfReturnFluidStorage", rodOfReturnFluidStorage, "How much fluid the rod can store")
        .getInt(rodOfReturnFluidStorage);
    rodOfReturnFluidUsePerTeleport = config
        .get(sectionRod.name, "rodOfReturnFluidUsePerTeleport", rodOfReturnFluidUsePerTeleport, "How much fluid is used per teleport")
        .getInt(rodOfReturnFluidUsePerTeleport);
    rodOfReturnFluidType = config.getString("rodOfReturnFluidType", sectionRod.name, rodOfReturnFluidType, "The type of fluid used by the rod.");

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

    darkSteelDrainPowerFromInventory = config
        .get(sectionDarkSteel.name, "darkSteelDrainPowerFromInventory", darkSteelDrainPowerFromInventory,
            "If true, dark steel armor will drain power stored energy in power containers in the players inventory.")
        .getBoolean(darkSteelDrainPowerFromInventory);

    darkSteelBootsJumpPowerCost = config
        .get(sectionDarkSteel.name, "darkSteelBootsJumpPowerCost", darkSteelBootsJumpPowerCost,
            "Base amount of power used per jump energy dark steel boots. The second jump in a 'double jump' uses 2x this etc")
        .getInt(darkSteelBootsJumpPowerCost);

    darkSteelFallDistanceCost = config.get(sectionDarkSteel.name, "darkSteelFallDistanceCost", darkSteelFallDistanceCost,
        "Amount of power used energy per block height of fall distance damage negated.").getInt(darkSteelFallDistanceCost);

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
    darkSteelThaumaturgeRobeCost = config.get(sectionDarkSteel.name, "darkSteelThaumaturgeRobeCost", darkSteelThaumaturgeRobeCost,
        "Number of levels required for the Thaumaturge's Robes upgrades.").getInt(darkSteelThaumaturgeRobeCost);

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
        .get(sectionDarkSteel.name, "vanillaSwordSkullLootingModifier", vanillaSwordSkullLootingModifier,
            "The chance per looting level that a skull will be dropped when using a non-dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(vanillaSwordSkullLootingModifier);

    ticCleaverSkullDropChance = (float) config.get(sectionDarkSteel.name, "ticCleaverSkullDropChance", ticCleaverSkullDropChance,
        "The base chance that an Enderman Skull will be dropped when using TiC Cleaver").getDouble(ticCleaverSkullDropChance);
    ticBeheadingSkullModifier = (float) config.get(sectionDarkSteel.name, "ticBeheadingSkullModifier", ticBeheadingSkullModifier,
        "The chance per level of Beheading that a skull will be dropped when using a TiC weapon").getDouble(ticBeheadingSkullModifier);

    fakePlayerSkullChance = (float) config.get(sectionDarkSteel.name, "fakePlayerSkullChance", fakePlayerSkullChance,
        "The ratio of skull drops when a mob is killed by a 'FakePlayer', such as Killer Joe. When set to 0 no skulls will drop, at 1 the rate of skull drops is not modified")
        .getDouble(fakePlayerSkullChance);

    darkSteelSwordPowerUsePerHit = config
        .get(sectionDarkSteel.name, "darkSteelSwordPowerUsePerHit", darkSteelSwordPowerUsePerHit, "The amount of energy used per hit.")
        .getInt(darkSteelSwordPowerUsePerHit);
    darkSteelSwordEnderPearlDropChance = config
        .get(sectionDarkSteel.name, "darkSteelSwordEnderPearlDropChance", darkSteelSwordEnderPearlDropChance,
            "The chance that an ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(darkSteelSwordEnderPearlDropChance);
    darkSteelSwordEnderPearlDropChancePerLooting = config
        .get(sectionDarkSteel.name, "darkSteelSwordEnderPearlDropChancePerLooting", darkSteelSwordEnderPearlDropChancePerLooting,
            "The chance for each looting level that an additional ender pearl will be dropped when using a dark steel sword (0 = no chance, 1 = 100% chance)")
        .getDouble(darkSteelSwordEnderPearlDropChancePerLooting);

    darkSteelPickPowerUseObsidian = config
        .get(sectionDarkSteel.name, "darkSteelPickPowerUseObsidian", darkSteelPickPowerUseObsidian, "The amount of energy used to break an obsidian block.")
        .getInt(darkSteelPickPowerUseObsidian);
    darkSteelPickEffeciencyObsidian = config.get(sectionDarkSteel.name, "darkSteelPickEffeciencyObsidian", darkSteelPickEffeciencyObsidian,
        "The efficiency when breaking obsidian with a powered Dark Pickaxe.").getInt(darkSteelPickEffeciencyObsidian);
    darkSteelPickApplyObsidianEffeciencyAtHardess = (float) config
        .get(sectionDarkSteel.name, "darkSteelPickApplyObsidianEffeciencyAtHardess", darkSteelPickApplyObsidianEffeciencyAtHardess,
            "If set to a value > 0, the obsidian speed and power use will be used for all blocks with hardness >= to this value.")
        .getDouble(darkSteelPickApplyObsidianEffeciencyAtHardess);
    darkSteelPickPowerUsePerDamagePoint = config.get(sectionDarkSteel.name, "darkSteelPickPowerUsePerDamagePoint", darkSteelPickPowerUsePerDamagePoint,
        "Energy use per damage/durability point avoided.").getInt(darkSteelPickPowerUsePerDamagePoint);
    darkSteelPickEffeciencyBoostWhenPowered = (float) config.get(sectionDarkSteel.name, "darkSteelPickEffeciencyBoostWhenPowered",
        darkSteelPickEffeciencyBoostWhenPowered, "The increase in efficiency when powered.").getDouble(darkSteelPickEffeciencyBoostWhenPowered);
    darkSteelPickMinesTiCArdite = config.getBoolean("darkSteelPickMinesTiCArdite", sectionDarkSteel.name, darkSteelPickMinesTiCArdite,
        "When true the dark steel pick will be able to mine TiC Ardite and Cobalt");

    darkSteelAxePowerUsePerDamagePoint = config
        .get(sectionDarkSteel.name, "darkSteelAxePowerUsePerDamagePoint", darkSteelAxePowerUsePerDamagePoint, "Energy per damage/durability point avoided.")
        .getInt(darkSteelAxePowerUsePerDamagePoint);
    darkSteelAxePowerUsePerDamagePointMultiHarvest = config.get(sectionDarkSteel.name, "darkSteelPickAxeUsePerDamagePointMultiHarvest",
        darkSteelAxePowerUsePerDamagePointMultiHarvest, "Energy per damage/durability point avoided when shift-harvesting multiple logs")
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
        "Energy use per damage/durability point avoided.").getInt(darkSteelShearsPowerUsePerDamagePoint);
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

    nutrientFoodBoostDelay = config.get(sectionFluid.name, "nutrientFluidFoodBoostDelay", nutrientFoodBoostDelay,
        "The delay in ticks between when nutrient distillation boosts your food value.").getInt((int) nutrientFoodBoostDelay);
    rocketFuelIsExplosive = config
        .get(sectionFluid.name, "rocketFuelIsExplosive", rocketFuelIsExplosive, "If enabled, Rocket Fuel will explode when in contact with fire.").getBoolean();

    xpVacuumRange = config.get(sectionAdvanced.name, "xpVacuumRange", xpVacuumRange, "The distance from which XP will be gathered by the XP vacuum.")
        .getDouble(xpVacuumRange);

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
        "The amount of energy required to change the type of a broken spawner.").getInt(soulBinderBrokenSpawnerRF);
    soulBinderReanimationRF = config
        .get(sectionSoulBinder.name, "soulBinderReanimationRF", soulBinderReanimationRF, "The amount of energy required to to re-animated a mob head.")
        .getInt(soulBinderReanimationRF);
    soulBinderEnderCystalRF = config
        .get(sectionSoulBinder.name, "soulBinderEnderCystalRF", soulBinderEnderCystalRF, "The amount of energy required to create an ender crystal.")
        .getInt(soulBinderEnderCystalRF);
    soulBinderAttractorCystalRF = config.get(sectionSoulBinder.name, "soulBinderAttractorCystalRF", soulBinderAttractorCystalRF,
        "The amount of energy required to create an attractor crystal.").getInt(soulBinderAttractorCystalRF);
    soulBinderTunedPressurePlateRF = config.get(sectionSoulBinder.name, "soulBinderTunedPressurePlateRF", soulBinderTunedPressurePlateRF,
        "The amount of energy required to tune a pressure plate.").getInt(soulBinderTunedPressurePlateRF);
    soulBinderPrecientCystalRF = config
        .get(sectionSoulBinder.name, "soulBinderPrecientCystalRF", soulBinderPrecientCystalRF, "The amount of energy required to create a precient crystal.")
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

    BaseConfig.load();
    BaseConfig.F.setConfig(config);
  }

  public static void init(FMLPostInitializationEvent event) {
  }

  private Config() {
  }
}
