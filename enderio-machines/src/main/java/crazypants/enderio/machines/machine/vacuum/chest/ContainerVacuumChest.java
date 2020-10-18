package crazypants.enderio.machines.machine.vacuum.chest;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.filter.IFilterContainer;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.base.filter.item.items.BasicFilterTypes;
import info.loenwind.processor.RemoteCall;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

@RemoteCall
public class ContainerVacuumChest extends ContainerEnderCap<EnderInventory, TileVacuumChest> implements IFilterContainer<IItemFilter> {

  private Slot filterSlot;
  private Runnable filterChangedCB;
  private @Nonnull EntityPlayer player;

  public ContainerVacuumChest(@Nonnull InventoryPlayer inventory, final @Nonnull TileVacuumChest te) {
    super(inventory, te.getInventory(), te);
    this.player = inventory.player;
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
    slots.add(new GhostBackgroundItemSlot(BasicFilterTypes.filterUpgradeBasic.getBasicFilterStack(), NullHelper.notnull(filterSlot, "slot awol")));
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

  @RemoteCall
  public void doOpenFilterGui(int filterIndex) {
    TileVacuumChest te = getTileEntity();
    if (te != null) {
      if (filterIndex == FilterGuiUtil.INDEX_NONE) {
        te.getItemFilter().openGui(player, filterSlot.getStack(), te.getWorld(), te.getPos());
      }
    }
  }

  @Override
  public @Nonnull IItemFilter getFilter(int index) {
    return NullHelper.notnull(NullHelper.notnull(getTileEntity(), "te-less container").getItemFilter(), "logic error in filter access");
  }

}
