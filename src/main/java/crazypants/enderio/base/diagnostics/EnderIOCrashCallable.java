package crazypants.enderio.base.diagnostics;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.api.EnderIOAPIProps;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID)
public class EnderIOCrashCallable implements ICrashCallable {

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void register(@Nonnull RegistryEvent.Register<Block> event) {
    FMLCommonHandler.instance().registerCrashCallable(new EnderIOCrashCallable());
  }

  private List<String> collectData() {
    List<String> result = new ArrayList<String>();
    if (FMLCommonHandler.instance().getSide() == Side.CLIENT && FMLClientHandler.instance().hasOptifine()) {
      result.add(" * Optifine is installed. This is NOT supported.");
    }

    for (ModContainer modContainer : ModAPIManager.INSTANCE.getAPIList()) {
      String apiVersionString = modContainer.getVersion();
      if (apiVersionString == null) {
        apiVersionString = "";
      }
      // if ("appliedenergistics2|API".equals(modContainer.getModId())) {
      // if ("rv1".equals(apiVersionString) || "rv2".equals(apiVersionString) || "rv3".equals(apiVersionString)) {
      // result.add(" * An unsupportted old AE2 API is installed (" + apiVersionString + " from "
      // + modContainer.getSource().getName() + ").");
      // result.add(" Ender IO was build against API version rv4 and will NOT work with older versions.");
      // } else if (!"rv4".equals(apiVersionString)) {
      // result.add(" * An unknown AE2 API is installed (" + apiVersionString + " from "
      // + modContainer.getSource().getName() + ").");
      // result.add(" Ender IO was build against API version rv4 and may or may not work with a newer version.");
      // }
      // } else
      if (modContainer.getModId() != null && modContainer.getModId().startsWith("EnderIOAPI")) {
        if (!EnderIOAPIProps.VERSION.equals(apiVersionString)) {
          result.add(" * Another mod is shipping a version of our API that doesn't match our version (" + apiVersionString
              + " from " + modContainer.getSource().getName() + "). That may not actually work.");
        } else if (modContainer.getSource().getName() != null
            && (!modContainer.getSource().getName().startsWith("EnderIO") && !modContainer.getSource().getName().startsWith("enderio") && !modContainer
                .getSource().getName().equals("bin"))) {
          result.add(" * Our API got loaded from " + modContainer.getSource().getName() + ". That's unexpected.");
        }
      }
    }

    String badBrand = null;
    for (String brand : FMLCommonHandler.instance().getModName().split(",")) {
      if (brand != null && !brand.equals("fml") && !brand.equals("forge")) {
        if (badBrand == null) {
          badBrand = brand;
        } else {
          badBrand += ", " + brand;
        }
      }
    }
    if (badBrand != null) {
      result.add("An unsupported base software is installed: '" + badBrand + "'. This is NOT supported.");
    }
    return result;
  }

  @Override
  public String call() throws Exception {
    String msg = "";
    List<String> data = collectData();
    if (data.isEmpty()) {
      msg += "No known problems detected.\n";
    } else {
      msg += "Found the following problem(s) with your installation (That does NOT mean that Ender IO caused the crash or was involved in it in "
          + "any way. We add this information to help finding common problems, not as an invitation to post any crash you encounter to "
          + "Ender IO's issue tracker. Always check the stack trace above to see which mod is most likely failing.):\n";
      for (String string : data) {
        msg += "                 " + string + "\n";
      }
      msg += "                 This may (look up the meaning of 'may' in the dictionary if you're not sure what it means) have caused the error. "
          + "Try reproducing the crash WITHOUT this/these mod(s) before reporting it.\n";
    }
    msg += "\tDetailed Tesla API diagnostics:\n";
    for (String string : teslaDiagnostics()) {
      msg += "                 " + string + "\n";
    }
    if (stopScreenMessage != null) {
      for (String s : stopScreenMessage) {
        msg += s + "\n";
      }
    }
    Collection<IDiagnosticsTracker> trackers = DiagnosticsRegistry.getActiveTrackers();
    if (trackers != null && !trackers.isEmpty()) {
      for (IDiagnosticsTracker tracker : trackers) {
        msg += "\t" + tracker.getActivityDescription() + "\n";
        for (String string : tracker.getLines()) {
          msg += "                 " + string + "\n";
        }
      }
    }
    msg += "\n";
    msg += "\t!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n";
    msg += "\t!!!You are looking at the diagnostics information, not at the crash. Scroll up!!!\n";
    msg += "\t!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n";
    return msg;
  }

  @Override
  public String getLabel() {
    return EnderIO.MOD_NAME;
  }

  // adapted from http://stackoverflow.com/a/19494116/4105897
  
  public static String whereFrom(String c) {
    if (c == null) {
      return null;
    }
    try {
      return whereFrom(Class.forName(c));
    }catch(Exception e) {
      return null;
    }
  }
  
  public static String whereFrom(Class<?> c) {
    if (c == null) {
      return null;
    }
    try {
      ClassLoader loader = c.getClassLoader();
      if (loader == null) {
        // Try the bootstrap classloader - obtained from the ultimate parent of the System Class Loader.
        loader = ClassLoader.getSystemClassLoader();
        while (loader != null && loader.getParent() != null) {
          loader = loader.getParent();
        }
      }
      if (loader != null) {
        String name = c.getCanonicalName();
        URL resource = loader.getResource(name.replace(".", "/") + ".class");
        if (resource != null) {
          return resource.toString();
        }
      }
    } catch (Throwable t) {
    }
    return "<unknown>";
  }

  public static List<String> teslaDiagnostics() {
    List<String> result = new ArrayList<String>();
    apiDiagnostics(result, "Tesla", "net.darkhax.tesla.", "Tesla");
    apiDiagnostics(result, "Tesla", "net.darkhax.tesla.capability.", "TeslaCapabilities");
    apiDiagnostics(result, "Tesla", "net.darkhax.tesla.api.", "ITeslaConsumer", "ITeslaHolder", "ITeslaProducer");
    apiDiagnostics(result, "Tesla", "net.darkhax.tesla.api.implementation.", "BaseTeslaContainer", "BaseTeslaContainerProvider", "InfiniteTeslaConsumer",
        "InfiniteTeslaConsumerProvider", "InfiniteTeslaProducer", "InfiniteTeslaProducerProvider");
    return result;
  }

  public static void apiDiagnostics(List<String> result, String displayName, String prefix, String... clazzes) {
    for (String clazz : clazzes) {
      try {
        Class<?> forName = Class.forName(prefix + clazz);
        result.add(" * " + displayName + " API class '" + clazz + "' is loaded from: " + whereFrom(forName));
      } catch (ClassNotFoundException e) {
        result.add(" * " + displayName + " API class '" + clazz + "' could not be loaded (reason: " + e + ")");
        if (Log.LOGGER.isDebugEnabled()) {
          // e.printStackTrace();
          Log.debug("Hey, you wanted diagnostics output? Guess what you're not getting it. Too many people reported it as bug. Deal with it.");
        }
      }
    }
  }

  private static String[] stopScreenMessage = null;

  public static void registerStopScreenMessage(String... message) {
    stopScreenMessage = message;
  }
}
