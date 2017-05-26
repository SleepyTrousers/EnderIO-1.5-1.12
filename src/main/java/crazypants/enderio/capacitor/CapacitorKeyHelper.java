package crazypants.enderio.capacitor;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.config.Config.Section;
import net.minecraftforge.common.config.Configuration;

public class CapacitorKeyHelper {

  private CapacitorKeyHelper() {
  }

  static @Nonnull String localizeComment(@Nonnull Section configSection, @Nonnull String configKey) {
    final String langKey = "config.capacitor." + configKey;
    if (!EnderIO.lang.canLocalize(langKey)) {
      Log.warn("Missing translation: " + langKey);
    }
    return EnderIO.lang.localize(langKey);
  }

  public static void processConfig(Configuration config, ICapacitorKey.Computable... keys) {
    for (ICapacitorKey.Computable key : keys) {
      key.setBaseValue(config.get(key.getConfigSection().name, key.getConfigKey(), key.getDefaultBaseValue(), key.getConfigComment()).getInt(key.getBaseValue()));
      String string = Scaler.Factory.toString(key.getScaler());
      if (string != null) {
        String string2 = config.get(key.getConfigSection().name, key.getConfigKey() + ".scaler", string, null).getString();
        Scaler tmp = Scaler.Factory.fromString(string2);
        if (tmp != null) {
          key.setScaler(tmp);
        } else {
          config.get(key.getConfigSection().name, key.getConfigKey() + ".scaler", string, null).set(string);
        }
      }
    }
  }

  static @Nonnull String createConfigKey(CapacitorKey key, String configKey) {
    return configKey == null ? key.name().toLowerCase(Locale.US) : configKey;
  }

}
