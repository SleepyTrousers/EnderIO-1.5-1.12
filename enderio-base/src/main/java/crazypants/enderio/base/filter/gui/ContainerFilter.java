package crazypants.enderio.base.filter.gui;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.TileEntityBase;
import com.enderio.core.common.inventory.EnderInventory;

import crazypants.enderio.base.filter.IFilterHolder;
import crazypants.enderio.base.filter.IItemFilter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;

public class ContainerFilter extends ContainerEnderCap<EnderInventory, TileEntityBase> implements IItemFilterContainer {

  private EnumFacing dir;
  private IFilterHolder filterHolder;

  // Used to hold extra information about the original filter container (e.g. which filter it is inside a conduit)
  public int filterIndex;

  public ContainerFilter(@Nonnull InventoryPlayer playerInv, int filterIndex, TileEntityBase te, EnumFacing dir) {
    super(playerInv, new EnderInventory(), te);
    this.dir = dir;
    this.filterIndex = filterIndex;

    if (te instanceof IFilterHolder) {
      filterHolder = (IFilterHolder) te;
    }
  }

  public int getParam1() {
    return dir.ordinal();
  }

  @Override
  protected void addSlots() {

  }

  @Override
  public IItemFilter getItemFilter() {
    if (filterHolder != null) {
      return filterHolder.getFilter(filterIndex, dir.ordinal());
    }
    return null;
  }

  // TODO move ghost slots here
  public void createGhostSlots(List<GhostSlot> slots) {
    slots.addAll(slots);
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(14, 119);
  }

  @Override
  public void onFilterChanged() {

  }

}
