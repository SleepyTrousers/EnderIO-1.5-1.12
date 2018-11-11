package crazypants.enderio.powertools.config;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class PersonalConfig {

  public static final IValueFactory F = Config.F.section("personal");

  public static final IValue<Boolean> capacitorBankRenderPowerOverlayOnItem = F.make("capacitorBankRenderPowerOverlayOnItem", false, //
      "When true, the capacitor bank item will get a power bar in addition to the gauge on the bank.");

}
