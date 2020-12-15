package crazypants.enderio.base.test;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.vecmath.Vector3f;

import net.minecraft.item.ItemDye;

class DyeColorTest {

  // data for https://github.com/YoussefRaafatNasry/identicon-generator

  // Thanks YoussefRaafatNasry!

  @Test
  void testItemDye() {
    for (int i = 0; i < ItemDye.DYE_COLORS.length; i++) {
      int col = ItemDye.DYE_COLORS[i];
      Vector3f c = ColorUtil.toFloat(col);
      int t = 0;
      t += c.x > .5 ? 1 : -1;
      t += c.y > .5 ? 1 : -1;
      t += c.z > .5 ? 1 : -1;
      if (t > 0) {
        c.x *= .8;
        c.y *= .8;
        c.z *= .8;
      } else {
        c.x *= 1.2;
        c.y *= 1.2;
        c.z *= 1.2;
      }
      int fg = ColorUtil.getRGB(c.x, c.y, c.z);
      System.out
          .print(String.format("COLOR_%s(%d, %d),", DyeColor.DYE_ORE_UNLOCAL_NAMES[i].replaceFirst("^.*\\.", "").toUpperCase(Locale.ENGLISH), i * 16, 240));
      System.out.println(String.format("// %d: #%x #%x %s", i, col, fg, DyeColor.DYE_ORE_UNLOCAL_NAMES[i].replaceFirst("^.*\\.", "")));
    }
    for (int i = 0; i < ItemDye.DYE_COLORS.length; i++) {
      System.out.print(String.format("EnderWidget.COLOR_%s,", DyeColor.DYE_ORE_UNLOCAL_NAMES[i].replaceFirst("^.*\\.", "").toUpperCase(Locale.ENGLISH)));
    }
  }

}
