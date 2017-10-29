package crazypants.enderio.farming;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.config.Config;
import crazypants.util.Prep;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;

public enum FarmingTool {
  HAND,
  HOE(Config.farmHoes),
  AXE {
    @Override
    protected boolean match(ItemStack item) {
      return item.getItem().getHarvestLevel(item, "axe", null, null) >= 0;
    }
  },
  TREETAP,
  SHEARS {
    @Override
    protected boolean match(ItemStack item) {
      return item.getItem() instanceof ItemShears;
    }
  },
  NONE {
    @Override
    protected boolean match(ItemStack item) {
      return false;
    }
  };
  
  private final Things things;

  private FarmingTool(String... things) {
    this(new Things());
    for (String s : things) {
      this.things.add(s);
    }
  }
  
  private FarmingTool(Things things) {
    this.things = things;
  }

  public final boolean itemMatches(@Nonnull ItemStack item) {
    return Prep.isValid(item) && match(item) && !isBrokenTinkerTool(item);
  }
  
  public final Things getThings() {
    return things;
  }

  @SuppressWarnings("null")
  private static boolean isBrokenTinkerTool(@Nonnull ItemStack item) {
    return Prep.isValid(item) && item.hasTagCompound() && item.getTagCompound().hasKey("Stats")
        && item.getTagCompound().getCompoundTag("Stats").getBoolean("Broken");
  }

  protected boolean match(@Nonnull ItemStack item) {
    return things.contains(item);
  }

  public static boolean isTool(@Nonnull ItemStack stack) {
    for (FarmingTool type : values()) {
      if (type.itemMatches(stack)) {
        return true;
      }
    }
    return false;
  }

  public static FarmingTool getToolType(@Nonnull ItemStack stack) {
    for (FarmingTool type : values()) {
      if (type.itemMatches(stack)) {
        return type;
      }
    }
    return NONE;
  }
}
