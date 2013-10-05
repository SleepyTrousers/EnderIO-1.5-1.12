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
