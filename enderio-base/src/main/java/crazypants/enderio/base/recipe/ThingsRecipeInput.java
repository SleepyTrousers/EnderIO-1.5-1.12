package crazypants.enderio.base.recipe;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * A RecipeInput that is capable of handling Things and stack sized and nbt.
 * 
 * Please note that this input can become suddenly invalid after "load complete" as OreDict Things wait until then to decide if an Oredict name actually exists.
 * If queried for its input item before "load complete" it may produce an empty stack.
 * 
 * @author Henry Loenwind
 *
 */
public class ThingsRecipeInput implements IRecipeInput {

  private final @Nonnull Things things;
  private final int slot;
  private final float multiplier;
  /**
   * Callers may modify this stackSize to keep track of things (obviously they need to copy this object first).
   */
  private int stackSize;

  public ThingsRecipeInput(@Nonnull Things things) {
    this(things, -1);
  }

  public ThingsRecipeInput(@Nonnull Things things, int slot) {
    this(things, slot, 1f);
  }

  public ThingsRecipeInput(@Nonnull Things things, int slot, float multiplier) {
    this(things, 1, slot, multiplier);
  }

  public ThingsRecipeInput(@Nonnull Things things, int stackSize, int slot, float multiplier) {
    this.things = things;
    this.stackSize = stackSize;
    this.slot = slot;
    this.multiplier = multiplier;
  }

  public @Nonnull ThingsRecipeInput setCount(int count) {
    stackSize = count;
    return this;
  }

  @Override
  public @Nonnull ThingsRecipeInput copy() {
    return new ThingsRecipeInput(things, stackSize, slot, multiplier);
  }

  @Override
  public boolean isFluid() {
    return false;
  }

  @Override
  public @Nonnull ItemStack getInput() {
    ItemStack itemStack = things.getItemStack().copy();
    itemStack.setCount(stackSize);
    return itemStack;
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
    final ItemStack[] result = things.getItemStacksRaw().toArray(new ItemStack[0]);
    for (int i = 0; i < result.length; i++) {
      result[i] = result[i].copy();
      result[i].setCount(stackSize);
    }
    return result;
  }

  @Override
  public boolean isValid() {
    return things.isPotentiallyValid();
  }

  @Override
  public void shrinkStack(int count) {
    stackSize -= count;
  }

}
