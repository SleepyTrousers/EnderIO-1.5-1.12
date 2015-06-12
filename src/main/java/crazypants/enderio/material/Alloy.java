package crazypants.enderio.material;

import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringUtils;

import crazypants.enderio.EnderIO;

public enum Alloy {

  ELECTRICAL_STEEL("electricalSteel", 6.0f),
  ENERGETIC_ALLOY("energeticAlloy", 7.0f),
  PHASED_GOLD("phasedGold", 4.0f),
  REDSTONE_ALLOY("redstoneAlloy", 1.0f),
  CONDUCTIVE_IRON("conductiveIron", 5.2f),
  PHASED_IRON("phasedIron", 7.0f),
  DARK_STEEL("darkSteel", 10.0f),
  SOULARIUM("soularium", 10.0f);

  public final String unlocalisedName;
  public final String iconKey;
  public final String oreIngot;
  public final String oreBlock;
  private final float hardness;

  private Alloy(String baseName, float hardness) {
    this.unlocalisedName = "enderio." + baseName;
    this.iconKey = "enderio:" + baseName;
    this.oreIngot = "ingot" + StringUtils.capitalize(baseName);
    this.oreBlock = "block" + StringUtils.capitalize(baseName);
    this.hardness = hardness;
  }

  public float getHardness() {
    return hardness;
  }

  public ItemStack getStackIngot() {
    return getStackIngot(1);
  }

  public ItemStack getStackIngot(int size) {
    return new ItemStack(EnderIO.itemAlloy, size, ordinal());
  }

  public ItemStack getStackBlock() {
    return getStackIngot(1);
  }

  public ItemStack getStackBlock(int size) {
    return new ItemStack(EnderIO.blockIngotStorage, size, ordinal());
  }
}
