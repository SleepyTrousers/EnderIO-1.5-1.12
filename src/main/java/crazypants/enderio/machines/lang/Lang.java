package crazypants.enderio.machines.lang;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import crazypants.enderio.machines.EnderIOMachines;
import net.minecraft.util.text.TextComponentString;

public enum Lang {

  GUI_STIRGEN_OUTPUT("gui.stirling_generator.output"),
  GUI_STIRGEN_RATE("gui.stirling_generator.burnRate"),
  GUI_STIRGEN_SLOT("gui.stirling_generator.upgradeslot"),
  GUI_STIRGEN_UPGRADES("gui.stirling_generator.upgrades"),
  GUI_STIRGEN_REMAINING("gui.stirling_generator.remaining"),
  GUI_ZOMBGEN_OUTPUT("gui.zombie_generator.output"),
  GUI_ZOMBGEN_FTANK("gui.zombie_generator.fuelTank"),
  GUI_ZOMBGEN_MINREQ("gui.zombie_generator.fluid.minReq"),
  GUI_JOE_FTANK("gui.killerJoe.fuelTank"),
  GUI_JOE_MINREQ("gui.killerJoe.fluid.minReq"),
  GUI_COMBGEN_OUTPUT("gui.combustion_generator.output"),
  GUI_ALLOY_MODE("gui.alloy.mode.heading"),
  GUI_ALLOY_MODE_ALL("gui.alloy.mode.all"),
  GUI_ALLOY_MODE_ALLOY("gui.alloy.mode.alloy"),
  GUI_ALLOY_MODE_FURNACE("gui.alloy.mode.furnace"),
  GUI_BUFFER_IN("gui.buffer.in"),
  GUI_BUFFER_OUT("gui.buffer.out"),
  GUI_FARM_BASEUSE("gui.farm.baseUse"),
  GUI_SHOW_RANGE("gui.ranged.showRange"),
  GUI_HIDE_RANGE("gui.ranged.hideRange"),
  GUI_RANGE("gui.ranged.range"),
  GUI_COMBGEN_CTANK("gui.combustion_generator.coolantTank"),
  GUI_COMBGEN_CTANK_EMPTY("gui.combustion_generator.coolantTank.empty"),
  GUI_COMBGEN_FTANK("gui.combustion_generator.fuelTank"),
  GUI_COMBGEN_FTANK_EMPTY("gui.combustion_generator.fuelTank.empty"),
  GUI_WEATHER_FTANK("gui.weather_obelisk.fuelTank"),
  GUI_XP_STORE_1_1("gui.xp_obelisk.button.store.1.line1"),
  GUI_XP_STORE_1_2("gui.xp_obelisk.button.store.1.line2"),
  GUI_XP_STORE_10_1("gui.xp_obelisk.button.store.10.line1"),
  GUI_XP_STORE_10_2("gui.xp_obelisk.button.store.10.line2"),
  GUI_XP_STORE_ALL_1("gui.xp_obelisk.button.store.all.line1"),
  GUI_XP_STORE_ALL_2("gui.xp_obelisk.button.store.all.line2"),
  GUI_XP_RETR_1_1("gui.xp_obelisk.button.retrieve.1.line1"),
  GUI_XP_RETR_1_2("gui.xp_obelisk.button.retrieve.1.line2"),
  GUI_XP_RETR_10_1("gui.xp_obelisk.button.retrieve.10.line1"),
  GUI_XP_RETR_10_2("gui.xp_obelisk.button.retrieve.10.line2"),
  GUI_XP_RETR_ALL_1("gui.xp_obelisk.button.retrieve.all.line1"),
  GUI_XP_RETR_ALL_2("gui.xp_obelisk.button.retrieve.all.line2"),
  SOLAR_MAXOUTPUT("block_solar_panel.maxoutput"),
  XXXXX39(""),

  ;

  private final @Nonnull String key;

  private Lang(boolean addDomain, @Nonnull String key) {
    if (addDomain) {
      this.key = EnderIOMachines.lang.addPrefix(key);
    } else {
      this.key = key;
    }
  }

  private Lang(@Nonnull String key) {
    this(true, key);
  }

  public @Nonnull String get() {
    return EnderIOMachines.lang.localizeExact(key);
  }

  public @Nonnull String get(@Nonnull Object... params) {
    return EnderIOMachines.lang.localizeExact(key, params);
  }

  public @Nonnull TextComponentString toChat() {
    return new TextComponentString(EnderIOMachines.lang.localizeExact(key));
  }

  public @Nonnull TextComponentString toChat(@Nonnull Object... params) {
    return new TextComponentString(EnderIOMachines.lang.localizeExact(key, params));
  }

  static {
    for (Lang text : values()) {
      if (!EnderIOMachines.lang.canLocalizeExact(text.key)) {
        Log.error("Missing translation for '" + text + "': " + text.get());
      }
    }
  }

  public @Nonnull String getKey() {
    return key;
  }

}
