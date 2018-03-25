package crazypants.enderio.conduits.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.IExternalConnectionContainer;
import crazypants.enderio.base.conduit.IFilterChangeListener;
import crazypants.enderio.base.conduit.item.FunctionUpgrade;
import crazypants.enderio.base.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.filter.network.IOpenFilterRemoteExec;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import crazypants.enderio.conduits.conduit.item.IItemConduit;
import crazypants.enderio.conduits.network.PacketSlotVisibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ExternalConnectionContainer extends ContainerEnderCap<InventoryUpgrades, TileConduitBundle>
    implements IExternalConnectionContainer, IOpenFilterRemoteExec.Container {

  private final IItemConduit itemConduit;

  private int speedUpgradeSlotLimit = 15;

  private static final int outputFilterSlot = 36;
  private static final int inputFilterSlot = 37;
  private static final int functionUpgradeSlot = 38;

  private Slot slotFunctionUpgrade;
  private Slot slotInputFilter;
  private Slot slotOutputFilter;

  private @Nonnull EnumFacing dir;
  private @Nonnull EntityPlayer player;

  final List<IFilterChangeListener> filterListeners = new ArrayList<IFilterChangeListener>();

  public ExternalConnectionContainer(@Nonnull InventoryPlayer playerInv, @Nonnull EnumFacing dir, @Nonnull TileConduitBundle bundle) {
    super(playerInv, new InventoryUpgrades(bundle.getConduit(IItemConduit.class), dir), bundle);
    this.itemConduit = bundle.getConduit(IItemConduit.class);
    this.dir = dir;
    this.player = playerInv.player;
    addSlots();
  }

  @Override
  protected void addSlots() {
    if (itemConduit != null) {

      addSlotToContainer(slotInputFilter = new FilterSlot(getItemHandler(), 3, 23, 71));
      addSlotToContainer(slotOutputFilter = new FilterSlot(getItemHandler(), 2, 113, 71));
      addSlotToContainer(slotFunctionUpgrade = new SlotItemHandler(getItemHandler(), 0, 131, 71) {
        @Override
        public boolean isItemValid(@Nonnull ItemStack itemStack) {
          return ExternalConnectionContainer.this.getItemHandler().isItemValidForSlot(0, itemStack);
        }

        @Override
        public int getSlotStackLimit() {
          return speedUpgradeSlotLimit;
        }
      });
    }
  }

  public void createGhostSlots(@Nonnull List<GhostSlot> ghostSlots) {
    if (itemConduit != null) {
      ghostSlots.add(new GhostBackgroundItemSlot(ModObject.itemBasicItemFilter.getItemNN(), slotOutputFilter));
      ghostSlots.add(new GhostBackgroundItemSlot(ModObject.itemBasicItemFilter.getItemNN(), slotInputFilter));

      NNList<ItemStack> ghostSlotIcons = new NNList<>(new ItemStack(ModObject.item_extract_speed_upgrade.getItemNN()),
          new ItemStack(ModObject.item_extract_speed_downgrade.getItemNN()), new ItemStack(ModObject.item_inventory_panel_upgrade.getItemNN()));
      ghostSlots.add(new GhostBackgroundItemSlot(ghostSlotIcons, slotFunctionUpgrade));
    }
  }

  @Override
  @Nonnull
  public Point getPlayerInventoryOffset() {
    return new Point(23, 113);
  }

  @Override
  public void addFilterListener(@Nonnull IFilterChangeListener list) {
    filterListeners.add(list);
  }

  protected void filterChanged() {
    for (IFilterChangeListener list : filterListeners) {
      list.onFilterChanged();
    }
  }

  @Override
  public boolean hasFunctionUpgrade() {
    return slotFunctionUpgrade != null && slotFunctionUpgrade.getHasStack();
  }

  @Override
  public boolean hasFilter(boolean input) {
    Slot slot = input ? slotInputFilter : slotOutputFilter;
    return slot != null && slot.getHasStack();
  }

  @Override
  public void setInOutSlotsVisible(boolean inputVisible, boolean outputVisible) {
    if (itemConduit == null) {
      return;
    }
    setSlotsVisible(inputVisible, inputFilterSlot, inputFilterSlot + 1);
    setSlotsVisible(inputVisible, functionUpgradeSlot, functionUpgradeSlot + 1);
    setSlotsVisible(outputVisible, outputFilterSlot, outputFilterSlot + 1);
    World world = itemConduit.getBundle().getBundleworld();
    if (world.isRemote) {
      PacketHandler.INSTANCE.sendToServer(new PacketSlotVisibility(inputVisible, outputVisible));
    }
  }

  @Override
  public void setInventorySlotsVisible(boolean visible) {
    setSlotsVisible(visible, 0, 36);
  }

  private void setSlotsVisible(boolean visible, int startIndex, int endIndex) {
    for (int i = startIndex; i < endIndex; i++) {
      Slot s = getSlot(i);
      if (visible) {
        s.xPos = slotLocations.get(s).x;
        s.yPos = slotLocations.get(s).y;
      } else {
        s.xPos = -3000;
        s.yPos = -3000;
      }
    }
  }

  @Override
  @Nonnull
  public ItemStack slotClick(int slotId, int dragType, @Nonnull ClickType clickTypeIn, @Nonnull EntityPlayer player) {
    ItemStack st = player.inventory.getItemStack();
    setFunctionUpgradeSlotLimit(st);
    try {
      return super.slotClick(slotId, dragType, clickTypeIn, player);
    } catch (Exception e) {
      // TODO Horrible work around for a bug when double clicking on a stack in inventory which matches a filter item
      // This does does double clicking to fill a stack from working with this GUI open.
      return ItemStack.EMPTY;
    }
  }

  private void setFunctionUpgradeSlotLimit(@Nonnull ItemStack st) {
    if (!st.isEmpty() && st.getItem() instanceof ItemFunctionUpgrade) {
      FunctionUpgrade speedUpgrade = ItemFunctionUpgrade.getFunctionUpgrade(st);
      speedUpgradeSlotLimit = speedUpgrade.maxStackSize;
    }
  }

  private boolean mergeItemStackSpecial(@Nonnull ItemStack origStack, @Nonnull Slot targetSlot) {
    if (!targetSlot.isItemValid(origStack)) {
      return false;
    }

    setFunctionUpgradeSlotLimit(origStack);
    ItemStack curStack = targetSlot.getStack();
    int maxStackSize = Math.min(origStack.getMaxStackSize(), targetSlot.getSlotStackLimit());

    if (curStack.isEmpty()) {
      curStack = origStack.copy();
      curStack.setCount(Math.min(origStack.getCount(), maxStackSize));
      origStack.shrink(curStack.getCount());
      targetSlot.putStack(curStack);
      targetSlot.onSlotChanged();
      return true;
    } else if (ItemUtil.areStackMergable(curStack, origStack)) {
      int mergedSize = curStack.getCount() + origStack.getCount();
      if (mergedSize <= maxStackSize) {
        origStack.setCount(0);
        curStack.setCount(mergedSize);
        targetSlot.onSlotChanged();
        return true;
      } else if (curStack.getCount() < maxStackSize) {
        origStack.shrink(maxStackSize - curStack.getCount());
        curStack.setCount(maxStackSize);
        targetSlot.onSlotChanged();
        return true;
      }
    }

    return false;
  }

  @Override
  @Nonnull
  public ItemStack transferStackInSlot(@Nonnull EntityPlayer entityPlayer, int slotIndex) {
    ItemStack copyStack = ItemStack.EMPTY;
    Slot slot = inventorySlots.get(slotIndex);
    if (slot != null && slot.getHasStack()) {
      ItemStack origStack = slot.getStack();
      copyStack = origStack.copy();

      boolean merged = false;
      if (slotIndex < outputFilterSlot) {
        for (int targetSlotIdx = outputFilterSlot; targetSlotIdx <= functionUpgradeSlot; targetSlotIdx++) {
          Slot targetSlot = inventorySlots.get(targetSlotIdx);
          if (targetSlot.xPos >= 0 && mergeItemStackSpecial(origStack, targetSlot)) {
            merged = true;
            break;
          }
        }
      } else {
        merged = mergeItemStack(origStack, 0, outputFilterSlot, false);
      }

      if (!merged) {
        return ItemStack.EMPTY;
      }

      slot.onSlotChange(origStack, copyStack);

      if (origStack.getCount() == 0) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }

      if (origStack.getCount() == copyStack.getCount()) {
        return ItemStack.EMPTY;
      }

      slot.onTake(entityPlayer, origStack);
    }

    return copyStack;
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
    if (itemConduit != null) {
      if (filterIndex == FilterGuiUtil.INDEX_INPUT) {
        itemConduit.getInputFilter(dir).openGui(player, itemConduit.getInputFilterUpgrade(dir), getTileEntity().getBundleworld(), getTileEntity().getPos(), dir,
            filterIndex);
      } else if (filterIndex == FilterGuiUtil.INDEX_OUTPUT) {
        itemConduit.getOutputFilter(dir).openGui(player, itemConduit.getOutputFilterUpgrade(dir), getTileEntity().getBundleworld(), getTileEntity().getPos(),
            dir, filterIndex);
      }
    }
    return null;
  }

  private class FilterSlot extends SlotItemHandler {
    public FilterSlot(IItemHandler handler, int index, int x, int y) {
      super(handler, index, x, y);
    }

    @Override
    public int getSlotStackLimit() {
      return 1;
    }

    @Override
    public void onSlotChanged() {
      filterChanged();
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      return ExternalConnectionContainer.this.getItemHandler().isItemValidForSlot(getSlotIndex(), stack);
    }

  }

}
