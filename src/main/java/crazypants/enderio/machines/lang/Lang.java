package crazypants.enderio.machines.lang;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import crazypants.enderio.machines.EnderIOMachines;
import net.minecraft.util.text.TextComponentString;

public enum Lang {

  XXXXXX1(""),
  XXXXXX2(""),
  XXXXXX3(""),
  XXXXXX4(""),
  XXXXXX5(""),
  XXXXXX6(""),
  XXXXXX7(""),
  XXXXXX8(""),
  XXXXXX9(""),
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
  GUI_COMBGEN_CTANK("gui.combustion_generator.coolantTank"),
  GUI_COMBGEN_CTANK_EMPTY("gui.combustion_generator.coolantTank.empty"),
  GUI_COMBGEN_FTANK("gui.combustion_generator.fuelTank"),
  GUI_COMBGEN_FTANK_EMPTY("gui.combustion_generator.fuelTank.empty"),
  XXXXX28(""),
  XXXXX29(""),
  XXXXX38(""),
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
