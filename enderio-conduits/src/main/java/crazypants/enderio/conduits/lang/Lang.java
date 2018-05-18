package crazypants.enderio.conduits.lang;

import javax.annotation.Nonnull;

import crazypants.enderio.base.lang.ILang;
import crazypants.enderio.conduits.EnderIOConduits;

public enum Lang implements ILang {

  GUI_CONDUIT_INSERT_MODE(".gui.conduit_insert_mode"),
  GUI_CONDUIT_EXTRACT_MODE(".gui.conduit_extract_mode"),
  GUI_CONDUIT_ENABLED_MODE(".gui.conduit_enabled_mode"),
  GUI_CONDUIT_DISABLED_MODE(".gui.conduit_enabled_mode"),

  GUI_CONDUIT_CHANNEL(".gui.conduit_channel"),
  GUI_ROUND_ROBIN_ENABLED(".gui.round_robin_enabled"),
  GUI_ROUND_ROBIN_DISABLED(".gui.round_robin_disabled"),
  GUI_SELF_FEED_ENABLED(".gui.self_feed_enabled"),
  GUI_SELF_FEED_DISABLED(".gui.self_feed_disabled"),
  GUI_ITEM_FILTER_UPGRADE(".gui.item_filter_upgrade"),
  GUI_ITEM_FUNCTION_UPGRADE(".gui.item_function_upgrade"),
  GUI_ITEM_FUNCTION_UPGRADE_2(".gui.item_function_upgrade2"),
  GUI_ITEM_FUNCTION_UPGRADE_3(".gui.item_function_upgrade3"),
  GUI_SIGNAL_COLOR(".gui.signal_color"),
  GUI_PRIORITY(".gui.priority"),

  GUI_LIQUID_AUTO_EXTRACT(".gui.liquid_auto_extract"),
  GUI_LIQUID_FILTER(".gui.liquid_filter"),
  GUI_LIQUID_WHITELIST(".gui.liquid_whitelist"),
  GUI_LIQUID_BLACKLIST(".gui.liquid_blacklist"),

  GUI_REDSTONE_SIGNAL_STRENGTH(".gui.redstone_signal_strength"),

  FLUID_MILLIBUCKETS_TICK(".fluid.millibuckets_tick"),
  GUI_LIQUID_TOOLTIP_MAX_EXTRACT(".item_liquid_conduit.tooltip.max_extract"),
  GUI_LIQUID_TOOLTIP_MAX_IO(".item_liquid_conduit.tooltip.max_io"),
  ITEM_LIQUID_CONDUIT_UNLOCKED_TYPE(".item_liquid_conduit.unlocked_type"),

  GUI_REDSTONE_CONDUIT_INPUT_MODE(".gui.redstone_conduit_input_mode"),
  GUI_REDSTONE_CONDUIT_OUTPUT_MODE(".gui.redstone_conduit_output_mode"),

  GUI_CONDUIT_BUNDLE_FULL(".gui.conduit_bundle_full"),

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
  @Nonnull
  public String getKey() {
    return key;
  }

  @Override
  @Nonnull
  public com.enderio.core.common.Lang getLang() {
    return EnderIOConduits.lang;
  }

  static {
    for (Lang text : values()) {
      text.checkTranslation();
    }
  }

}
