package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class TopConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(BaseConfig.F, new Section("", "top"));

  public static final IValue<Boolean> enabled = F.make("enabled", true, //
      "If true, 'The One Probe' by McJty will be supported.").sync();

  public static final IValue<Boolean> showProgressByDefault = F.make("showProgressByDefault", true, //
      "If true, the progress will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed).").sync();

  public static final IValue<Boolean> showPowerByDefault = F.make("showPowerByDefault", true, //
      "If true, the power level will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed).").sync();

  public static final IValue<Boolean> showRedstoneByDefault = F.make("showRedstoneByDefault", false, //
      "If true, the resdstone status will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed).").sync();

  public static final IValue<Boolean> showSideConfigByDefault = F.make("showSideConfigByDefault", false, //
      "If true, the side config will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed).").sync();

  public static final IValue<Boolean> showRangeByDefault = F.make("showRangeByDefault", false, //
      "If true, the range will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed).").sync();

  public static final IValue<Boolean> showMobsByDefault = F.make("showMobsByDefault", true, //
      "If true, the mob list will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed).").sync();

  public static final IValue<Boolean> showTanksByDefault = F.make("showTanksByDefault", true, //
      "If true, the tank content will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed).").sync();

  public static final IValue<Boolean> showXPByDefault = F.make("showXPByDefault", true, //
      "If true, the XP level will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed).").sync();

  public static final IValue<Boolean> showItemCountDefault = F.make("showItemCountDefault", true, //
      "If true, the item count will be shown always, otherwise only it will only be shown on 'extended' mode (e.g. with shift pressed).").sync();

}
