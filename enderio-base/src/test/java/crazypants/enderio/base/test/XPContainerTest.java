package crazypants.enderio.base.test;

import java.util.Random;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import crazypants.enderio.base.xp.ExperienceContainer;
import crazypants.enderio.base.xp.XpUtil;
import net.minecraftforge.fluids.Fluid;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class XPContainerTest {

  private static final @Nonnull Fluid FLUID = new Fluid("xpjuice", null, null);

  @BeforeAll
  static void setup() {
    ExperienceContainer.XP = () -> FLUID;
  }

  @Test
  void simpleMathTest() {
    ExperienceContainer x = new ExperienceContainer();

    assertEquals(0, x.getExperienceTotal());
    assertEquals(0, x.getExperienceTotalIntLimited());

    assertEquals(Integer.MAX_VALUE, x.addExperience((long) Integer.MAX_VALUE));
    assertEquals(Integer.MAX_VALUE, x.getExperienceTotal());
    assertEquals(Integer.MAX_VALUE, x.getExperienceTotalIntLimited());

    assertEquals(1, x.removeExperience(1));
    assertEquals(Integer.MAX_VALUE - 1, x.getExperienceTotal());
    assertEquals(Integer.MAX_VALUE - 1, x.getExperienceTotalIntLimited());

    assertEquals(1, x.addExperience(1));
    assertEquals(Integer.MAX_VALUE, x.getExperienceTotal());
    assertEquals(Integer.MAX_VALUE, x.getExperienceTotalIntLimited());

    assertEquals(1, x.addExperience(1));
    assertEquals(Integer.MAX_VALUE + 1L, x.getExperienceTotal());
    assertEquals(Integer.MAX_VALUE, x.getExperienceTotalIntLimited());
  }

  @Test
  void xpToolTest2() {
    final long maxLevelXP = XpUtil.getExperienceForLevelL(XpUtil.getMaxLevelsStorableL());
    for (long v = maxLevelXP; v < maxLevelXP + 100; v++) {
      final long v2 = v;
      assertEquals(XpUtil.getMaxLevelsStorableL(), (int) assertDoesNotThrow(() -> XpUtil.getLevelForExperience(v2), "" + v2), "" + v);
    }
    for (long v = Long.MAX_VALUE - 100; v > 0; v++) {
      final long v2 = v;
      assertEquals(XpUtil.getMaxLevelsStorableL(), (int) assertDoesNotThrow(() -> XpUtil.getLevelForExperience(v2), "" + v2), "" + v);
    }
  }

  // @Test // normally disabled because it takes ages
  void xpToolTest() {
    int p = -1;
    for (int level = /* XpUtil.getMaxLevelsStorableL() - 1 */0; level <= XpUtil.getMaxLevelsStorableL(); level++) {
      long xp = XpUtil.getExperienceForLevelL(level);
      long min = Math.max(0, xp - 1);
      long max = Math.min(xp, Math.max(xp + 1, 0)); // sic! xp+10 goes negative!
      final String message = " for " + xp + " level " + level;
      for (long v = min; v <= max; v++) {
        final long v2 = v;
        assertEquals(v < xp ? level - 1 : level, (int) assertDoesNotThrow(() -> XpUtil.getLevelForExperienceWithChecks(v2), "" + v2 + message), v + message);
      }
      int n = (int) ((level * 1000L) / XpUtil.getMaxLevelsStorableL());
      if (n != p) {
        System.out.println((n / 10f) + "% - " + level + " / " + XpUtil.getMaxLevelsStorableL());
        p = n;
      }
    }
  }

  @Test // quick version that only tests some random values
  void xpToolTestQuick() {
    Random rand = new Random();
    int p = -1;
    for (int level = 0; level <= XpUtil.getMaxLevelsStorableL(); level += rand.nextInt(XpUtil.getMaxLevelsStorableL() / 100)) {
      long xp = XpUtil.getExperienceForLevelL(level);
      long min = Math.max(0, xp - 1);
      long max = Math.min(xp, Math.max(xp + 1, 0)); // sic! xp+10 goes negative!
      final String message = " for " + xp + " level " + level;
      for (long v = min; v <= max; v++) {
        final long v2 = v;
        assertEquals(v < xp ? level - 1 : level, (int) assertDoesNotThrow(() -> XpUtil.getLevelForExperienceWithChecks(v2), "" + v2 + message), v + message);
      }
      int n = (int) ((level * 100L) / XpUtil.getMaxLevelsStorableL());
      if (n != p) {
        System.out.println(n + "% - " + level + " / " + XpUtil.getMaxLevelsStorableL());
        p = n;
      }
    }
  }

}
