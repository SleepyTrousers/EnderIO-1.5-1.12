package crazypants.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//TODO 1.11 - use ec version
public final class NullHelper {

  private NullHelper() {
  }

  @Nonnull
  public final static <P> P notnull(P o, @Nonnull String message) {
    if (o == null) {
      throw new NullPointerException("Houston we have a problem: '" + message + "'. "
          + "Please report that on our bugtracker unless you are using some old version. Thank you.");
    }
    return o;
  }

  @Nonnull
  public final static <P> P notnullJ(P o, @Nonnull String message) {
    if (o == null) {
      throw new NullPointerException("There was a problem with Java: The call '" + message
          + "' returned null even though it should not be able to do that. Is your Java broken? "
          + "Are you using a version that is much newer than the one Ender IO was developed with?");
    }
    return o;
  }

  @Nonnull
  public final static <P> P notnullM(P o, @Nonnull String message) {
    if (o == null) {
      throw new NullPointerException("There was a problem with Minecraft: The call '" + message
          + "' returned null even though it should not be able to do that. Is your Minecraft broken? Did some other mod break it?");
    }
    return o;
  }

  @Nonnull
  public final static <P> P notnullF(P o, @Nonnull String message) {
    if (o == null) {
      throw new NullPointerException("There was a problem with Forge: The call '" + message
          + "' returned null even though it should not be able to do that. Is your Forge broken? Did some other mod break it? "
          + "Are you using a version that is much newer than the one Ender IO was developed with?");
    }
    return o;
  }

  @SuppressWarnings({ "null", "unused" })
  @Nonnull
  public final static <P> P untrusted(@Nonnull P o, @Nonnull String message) {
    if (o == null) {
      throw new NullPointerException("There was a problem with Minecraft: The call '" + message
          + "' returned null even though it says it is not be able to do that. " //
          + "Your Minecraft is broken. Ender IO is NOT(!) the cause of this crash");
    }
    return o;
  }

  /**
   * Returns its {@link Nonnull} argument unchanged as {@link Nullable}. Use this if you want to null-check values that are annotated non-null but are known not
   * to be.
   */
  @Nullable
  public final static <P> P untrust(@Nonnull P o) {
    return o;
  }

}
