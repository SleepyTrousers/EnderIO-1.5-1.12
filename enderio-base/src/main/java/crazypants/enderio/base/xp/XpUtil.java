package crazypants.enderio.base.xp;

import java.math.BigInteger;
import java.math.RoundingMode;

import javax.annotation.Nonnull;

import com.google.common.math.BigIntegerMath;
import com.google.common.math.LongMath;

import crazypants.enderio.util.MathUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Values taken from OpenMods EnchantmentUtils to ensure consistent behavior
 * 
 * @see <a href="https://github.com/OpenMods/OpenModsLib/blob/1.7.2/src/main/java/openmods/utils/EnchantmentUtils.java">OpenMods</a>
 *
 */
public class XpUtil {

  // Values taken from OpenBlocks to ensure compatibility
  public static final long RATIO = 20;

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
    return (int) (liquid / RATIO);
  }

  /**
   * Converts the given fluid amount into experience points.
   * <p>
   * Note that this calculation has a remainder!
   * 
   * @param liquid
   *          fluid amount (in mB)
   * @return experience
   */
  public static long liquidToExperience(long liquid) {
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
    return Math.multiplyExact(xp, (int) RATIO);
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
  public static long experienceToLiquid(long xp) {
    return Math.multiplyExact(xp, RATIO);
  }

  /**
   * The highest level that can be converted into experience points that can be stored as an {@link Long}.
   */
  private static final int MAX_LEVEL_INT = 21862;
  private static final int MAX_LEVEL_LONG = 1431655783;

  /**
   * The highest level that can be converted into experience points that can be stored as an {@link Integer}.
   */
  public static int getMaxLevelsStorable() {
    return MAX_LEVEL_INT;
  }

  /**
   * The highest level that can be converted into experience points that can be stored as an {@link Long}.
   */
  public static int getMaxLevelsStorableL() {
    return MAX_LEVEL_LONG;
  }

  /**
   * 
   * @param level
   * @return The amount of XP a player needs to get from level 0 to the given level
   */
  public static int getExperienceForLevel(int level) {
    if (level < 0) {
      throw new ArithmeticException("level underflow");
    }
    return Math.toIntExact(calculateXPfromLevel(level));
  }

  /**
   * 
   * @param level
   * @return The amount of XP a player needs to get from level 0 to the given level
   */
  public static long getExperienceForLevelL(int level) {
    if (level < 0) {
      throw new ArithmeticException("level underflow");
    }
    if (level > MAX_LEVEL_LONG) {
      return Long.MAX_VALUE;
    }
    return calculateXPfromLevel(level);
  }

  /**
   * See {@link net.minecraft.entity.player.EntityPlayer#xpBarCap()}
   * 
   * @param level
   * @return The amount of XP a player at the given level needs to get to the <em>next</em> level
   */
  public static int getXpBarCapacity(int level) {
    if (level >= 30) {
      return -158 + level * 9;
    } else if (level >= 15) {
      return -38 + level * 5;
    } else if (level >= 0) {
      return 7 + level * 2;
    } else {
      throw new ArithmeticException("level underflow");
    }
  }

  /*
   * The level has 3 ranges with different formula. For the highest range, we can use a neat reverse formula but that one would ignore the lower levels. So we
   * calculate the offset from the real value beforehand using using the "not that good" formulas.
   */
  private static final long LVLOFFSET32 = -calculateXPfromLevelHigh(32) + calculateXPfromLevelLow(32);

  private static long calculateXPfromLevel(int level) {
    if (level >= 32) {
      return calculateXPfromLevelHigh(level) + LVLOFFSET32;
    } else {
      return calculateXPfromLevelLow(level);
    }
  }

  private static long calculateXPfromLevelHigh(int level) {
    return -158L * (level + 1L) + MathUtil.termial(level - 1) * 9L; // correct in long, but offset by LVLOFFSET32
  }

  private static long calculateXPfromLevelLow(int level) {
    if (level >= 1 && level <= 16) {
      return (long) (Math.pow(level, 2) + 6 * level);
    } else if (level >= 17 && level <= 31) {
      return (long) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
    } else if (level >= 32) {
      return (long) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220); // bad formula in long
    } else {
      return 0;
    }
  }

  /**
   * 
   * @param experience
   * @return The level a player with the given experience has.
   */
  public static int getLevelForExperience(long experience) {
    return getLevelFromExp(experience);
  }

  /**
   * Variant of {@link #getLevelForExperience(long)} for unit tests only. This checks its result and if it is wrong, will output debug info t0 stdout and thrown
   * an {@link ArithmeticException}.
   */
  public static int getLevelForExperienceWithChecks(long experience) {
    int guess = getLevelFromExp(experience);

    long high = calculateXPfromLevel(guess + 1);
    long low = calculateXPfromLevel(guess);
    if (experience >= low && (experience < high || high < low)) {
      return guess;
    }
    System.out.println("guess: " + guess + " levels");
    System.out.println("low: " + low);
    System.out.println("XP: " + experience);
    System.out.println("high: " + high);
    throw new ArithmeticException("level calculation error");
  }

  private static final BigInteger B72 = BigInteger.valueOf(72);
  private static final BigInteger B54215 = BigInteger.valueOf(54215);
  private static final BigInteger B325 = BigInteger.valueOf(325);
  private static final BigInteger B18 = BigInteger.valueOf(18);

  /**
   * Found this on Google. It seems to work fine, but I do not trust it---getXPfromLevelLow() also has issues...
   * <p>
   * Updated to use sqrt() impl that work over the full range of long. Ran through an extensive test, now the result is good.
   */
  @SuppressWarnings("null")
  private static int getLevelFromExp(long exp) {
    if (exp > Long.MAX_VALUE / 72) {
      return BigIntegerMath.sqrt(BigInteger.valueOf(exp).multiply(B72).subtract(B54215), RoundingMode.DOWN).add(B325).divide(B18).intValueExact();
    }
    if (exp > Integer.MAX_VALUE) {
      return (int) ((LongMath.sqrt(72 * exp - 54215, RoundingMode.DOWN) + 325) / 18);
    }
    if (exp > 1395) {
      return (int) ((Math.sqrt(72 * exp - 54215) + 325) / 18);
    }
    if (exp > 315) {
      return (int) (Math.sqrt(40 * exp - 7839) / 10 + 8.1);
    }
    if (exp > 0) {
      return (int) (Math.sqrt(exp + 9) - 3);
    }
    return 0;
  }

  /**
   * Gets the total amount of XP a player has, calculating it from their level and XP bar. Note that the player object has the field
   * {@link EntityPlayer#experienceTotal} that should already contain this number. There's just one issue with that---Minecraft uses level+exp more often than
   * total and there's a math error. For example, adding 91 XP points gives 91 points in experienceTotal (that's exactly 7 levels), but 6 levels and 99.999994%
   * on levels+exp. The HUD will then display 6 levels and a full bar and the enchanting table will read 6 levels.
   * 
   * @param player
   *          the player
   * @return The total amount of experience (from both their level and their xp bar) the given player has
   * @throws ArithmeticException
   *           if the total experience of the player would overflow an int (very unexpected)
   */
  public static int getPlayerXP(@Nonnull EntityPlayer player) throws TooManyXPLevelsException {
    try {
      return player.capabilities.isCreativeMode ? Integer.MAX_VALUE / 2
          : Math.addExact(getExperienceForLevel(player.experienceLevel), (int) (player.experience * player.xpBarCap()));
    } catch (ArithmeticException e) {
      throw new TooManyXPLevelsException();
    }
  }

  public static long getPlayerXPL(@Nonnull EntityPlayer player) {
    return player.capabilities.isCreativeMode ? Integer.MAX_VALUE / 2
        : Math.addExact(getExperienceForLevelL(player.experienceLevel), (long) (player.experience * player.xpBarCap()));
  }

  /**
   * Adds the given experience to the player, throwing an exception if the total experience of the player would overflow an {@code int}. In this case the player
   * will not be changed.
   *
   * @param player
   *          the player
   * @param amount
   *          the amount to add
   * @throws ArithmeticException
   *           if the total experience of the player would overflow an int
   */
  public static void addPlayerXP(@Nonnull EntityPlayer player, int amount) throws TooManyXPLevelsException {
    try {
      int experience = Math.max(0, Math.addExact(getPlayerXP(player), amount));
      player.experienceTotal = experience;
      player.experienceLevel = getLevelForExperience(experience);
      int expForLevel = getExperienceForLevel(player.experienceLevel);
      player.experience = (float) (experience - expForLevel) / (float) getXpBarCapacity(player.experienceLevel);
    } catch (ArithmeticException e) {
      throw new TooManyXPLevelsException();
    }
  }

  public static void addPlayerXP(@Nonnull EntityPlayer player, long amount) throws TooManyXPLevelsException {
    try {
      long experience = Math.max(0, Math.addExact(getPlayerXPL(player), amount));
      player.experienceTotal = MathUtil.limit(experience);
      player.experienceLevel = getLevelForExperience(experience);
      long expForLevel = getExperienceForLevelL(player.experienceLevel);
      player.experience = (float) (experience - expForLevel) / (float) getXpBarCapacity(player.experienceLevel);
    } catch (ArithmeticException e) {
      throw new TooManyXPLevelsException();
    }
  }

  public static class TooManyXPLevelsException extends Exception {
    private static final long serialVersionUID = -3819421185545900261L;
  }

}
