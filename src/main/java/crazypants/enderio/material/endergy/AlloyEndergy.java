package crazypants.enderio.material.endergy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringUtils;

import crazypants.enderio.EnderIO;

public enum AlloyEndergy {

  CRUDE_STEEL("crudeSteel", 2.0f),
  CRYSTALLINE_ALLOY("crystallineAlloy", 6.0f),
  MELODIC_ALLOY("melodicAlloy", 5.0f),
  STELLAR_ALLOY("stellarAlloy", 10.0f),
  CRYSTALLINE_PINK_SLIME("crystallinePinkSlime", 7.0f),
  ENERGETIC_SILVER("energeticSilver", 7.0f),
  VIVID_ALLOY("vividAlloy", 4.0f);


  public final String unlocalisedName;
  public final String iconKey;
  private final List<String> oreIngots = new ArrayList<String>();
  private final List<String> oreBlocks = new ArrayList<String>();
  private final float hardness;

  private AlloyEndergy(String baseName, float hardness, String oreDictName) {
    this.unlocalisedName = "enderio." + baseName;
    this.iconKey = "enderio:" + baseName;
    if(oreDictName != null) {
      this.oreIngots.add("ingot" + StringUtils.capitalize(oreDictName));
      this.oreBlocks.add("block" + StringUtils.capitalize(oreDictName));
    }
    this.oreIngots.add("ingot" + StringUtils.capitalize(baseName));
    this.oreBlocks.add("block" + StringUtils.capitalize(baseName));
    this.hardness = hardness;
  }

  private AlloyEndergy(String baseName, float hardness) {
    this(baseName, hardness, null);
  }

  public float getHardness() {
    return hardness;
  }

  public ItemStack getStackIngot() {
    return getStackIngot(1);
  }

  public ItemStack getStackIngot(int size) {
    return new ItemStack(EnderIO.itemAlloyEndergy, size, ordinal());
  }

  public ItemStack getStackBall(int size) {
		return new ItemStack(EnderIO.itemGrindingBallEndergy, size, ordinal());
  }

  public ItemStack getStackBlock() {
    return getStackBlock(1);
  }

  public ItemStack getStackBlock(int size) {
    return new ItemStack(EnderIO.blockIngotStorageEndergy, size, ordinal());
  }

  public String getOreIngot() {
    return oreIngots.get(0);
  }

  public String getOreBlock() {
    return oreBlocks.get(0);
  }

  public List<String> getOreIngots() {
    return oreIngots;
  }

  public List<String> getOreBlocks() {
    return oreBlocks;
  }

}
