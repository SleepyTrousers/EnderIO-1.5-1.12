package crazypants.enderio.machine;

import net.minecraft.item.ItemStack;

public class MachineRecipeInput {

  public static ItemStack getInputForSlot(int slot, MachineRecipeInput... inputs) {
    for (MachineRecipeInput ri : inputs) {
      if (ri.slotNumber == slot) {
        return ri.item;
      }
    }
    return null;
  }

  public static MachineRecipeInput create(int slotNumber, ItemStack item) {
    return new MachineRecipeInput(slotNumber, item);
  }

  public final int slotNumber;
  public final ItemStack item;

  public MachineRecipeInput(int slotNumber, ItemStack item) {
    super();
    this.slotNumber = slotNumber;
    this.item = item;
  }

}