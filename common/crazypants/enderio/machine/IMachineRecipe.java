package crazypants.enderio.machine;

import java.util.List;

import net.minecraft.item.ItemStack;
import crazypants.enderio.crafting.IEnderIoRecipe;

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
  float getEnergyRequired(MachineRecipeInput... inputs);

  /**
   * Only returns true if output can be generated using these inputs. If
   * partially complete inputs are provided (for example only one of two
   * required inputs are present) the method should return false.
   * 
   * @param inputs
   * @return
   */
  public boolean isRecipe(MachineRecipeInput... inputs);

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
  ItemStack[] getCompletedResult(float randomChance, MachineRecipeInput... inputs);

  /**
   * Returns the experience a user gains when this recipe has generated the
   * specified output. If the output is the result of several recipe cycles, the
   * accumulated total of experience gained for all cycles should be returned.
   * 
   * @param output
   * @return
   */
  float getExperianceForOutput(ItemStack output);

  /**
   * Should return true if the specified parameter is can be used in this
   * recipe.
   * 
   * @param input
   * @return
   */
  boolean isValidInput(MachineRecipeInput input);

  /**
   * The name of the machine this recipe can be crafted by.
   * 
   * @return
   */
  String getMachineName();

  /**
   * Returns the quantity of inputs consumed by a single cycle of the recipe.
   * 
   * @param inputs
   * @return
   */
  MachineRecipeInput[] getQuantitiesConsumed(MachineRecipeInput[] inputs);

  /**
   * Returns all possible outputs from the recipe.
   * 
   * @return
   */
  List<IEnderIoRecipe> getAllRecipes();

}
