package crazypants.enderio.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import crazypants.enderio.capacitor.CapacitorHelper;
import crazypants.enderio.machine.sagmill.SagMillRecipeManager;
import crazypants.enderio.paint.IPaintable;
import crazypants.util.CapturedMob;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.ModObject.itemBasicCapacitor;
import static crazypants.enderio.ModObject.itemSoulVessel;

//TODO 1.11 - use ec version for some of these
public class Filters {

  public static final @Nonnull Callback<ItemStack> NO_CALLBACK = new Callback<ItemStack>() {
    @Override
    public final void onChange(@Nullable ItemStack oldStack, @Nullable ItemStack newStack) {
    }
  };

  @SuppressWarnings("null")
  public static final @Nonnull Predicate<ItemStack> ALWAYS_TRUE = Predicates.<ItemStack> alwaysTrue();

  @SuppressWarnings("null")
  public static final @Nonnull Predicate<ItemStack> ALWAYS_FALSE = Predicates.<ItemStack> alwaysFalse();

  public static final @Nonnull Predicate<ItemStack> ONLY_STACKABLE = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return input.isStackable();
    }
  };

  public static final @Nonnull Predicate<ItemStack> PAINTABLE = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return Block.getBlockFromItem(input.getItem()) instanceof IPaintable;
    }
  };

  public static final @Nonnull Predicate<ItemStack> SOUL_VIALS = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return input.getItem() == itemSoulVessel.getItem();
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
      return (input.getItem() == itemBasicCapacitor.getItem() && input.getItemDamage() > 0) || CapacitorHelper.getCapacitorDataFromItemStack(input) != null;
    }
  };

  public static @Nonnull Predicate<ItemStack> and(final @Nonnull Predicate<ItemStack> a, final @Nonnull Predicate<ItemStack> b) {
    return new PredicateItemStack() {
      @Override
      public boolean doApply(@Nonnull ItemStack input) {
        return a.apply(input) && b.apply(input);
      }
    };
  }

  public static @Nonnull Predicate<ItemStack> or(final @Nonnull Predicate<ItemStack> a, final @Nonnull Predicate<ItemStack> b) {
    return new PredicateItemStack() {
      @Override
      public boolean doApply(@Nonnull ItemStack input) {
        return a.apply(input) || b.apply(input);
      }
    };
  }

  public static @Nonnull Predicate<ItemStack> not(final @Nonnull Predicate<ItemStack> a) {
    return new PredicateItemStack() {
      @Override
      public boolean doApply(@Nonnull ItemStack input) {
        return !a.apply(input);
      }
    };
  }

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
      return Prep.isValid(input) && doApply(input);
    }

    public abstract boolean doApply(@Nonnull ItemStack input);

  }

}
