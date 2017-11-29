package crazypants.enderio.base.power;

import java.text.NumberFormat;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PowerDisplayUtil {

  private static final @Nonnull NumberFormat INT_NF = NumberFormat.getIntegerInstance();
  private static final @Nonnull NumberFormat FLOAT_NF = NumberFormat.getInstance();

  static {
    FLOAT_NF.setMinimumFractionDigits(1);
    FLOAT_NF.setMaximumFractionDigits(1);
  }

  public static String perTickStr() {
    return EnderIO.lang.localize("power.tick");
  }

  public static String ofStr() {
    return EnderIO.lang.localize("gui.powerMonitor.of");
  }

  public static String formatPowerPerTick(int powerPerTick) {
    return formatPower(powerPerTick) + " " + abrevation() + perTickStr();
  }

  public static String formatStoredPower(int amount, int capacity) {
    return formatPower(amount) + "/" + formatPower(capacity) + " " + PowerDisplayUtil.abrevation();
  }

  public static String formatPower(long amount) {
    return INT_NF.format(amount);
  }

  public static String formatInteger(int value) {
    return formatPower(value);
  }

  public static String formatInteger(float value) {
    return formatPower((long) value);
  }

  public static String formatPower(int powerRF) {
    return INT_NF.format(powerRF);
  }

  public static String formatPowerFloat(float averageRfTickSent) {
    return FLOAT_NF.format(averageRfTickSent);
  }

  public static Integer parsePower(String power) {
    if (power == null) {
      return null;
    }
    try {
      Number d = INT_NF.parse(power);
      if (d == null) {
        return null;
      }
      return d.intValue();
    } catch (Exception e) {
      return null;
    }
  }

  public static String abrevation() {
    return EnderIO.lang.localize("power.rf");
  }

  @SideOnly(Side.CLIENT)
  public static int parsePower(GuiTextField tf) {
    String txt = tf.getText();
    try {
      Integer power = PowerDisplayUtil.parsePower(txt);
      if (power == null) {
        return -1;
      }
      return power.intValue();
    } catch (Exception e) {
      return -1;
    }
  }

}
