package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class SoulBinderConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "soulbinder"));

  public static final IValue<Integer> soulFluidInputRate = F.make("soulFluidInputRate", 50, //
      "Amount of XP fluid in mB the Soul Binder can accept per tick.").setMin(1);

  public static final IValue<Integer> soulFluidOutputRate = F.make("soulFluidOutputRate", 50, //
      "Amount of XP fluid in mB that can be extracted from the Soul Binder per tick.").setMin(1);

}
