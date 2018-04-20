package crazypants.enderio.base.conduit.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;

public enum FunctionUpgrade {

  INVENTORY_PANEL("inventory_panel_upgrade", "item.item_inventory_panel_upgrade", 1),
  EXTRACT_SPEED_UPGRADE("extract_speed_upgrade", "item.item_extract_speed_upgrade", 15) {
    @Override
    public int getMaximumExtracted(int stackSize) {
      return BASE_MAX_EXTRACTED + Math.min(stackSize, maxStackSize) * 4;
    }
  },
  EXTRACT_SPEED_DOWNGRADE("extract_speed_downgrade", "item.item_extract_speed_downgrade", 1) {
    @Override
    public int getMaximumExtracted(int stackSize) {
      return 1;
    }
  };

  public static final int BASE_MAX_EXTRACTED = 4;
  public static final int LIQUID_MAX_EXTRACTED_SCALER = 2;

  public final @Nonnull String baseName;
  public final @Nonnull String iconName;
  public final @Nonnull String unlocName;
  public final int maxStackSize;

  public static List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for (FunctionUpgrade c : values()) {
      res.add(new ResourceLocation(c.iconName));
    }
    return res;
  }

  private FunctionUpgrade(@Nonnull String name, @Nonnull String unlocName, int maxStackSize) {
    this.baseName = name;
    this.iconName = "enderio:" + name;
    this.unlocName = unlocName;
    this.maxStackSize = maxStackSize;
  }

  public int getMaximumExtracted(int stackSize) {
    return BASE_MAX_EXTRACTED;
  }
}
