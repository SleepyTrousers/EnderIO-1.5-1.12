package crazypants.enderio.conduit.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.base.conduit.IExternalConnectionContainer;
import crazypants.enderio.base.conduit.IFilterChangeListener;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.filter.network.IOpenFilterRemoteExec;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.gui.item.InventoryUpgrades;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemExtractSpeedUpgrade;
import crazypants.enderio.conduit.item.SpeedUpgrade;
import crazypants.enderio.conduit.packet.PacketSlotVisibility;
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

import static crazypants.enderio.base.init.ModObject.itemItemFilter;
import static crazypants.enderio.conduit.init.ConduitObject.item_extract_speed_upgrade;
import static crazypants.enderio.conduit.init.ConduitObject.item_function_upgrade;

public class ExternalConnectionContainer extends ContainerEnderCap<InventoryUpgrades, TileConduitBundle>
    implements IExternalConnectionContainer, IOpenFilterRemoteExec.Container {

  private final IItemConduit itemConduit;

  // TODO Improve speed upgrades
  private int speedUpgradeSlotLimit = 15;

  private static final int outputFilterSlot = 36;
  private static final int inputFilterSlot = 37;
  private static final int speedUpgradeSlot = 38;
  private static final int functionUpgradeSlot = 39;

  private Slot slotSpeedUpgrades;
  private Slot slotFunctionUpgrades;
  private Slot slotInputFilter;
  private Slot slotOutputFilter;

  private EnumFacing dir;
  private EntityPlayer player;

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

      addSlotToContainer(slotInputFilter = new FilterSlot(getItemHandler(), 2, 23, 71));
      addSlotToContainer(slotOutputFilter = new FilterSlot(getItemHandler(), 3, 113, 71));
      addSlotToContainer(slotSpeedUpgrades = new SlotItemHandler(getItemHandler(), 0, 131, 71) {
        @Override
        public boolean isItemValid(@Nonnull ItemStack itemStack) {
          return isItemValidForSlot(0, itemStack);
        }

        @Override
        public int getSlotStackLimit() {
          return speedUpgradeSlotLimit;
        }
      });
      addSlotToContainer(slotFunctionUpgrades = new SlotItemHandler(getItemHandler(), 1, 157, 71) {
        @Override
        public boolean isItemValid(@Nonnull ItemStack itemStack) {
          return isItemValidForSlot(1, itemStack);
        }

        @Override
        public int getSlotStackLimit() {
          return 1;
        }
      });
    }
  }

  private boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    switch (slot) {
    case 0:
      return stack.getItem() instanceof ItemExtractSpeedUpgrade;
    // TODO Inventory
    // case 1:
    // final FunctionUpgrade functionUpgrade = ItemFunctionUpgrade.getFunctionUpgrade(item);
    // return functionUpgrade != null
    // && (functionUpgrade != FunctionUpgrade.INVENTORY_PANEL || !itemConduit.isConnectedToNetworkAwareBlock(dir));
    case 2:
    case 3:
      return stack.getItem() instanceof IItemFilterUpgrade;
    }
    return false;
  }

  public void createGhostSlots(@Nonnull List<GhostSlot> ghostSlots) {
    if (itemConduit != null) {
      ghostSlots.add(new GhostBackgroundItemSlot(itemItemFilter.getItemNN(), slotOutputFilter));
      ghostSlots.add(new GhostBackgroundItemSlot(itemItemFilter.getItemNN(), slotInputFilter));
      ghostSlots.add(new GhostBackgroundItemSlot(item_extract_speed_upgrade.getItemNN(), slotSpeedUpgrades));
      ghostSlots.add(new GhostBackgroundItemSlot(item_function_upgrade.getItemNN(), slotFunctionUpgrades));
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
  public boolean hasSpeedUpgrades() {
    return slotSpeedUpgrades != null && slotSpeedUpgrades.getHasStack();
  }

  @Override
  public boolean hasFunctionUpgrade() {
    return slotFunctionUpgrades != null && slotFunctionUpgrades.getHasStack();
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
    setSlotsVisible(inputVisible, speedUpgradeSlot, speedUpgradeSlot + 1);
    setSlotsVisible(outputVisible, outputFilterSlot, outputFilterSlot + 1);
    setSlotsVisible(inputVisible || outputVisible, functionUpgradeSlot, functionUpgradeSlot + 1);
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
    setSpeedUpgradeSlotLimit(st);
    try {
      return super.slotClick(slotId, dragType, clickTypeIn, player);
    } catch (Exception e) {
      // TODO Horrible work around for a bug when double clicking on a stack in inventory which matches a filter item
      // This does does double clicking to fill a stack from working with this GUI open.
      return ItemStack.EMPTY;
    }
  }

  private void setSpeedUpgradeSlotLimit(@Nonnull ItemStack st) {
    if (!st.isEmpty() && st.getItem() == item_extract_speed_upgrade.getItem()) {
      SpeedUpgrade speedUpgrade = ItemExtractSpeedUpgrade.getSpeedUpgrade(st);
      speedUpgradeSlotLimit = speedUpgrade.maxStackSize;
    }
  }

  private boolean mergeItemStackSpecial(@Nonnull ItemStack origStack, @Nonnull Slot targetSlot) {
    if (!targetSlot.isItemValid(origStack)) {
      return false;
    }

    setSpeedUpgradeSlotLimit(origStack);
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

  // TODO move openGui to IItemFilter so that it no longer causes crashes with any other kind of filter
  @Override
  public IMessage doOpenFilterGui(int filterIndex) {
    if (itemConduit != null) {
      if (filterIndex == FilterGuiUtil.INDEX_INPUT) {
        ((IItemFilter) itemConduit.getInputFilter(dir)).openGui(player, itemConduit.getInputFilterUpgrade(dir), getTileEntity().getBundleworld(),
            getTileEntity().getPos(), dir, filterIndex);
      } else if (filterIndex == FilterGuiUtil.INDEX_OUTPUT) {
        ((IItemFilter) itemConduit.getOutputFilter(dir)).openGui(player, itemConduit.getOutputFilterUpgrade(dir), getTileEntity().getBundleworld(),
            getTileEntity().getPos(), dir, filterIndex);
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
      return inventory.isItemValidForSlot(getSlotIndex(), stack);
    }

  }

}
