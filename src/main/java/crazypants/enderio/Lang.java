package crazypants.enderio;

import javax.annotation.Nonnull;

import net.minecraft.util.text.TextComponentString;

public enum Lang {

  PRINTOUT_ADDTARGET("item.item_location_printout.chat.addTarget"),
  PRINTOUT_SETTARGET("item.item_location_printout.chat.setTarget"),
  PRINTOUT_PRIVATE("item.item_location_printout.chat.privateBlock"),
  PRINTOUT_NOPAPER("item.item_location_printout.chat.noPaper"),
  MACHINE_CONFIGURED(true, "machine.tooltip.configured"),
  AXE_MULTIHARVEST("item.item_dark_steel_axe.tooltip.multiHarvest"),
  AXE_POWERED("item.item_dark_steel_axe.tooltip.effPowered"),
  PICK_POWERED("item.item_dark_steel_pickaxe.tooltip.effPowered"),
  PICK_OBSIDIAN("item.item_dark_steel_pickaxe.tooltip.effObs"),
  PICK_OBSIDIAN_COST("item.item_dark_steel_pickaxe.tooltip.cost"),
  SHEARS_MULTIHARVEST("item.item_dark_steel_shears.tooltip.multiHarvest"),
  SHEARS_POWERED("item.item_dark_steel_shears.tooltip.effPowered"),
  RETURN_ROD_FLUID("item.item_rod_of_return.tooltip.fluid"),
  RETURN_ROD_POWER("item.item_rod_of_return.tooltip.power"),
  RETURN_ROD_NO_POWER("item.item_rod_of_return.chat.notEnoughPower"),
  RETURN_ROD_NO_TARGET("item.item_rod_of_return.chat.targetNotSet"),
  RETURN_ROD_NO_FLUID("item.item_rod_of_return.chat.notEnoughFluid"),
  RETURN_ROD_SYNC_TELEPAD("item.item_rod_of_return.chat.sync.telepad"),
  RETURN_ROD_SYNC("item.item_rod_of_return.chat.sync"),
  GRINDING_BALL_1(true, "grindingball.tooltip.line1"),
  GRINDING_BALL_2(true, "grindingball.tooltip.line2"),
  GRINDING_BALL_3(true, "grindingball.tooltip.line3"),
  GRINDING_BALL_4(true, "grindingball.tooltip.line4"),
  STAFF_NO_POWER("item.item_travel_staff.chat.notEnoughPower"),
  COLD_FIRE_NO_FLUID("item.item_cold_fire_igniter.chat.outoffluid"),
  DARK_STEEL_POWERED("item.item_dark_steel_armor.tooltip.line1"),
  DARK_BOOTS_POWERED("item.item_dark_steel_boots.tooltip.line1"),
  DARK_STEEL_LEVELS1("item.item_dark_steel_armor.tooltip.levelcost.line1"),
  DARK_STEEL_LEVELS2("item.item_dark_steel_armor.tooltip.levelcost.line2"),
  PRESSURE_PLATE_TUNED("tile.block_painted_pressure_plate.tuned.tooltip"),
  XXXXXXj(""),
  XXXXXXk(""),
  XXXXXXl(""),

  ;

  private final @Nonnull String key;

  private Lang(boolean addDomain, @Nonnull String key) {
    if (addDomain) {
      this.key = EnderIO.DOMAIN + "." + key;
    } else {
      this.key = key;
    }
  }

  private Lang(@Nonnull String key) {
    this(false, key);
  }

  public @Nonnull String get() {
    return EnderIO.lang.localizeExact(key);
  }

  public @Nonnull String get(@Nonnull Object... params) {
    return EnderIO.lang.localizeExact(key, params);
  }

  public @Nonnull TextComponentString toChat() {
    return new TextComponentString(EnderIO.lang.localizeExact(key));
  }

  public @Nonnull TextComponentString toChat(@Nonnull Object... params) {
    return new TextComponentString(EnderIO.lang.localizeExact(key, params));
  }

  static {
    for (Lang lang : values()) {
      if (!EnderIO.lang.canLocalizeExact(lang.key)) {
        Log.error("Missing translation for '" + lang + "': " + lang.get());
      }
    }
  }

}
