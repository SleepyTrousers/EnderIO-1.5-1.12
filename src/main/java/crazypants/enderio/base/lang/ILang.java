package crazypants.enderio.base.lang;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.Log;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public interface ILang {

  @Nonnull
  String getKey();

  @Nonnull
  com.enderio.core.common.Lang getLang();

  // ========================

  default @Nonnull String get() {
    return getLang().localizeExact(getKey());
  }

  default @Nonnull String get(@Nonnull Object... params) {
    return getLang().localizeExact(getKey(), params);
  }

  default @Nonnull TextComponentString toChat() {
    return new TextComponentString(getLang().localizeExact(getKey()));
  }

  default @Nonnull TextComponentString toChat(@Nonnull Object... params) {
    return new TextComponentString(getLang().localizeExact(getKey(), params));
  }

  default @Nonnull TextComponentTranslation toChatServer() {
    return new TextComponentTranslation(getKey());
  }

  default @Nonnull TextComponentTranslation toChatServer(@Nonnull Object... params) {
    return new TextComponentTranslation(getKey(), params);
  }

  default @Nonnull NNList<String> getLines(@Nonnull Object... params) {
    NNList<String> result = new NNList<>();
    for (int i = 1; i < 12; i++) {
      final String linekey = getKey() + ".line" + i;
      if (getLang().canLocalizeExact(linekey)) {
        result.add(getLang().localizeExact(linekey, params));
      } else {
        return result;
      }
    }
    return result;
  }

  default void checkTranslation() {
    if (!getLang().canLocalizeExact(getKey()) && !getLang().canLocalizeExact(getKey() + ".line1")) {
      Log.error("Missing translation for '" + this + "': " + get());
    }
  }

}
