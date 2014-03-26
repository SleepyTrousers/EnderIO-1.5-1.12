package crazypants.enderio.machine.still;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.SlotDefinition;

public class TileStill extends AbstractPoweredTaskEntity {

  public TileStill() {
    super(new SlotDefinition(0, 1, -1, -1, -1, -1));
  }

  @Override
  public String getInventoryName() {
    return ModObject.blockStill.unlocalisedName;
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockStill.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return StillRecipeManager.getInstance().isValidInput(new MachineRecipeInput(i, itemstack));
  }

}
