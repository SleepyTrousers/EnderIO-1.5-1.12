package crazypants.enderio.conduit.refinedstorage.lang;

import javax.annotation.Nonnull;

import crazypants.enderio.base.lang.ILang;
import crazypants.enderio.conduits.EnderIOConduits;

public enum Lang implements ILang {

  GUI_RS_FILTER_UPGRADE_IN(".gui.rs_filter_upgrade_in"),
  GUI_RS_FILTER_UPGRADE_IN_2(".gui.rs_filter_upgrade_in2"),
  GUI_RS_FILTER_UPGRADE_IN_3(".gui.rs_filter_upgrade_in3"),

  GUI_RS_FILTER_UPGRADE_OUT(".gui.rs_filter_upgrade_out"),
  GUI_RS_FILTER_UPGRADE_OUT_2(".gui.rs_filter_upgrade_out2"),
  GUI_RS_FILTER_UPGRADE_OUT_3(".gui.rs_filter_upgrade_out3"),

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
  @Nonnull
  public String getKey() {
    return key;
  }

  @Override
  @Nonnull
  public com.enderio.core.common.Lang getLang() {
    return EnderIOConduits.lang;
  }

  static {
    for (Lang text : values()) {
      text.checkTranslation();
    }
  }

}
