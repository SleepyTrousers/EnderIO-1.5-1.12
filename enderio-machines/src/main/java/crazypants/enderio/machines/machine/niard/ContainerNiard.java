package crazypants.enderio.machines.machine.niard;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;

import crazypants.enderio.machines.machine.tank.ContainerTank;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerNiard<T extends TileNiard> extends ContainerEnderCap<EnderInventory, T> {

  public ContainerNiard(@Nonnull InventoryPlayer playerInv, @Nonnull T te) {
    super(playerInv, te.getInventory(), te);
  }

  @Override
  protected void addSlots() {
    addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.UPGRADE), "cap", 12, 60));
    addSlotToContainer(new EnderSlot(Type.INPUT, getItemHandler(), TileNiard.SLOT.INPUT, 44, 21));
    addSlotToContainer(new EnderSlot(Type.OUTPUT, getItemHandler(), TileNiard.SLOT.OUTPUT, 44, 52));
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 84);
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    Slot slot = inventorySlots.get(1);
    if (slot != null) {
      slots.add(new GhostBackgroundItemSlot(ContainerTank.slotItemsFull.getItemStacks(), slot));
    }
    slot = inventorySlots.get(2);
    if (slot != null) {
      slots.add(new GhostBackgroundItemSlot(ContainerTank.slotItemsEmpty.getItemStacks(), slot));
    }
  }
}
