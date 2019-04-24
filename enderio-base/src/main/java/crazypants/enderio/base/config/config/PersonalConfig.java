package crazypants.enderio.base.config.config;

import java.util.function.Supplier;

import crazypants.enderio.base.EnderIO;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class PersonalConfig {

  public static final IValueFactory F = BaseConfig.F.section("personal");

  // Recipe Button

  public static final IValue<Boolean> recipeButtonDisableAlways = F.make("recipeButtonDisableAlways", false, //
      "Should the annoying recipe button be always disabled?");
  public static final IValue<Boolean> recipeButtonDisableWithJei = F.make("recipeButtonDisableWithJei", false, //
      "Should the annoying recipe button be disabled if JEI is installed? (no effect is recipeButtonReplaceWithJei is set)");
  public static final IValue<Boolean> recipeButtonReplaceWithJei = F.make("recipeButtonReplaceWithJei", true, //
      "Should the annoying recipe button be replaced with a JEI recipe button if JEI is installed?");

  public static final IValue<Boolean> recipeButtonInMachineGuis = F.make("recipeButtonInMachineGuis", Math.random() > .75, //
      "Should the JEI recipe button in the machine GUIs use the vanilla icon?");

  // Yeta Wrench

  public static final IValue<Boolean> yetaUseSneakMouseWheel = F.make("yetaUseSneakMouseWheel", true, //
      "If true, shift-mouse wheel will change the conduit display mode when the YetaWrench is equipped.");

  public static final IValue<Boolean> yetaUseSneakRightClick = F.make("yetaUseSneakRightClick", false, //
      "If true, shift-clicking the YetaWrench on a null or non wrenchable object will change the conduit display mode.");

  public static final IValue<Integer> yetaOverlayMode = F.make("yetaOverlayMode", 0, //
      "What kind of overlay to use when holding the yeta wrench\n\n" + "0 - Sideways scrolling in center of screen\n"
          + "1 - Vertical icon bar in bottom right\n" + "2 - Old-style group of icons in bottom right")
      .setRange(0, 2);

  // Sounds

  public static final IValue<Boolean> machineSoundsEnabled = F.make("machineSoundsEnabled", true, //
      "If true, machines will make sounds.");

  public static final IValue<Float> machineSoundsVolume = F.make("machineSoundsVolume", 1F, //
      "Volume of machine sounds.");

  // Tooltips

  public static final IValue<Boolean> tooltipsAddFuelToFluidContainers = F.make("tooltipsAddFuelToFluidContainers", true, //
      "If true, adds energy value and burn time tooltips to fluid containers with liquid fuel.");

  public static final IValue<Boolean> tooltipsAddFurnaceFuel = F.make("tooltipsAddFurnaceFuel", true, //
      "If true, adds burn duration tooltips to furnace fuels.");

  // Colors

  public static final IValue<Boolean> candyColors = F.make("candyColors", false, //
      "Should the annoying new candy colors be used for Fused Glass/Quartz?");

  // Easter Eggs

  public static final IValue<Boolean> celebrateSpaceDay = F.make("celebrateSpaceDay", true, //
      "Celebrate the International Space Day?");

  public static final IValue<Boolean> celebrateChristmas = F.make("celebrateChristmas", true, //
      "Celebrate Christmas?");

  public static final IValue<Boolean> celebrateNicholas = F.make("celebrateNicholas", true, //
      "Celebrate St Nicholas' Day?").sync();

  public static final IValue<Boolean> celebrateReformation = F.make("celebrateReformation", true, //
      "Celebrate Reformation Day?").sync(); // HL: yes, it's actually Halloween, but I'm a troll ;)

  // GUI Branding

  public static final IValue<Boolean> GUIBrandingEnabled = F.make("GUIBrandingEnabled", true, //
      "Should the GUI background be branded?");

  public static final IValue<String> GUIBrandingTexture = F.make("GUIBrandingTexture", EnderIO.DOMAIN + ":textures/items/item_enderface_none.png", //
      "Texture for the GUI background branding.");

  public static final IValue<Float> GUIBrandingAlpha = F.make("GUIBrandingAlpha", 0.05F, //
      "Alpha (transparency) for the GUI background branding.").setRange(0.02f, 1f);

  public static final IValue<Integer> GUIBrandingTiles = F.make("GUIBrandingTiles", 2, //
      "Number of tiles for the GUI background branding.").setRange(1, 64);

  // Visuals

  public static final IValue<Boolean> animatedEnderArmorEnabled = F.make("animatedEnderArmorEnabled", true, //
      "Should the Ender Armor texture be animated?");

  // TODO change geometry to be re-baked after server join
  public static final IValue<Integer> conduitPixels = F.make("conduitPixels", 3, //
      "Valid values are between 2-5, smallest conduits at 2, largest at 5.").setRange(2, 5).startup();

  // Item Hiding

  public static final IValueFactory H = F.section(".hiding");

  public static final IValue<Boolean> disableHiding = H.make("disableHiding", false, //
      "If true, nothing will be hidden in JEI regardless of the xml configuration for ingredient hiding.");

  // Tooltips

  public static final IValueFactory T = F.section(".tooltips");

  public enum TooltipPaintEnum implements Supplier<Boolean> {
    NEVER {
      @Override
      public Boolean get() {
        return false;
      }
    },
    AUTO {
      @Override
      public Boolean get() {
        return painterAvailable;
      }
    },
    ALWAYS {
      @Override
      public Boolean get() {
        return true;
      }
    };

    private static boolean painterAvailable = false;

    public static void setPainterAvailable() {
      TooltipPaintEnum.painterAvailable = true;
    }

  }

  public static final IValue<TooltipPaintEnum> enablePainterTooltip = T.make("enablePainterTooltip", TooltipPaintEnum.AUTO,
      "Should paintable (and painted) items have a tooltip showing their paint information? Possible values are ALWAYS, NEVER and AUTO. The latter will only "
          + "show tooltips if there is some way to paint items available (e.g. the Painting Machine).");

}
