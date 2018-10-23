package crazypants.enderio.util;

import java.util.Formatter;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

/**
 * Some duplication of jrt code that is properly null-annotated
 * <p>
 * IU hate this, but external annotations sadly break all the time...
 * 
 * @author Henry Loenwind
 *
 */
public class StringUtil {

  public static final @Nonnull String format(@Nonnull String format, Object... args) {
    try (Formatter f = new Formatter()) {
      return NullHelper.first(f.format(format, args).toString(), "");
    }
  }

}
