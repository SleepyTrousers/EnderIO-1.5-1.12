package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

public enum SpeedUpgrade {

  UPGRADE("extractSpeedUpgrade", "item.itemExtractSpeedUpgrade", 15) {
    @Override
    public int getMaximumExtracted(int stackSize) {
      return BASE_MAX_EXTRACTED + Math.min(stackSize, maxStackSize) * 4;
    }
  },
  DOWNGRADE("extractSpeedDowngrade", "item.itemExtractSpeedDowngrade", 1) {
    @Override
    public int getMaximumExtracted(int stackSize) {
      return 1;
    }
  };

  public static List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for(SpeedUpgrade c : values()) {
      res.add(new ResourceLocation(c.iconName));
    }
    return res;
  }
  
  public static final int BASE_MAX_EXTRACTED = 4;

  public final String baseName;
  public final String iconName;
  public final String unlocName;
  public final int maxStackSize;

  private SpeedUpgrade(String iconName, String unlocName, int maxStackSize) {
    baseName= iconName;
    this.iconName = "enderio:" + iconName;
    this.unlocName = unlocName;
    this.maxStackSize = maxStackSize;
  }

  public abstract int getMaximumExtracted(int stackSize);

}
