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
  GUI_CONDUIT_PROBE_ITEM_HEADING(".gui.conduit_probe.item.heading"),
  GUI_CONDUIT_PROBE_ITEM_HEADING_NO_CONNECTIONS(".gui.conduit_probe.item.heading.no_connections"),

  GUI_CONDUIT_PROBE_NO_ITEMS(".gui.conduit_probe.item.receive.no_items"),
  GUI_CONDUIT_PROBE_NO_ITEM(".gui.conduit_probe.item.receive.no_item"),
  GUI_CONDUIT_PROBE_RECEIVE_ITEMS(".gui.conduit_probe.item.receive.items"),
  GUI_CONDUIT_PROBE_RECEIVE_ITEM(".gui.conduit_probe.item.receive.item"),

  GUI_ENERGY_CONDUIT(".gui.conduit_energy.header"),
  GUI_ITEM_CONDUIT(".gui.conduit_item.header"),
  GUI_FLUID_CONDUIT(".gui.conduit_fluid.header"),
  GUI_REDSTONE_CONDUIT(".gui.conduit_redstone.header"),

  GUI_CONDUIT_PROBE_EXTRACT_NO_ITEM_NO_TARGET(".gui.conduit_probe.item.extract.no_item.no_targets"),
  GUI_CONDUIT_PROBE_EXTRACT_NO_ITEM_TARGETS(".gui.conduit_probe.item.extract.no_item.targets"),
  GUI_CONDUIT_PROBE_EXTRACT_ITEM_NO_TARGET(".gui.conduit_probe.item.extract.item.no_targets"),
  GUI_CONDUIT_PROBE_EXTRACT_ITEM_TARGETS(".gui.conduit_probe.item.extract.item.targets"),

  GUI_CONDUIT_PROBE_POWER_TRACKED_1(".gui.conduit_probe.power.tracked_conduit.line1"),
  GUI_CONDUIT_PROBE_POWER_TRACKED_2(".gui.conduit_probe.power.tracked_conduit.line2"),
  GUI_CONDUIT_PROBE_POWER_TRACKED_3(".gui.conduit_probe.power.tracked_conduit.line3"),
  GUI_CONDUIT_PROBE_POWER_TRACKED_4(".gui.conduit_probe.power.tracked_conduit.line4"),

  GUI_CONDUIT_PROBE_POWER_NETWORK_1(".gui.conduit_probe.power.tracked_network.line1"),
  GUI_CONDUIT_PROBE_POWER_NETWORK_2(".gui.conduit_probe.power.tracked_network.line2"),
  GUI_CONDUIT_PROBE_POWER_NETWORK_3(".gui.conduit_probe.power.tracked_network.line3"),
  GUI_CONDUIT_PROBE_POWER_NETWORK_4(".gui.conduit_probe.power.tracked_network.line4"),
  GUI_CONDUIT_PROBE_POWER_NETWORK_5(".gui.conduit_probe.power.tracked_network.line5"),
  GUI_CONDUIT_PROBE_POWER_NETWORK_6(".gui.conduit_probe.power.tracked_network.line6"),

  GUI_CONDUIT_PROBE_REDSTONE_HEADING(".gui.conduit_probe.redstone.heading"),
  GUI_CONDUIT_PROBE_REDSTONE_HEADING_NO_CONNECTIONS(".gui.conduit_probe.redstone.heading.no_connections"),

  GUI_CONDUIT_PROBE_REDSTONE_STRONG(".gui.conduit_probe.redstone.strong"),
  GUI_CONDUIT_PROBE_REDSTONE_WEAK(".gui.conduit_probe.redstone.weak"),
  GUI_CONDUIT_PROBE_REDSTONE_EXTERNAL(".gui.conduit_probe.redstone.external"),

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
