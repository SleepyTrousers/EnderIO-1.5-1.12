package crazypants.enderio.capability;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.Filters.PredicateItemStack;
import com.google.common.base.Predicate;

import crazypants.enderio.capacitor.CapacitorHelper;
import crazypants.enderio.machine.sagmill.SagMillRecipeManager;
import crazypants.enderio.paint.IPaintable;
import crazypants.util.CapturedMob;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import static com.enderio.core.common.inventory.Filters.and;
import static com.enderio.core.common.inventory.Filters.not;
import static crazypants.enderio.ModObject.itemBasicCapacitor;
import static crazypants.enderio.ModObject.itemSoulVessel;

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

}
