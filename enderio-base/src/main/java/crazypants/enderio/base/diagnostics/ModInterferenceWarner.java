package crazypants.enderio.base.diagnostics;

import java.util.function.Supplier;
import java.util.zip.CRC32;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.BaseConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.StartupQuery;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ModInterferenceWarner {

  @SubscribeEvent
  public static void onEvent(EnderIOLifecycleEvent.ServerAboutToStart.Pre event) {
    // Note: We are in this event because StartupQuery doesn't work earlier
    try {
      warn(() -> Loader.isModLoaded("spongeforge"), "SpongeForge is known for causing issues with Forge mods.\n\n"
          + "If you run into any issue with a Forge mod,\nfirst try uninstalling SpongeForge before you contact the mod author.", "spongeForgeConfirmed");
      warn(() -> FMLClientHandler.instance().hasOptifine(), "Optifine is known for causing issues with mods.\n\n"
          + "If you run into any rendering issues,\nfirst try uninstalling Optifine before you contact the mod author.", "optifineConfirmed");
    } catch (StartupQuery.AbortedException e) {
      // User pressed "no", this is as high as we need to back out
    }
  }

  public static void warn(Supplier<Boolean> check, String text, String config) {
    if (check.get()) {
      long value = getInstallationSpecificValue();
      Property property = BaseConfig.F.getConfig().get("diagnostics", config, "0", "");
      System.out.println(value + " /" + property.getLong());
      if (property.getLong() != value) {
        if (StartupQuery.confirm(text + "\n\nContinue loading anyway?")) {
          property.set(value);
          BaseConfig.F.getConfig().save();
          return;
        }
        StartupQuery.abort();
      }
    }
  }

  private static long getInstallationSpecificValue() {
    // modpacks will ship the config file, but we want the enduser to see this. So try to make this value as
    // installation specific as possible without digging into parts of the user's pc we have no business of
    // touching (like hdd ID's). The only store a CRC of the data to not expose people's personal data when
    // they share config files.
    String installationSpecificString = System.getProperty("user.dir") + EnderIO.VERSION;
    CRC32 crc32 = new CRC32();
    crc32.update(installationSpecificString.getBytes(), 0, installationSpecificString.getBytes().length);
    return crc32.getValue();
  }

}
