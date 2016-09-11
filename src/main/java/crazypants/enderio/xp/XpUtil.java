package crazypants.enderio.xp;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Values taken from OpenMods EnchantmentUtils to ensure consistent behavior
 * @see {@link https://github.com/OpenMods/OpenModsLib/blob/master/src/main/java/openmods/utils/EnchantmentUtils.java}
 *
 */
public class XpUtil {

  //Values taken from OpenBlocks to ensure compatibility
  
  public static final int XP_PER_BOTTLE = 8;
  public static final int RATIO = 20;
  public static final int LIQUID_PER_XP_BOTTLE = XP_PER_BOTTLE * RATIO;
  
  
  public static int liquidToExperience(int liquid) {
    return liquid / RATIO;
  }

  public static int experienceToLiquid(int xp) {
    return xp * RATIO;
  }

  public static int getLiquidForLevel(int level) {
    return experienceToLiquid(getExperienceForLevel(level));
  }
  
  public static int getExperienceForLevel(int level) {
    if (level == 0) { return 0; }
    if (level > 0 && level < 16) {
      return level * 17;
    } else if (level > 15 && level < 31) {
      return (int)(1.5 * Math.pow(level, 2) - 29.5 * level + 360);
    } else {
      return (int)(3.5 * Math.pow(level, 2) - 151.5 * level + 2220);
    }
  }
  
  public static int getXpBarCapacity(int level) {
    return level >= 30 ? 62 + (level - 30) * 7 : (level >= 15 ? 17 + (level - 15) * 3 : 17);
  }
  
  public static int getLevelForExperience(int experience) {
    int i = 0;
    while (getExperienceForLevel(i) <= experience) {
      i++;
    }
    return i - 1;
  }

  public static int getPlayerXP(EntityPlayer player) {
    return (int) (getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
  }
  
  public static void addPlayerXP(EntityPlayer player, int amount) {
    int experience = getPlayerXP(player) + amount;
    player.experienceTotal = experience;
    player.experienceLevel = getLevelForExperience(experience);
    int expForLevel = getExperienceForLevel(player.experienceLevel);
    player.experience = (float)(experience - expForLevel) / (float)player.xpBarCap();
  }
  
  
  
}
