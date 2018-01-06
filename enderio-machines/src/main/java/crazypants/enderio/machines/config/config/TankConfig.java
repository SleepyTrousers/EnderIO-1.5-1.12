package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class TankConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "client"));

  public static final IValue<Boolean> tankSmeltTrashIntoLava = F.make("tankSmeltTrashIntoLava", true, //
          "If true, when trashing items in lava, a tiny amount more lava will be produced. Trashing items in other hot liquids will NOT have this effect.")
      .sync();

  public static final IValue<Integer> tankSizeNormal = F.make("tankSizeNormal", 16000, //
      "The size of a normal tank in mB.").setRange(0, Integer.MAX_VALUE).sync();

  public static final IValue<Integer> tankSizeAdvanced = F.make("tankSizeAdvanced", 32000, //
      "The size of an advanced tank in mB.").setRange(0, Integer.MAX_VALUE).sync();

}
