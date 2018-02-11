package crazypants.enderio.base.filter.gui;

import java.awt.Point;

import javax.annotation.Nonnull;

import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;

import crazypants.enderio.base.filter.IItemFilter;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerItemFilter extends ContainerEnderCap<EnderInventory, TileEntity> implements IItemFilterContainer {

  private IItemFilter filter;

  public ContainerItemFilter(@Nonnull InventoryPlayer playerInv, @Nonnull IItemFilter filter) {
    super(playerInv, new EnderInventory(), null);
    this.filter = filter;
  }

  @Override
  @Nonnull
  public IItemFilter getItemFilter() {
    return filter;
  }

  @Override
  public void onFilterChanged() {

  }

  @Override
  protected void addSlots() {

  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(23, 113);
  }

}
