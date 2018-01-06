package crazypants.enderio.base.recipe;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * A MachineRecipe implementation must be stateless, always returning the same
 * values given the same parameters. The 'in progress' recipe for a machine is
 * stored to and retrieved from a machines TileEntity soley based on its UID.
 * 
 * @author cp
 * 
 */
public interface IMachineRecipe {

  /**
   * Returns the globally unique ID for the recipe.
   * 
   * @return
   */
  @Nonnull
  String getUid();

  /**
   * The amount total number of MJ required to craft the recipe based on the
   * inputs. The amount returned should be for a single 'cycle' of the recipe.
   * For examples, if the inputs would allow the recipe to completed 10 times,
   * this method should only return the amount of energy used for a single
   * cycle.
   * 
   * @param inputs
   * @return
   */
  int getEnergyRequired(@Nonnull MachineRecipeInput... inputs);

  /**
   * Returns the how bonus should be handled for this input
   **/
  default @Nonnull RecipeBonusType getBonusType(@Nonnull MachineRecipeInput... inputs) {
    return RecipeBonusType.NONE;
  }

  /**
   * Only returns true if output can be generated using these inputs. If
   * partially complete inputs are provided (for example only one of two
   * required inputs are present) the method should return false.
   * 
   * @param inputs
   * @return
   */
  public boolean isRecipe(@Nonnull MachineRecipeInput... inputs);

  /**
   * Returns the output from a single 'cycle' of the recipe (even if the inputs
   * would allow the recipe to be crafted several times).
   * 
   * This method must not return null. If no output is generated an empty array
   * should be returned.
   * 
   * @param randomChance
   *          a random number to be used as a seed for determining whether '%
   *          chance' outputs are returned. This value is in the range 0-1.
   * @param inputs
   * @return
   */
  @Nonnull
  ResultStack[] getCompletedResult(float randomChance, @Nonnull MachineRecipeInput... inputs);

  /**
   * Returns the experience a user gains when this recipe has generated the
   * specified output. If the output is the result of several recipe cycles, the
   * accumulated total of experience gained for all cycles should be returned.
   * 
   * @param output
   * @return
   */
  default float getExperienceForOutput(@Nonnull ItemStack output) {
    return 0;
  }

  /**
   * Should return true if the specified parameter is can be used in this
   * recipe.
   * 
   * @param input
   * @return
   */
  boolean isValidInput(@Nonnull MachineRecipeInput input);

  /**
   * The name of the machine this recipe can be crafted by.
   * 
   * @return
   */
  @Nonnull
  String getMachineName();

  /**
   * Returns the quantity of inputs consumed by a single cycle of the recipe.
   * 
   * @param inputs
   * @return
   */
  @Nonnull
  List<MachineRecipeInput> getQuantitiesConsumed(@Nonnull MachineRecipeInput... inputs);

  public static class ResultStack {

    public final @Nonnull ItemStack item;
    public final @Nullable FluidStack fluid;

    public ResultStack(@Nonnull ItemStack item, @Nullable FluidStack fluid) {
      this.item = item;
      this.fluid = fluid;
    }

    public ResultStack(@Nonnull ItemStack item) {
      this.item = item;
      this.fluid = null;
    }

    public ResultStack(@Nonnull FluidStack fluid) {
      this.item = Prep.getEmpty();
      this.fluid = fluid;
    }

  }

}
