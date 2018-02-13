package crazypants.enderio.base.capacitor;

import javax.annotation.Nonnull;

import com.enderio.core.common.Lang;

import crazypants.enderio.base.config.Config.Section;
import net.minecraftforge.common.config.Configuration;

public class CapacitorKeyHelper {

  private CapacitorKeyHelper() {
  }

  static @Nonnull public String localizeComment(@Nonnull Lang lang, @Nonnull Section configSection, @Nonnull String configKey) {
    final String langKey = "config.capacitor." + configKey;
    return lang.localize(langKey);
  }

  public static void processConfig(Configuration config, ICapacitorKey.Computable... keys) {
    for (ICapacitorKey.Computable key : keys) {
      key.setBaseValue(config.getInt(key.getConfigKey(), key.getConfigSection().name, key.getDefaultBaseValue(), Integer.MIN_VALUE, Integer.MAX_VALUE,
          key.getConfigComment()));
      String string = Scaler.Factory.toString(key.getScaler());
      if (string != null) {
        String string2 = config.getString(key.getConfigKey() + ".scaler", key.getConfigSection().name, string, "Scaler for " + key.getConfigKey());
        Scaler tmp = Scaler.Factory.fromString(string2);
        if (tmp != null) {
          key.setScaler(tmp);
        } else {
          config.get(key.getConfigSection().name, key.getConfigKey() + ".scaler", string, "Scaler for " + key.getConfigKey()).set(string);
        }
      }
    }
  }

  static @Nonnull public String createConfigKey(ICapacitorKey key, String configKey) {
    return configKey == null ? key.getName() : configKey;
  }

}
