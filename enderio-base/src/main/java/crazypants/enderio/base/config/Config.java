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

  private static final @Nonnull Section sectionRod = new Section("Rod of Return Settings", "rod");
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

  /**
   * Note: If someone asks you to include a hoe in this (no longer existing) list, the correct answer is:
   * 
   * "No. Get the other mod author to oredict their hoe(s) as 'toolHoe'"
   */

  public static NNList<ResourceLocation> soulVesselBlackList = new NNList<ResourceLocation>();
  public static NNList<ResourceLocation> soulVesselUnspawnableList = new NNList<ResourceLocation>();
  public static boolean soulVesselCapturesBosses = false;

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
