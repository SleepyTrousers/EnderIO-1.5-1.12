package crazypants.enderio.api;

import crazypants.util.Lang;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * An enum to represent the colors of dye, in order. Used mainly for redstone
 * and item conduit channels.
 */
public enum DyeColor {

  BLACK,
  RED,
  GREEN,
  BROWN,
  BLUE,
  PURPLE,
  CYAN,
  SILVER,
  GRAY,
  PINK,
  LIME,
  YELLOW,
  LIGHT_BLUE,
  MAGENTA,
  ORANGE,
  WHITE;

  public static final String[] DYE_ORE_NAMES = {
      "dyeBlack",
      "dyeRed",
      "dyeGreen",
      "dyeBrown",
      "dyeBlue",
      "dyePurple",
      "dyeCyan",
      "dyeLightGray",
      "dyeGray",
      "dyePink",
      "dyeLime",
      "dyeYellow",
      "dyeLightBlue",
      "dyeMagenta",
      "dyeOrange",
      "dyeWhite"
  };

  public static final String[] DYE_ORE_UNLOCAL_NAMES = {

      "item.fireworksCharge.black",
      "item.fireworksCharge.red",
      "item.fireworksCharge.green",
      "item.fireworksCharge.brown",
      "item.fireworksCharge.blue",
      "item.fireworksCharge.purple",
      "item.fireworksCharge.cyan",
      "item.fireworksCharge.silver",
      "item.fireworksCharge.gray",
      "item.fireworksCharge.pink",
      "item.fireworksCharge.lime",
      "item.fireworksCharge.yellow",
      "item.fireworksCharge.lightBlue",
      "item.fireworksCharge.magenta",
      "item.fireworksCharge.orange",
      "item.fireworksCharge.white"

  };
  
  /**
   * All valid colors. Use instead of {@code values()} to prevent extra array allocations.
   */
  public static final DyeColor[] COLORS = values();

  /**
   * Used to cycle through dye colors.
   * 
   * @param col
   *          The current color
   * @return The next dye color in order
   */
  public static DyeColor getNext(DyeColor col) {
    int ord = col.ordinal() + 1;
    if(ord >= DyeColor.values().length) {
      ord = 0;
    }
    return DyeColor.values()[ord];
  }

  /**
   * Gets an {@link DyeColor} instance from an {@link ItemStack}
   * 
   * @param dye
   *          The {@link ItemStack} that is a dye
   * @return A {@link DyeColor} that represents the color of this
   *         {@link ItemStack}
   */
  public static DyeColor getColorFromDye(ItemStack dye) {
    if(dye == null) {
      return null;
    }
    int[] oreIds = OreDictionary.getOreIDs(dye);
    if(oreIds.length <= 0) {
      return null;
    }
    for (int i = 0; i < DYE_ORE_NAMES.length; i++) {
      String dyeName = DYE_ORE_NAMES[i];
      for (int id : oreIds) {
        if(OreDictionary.getOreID(dyeName) == id) {
          return DyeColor.values()[i];
        }
      }
    }
    return null;
  }

  public static DyeColor fromIndex(int index) {
    return DyeColor.values()[index];
  }

  private DyeColor() {
  }

  /**
   * @return The hexadecimal representation of this color.
   */
  public int getColor() {
    return ItemDye.field_150922_c[ordinal()];
  }

  /**
   * @return The simple name of this color. Not localized.
   */
  public String getName() {
    return ItemDye.field_150921_b[ordinal()];
  }

  /**
   * @return The localized name of this color.
   */
  public String getLocalisedName() {
    return Lang.localize(DYE_ORE_UNLOCAL_NAMES[ordinal()], false);
  }

  @Override
  public String toString() {
    return getName();
  }

}