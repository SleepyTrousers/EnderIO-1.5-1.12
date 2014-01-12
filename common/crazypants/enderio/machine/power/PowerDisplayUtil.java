package crazypants.enderio.machine.power;

import java.text.NumberFormat;

import crazypants.enderio.EnderIO;

public class PowerDisplayUtil {

  public static enum PowerType {
    MJ(1, EnderIO.localize("power.mj")),
    RF(10, EnderIO.localize("power.rf"));

    private final double ratio;
    private final String abr;

    private PowerType(double ratio, String abr) {
      this.ratio = ratio;
      this.abr = abr;
    }

    String abr() {
      return abr;
    }

    double displayString(double powerMJ) {
      return powerMJ * ratio;
    }
  }

  private static final NumberFormat INT_NF = NumberFormat.getIntegerInstance();

  private static final NumberFormat FLOAT_NF = NumberFormat.getInstance();

  private static PowerType currentPowerType = PowerType.MJ;

  private static final String PER_TICK = EnderIO.localize("power.tick");

  static {
    FLOAT_NF.setMinimumFractionDigits(1);
    FLOAT_NF.setMaximumFractionDigits(1);
  }

  public static String formatPower(double powerMJ) {
    return INT_NF.format(currentPowerType.displayString(powerMJ));
  }

  public static String formatPowerFloat(double powerMJ) {
    return FLOAT_NF.format(currentPowerType.displayString(powerMJ));
  }

  public static String abrevation() {
    return currentPowerType.abr();
  }

  public static String perTick() {
    return PER_TICK;
  }

}
