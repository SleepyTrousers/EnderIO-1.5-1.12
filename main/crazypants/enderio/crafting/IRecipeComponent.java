package crazypants.enderio.crafting;

import net.minecraft.item.ItemStack;

public abstract interface IRecipeComponent {

  /**
   * Returns a prototype of the ItemStack as required by the recipe. The
   * returned stack must be a valid component of the recipe, but it may not
   * reflect all possible valid values. For example, if a recipe input accepts
   * multiple metadata or NBT values, this stack will only be one of the
   * possible valid inputs. To test if a particular instance of an item stack
   * may be used the isEquivalent method should be called.
   * 
   * @return
   */
  ItemStack getItem();

  /**
   * Equivalent to getItem().stacksize
   * 
   * @return
   */
  int getQuantity();

  /**
   * Returns the slot this component must be placed in. If any slot can be used
   * the method return -1.
   * 
   * @return
   */
  int getSlot();

  boolean isValidSlot(int slot);

  boolean isEquivalent(ItemStack candidate);

}
