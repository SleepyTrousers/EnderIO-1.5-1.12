package crazypants.enderio.base;

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
  GUI_PERMISSION_DENIED(true, "gui.permission.denied"),
  BLOCK_BLAST_RESISTANT(true, "block.tooltip.blastResistant"),
  MACHINE_UPGRADE(true, "machine.tooltip.upgrade"),
  ENCHANT_SOULBOUND("description.enchantment.enderio.soulBound"),
  FLUID_AMOUNT(true, "fluid.millibucket.format"),
  FLUID_LEVEL(true, "fluid.millibucket.format.of"),
  GUI_REDSTONE_MODE(true, "gui.tooltip.redstoneControlMode"),
  FUEL_GENERATES(true, "fuel.tooltip.generates"),
  FUEL_BURNTIME(true, "fuel.tooltip.burnTime"),
  FUEL_HEADING(true, "fuel.tooltip.heading"),
  COOLANT_HEADING(true, "coolant.tooltip.heading"),
  COOLANT_DEGREES(true, "coolant.tooltip.degreesPerBucket"),
  FLUID_AMOUNT_NAME(true, "fluid.millibucket.format.name"),
  FLUID_LEVEL_NAME(true, "fluid.millibucket.format.ofname"),
  SOUL_VIAL_HEALTH("item.item_soul_vial.tooltip.health"),
  SOUL_VIAL_FLUID("item.item_soul_vial.tooltip.fluidname"),
  SOUL_VIAL_COLOR("item.item_soul_vial.tooltip.color"),
  SOUL_VIAL_EMPTY("item.item_soul_vial.tooltip.empty"),
  CONDUIT_FILTER("itemConduitFilterUpgrade"),
  CONDUIT_FILTER_CONFIGURED("itemConduitFilterUpgrade.configured"),
  CONDUIT_FILTER_CLEAR("itemConduitFilterUpgrade.clearConfigMethod"),
  CONDUIT_FILTER_CLEAR_WARNING("itemConduitFilterUpgrade.clearConfigWarning"),
  CONDUIT_FILTER_UPDATED("item.itemExistingItemFilter.filterUpdated"),
  CONDUIT_FILTER_NOTUPDATED("item.itemExistingItemFilter.filterNotUpdated"),
  XXXXXX0(""),

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

  public @Nonnull String getKey() {
    return key;
  }

}
