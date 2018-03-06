package crazypants.enderio.base.lang;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;

public enum Lang implements ILang {

  PRINTOUT_ADDTARGET("item.item_location_printout.chat.addTarget"),
  PRINTOUT_SETTARGET("item.item_location_printout.chat.setTarget"),
  PRINTOUT_PRIVATE("item.item_location_printout.chat.privateBlock"),
  PRINTOUT_NOPAPER("item.item_location_printout.chat.noPaper"),
  MACHINE_CONFIGURED(".machine.tooltip.configured"),
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
  GRINDING_BALL_1(".grindingball.tooltip.line1"),
  GRINDING_BALL_2(".grindingball.tooltip.line2"),
  GRINDING_BALL_3(".grindingball.tooltip.line3"),
  GRINDING_BALL_4(".grindingball.tooltip.line4"),
  STAFF_NO_POWER("item.item_travel_staff.chat.notEnoughPower"),
  COLD_FIRE_NO_FLUID("item.item_cold_fire_igniter.chat.outoffluid"),
  DARK_STEEL_POWERED("item.item_dark_steel_armor.tooltip"),
  DARK_BOOTS_POWERED("item.item_dark_steel_boots.tooltip"),
  DARK_STEEL_LEVELS1("item.item_dark_steel_armor.tooltip.levelcost.line1"),
  DARK_STEEL_LEVELS2("item.item_dark_steel_armor.tooltip.levelcost.line2"),
  PRESSURE_PLATE_TUNED("tile.block_painted_pressure_plate.tuned.tooltip"),
  GUI_PERMISSION_DENIED(".gui.permission.denied"),
  BLOCK_BLAST_RESISTANT(".block.tooltip.blastResistant"),
  BLOCK_LIGHT_EMITTER(".block.tooltip.lightEmitter"),
  BLOCK_LIGHT_BLOCKER(".block.tooltip.lightBlocker"),
  MACHINE_UPGRADE(".machine.tooltip.upgrade"),
  ENCHANT_SOULBOUND("description.enchantment.enderio.soulbound"),
  FLUID_TICKPER(".fluid.millibucket.format.tickper"),
  FLUID_AMOUNT(".fluid.millibucket.format"),
  FLUID_LEVEL(".fluid.millibucket.format.of"),
  GUI_REDSTONE_MODE(".gui.tooltip.redstoneControlMode"),
  FUEL_GENERATES(".fuel.tooltip.generates"),
  FUEL_BURNTIME(".fuel.tooltip.burnTime"),
  FUEL_HEADING(".fuel.tooltip.heading"),
  COOLANT_HEADING(".coolant.tooltip.heading"),
  COOLANT_DEGREES(".coolant.tooltip.degreesPerBucket"),
  FLUID_AMOUNT_NAME(".fluid.millibucket.format.name"),
  FLUID_LEVEL_NAME(".fluid.millibucket.format.ofname"),
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
  POWER_SYMBOL(".power.symbol"),
  POWER(".power.format"),
  POWER_OF(".power.format.of"),
  POWER_PERTICK(".power.format.pertick"),
  POWER_NAME(".power.format.name"),
  POWER_DETAILS(".power.format.details"),
  GUI_GENERIC_MAX(".gui.generic.max"),
  GUI_GENERIC_PROGRESS(".gui.generic.progress"),
  GUI_GENERIC_OVERLAY(".gui.generic.ioMode.overlay.tooltip"),
  GUI_GENERIC_OVERLAY_ON(".gui.generic.ioMode.overlay.tooltip.visible"),
  RECIPE_CLEAR(".recipe.tooltip.clearConfig"),
  BETTER_WITH_BACON(".block.tooltip.isBeaconBase"),
  PAINTED_WITH(".generic.tooltip.paintedWith"),
  PAINTED_NOT(".generic.tooltip.unpainted"),
  WRENCH_DENIED(".wrench.permission.denied"),
  EASTER_PIGGY(".easteregg.piginabottle"),
  SOUL_VIAL_DENIED(".soulvial.denied"),
  SOUL_VIAL_DENIED_OWNED_PET(".soulvial.owned.denied"),
  SOUL_VIAL_DENIED_PLAYER(".soulvial.player.denied"),
  SOUL_VIAL_DENIED_AALISTED(".soulvial.blacklisted.denied"),
  CONFIG_TITLE(".config.title"),
  GUI_NOCAP(".gui.generic.nocap"),
  GUI_PROBE_COPIED(".gui.probe.copied"),

  // FILTERS
  GUI_ITEM_FILTER_CLOSE(".gui.item_filter.close"),
  GUI_ITEM_FILTER_CLOSE_2(".gui.item_filter.close2"),

  GUI_ITEM_FILTER_WHITELIST(".gui.item_filter.whitelist"),
  GUI_ITEM_FILTER_BLACKLIST(".gui.item_filter.blacklist"),
  GUI_ITEM_FILTER_MATCH_META(".gui.item_filter.match_meta"),
  GUI_ITEM_FILTER_IGNORE_META(".gui.item_filter.ignore_meta"),
  GUI_ITEM_FILTER_STICKY_ENABLED(".gui.item_filter.sticky_enabled"),
  GUI_ITEM_FILTER_STICKY_ENABLED_2(".gui.item_filter.sticky_enabled2"),
  GUI_ITEM_FILTER_STICKY_DISABLED(".gui.item_filter.sticky_disabled"),
  GUI_ITEM_FILTER_ORE_DIC_ENABLED(".gui.item_filter.ore_dic_enabled"),
  GUI_ITEM_FILTER_ORE_DIC_DISABLED(".gui.item_filter.ore_dic_disabled"),
  GUI_ITEM_FILTER_MATCH_NBT(".gui.item_filter.match_nbt"),
  GUI_ITEM_FILTER_IGNORE_NBT(".gui.item_filter.ignore_nbt"),

  GUI_EXISTING_ITEM_FILTER_SNAPSHOT(".gui.existing.item_filter.snapshot"),
  GUI_EXISTING_ITEM_FILTER_SNAPSHOT_2(".gui.existing.item_filter.snapshot2"),
  GUI_EXISTING_ITEM_FILTER_MERGE(".gui.existing.item_filter.merge"),
  GUI_EXISTING_ITEM_FILTER_CLEAR(".gui.existing.item_filter.clear"),
  GUI_EXISTING_ITEM_FILTER_SHOW(".gui.existing.item_filter.show"),

  ;

  private final @Nonnull String key;

  private Lang(@Nonnull String key) {
    if (key.startsWith(".")) {
      this.key = getLang().addPrefix(key.substring(1));
    } else {
      this.key = key;
    }
  }

  @Override
  public @Nonnull String getKey() {
    return key;
  }

  @Override
  public @Nonnull com.enderio.core.common.Lang getLang() {
    return EnderIO.lang;
  }

  static {
    for (Lang lang : values()) {
      lang.checkTranslation();
    }
  }

}
