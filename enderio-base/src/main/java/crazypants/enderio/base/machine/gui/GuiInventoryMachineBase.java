package crazypants.enderio.base.machine.gui;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.baselegacy.AbstractInventoryMachineEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.modes.IoMode;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class GuiInventoryMachineBase<T extends AbstractInventoryMachineEntity> extends GuiMachineBase<T> {

  protected GuiInventoryMachineBase(@Nonnull T machine, @Nonnull Container par1Container, String... guiTexture) {
    super(machine, par1Container, guiTexture);
  }

  @Override
  public void renderSlotHighlights(@Nonnull IoMode mode) {
    SlotDefinition slotDef = getTileEntity().getSlotDefinition();

    for (Slot invSlot : inventorySlots.inventorySlots) {
      if (invSlot.inventory == getTileEntity()) {
        if ((mode == IoMode.PULL || mode == IoMode.PUSH_PULL) && slotDef.isInputSlot(invSlot.getSlotIndex())) {
          renderSlotHighlight(invSlot, PULL_COLOR);
        } else if ((mode == IoMode.PUSH || mode == IoMode.PUSH_PULL) && slotDef.isOutputSlot(invSlot.getSlotIndex())) {
          renderSlotHighlight(invSlot, PUSH_COLOR);
        }
      }
    }
  }
}
