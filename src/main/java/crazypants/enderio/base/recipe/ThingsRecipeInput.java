package crazypants.enderio.base.recipe;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ThingsRecipeInput implements IRecipeInput {

  private final @Nonnull Things things;
  /**
   * A stack to represent this input in situations where a single stack is needed. It also holds the stackSize for the input. Callers may modify this stack's
   * size to keep track of things (obviously they need to copy this object first).
   */
  private final @Nonnull ItemStack leadStack;
  private final int slot;
  private final float multiplier;

  public ThingsRecipeInput(@Nonnull Things things) {
    this(things, -1);
  }

  public ThingsRecipeInput(@Nonnull Things things, int slot) {
    this(things, slot, 1f);
  }

  public ThingsRecipeInput(@Nonnull Things things, int slot, float multiplier) {
    this.things = things;
    final NNList<ItemStack> stacks = things.getItemStacks();
    this.leadStack = stacks.isEmpty() ? Prep.getEmpty() : stacks.get(0).copy();
    this.slot = slot;
    this.multiplier = multiplier;
  }

  public @Nonnull ThingsRecipeInput setCount(int count) {
    leadStack.setCount(count);
    return this;
  }

  @Override
  public @Nonnull ThingsRecipeInput copy() {
    return new ThingsRecipeInput(things, slot, multiplier);
  }

  @Override
  public boolean isFluid() {
    return false;
  }

  @Override
  public @Nonnull ItemStack getInput() {
    return leadStack;
  }

  @Override
  public FluidStack getFluidInput() {
    return null;
  }

  @Override
  public float getMulitplier() {
    return multiplier; // used by Vat recipes only
  }

  @Override
  public int getSlotNumber() {
    return slot;
  }

  @Override
  public boolean isInput(@Nonnull ItemStack test) {
    return things.contains(test);
  }

  @Override
  public boolean isInput(FluidStack test) {
    return false;
  }

  @Override
  public ItemStack[] getEquivelentInputs() {
    return things.getItemStacksRaw().toArray(new ItemStack[0]);
  }

  @Override
  public boolean isValid() {
    return Prep.isValid(leadStack);
  }

}
