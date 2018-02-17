package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class PersonalConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(BaseConfig.F, new Section("", "personal"));

  public static final IValue<Boolean> recipeButtonDisableAlways = F.make("recipeButtonDisableAlways", false, //
      "Should the annoying recipe button be always disabled?");
  public static final IValue<Boolean> recipeButtonDisableWithJei = F.make("recipeButtonDisableWithJei", false, //
      "Should the annoying recipe button be disabled if JEI is installed? (no effect is recipeButtonReplaceWithJei is set)");
  public static final IValue<Boolean> recipeButtonReplaceWithJei = F.make("recipeButtonReplaceWithJei", true, //
      "Should the annoying recipe button be replaced with a JEI recipe button if JEI is installed?");

  public static final IValue<Boolean> yetaUseSneakMouseWheel = F.make("yetaUseSneakMouseWheel", true, //
      "If true, shift-mouse wheel will change the conduit display mode when the YetaWrench is equipped.");

  public static final IValue<Boolean> yetaUseSneakRightClick = F.make("yetaUseSneakRightClick", false, //
      "If true, shift-clicking the YetaWrench on a null or non wrenchable object will change the conduit display mode.");

  public static final IValue<Integer> yetaOverlayMode = F.make("yetaOverlayMode", 0, //
      "What kind of overlay to use when holding the yeta wrench\n\n" + "0 - Sideways scrolling in center of screen\n"
          + "1 - Vertical icon bar in bottom right\n" + "2 - Old-style group of icons in bottom right")
      .setRange(0, 2);

  public static final IValue<Boolean> machineSoundsEnabled = F.make("machineSoundsEnabled", true, //
      "If true, machines will make sounds.");

  public static final IValue<Float> machineSoundsVolume = F.make("machineSoundsVolume", 1F, //
      "Volume of machine sounds.");

  public static final IValue<Boolean> tooltipsAddFuelToFluidContainers = F.make("tooltipsAddFuelToFluidContainers", true, //
      "If true, adds energy value and burn time tooltips to fluid containers with liquid fuel.");

  public static final IValue<Boolean> tooltipsAddFurnaceFuel = F.make("tooltipsAddFurnaceFuel", true, //
      "If true, adds burn duration tooltips to furnace fuels.");

}
