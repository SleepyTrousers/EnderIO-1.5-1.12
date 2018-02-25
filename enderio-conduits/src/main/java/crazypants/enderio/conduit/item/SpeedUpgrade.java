package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

public enum SpeedUpgrade {

  UPGRADE("extract_speed_upgrade", "item.item_extract_speed_upgrade", 15) {
    @Override
    public int getMaximumExtracted(int stackSize) {
      return BASE_MAX_EXTRACTED + Math.min(stackSize, maxStackSize) * 4;
    }
  },
  DOWNGRADE("extract_speed_downgrade", "item.item_extract_speed_downgrade", 1) {
    @Override
    public int getMaximumExtracted(int stackSize) {
      return 1;
    }
  };

  public static List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for (SpeedUpgrade c : values()) {
      res.add(new ResourceLocation(c.iconName));
    }
    return res;
  }

  public static final int BASE_MAX_EXTRACTED = 4;

  public final String baseName;
  public final String iconName;
  public final String unlocName;
  public final int maxStackSize;

  private SpeedUpgrade(String name, String unlocName, int maxStackSize) {
    this.baseName = name;
    this.iconName = "enderio:" + name;
    this.unlocName = unlocName;
    this.maxStackSize = maxStackSize;
  }

  public abstract int getMaximumExtracted(int stackSize);

}
