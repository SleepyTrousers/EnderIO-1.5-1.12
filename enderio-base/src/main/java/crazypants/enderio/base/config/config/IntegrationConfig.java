package crazypants.enderio.base.config.config;

import java.awt.Rectangle;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class IntegrationConfig {

  public static final IValueFactory F = BaseConfig.F.section("integration");

  public static final IValue<Boolean> enableActuallyAdditions = F.make("enableActuallyAdditions", true, //
      "Enables the integration with Actually Additions (fertilizer, hoe).").startup();

  public static final IValue<Boolean> enableAE2 = F.make("enableAE2", true, //
      "Enables the integration with Applied Energistics 2 (hoe).").startup();

  public static final IValue<Boolean> enableBaseMetals = F.make("enableBaseMetals", true, //
      "Enables the integration with BaseMetals (hoe).").startup();

  public static final IValue<Boolean> enableBaubles = F.make("enableBaubles", true, //
      "Enables the integration with Baubles.").sync(); // yes, this can be switched any time

  public static final IValue<Boolean> enableBigReactors = F.make("enableBigReactors", true, //
      "Enables the integration with Big/Extreme Reactors (fluids and blocks in reactor).").startup();

  public static final IValue<Boolean> enableBoP = F.make("enableBoP", true, //
      "Enables the integration with Biomes'o'Plenty (harvesting flowers).").startup();

  public static final IValue<Boolean> enableProjectIntelligence = F.make("enableProjectIntelligence", true, //
      "Enables the integration with Project Intelligence (ingame documentation).");

  public static final IValueFactory FPI = F.section(".projectIntelligence");

  public static final IValue<Integer> marginTop = FPI.make("marginTop", 3, "Margin around the documentation.");
  public static final IValue<Integer> marginLeft = FPI.make("marginLeft", 3, "Margin around the documentation.");
  public static final IValue<Integer> marginRight = FPI.make("marginRight", 3, "Margin around the documentation.");
  public static final IValue<Integer> marginBottom = FPI.make("marginBottom", 3, "Margin around the documentation.");

  public static Rectangle rectangleWithPIMargins(int x, int y, int width, int height) {
    return new Rectangle(x + marginLeft.get(), y + marginTop.get(), width - marginLeft.get() - marginRight.get(),
        height - marginTop.get() - marginBottom.get());
  }

  public static final IValue<Boolean> enableThaumcraftAspects = F.make("enableThaumcraftAspects", true, //
      "Registers aspects for Ender IO items with Thaumcraft.");

}
