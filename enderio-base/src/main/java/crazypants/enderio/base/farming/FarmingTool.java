package crazypants.enderio.base.farming;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.OreDictionaryHelper;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.api.farm.IFarmingTool;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelTreetap;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public enum FarmingTool implements IFarmingTool {
  HAND,
  HOE {
    @Override
    protected boolean match(@Nonnull ItemStack item) {
      return item.getItem() instanceof ItemHoe || OreDictionaryHelper.hasName(item, "toolHoe");
    }
  },
  AXE {
    @Override
    protected boolean match(@Nonnull ItemStack item) {
      return item.getItem().getHarvestLevel(item, "axe", null, null) >= 0;
    }
  },
  TREETAP {
    @Override
    protected boolean match(@Nonnull ItemStack item) {
      return super.match(item) || item.getItem() instanceof ItemDarkSteelTreetap || OreDictionaryHelper.hasName(item, "toolTreetap");
    }
  },
  SHEARS {
    @Override
    protected boolean match(@Nonnull ItemStack item) {
      return item.getItem() instanceof ItemShears || OreDictionaryHelper.hasName(item, "toolShears");
    }
  },
  NONE {
    @Override
    protected boolean match(@Nonnull ItemStack item) {
      return false;
    }
  };

  private final @Nonnull Things things;

  private FarmingTool(String... things) {
    this(new Things());
    for (String s : things) {
      this.things.add(s);
    }
  }

  private FarmingTool(@Nonnull Things things) {
    this.things = things;
  }

  /*
   * (non-Javadoc)
   * 
   * @see crazypants.enderio.base.farming.IFarmingTool#itemMatches(net.minecraft.item.ItemStack)
   */
  @Override
  public final boolean itemMatches(@Nonnull ItemStack item) {
    return Prep.isValid(item) && match(item);
  }

  public final @Nonnull Things getThings() {
    return things;
  }

  protected boolean match(@Nonnull ItemStack item) {
    return things.contains(item);
  }

  public static boolean isTool(@Nonnull ItemStack stack) {
    for (IFarmingTool type : values()) {
      if (type.itemMatches(stack)) {
        return true;
      }
    }
    return false;
  }

  public static @Nonnull FarmingTool getToolType(@Nonnull ItemStack stack) {
    if (Prep.isValid(stack)) {
      for (FarmingTool type : values()) {
        if (type.itemMatches(stack)) {
          return type;
        }
      }
    }
    return NONE;
  }

  public static boolean isDryRfTool(@Nonnull ItemStack stack) {
    IEnergyStorage cap = PowerHandlerUtil.getCapability(stack, null);
    return cap != null && cap.getMaxEnergyStored() > 0 && cap.getEnergyStored() <= 0;
  }

  public static boolean canDamage(@Nonnull ItemStack stack) {
    return stack.isItemStackDamageable() && stack.getItem().isDamageable();
  }

  static {
    IFarmingTool.Tools.HAND = HAND;
    IFarmingTool.Tools.HOE = HOE;
    IFarmingTool.Tools.AXE = AXE;
    IFarmingTool.Tools.TREETAP = TREETAP;
    IFarmingTool.Tools.SHEARS = SHEARS;
    IFarmingTool.Tools.NONE = NONE;
  }

}
