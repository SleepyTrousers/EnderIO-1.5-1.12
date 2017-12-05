package crazypants.enderio.base.power;

import crazypants.enderio.base.lang.LangPower;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PowerDisplayUtil {

  public static Integer parsePower(String power) {
    if (power == null) {
      return null;
    }
    try {
      Number d = LangPower.INT_NF.parse(power);
      if (d == null) {
        return null;
      }
      return d.intValue();
    } catch (Exception e) {
      return null;
    }
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
