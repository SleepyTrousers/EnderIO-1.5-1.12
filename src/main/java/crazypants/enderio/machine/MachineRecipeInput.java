package crazypants.enderio.machine;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class MachineRecipeInput {

  public static ItemStack getInputForSlot(int slot, MachineRecipeInput... inputs) {
    for (MachineRecipeInput ri : inputs) {
      if(ri.slotNumber == slot) {
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

  public final FluidStack fluid;

  public MachineRecipeInput(int slotNumber, ItemStack item) {
    this.slotNumber = slotNumber;
    this.item = item;
    this.fluid = null;
  }

  public MachineRecipeInput(int slotNumber, FluidStack fluid) {
    this.slotNumber = slotNumber;
    this.item = null;
    this.fluid = fluid;
  }

}