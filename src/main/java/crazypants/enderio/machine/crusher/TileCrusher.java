package crazypants.enderio.machine.crusher;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;
import crazypants.enderio.machine.SlotDefinition;

public class TileCrusher extends AbstractPoweredTaskEntity {

  public TileCrusher() {
    super(new SlotDefinition(1, 4));
  }

  @Override
  public String getInventoryName() {
    return ModObject.blockSagMill.unlocalisedName;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockSagMill.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return CrusherRecipeManager.instance.getRecipeForInput(itemstack) != null;
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

}
