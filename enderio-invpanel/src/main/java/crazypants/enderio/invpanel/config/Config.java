package crazypants.enderio.invpanel.config;

import javax.annotation.ParametersAreNonnullByDefault;

import crazypants.enderio.base.config.factory.ValueFactoryEIO;
import crazypants.enderio.invpanel.EnderIOInvPanel;

@ParametersAreNonnullByDefault // Not the right one, but eclipse knows only 3 null annotations anyway, so it's ok
public final class Config {

  public static final ValueFactoryEIO F = new ValueFactoryEIO(EnderIOInvPanel.MODID);

  static {
    // force sub-configs to be classloaded with the main config
    InvpanelConfig.F.getClass();
  }
}
