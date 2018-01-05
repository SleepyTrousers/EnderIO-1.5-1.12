package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class VatConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "vat"));

  public static final IValue<Integer> vatInputTankSize = F.make("vatInputTankSize", 8000, //
      "Size of the Vat's input tank in mB.").setMin(1).sync();
  public static final IValue<Integer> vatOutputTankSize = F.make("vatOutputTankSize", 8000, //
      "Size of the Vat's output tank in mB.").setMin(1).sync();

}
