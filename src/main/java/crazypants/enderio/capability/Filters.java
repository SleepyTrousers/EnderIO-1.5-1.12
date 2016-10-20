package crazypants.enderio.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import crazypants.enderio.machine.sagmill.SagMillRecipeManager;
import crazypants.enderio.paint.IPaintable;
import crazypants.util.CapturedMob;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.ModObject.itemSoulVessel;

public class Filters {

  public static final Callback<ItemStack> NO_CALLBACK = new Callback<ItemStack>() {
    @Override
    public final void onChange(@Nullable ItemStack oldStack, @Nullable ItemStack newStack) {
    }
  };

  public static final Predicate<ItemStack> ALWAYS_TRUE = Predicates.<ItemStack> alwaysTrue();

  public static final Predicate<ItemStack> ALWAYS_FALSE = Predicates.<ItemStack> alwaysFalse();

  public static final Predicate<ItemStack> ONLY_STACKABLE = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return input.isStackable();
    }
  };

  public static final Predicate<ItemStack> ONLY_PAINTABLE = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return Block.getBlockFromItem(input.getItem()) instanceof IPaintable;
    }
  };

  public static final Predicate<ItemStack> ONLY_SOUL_VIALS = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return input.getItem() == itemSoulVessel.getItem();
    }
  };

  public static final Predicate<ItemStack> ONLY_FILLED_SOUL_VIALS = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return input.getItem() == itemSoulVessel.getItem() && CapturedMob.containsSoul(input);
    }
  };

  public static final Predicate<ItemStack> ONLY_GRINDING_BALLS = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return SagMillRecipeManager.getInstance().isValidSagBall(input);
    }
  };

  // ///////////////////////////////////////////////////////////////////

  private Filters() {
  }

  public static abstract class PredicateItemStack implements Predicate<ItemStack> {

    @Override
    public int hashCode() {
      return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      return super.equals(obj);
    }

    @Override
    public boolean apply(@Nullable ItemStack input) {
      return input != null && doApply(input);
    }

    public abstract boolean doApply(@Nonnull ItemStack input);

  }

}
