package crazypants.enderio.tool;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.API;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.EnderIO;
import crazypants.enderio.api.EnderIOAPIProps;

public class EnderIOCrashCallable implements ICrashCallable {

  public static void create() {
    FMLCommonHandler.instance().registerCrashCallable(new EnderIOCrashCallable());
  }

  private List<String> collectData() {
    List<String> result = new ArrayList<String>();
    if (FMLCommonHandler.instance().getSide() == Side.CLIENT && FMLClientHandler.instance().hasOptifine()) {
      result.add(" * Optifine is installed. This is NOT supported.");
    }

    for (ModContainer modContainer : ModAPIManager.INSTANCE.getAPIList()) {
      if ("appliedenergistics2|API".equals(modContainer.getModId())) {
        if ("rv1".equals(modContainer.getVersion()) || "rv2".equals(modContainer.getVersion())) {
          result.add(" * An unsupportted old AE2 API is installed (" + modContainer.getVersion() + " from "
              + modContainer.getSource().getName() + ").");
          result.add("   Ender IO was build against API version rv3 and will NOT work with older versions.");
        } else if (!"rv3".equals(modContainer.getVersion())) {
          result.add(" * An unknown AE2 API is installed (" + modContainer.getVersion() + " from "
              + modContainer.getSource().getName() + ").");
          result.add("   Ender IO was build against API version rv3 and may or may not work with a newer version.");
        }
      } else if ("CoFHAPI|energy".equals(modContainer.getModId())) {
        if ("1.7.10R1.0.0".equals(modContainer.getVersion()) || "1.7.10R1.0.1".equals(modContainer.getVersion())) {
          result.add(" * An unsupportted old RF API is installed (" + modContainer.getVersion() + " from "
              + modContainer.getSource().getName() + ").");
          result.add("   Ender IO needs at least 1.7.10R1.0.2 and will NOT work with older versions.");
        } else {
          Package caep = Package.getPackage("cofh.api.energy");
          if (caep != null) {
            API api = caep.getAnnotation(cpw.mods.fml.common.API.class);
            if (api != null) {
              String apiVersion = api.apiVersion();
              if (apiVersion != null) {
                if (!apiVersion.equals(modContainer.getVersion())) {
                  if ("1.7.10R1.0.0".equals(apiVersion) || "1.7.10R1.0.1".equals(apiVersion)) {
                    result.add(" * An unsupportted old RF API is installed (" + apiVersion + " from <unknown>).");
                    result.add("   Ender IO needs at least 1.7.10R1.0.2 and will NOT work with older versions.");
                  } else {
                    result.add(" * The RF API that is being used (" + apiVersion
                        + " from <unknown>) differes from that that is reported as being loaded (" + modContainer.getVersion()
                        + " from " + modContainer.getSource().getName() + ").");
                    result.add("   It is a supported version, but that difference may lead to problems.");
                  }
                }
              } else {
                result.add(" * The RF API that is being used has no version number. This may lead to problems.");
              }
            } else {
              result.add(" * The RF API that is being used has no API annotation. This may lead to problems.");
            }
          } else {
            result.add(" * No RF API could be found in memory. This may be may due to an early crash.");
          }
        }
      } else if (modContainer.getModId() != null && modContainer.getModId().startsWith("EnderIOAPI")) {
        if (!EnderIOAPIProps.VERSION.equals(modContainer.getVersion())) {
          result.add(" * Another mod is shipping a version of our API that doesn't match our version (" + modContainer.getVersion()
              + " from " + modContainer.getSource().getName() + "). That may not actually work.");
        } else if (modContainer.getSource().getName() != null
            && (!modContainer.getSource().getName().startsWith("EnderIO") && !modContainer.getSource().getName()
                .startsWith("enderio"))) {
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
    List<String> data = collectData();
    if (data.isEmpty()) {
      return "No known problems detected.";
    } else {
      String msg = "Found the following problem(s) with your installation:\n";
      for (String string : data) {
        msg += "                 " + string + "\n";
      }
      msg += "                 This may have caused the error. Try reproducing the crash WITHOUT this/these mod(s) before reporting it.";
      return msg;
    }
  }

  @Override
  public String getLabel() {
    return EnderIO.MODID;
  }

}
