package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.ValueFactory;
import crazypants.enderio.machines.config.Config;

public final class VatConfig {

  public static final ValueFactory F = Config.F.section("vat");

  public static final IValue<Integer> vatInputTankSize = F.make("vatInputTankSize", 8000, //
      "Size of the Vat's input tank in mB.").setMin(1).sync();
  public static final IValue<Integer> vatOutputTankSize = F.make("vatOutputTankSize", 8000, //
      "Size of the Vat's output tank in mB.").setMin(1).sync();

}
