package crazypants.enderio.machines.machine.vacuum;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;

import crazypants.enderio.base.filter.items.BasicFilterTypes;

import com.enderio.core.common.inventory.EnderSlot;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerVacuumChest extends ContainerEnderCap<EnderInventory, TileVacuumChest> {

  private @Nonnull Slot filterSlot;
  private Runnable filterChangedCB;

  public ContainerVacuumChest(@Nonnull InventoryPlayer inventory, final @Nonnull TileVacuumChest te) {
    super(inventory, te.getInventory(), te);
  }

  @Override
  protected void addSlots() {
    addSlotToContainer(filterSlot = new EnderSlot(getItemHandler().getView(Type.UPGRADE), "filter", 8, 86) {
      @Override
      public void onSlotChanged() {
        filterChanged();
      }
    });

    int x = 8;
    int y = 18;
    for (EnderSlot slot : EnderSlot.create(getItemHandler(), EnderInventory.Type.OUTPUT, x, y, TileVacuumChest.ITEM_COLS, TileVacuumChest.ITEM_ROWS)) {
      if (slot != null) {
        addSlotToContainer(slot);
      }
    }
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.add(new GhostBackgroundItemSlot(BasicFilterTypes.filterUpgradeBasic.getStack(), filterSlot));
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    Point p = super.getPlayerInventoryOffset();
    p.translate(8, 70);
    return p;
  }

  void setFilterChangedCB(Runnable filterChangedCB) {
    this.filterChangedCB = filterChangedCB;
  }

  void filterChanged() {
    if (filterChangedCB != null) {
      filterChangedCB.run();
    }
  }

}
