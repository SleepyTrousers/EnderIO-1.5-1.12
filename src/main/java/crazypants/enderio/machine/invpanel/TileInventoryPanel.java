package crazypants.enderio.machine.invpanel;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class TileInventoryPanel extends AbstractMachineEntity {

  public Container eventHandler;

  public TileInventoryPanel() {
    super(new SlotDefinition(0, 8, 10, 19, 20, 19));
  }

  @Override
  public boolean canInsertItem(int slot, ItemStack var2, int side) {
    return false;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return true;
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack res = super.decrStackSize(fromSlot, amount);
    if(res != null && fromSlot < 9 && eventHandler != null) {
      eventHandler.onCraftMatrixChanged(this);
    }
    return res;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {
    super.setInventorySlotContents(slot, contents);
    if(slot < 9 && eventHandler != null) {
      eventHandler.onCraftMatrixChanged(this);
    }
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public void doUpdate() {
    
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockInventoryPanel.unlocalisedName;
  }
}
