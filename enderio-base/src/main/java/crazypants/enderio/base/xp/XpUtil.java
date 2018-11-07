package crazypants.enderio.base.xp;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
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

  /**
   * Converts the given fluid amount into experience points.
   * <p>
   * Note that this calculation has a remainder!
   * 
   * @param liquid
   *          fluid amount (in mB)
   * @return experience
   */
  public static int liquidToExperience(int liquid) {
    return liquid / RATIO;
  }

  /**
   * Converts the given experience into fluid.
   * <p>
   * This conversion is lossless.
   * 
   * @param xp
   *          experience
   * @return fluid amount (in mB)
   */
  public static int experienceToLiquid(int xp) {
    return xp * RATIO;
  }

  /**
   * The highest level that can be converted into experience points that can be stored as an {@link Integer}.
   */
  private static final int MAX_LEVEL = 21862;
  private static final int[] xpmap = new int[MAX_LEVEL + 1];

  /**
   * The highest level that can be converted into experience points that can be stored as an {@link Integer}.
   */
  public static int getMaxLevelsStorable() {
    return MAX_LEVEL;
  }

  /**
   * 
   * @param level
   * @return The amount of XP a player needs to get from level 0 to the given level
   */
  public static int getExperienceForLevel(int level) {
    if (level <= 0) {
      return 0;
    }
    if (level > MAX_LEVEL) {
      return Integer.MAX_VALUE;
    }
    return xpmap[level];
  }

  static {
    int res = 0;
    for (int i = 0; i <= MAX_LEVEL; i++) {
      if (res < 0) {
        res = Integer.MAX_VALUE;
        Log.error("Internal XP calculation is wrong. Level " + i + " already maxes out.");
      }
      xpmap[i] = res;
      res += getXpBarCapacity(i);
    }
  }

  /**
   * See {@link net.minecraft.entity.player.EntityPlayer#xpBarCap()}
   * 
   * @param level
   * @return The amount of XP a player at the given level needs to get to the <em>next</em> level
   */
  public static int getXpBarCapacity(int level) {
    if (level >= 30) {
      return 112 + (level - 30) * 9;
    } else {
      return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }
  }

  /**
   * 
   * @param experience
   * @return The level a player with the given experience has.
   */
  public static int getLevelForExperience(int experience) {
    for (int i = 1; i < xpmap.length; i++) {
      if (xpmap[i] > experience) {
        return i - 1;
      }
    }
    return xpmap.length;
  }

  /**
   * 
   * @param player
   * @return The total amount of experience (from both their level and their xp bar) the given player has
   */
  public static int getPlayerXP(@Nonnull EntityPlayer player) {
    return (int) (getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
  }

  public static void addPlayerXP(@Nonnull EntityPlayer player, int amount) {
    int experience = Math.max(0, getPlayerXP(player) + amount);
    player.experienceTotal = experience;
    player.experienceLevel = getLevelForExperience(experience);
    int expForLevel = getExperienceForLevel(player.experienceLevel);
    player.experience = (float) (experience - expForLevel) / (float) getXpBarCapacity(player.experienceLevel);
  }

}
