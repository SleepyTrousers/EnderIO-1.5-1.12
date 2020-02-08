package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringUtils;

import crazypants.enderio.EnderIO;

public enum Alloy {

  ELECTRICAL_STEEL("electricalSteel", 6.0f),
  ENERGETIC_ALLOY("energeticAlloy", 7.0f),
  PHASED_GOLD("phasedGold", 4.0f, "vibrantAlloy"),
  REDSTONE_ALLOY("redstoneAlloy", 1.0f),
  CONDUCTIVE_IRON("conductiveIron", 5.2f),
  PHASED_IRON("phasedIron", 7.0f, "pulsatingIron"),
  DARK_STEEL("darkSteel", 10.0f),
  SOULARIUM("soularium", 10.0f),
  END_STEEL("endSteel", 10.0f);


  public final String unlocalisedName;
  public final String iconKey;
  private final List<String> oreIngots = new ArrayList<String>();
  private final List<String> oreBlocks = new ArrayList<String>();
  private final float hardness;

  private Alloy(String baseName, float hardness, String oreDictName) {
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

  private Alloy(String baseName, float hardness) {
    this(baseName, hardness, null);
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

  public ItemStack getStackBall(int size) {
		return new ItemStack(EnderIO.itemGrindingBall, size, ordinal());
  }

  public ItemStack getStackBlock() {
    return getStackBlock(1);
  }

  public ItemStack getStackBlock(int size) {
    return new ItemStack(EnderIO.blockIngotStorage, size, ordinal());
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
