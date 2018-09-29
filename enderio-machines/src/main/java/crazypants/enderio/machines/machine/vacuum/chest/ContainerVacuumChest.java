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
import crazypants.enderio.base.filter.network.IOpenFilterRemoteExec;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerVacuumChest extends ContainerEnderCap<EnderInventory, TileVacuumChest>
    implements IOpenFilterRemoteExec.Container, IFilterContainer<IItemFilter> {

  private Slot filterSlot;
  private Runnable filterChangedCB;
  private EntityPlayer player;

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

  private int guiId = -1;

  @Override
  public void setGuiID(int id) {
    guiId = id;
  }

  @Override
  public int getGuiID() {
    return guiId;
  }

  @Override
  public IMessage doOpenFilterGui(int filterIndex) {
    TileVacuumChest te = getTileEntity();
    if (te != null) {
      if (filterIndex == FilterGuiUtil.INDEX_NONE) {
        te.getItemFilter().openGui(player, filterSlot.getStack(), te.getWorld(), te.getPos());
      }
    }
    return null;
  }

  @Override
  public @Nonnull IItemFilter getFilter(int index) {
    return getTileEntity().getItemFilter();
  }

}
