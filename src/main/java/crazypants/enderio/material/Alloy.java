package crazypants.enderio.material;

import org.apache.commons.lang3.StringUtils;

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
  public final String oredictIngotName;
  public final String oredictBlockName;
  private final float hardness;

  private Alloy(String baseName, float hardness) {
    this.unlocalisedName = "enderio." + baseName;
    this.iconKey = "enderio:" + baseName;
    this.oredictIngotName = "ingot" + StringUtils.capitalize(baseName);
    this.oredictBlockName = "block" + StringUtils.capitalize(baseName);
    this.hardness = hardness;
  }

  public float getHardness() {
    return hardness;
  }
}
