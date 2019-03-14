package crazypants.enderio.base.capability;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.Filters.PredicateItemStack;
import com.google.common.base.Predicate;

import crazypants.enderio.base.capacitor.CapacitorHelper;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import static com.enderio.core.common.inventory.Filters.and;
import static com.enderio.core.common.inventory.Filters.not;
import static crazypants.enderio.base.init.ModObject.itemBasicCapacitor;
import static crazypants.enderio.base.init.ModObject.itemSoulVial;

public class Filters {

  public static final @Nonnull Predicate<ItemStack> PAINTABLE = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return Block.getBlockFromItem(input.getItem()) instanceof IPaintable;
    }
  };

  public static final @Nonnull Predicate<ItemStack> SOUL_VIALS = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return input.getItem() == itemSoulVial.getItem();
    }
  };

  public static final @Nonnull Predicate<ItemStack> WITH_MOB_SOUL = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return CapturedMob.containsSoul(input);
    }
  };

  public static final @Nonnull Predicate<ItemStack> FILLED_SOUL_VIALS = and(SOUL_VIALS, WITH_MOB_SOUL);

  public static final @Nonnull Predicate<ItemStack> EMPTY_SOUL_VIALS = and(SOUL_VIALS, not(WITH_MOB_SOUL));

  public static final @Nonnull Predicate<ItemStack> GRINDING_BALLS = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return SagMillRecipeManager.getInstance().isValidSagBall(input);
    }
  };

  public static final @Nonnull Predicate<ItemStack> CAPACITORS = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return input.getItem() == itemBasicCapacitor.getItem() || CapacitorHelper.getCapacitorDataFromItemStack(input) != null;
    }
  };

}
