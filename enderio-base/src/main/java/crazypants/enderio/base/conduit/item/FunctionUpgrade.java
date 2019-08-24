package crazypants.enderio.base.conduit.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public enum FunctionUpgrade {

  INVENTORY_PANEL("inventory_panel_upgrade", "item.item_inventory_panel_upgrade", 1),
  EXTRACT_SPEED_UPGRADE("extract_speed_upgrade", "item.item_extract_speed_upgrade", 15) {
    @Override
    public int getMaximumExtracted(int stackSize) {
      return BASE_MAX_EXTRACTED + Math.min(stackSize, getMaxStackSize()) * 4;
    }

    @Override
    public float getFluidSpeedMultiplier(int stackSize) {
      return 1 + Math.min(getMaxStackSize(), stackSize);
    }
  },
  EXTRACT_SPEED_DOWNGRADE("extract_speed_downgrade", "item.item_extract_speed_downgrade", 3) {
    @Override
    public int getMaximumExtracted(int stackSize) {
      return stackSize;
    }

    @Override
    public float getFluidSpeedMultiplier(int stackSize) {
      return .25f * stackSize;
    }
  },

  RS_CRAFTING_UPGRADE("rs_crafting_upgrade", "item.item_rs_crafting_upgrade", 1),
  RS_CRAFTING_SPEED_UPGRADE("rs_crafting_upgrade", "item.item_rs_crafting_upgrade", 15) {
    @Override
    public int getMaximumExtracted(int stackSize) {
      return BASE_MAX_EXTRACTED + Math.min(stackSize, getMaxStackSize()) * 4;
    }
  },
  RS_CRAFTING_SPEED_DOWNGRADE("rs_crafting_speed_downgrade", "item.item_rs_crafting_speed_downgrade", 1) {
    @Override
    public int getMaximumExtracted(int stackSize) {
      return 1;
    }
  },

  ;

  public static final int BASE_MAX_EXTRACTED = 4;

  public final @Nonnull String baseName;
  public final @Nonnull String iconName;
  public final @Nonnull String unlocName;
  private final int maxStackSize;

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

  public static int getMaximumExtracted(@Nullable FunctionUpgrade upgrade, int stackSize) {
    return upgrade == null ? BASE_MAX_EXTRACTED : upgrade.getMaximumExtracted(stackSize);
  }

  public static int getMaximumExtracted(@Nonnull ItemStack upgradeStack) {
    FunctionUpgrade upgrade = ItemFunctionUpgrade.getFunctionUpgrade(upgradeStack);
    return upgrade == null ? BASE_MAX_EXTRACTED : upgrade.getMaximumExtracted(upgradeStack.getCount());
  }

  /**
   * @return Maximum stack size allowed in the upgrade slot. Has no effect on the stack size in normal inventories.
   */
  public int getMaxStackSize() {
    return maxStackSize;
  }

  /**
   * @param stackSize
   *          size of the stack of upgrades
   * @return Multiplier of the base speed for fluid conduits.
   */
  public float getFluidSpeedMultiplier(int stackSize) {
    return 0;
  }

}
