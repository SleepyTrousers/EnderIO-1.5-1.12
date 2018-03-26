package crazypants.enderio.powertools.lang;

import javax.annotation.Nonnull;

import crazypants.enderio.base.lang.ILang;
import crazypants.enderio.powertools.EnderIOPowerTools;

public enum Lang implements ILang {

  CAPBANK_TOOLTIP_WITH_ITEMS("tile.block_cap_bank.tooltip.has_items"),
  GUI_CAPBANK_OUTPUT_MODE(".gui.cap_bank.output_mode"),
  GUI_CAPBANK_INPUT_MODE(".gui.cap_bank.input_mode"),
  GUI_CAPBANK_MAX_IO(".gui.cap_bank.max_io"),
  GUI_CAPBANK_MAX_INPUT(".gui.cap_bank.max_input"),
  GUI_CAPBANK_MAX_OUTPUT(".gui.cap_bank.max_output"),

  GUI_ENABLED(".gui.enabled"),
  GUI_DISABLED(".gui.disabled"),

  GUI_POWER_MONITOR_CONDUIT_STORAGE(".gui.power_monitor.conduit_storage"),
  GUI_POWER_MONITOR_CAPBANK_STORAGE(".gui.power_monitor.capbank_storage"),
  GUI_POWER_MONITOR_MACHINE_BUFFER(".gui.power_monitor.machine_buffer"),
  GUI_POWER_MONITOR_AVERAGE_OUTPUT(".gui.power_monitor.average_output"),
  GUI_POWER_MONITOR_AVERAGE_INPUT(".gui.power_monitor.average_input"),
  GUI_POWER_MONITOR_NO_NETWORK_ERROR(".gui.power_monitor.no_network_error"),

  GUI_POWER_MONITOR_ENGINE_1(".gui.power_monitor.engine_section1"),
  GUI_POWER_MONITOR_ENGINE_2(".gui.power_monitor.engine_section2"),
  GUI_POWER_MONITOR_ENGINE_3(".gui.power_monitor.engine_section3"),
  GUI_POWER_MONITOR_ENGINE_4(".gui.power_monitor.engine_section4"),
  GUI_POWER_MONITOR_ENGINE_5(".gui.power_monitor.engine_section5"),

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
    return EnderIOPowerTools.lang;
  }

  static {
    for (Lang lang : values()) {
      lang.checkTranslation();
    }
  }

}
