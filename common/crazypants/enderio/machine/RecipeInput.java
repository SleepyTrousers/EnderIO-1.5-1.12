package crazypants.enderio.machine;

import net.minecraft.item.ItemStack;

public class RecipeInput {

  public static ItemStack getInputForSlot(int slot, RecipeInput... inputs) {
    for (RecipeInput ri : inputs) {
      if (ri.slotNumber == slot) {
        return ri.item;
      }
    }
    return null;
  }

  public static RecipeInput create(int slotNumber, ItemStack item) {
    return new RecipeInput(slotNumber, item);
  }

  public final int slotNumber;
  public final ItemStack item;

  public RecipeInput(int slotNumber, ItemStack item) {
    super();
    this.slotNumber = slotNumber;
    this.item = item;
  }

}