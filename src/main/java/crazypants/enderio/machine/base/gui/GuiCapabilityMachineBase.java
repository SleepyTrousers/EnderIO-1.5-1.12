package crazypants.enderio.machine.base.gui;

import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;

import crazypants.enderio.machine.base.te.AbstractCapabilityMachineEntity;
import crazypants.enderio.machine.gui.GuiMachineBase;
import crazypants.enderio.machine.modes.IoMode;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;

public class GuiCapabilityMachineBase<T extends AbstractCapabilityMachineEntity> extends GuiMachineBase<T> {

  protected GuiCapabilityMachineBase(T machine, Container par1Container, String[] guiTexture) {
    super(machine, par1Container, guiTexture);
  }

  @Override
  public void renderSlotHighlights(IoMode mode) {
    EnderInventory inv = getTileEntity().getInventory();

    for (Slot invSlot : inventorySlots.inventorySlots) {
      // FIXME: Massive hack for detecting SlotItemHandler
      if (invSlot instanceof SlotItemHandler) {
        if ((mode == IoMode.PULL || mode == IoMode.PUSH_PULL) && ((SlotItemHandler)invSlot).getItemHandler() == inv.getView(Type.INPUT)) {
          renderSlotHighlight(invSlot, PULL_COLOR);
        } else if ((mode == IoMode.PUSH || mode == IoMode.PUSH_PULL) && ((SlotItemHandler)invSlot).getItemHandler() == inv.getView(Type.OUTPUT)) {
          renderSlotHighlight(invSlot, PUSH_COLOR);
        }
      }
    }
  }
}
