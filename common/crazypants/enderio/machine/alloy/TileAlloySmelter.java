package crazypants.enderio.machine.alloy;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.RecipeInput;

public class TileAlloySmelter extends AbstractPoweredTaskEntity {

  public TileAlloySmelter() {
    super(4);
  }

  @Override
  public String getInvName() {
    return "Alloy Smelter";
  }

  @Override
  public String getMachineName() {
    return ModObject.blockAlloySmelter.unlocalisedName;
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if (i >= 3) {
      return false;
    }
    return !MachineRecipeRegistry.instance.getRecipesForInput(getMachineName(), RecipeInput.create(i, itemstack)).isEmpty();
  }

}
