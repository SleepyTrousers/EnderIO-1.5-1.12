package crazypants.enderio.base.config;

import java.io.File;

import javax.annotation.Nonnull;

import com.enderio.core.common.event.ConfigFileChangedEvent;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.BaseConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
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

  private static final @Nonnull Section sectionPower = new Section("Power Settings", "power");
  private static final @Nonnull Section sectionRod = new Section("Rod of Return Settings", "rod");
  private static final @Nonnull Section sectionAdvanced = new Section("Advanced Settings", "advanced");
  private static final @Nonnull Section sectionFluid = new Section("Fluid Settings", "fluid");
  private static final @Nonnull Section sectionSoulBinder = new Section("Soul Binder Settings", "soulBinder");
  private static final @Nonnull Section sectionSoulVial = new Section("", "soulvial");
  private static final @Nonnull Section sectionMisc = new Section("Misc", "misc");

  public static final int DEFAULT_CONDUIT_PIXELS = 3;

  public static final float EXPLOSION_RESISTANT = 2000f * 3.0f / 5.0f; // obsidian

  public static int conduitPixels = DEFAULT_CONDUIT_PIXELS;

  public static File configDirectory;

  public static @Nonnull File getConfigDirectory() {
    return NullHelper.notnull(configDirectory, "trying to access config before preInit");
  }

  public static int hootchPowerPerCycleRF = 60;
  public static int hootchPowerTotalBurnTime = 6000;
  public static int rocketFuelPowerPerCycleRF = 160;
  public static int rocketFuelPowerTotalBurnTime = 7000;
  public static int fireWaterPowerPerCycleRF = 80;
  public static int fireWaterPowerTotalBurnTime = 15000;

  /**
   * Note: If someone asks you to include a hoe in this (no longer existing) list, the correct answer is:
   * 
   * "No. Get the other mod author to oredict their hoe(s) as 'toolHoe'"
   */

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

  public static int soulBinderSoulFilterLevels = 12;
  public static int soulBinderSoulFilterRF = 5_000_000;

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

    BaseConfig.load();
    BaseConfig.F.setConfig(config);
  }

  public static void init(FMLPostInitializationEvent event) {
  }

  private Config() {
  }
}
