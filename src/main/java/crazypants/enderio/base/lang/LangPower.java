package crazypants.enderio.base.lang;

import java.text.NumberFormat;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;

import static crazypants.enderio.base.lang.Lang.POWER;
import static crazypants.enderio.base.lang.Lang.POWER_OF;
import static crazypants.enderio.base.lang.Lang.POWER_PERTICK;
import static crazypants.enderio.base.lang.Lang.POWER_SYMBOL;

public final class LangPower {

  public static final @Nonnull NumberFormat INT_NF = NumberFormat.getIntegerInstance();
  public static final @Nonnull NumberFormat FLOAT_NF = NumberFormat.getInstance();

  static {
    LangPower.FLOAT_NF.setMinimumFractionDigits(1);
    LangPower.FLOAT_NF.setMaximumFractionDigits(1);
  }

  @Deprecated // use RFt()
  public static @Nonnull String perTickStr() {
    return EnderIO.lang.localize("power.tick");
  }

  @Deprecated // use RF(a, b)
  public static @Nonnull String ofStr() {
    return EnderIO.lang.localize("gui.powerMonitor.of");
  }

  public static @Nonnull String RFt(int amountPerTick) {
    return POWER_PERTICK.get(format(amountPerTick));
  }

  public static @Nonnull String RF(int amount, int capacity) {
    return POWER_OF.get(format(amount), format(capacity));
  }

  public static @Nonnull String RF(long amount) {
    return POWER.get(format(amount));
  }

  public static @Nonnull String RF(int amount) {
    return POWER.get(format(amount));
  }

  public static @Nonnull String RF(float amount) {
    return POWER.get(format(amount));
  }

  public static @Nonnull String RF() {
    return POWER_SYMBOL.get();
  }

  public static @Nonnull String format(long amount) {
    return LangPower.INT_NF.format(amount);
  }

  public static @Nonnull String format(int amount) {
    return LangPower.INT_NF.format(amount);
  }

  public static @Nonnull String format(float amount) {
    return LangPower.FLOAT_NF.format(amount);
  }

  @Deprecated // use RF()
  public static @Nonnull String abrevation() {
    return EnderIO.lang.localize("power.rf");
  }

}
