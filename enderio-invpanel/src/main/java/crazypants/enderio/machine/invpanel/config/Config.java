package crazypants.enderio.machine.invpanel.config;

import javax.annotation.ParametersAreNonnullByDefault;

import crazypants.enderio.base.config.factory.ValueFactory;
import crazypants.enderio.machine.EnderIOInvPanel;

@ParametersAreNonnullByDefault // Not the right one, but eclipse knows only 3 null annotations anyway, so it's ok
public final class Config {

  public static final ValueFactory F = new ValueFactory(EnderIOInvPanel.MODID);

  public static void load() {
    // force sub-configs to be classloaded with the main config
    InvpanelConfig.F.getClass();
  }
}
