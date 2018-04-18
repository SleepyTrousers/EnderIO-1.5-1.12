package crazypants.enderio.base.xp;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Values taken from OpenMods EnchantmentUtils to ensure consistent behavior
 * 
 * @see <a href="https://github.com/OpenMods/OpenModsLib/blob/master/src/main/java/openmods/utils/EnchantmentUtils.java">OpenMods</a>
 *
 */
public class XpUtil {

  // Values taken from OpenBlocks to ensure compatibility

  public static final int RATIO = 20;

  public static int liquidToExperience(int liquid) {
    return liquid / RATIO;
  }

  public static int experienceToLiquid(int xp) {
    return xp * RATIO;
  }

  public static int getLiquidForLevel(int level) {
    return experienceToLiquid(getExperienceForLevel(level));
  }

  private static final int[] xpmap = new int[21863];

  public static int getExperienceForLevel(int level) {
    if (level <= 0) {
      return 0;
    }
    if (level >= 21863) {
      return Integer.MAX_VALUE;
    }
    return xpmap[level];
  }

  static {
    int res = 0;
    for (int i = 0; i < 21863; i++) {
      res += getXpBarCapacity(i);
      if (res < 0) {
        res = Integer.MAX_VALUE;
      }
      xpmap[i] = res;
    }
  }

  public static int getXpBarCapacity(int level) {
    if (level == 0) {
      return 0;
    } else if (level <= 15) {
      return sum(level, 7, 2);
    } else if (level <= 30) {
      return 315 + sum(level - 15, 37, 5);
    }
    return 1395 + sum(level - 30, 112, 9);
  }

  private static int sum(int level, int a, int d) {
    return level * (2 * a + (level - 1) * d) / 2;
  }

  public static int getLevelForExperience(int experience) {
    for (int i = 1; i < xpmap.length; i++) {
      if (xpmap[i] > experience) {
        return i - 1;
      }
    }
    return xpmap.length;
  }

  public static int getPlayerXP(@Nonnull EntityPlayer player) {
    return (int) (getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
  }

  public static void addPlayerXP(@Nonnull EntityPlayer player, int amount) {
    int experience = Math.max(0, getPlayerXP(player) + amount);
    player.experienceTotal = experience;
    player.experienceLevel = getLevelForExperience(experience);
    int expForLevel = getExperienceForLevel(player.experienceLevel);
    player.experience = (float) (experience - expForLevel) / (float) player.xpBarCap();
  }

}
