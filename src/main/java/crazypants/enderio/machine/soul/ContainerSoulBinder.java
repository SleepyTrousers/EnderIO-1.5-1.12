package crazypants.enderio.machine.soul;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.gui.AbstractMachineContainer;

public class ContainerSoulBinder  extends AbstractMachineContainer<TileSoulBinder> {

  public ContainerSoulBinder(InventoryPlayer playerInv, TileSoulBinder te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(getInv(), 0, 38, 34) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }
    });
    addSlotToContainer(new Slot(getInv(), 1, 59, 34) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return getInv().isItemValidForSlot(1, itemStack);
      }      
    });    
    addSlotToContainer(new Slot(getInv(), 2, 112, 34) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(getInv(), 3, 134, 34) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
      }
    });
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.add(new GhostBackgroundItemSlot(EnderIO.itemSoulVessel, 38, 34));
    slots.add(new GhostBackgroundItemSlot(EnderIO.itemBrokenSpawner, 59, 34));
  }

}
