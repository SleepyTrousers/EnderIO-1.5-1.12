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
  GUI_POWER_MONITOR_OF(".gui.power_monitor.of"),

  // Conduit Probe
  GUI_CONDUIT_PROBE_NETWORK_HEADING(".gui.conduit_probe.network_heading"),
  GUI_CONDUIT_PROBE_CONDUIT_BUFFER(".gui.conduit_probe.conduit_buffer"),
  GUI_CONDUIT_PROBE_REQUEST_RANGE(".gui.conduit_probe.request_range"),
  GUI_CONDUIT_PROBE_CURRENT_REQUEST(".gui.conduit_probe.current_request"),
  GUI_CONDUIT_PROBE_ITEM_HEADING(".gui.conduit_probe.item_heading"),
  GUI_CONDUIT_PROBE_ITEM_NO_CONNECTIONS(".gui.conduit_probe.item_no_connections"),
  GUI_CONDUIT_PROBE_NO_POWER_FROM_SIDE(".gui.conduit_probe.no_power_from_side"),
  GUI_CONDUIT_PROBE_CONNECTION_DIR(".gui.conduit_probe.connection_dir"),
  GUI_CONDUIT_PROBE_EXTRACTED_ITEMS(".gui.conduit_probe.extracted_items"),
  GUI_CONDUIT_PROBE_EXTRACTED_ITEM(".gui.conduit_probe.extracted_item"),
  GUI_CONDUIT_PROBE_NO_OUTPUTS(".gui.conduit_probe.no_outputs"),
  GUI_CONDUIT_PROBE_INSERTED_INTO(".gui.conduit_probe.inserted_into"),
  GUI_CONDUIT_PROBE_NO_ITEMS(".gui.conduit_probe.no_items"),
  GUI_CONDUIT_PROBE_NO_ITEM(".gui.conduit_probe.no_item"),
  GUI_CONDUIT_PROBE_RECEIVE_ITEMS(".gui.conduit_probe.receive_items"),
  GUI_CONDUIT_PROBE_RECEIVE_ITEM1(".gui.conduit_probe.receive_item1"),
  GUI_CONDUIT_PROBE_RECEIVE_ITEM2(".gui.conduit_probe.receive_item2"),

  GUI_ENERGY_CONDUIT(".gui.conduit_energy.header"),
  GUI_ITEM_CONDUIT(".gui.conduit_item.header"),
  GUI_FLUID_CONDUIT(".gui.conduit_fluid.header"),
  GUI_REDSTONE_CONDUIT(".gui.conduit_redstone.header"),

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
