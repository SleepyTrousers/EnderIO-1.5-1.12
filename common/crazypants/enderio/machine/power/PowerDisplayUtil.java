package crazypants.enderio.machine.power;

import java.text.NumberFormat;

import crazypants.enderio.Config;
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

    double toDisplayValue(double powerMJ) {
      return powerMJ * ratio;
    }

    double fromDisplayValue(double powerDisplayed) {
      return powerDisplayed / ratio;
    }
  }

  private static final NumberFormat INT_NF = NumberFormat.getIntegerInstance();

  private static final NumberFormat FLOAT_NF = NumberFormat.getInstance();

  private static PowerType currentPowerType = Config.useRfAsDefault ? PowerType.RF : PowerType.MJ;

  private static final String PER_TICK = EnderIO.localize("power.tick");

  public static final String OF = EnderIO.localize("gui.powerMonitor.of");

  static {
    FLOAT_NF.setMinimumFractionDigits(1);
    FLOAT_NF.setMaximumFractionDigits(1);
  }

  public static String formatPower(double powerMJ) {
    return INT_NF.format(currentPowerType.toDisplayValue(powerMJ));
  }

  public static Float parsePower(String power) {
    if(power == null) {
      return null;
    }
    try {
      Number d = FLOAT_NF.parse(power);
      if(d == null) {
        return null;
      }
      return (float) currentPowerType.fromDisplayValue(d.doubleValue());
    } catch (Exception e) {
      return null;
    }
  }

  public static String formatPowerFloat(double powerMJ) {
    if(currentPowerType == PowerType.RF) {
      return formatPower(powerMJ);
    }
    return FLOAT_NF.format(currentPowerType.toDisplayValue(powerMJ));
  }

  public static String abrevation() {
    return currentPowerType.abr();
  }

  public static String perTick() {
    return PER_TICK;
  }

  public static int fromDisplay(int input) {
    return (int) currentPowerType.fromDisplayValue(input);
  }

}
