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
import crazypants.enderio.base.filter.filters.ItemFilter;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerFilter extends ContainerEnderCap<EnderInventory, TileEntityBase> implements IItemFilterContainer, IFilterGuiRemoteExec.Container {

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
  @Nonnull
  public IItemFilter getItemFilter() {
    if (filterHolder != null) {
      return filterHolder.getFilter(filterIndex, dir.ordinal());
    }
    return null;
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.addAll(slots);
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(23, 113);
  }

  @Override
  public void onFilterChanged() {
    PacketHandler.INSTANCE.sendToServer(new PacketFilterUpdate(getTileEntity(), getItemFilter(), filterIndex, dir.ordinal()));
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
  public IMessage doSetWhitelistMode(boolean isBlacklist) {
    if (filterHolder != null) {
      IItemFilter filter = filterHolder.getFilter(filterIndex, dir.ordinal());
      if (filter instanceof ItemFilter) {
        ((ItemFilter) filter).setBlacklist(isBlacklist);
      }
    }
    return null;
  }

  @Override
  public IMessage doSetMatchMetaMode(boolean isMatchMeta) {
    if (filterHolder != null) {
      IItemFilter filter = filterHolder.getFilter(filterIndex, dir.ordinal());
      if (filter instanceof ItemFilter) {
        ((ItemFilter) filter).setMatchMeta(isMatchMeta);
      }
    }
    return null;
  }

}
