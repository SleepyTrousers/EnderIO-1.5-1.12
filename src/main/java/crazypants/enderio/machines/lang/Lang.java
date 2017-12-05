package crazypants.enderio.machines.lang;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import crazypants.enderio.machines.EnderIOMachines;
import net.minecraft.util.text.TextComponentString;

public enum Lang {

  COMB_GEN_OUTPUT("generator.combustion.output"),
  XXXXXX1(""),
  XXXXXX2(""),
  XXXXXX3(""),
  XXXXXX4(""),
  XXXXXX5(""),
  XXXXXX6(""),
  XXXXXX7(""),
  XXXXXX8(""),
  XXXXXX9(""),

  ;

  private final @Nonnull String key;

  private Lang(boolean addDomain, @Nonnull String key) {
    if (addDomain) {
      this.key = EnderIOMachines.lang.addPrefix(key);
    } else {
      this.key = key;
    }
  }

  private Lang(@Nonnull String key) {
    this(true, key);
  }

  public @Nonnull String get() {
    return EnderIOMachines.lang.localizeExact(key);
  }

  public @Nonnull String get(@Nonnull Object... params) {
    return EnderIOMachines.lang.localizeExact(key, params);
  }

  public @Nonnull TextComponentString toChat() {
    return new TextComponentString(EnderIOMachines.lang.localizeExact(key));
  }

  public @Nonnull TextComponentString toChat(@Nonnull Object... params) {
    return new TextComponentString(EnderIOMachines.lang.localizeExact(key, params));
  }

  static {
    for (Lang text : values()) {
      if (!EnderIOMachines.lang.canLocalizeExact(text.key)) {
        Log.error("Missing translation for '" + text + "': " + text.get());
      }
    }
  }

  public @Nonnull String getKey() {
    return key;
  }

}
