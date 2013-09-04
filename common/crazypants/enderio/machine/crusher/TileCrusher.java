package crazypants.enderio.machine.crusher;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPoweredTaskEntity;

public class TileCrusher extends AbstractPoweredTaskEntity {

  public TileCrusher() {
    super(2);
  }

  @Override
  public String getInvName() {
    return ModObject.blockCrusher.name;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockCrusher.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {    
    return CrusherRecipeManager.instance.getRecipeForInput(itemstack) != null;
  }

}
