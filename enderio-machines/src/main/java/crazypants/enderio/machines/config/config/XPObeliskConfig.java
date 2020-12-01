package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.xp.ExperienceContainer;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.config.Config;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class XPObeliskConfig {

  private static final int LIMIT = Math.min(XpUtil.getMaxLevelsStorableL(), XpUtil.getLevelForExperience(ExperienceContainer.MAX_XP_POINTS));

  public static final IValueFactory F = Config.F.section("xpobelisk");

  public static final IValue<Integer> maxLevels = F.make("maxLevelsStored", LIMIT, //
      "Maximum level of XP the xp obelisk can contain.").setRange(1, LIMIT).sync();

}
