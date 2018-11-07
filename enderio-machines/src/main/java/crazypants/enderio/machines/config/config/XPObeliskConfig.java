package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.config.Config;

public final class XPObeliskConfig {

  public static final IValueFactory F = Config.F.section("xpobelisk");

  public static final IValue<Integer> maxLevels = F.make("maxLevelsStored", XpUtil.getMaxLevelsStorable(), //
      "Maximum level of XP the xp obelisk can contain.").setRange(1, XpUtil.getMaxLevelsStorable()).sync();

}
