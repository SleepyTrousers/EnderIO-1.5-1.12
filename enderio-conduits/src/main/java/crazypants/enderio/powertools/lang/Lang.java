package crazypants.enderio.powertools.lang;

import javax.annotation.Nonnull;

import crazypants.enderio.base.lang.ILang;
import crazypants.enderio.powertools.EnderIOPowerTools;

public enum Lang implements ILang {

  CAPBANK_TOOLTIP_WITH_ITEMS("tile.block_cap_bank.tooltip.has_items"),

  ;

  private final @Nonnull String key;

  private Lang(@Nonnull String key) {
    if (key.startsWith(".")) {
      this.key = getLang().addPrefix(key.substring(1));
    } else {
      this.key = key;
    }
  }

  @Override
  public @Nonnull String getKey() {
    return key;
  }

  @Override
  public @Nonnull com.enderio.core.common.Lang getLang() {
    return EnderIOPowerTools.lang;
  }

  static {
    for (Lang lang : values()) {
      lang.checkTranslation();
    }
  }

}
