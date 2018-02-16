package crazypants.enderio.powertools.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class PersonalConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "personal"));

  public static final IValue<Boolean> capacitorBankRenderPowerOverlayOnItem = F.make("capacitorBankRenderPowerOverlayOnItem", false, //
      "When true, the capacitor bank item will get a power bar in addition to the gauge on the bank.");

}
