package crazypants.enderio.machines.config.config;

import crazypants.enderio.machines.config.Config;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class TankConfig {

  public static final IValueFactory F = Config.F.section("tank");

  public static final IValue<Boolean> tankSmeltTrashIntoLava = F.make("tankSmeltTrashIntoLava", true, //
      "If true, when trashing items in lava, a tiny amount more lava will be produced. Trashing items in other hot liquids will NOT have this effect.").sync();

  public static final IValue<Integer> tankSizeNormal = F.make("tankSizeNormal", 16000, //
      "The size of a normal tank in mB.").setRange(0, Integer.MAX_VALUE).sync();

  public static final IValue<Integer> tankSizeAdvanced = F.make("tankSizeAdvanced", 32000, //
      "The size of an advanced tank in mB.").setRange(0, Integer.MAX_VALUE).sync();

  public static final IValue<Boolean> allowMending = F.make("allowMending", true, //
      "If true, the tank can mend items enchanted with Mending using liquid XP.").sync();

  public static final IValue<Boolean> allowVoiding = F.make("allowVoiding", true, //
      "If true, the tank can void items when filled with a hot fluid.").sync();

}
