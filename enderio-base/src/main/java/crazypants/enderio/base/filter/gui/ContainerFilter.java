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
import crazypants.enderio.base.filter.network.ICloseFilterRemoteExec;
import crazypants.enderio.base.init.ModObjectRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerFilter extends ContainerEnderCap<EnderInventory, TileEntityBase> implements ICloseFilterRemoteExec.Container {

  private EnumFacing dir;
  private IFilterHolder filterHolder;
  private @Nonnull EntityPlayer player;

  // Used to hold extra information about the original filter container (e.g. which filter it is inside a conduit)
  public int filterIndex;

  public ContainerFilter(@Nonnull InventoryPlayer playerInv, int filterIndex, TileEntityBase te, EnumFacing dir) {
    super(playerInv, new EnderInventory(), te);
    this.dir = dir;
    this.filterIndex = filterIndex;
    this.player = playerInv.player;

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
  public IMessage doCloseFilterGui() {
    TileEntityBase te = getTileEntity();
    if (te != null) {
      ModObjectRegistry.getModObjectNN(te.getBlockType()).openGui(te.getWorld(), te.getPos(), player, dir, getParam1());
    } else {
      player.closeScreen();
    }
    return null;
  }

}
