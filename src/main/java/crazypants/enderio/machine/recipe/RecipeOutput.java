package crazypants.enderio.machine.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeOutput {

  private final @Nullable FluidStack outputFluid;
  private final @Nonnull ItemStack output;
  private final float chance;
  private final float exp;

  public RecipeOutput(@Nonnull Block output) {
    this(new ItemStack(output), 1);
  }

  public RecipeOutput(@Nonnull Block output, float chance) {
    this(new ItemStack(output), chance);
  }

  public RecipeOutput(@Nonnull Item output, float chance) {
    this(new ItemStack(output), chance);
  }

  public RecipeOutput(@Nonnull Item output) {
    this(new ItemStack(output), 1);
  }

  public RecipeOutput(@Nonnull ItemStack output) {
    this(output, 1);
  }

  public RecipeOutput(@Nonnull ItemStack output, float chance) {
    this(output, chance, 0);
  }

  public RecipeOutput(@Nonnull ItemStack output, float chance, float exp) {
    this.output = output.copy();
    this.chance = chance;
    this.exp = exp;
    outputFluid = null;
  }

  public RecipeOutput(@Nonnull FluidStack output) {
    this.outputFluid = output;
    this.output = Prep.getEmpty();
    this.chance = 1f;
    this.exp = 0;
  }

  public float getChance() {
    return chance;
  }

  public float getExperiance() {
    return exp;
  }

  public @Nonnull ItemStack getOutput() {
    return output;
  }

  public @Nullable FluidStack getFluidOutput() {
    return outputFluid;
  }

  public boolean isFluid() {
    return outputFluid != null;
  }

  public boolean isValid() {
    return Prep.isValid(output) || (outputFluid != null && outputFluid.getFluid() != null);
  }

}
