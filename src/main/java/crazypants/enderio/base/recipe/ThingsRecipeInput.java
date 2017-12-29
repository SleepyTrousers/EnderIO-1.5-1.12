package crazypants.enderio.base.recipe;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ThingsRecipeInput implements IRecipeInput {

  private final @Nonnull Things things;
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
    this.leadStack = things.getItemStacks().get(0).copy();
    this.slot = slot;
    this.multiplier = multiplier;
  }

  @Override
  @Nonnull
  public ThingsRecipeInput copy() {
    return new ThingsRecipeInput(things, slot, multiplier);
  }

  @Override
  public boolean isFluid() {
    return false;
  }

  @Override
  @Nonnull
  public ItemStack getInput() {
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
    return things.getItemStacks().toArray(new ItemStack[0]);
  }

  @Override
  public boolean isValid() {
    return !things.isEmpty();
  }

}
