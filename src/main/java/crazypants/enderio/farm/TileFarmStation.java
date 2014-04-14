package crazypants.enderio.farm;

import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;

public class TileFarmStation extends AbstractMachineEntity {

  @Override
  public String getInventoryName() {
    return EnderIO.blockFarmStation.getLocalizedName();
  }

  public TileFarmStation() {
    super(new SlotDefinition(6, 4));
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockFarmStation.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return slotDefinition.isInputSlot(i);
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public float getProgress() {
    return 0;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    return false;
  }

}
