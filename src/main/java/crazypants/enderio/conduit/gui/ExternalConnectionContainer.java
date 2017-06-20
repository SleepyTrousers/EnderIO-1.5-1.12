package crazypants.enderio.conduit.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.enderio.core.client.gui.GhostSlotHandler;
import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnder;
import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.gui.item.InventoryUpgrades;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemExtractSpeedUpgrade;
import crazypants.enderio.conduit.item.SpeedUpgrade;
import crazypants.enderio.conduit.packet.PacketSlotVisibility;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import static crazypants.enderio.ModObject.itemBasicFilterUpgrade;
import static crazypants.enderio.ModObject.itemExtractSpeedUpgrade;
import static crazypants.enderio.ModObject.itemFunctionUpgrade;

public class ExternalConnectionContainer extends ContainerEnder<InventoryUpgrades> {

  private final IItemConduit itemConduit;

  private int speedUpgradeSlotLimit = 15;

  private static final int outputFilterUpgradeSlot = 36;
  private static final int inputFilterUpgradeSlot = 37;
  private static final int speedUpgradeSlot = 38;
  private static final int functionUpgradeSlot = 39;

  private Slot slotSpeedUpgrades;
  private Slot slotFunctionUpgrades;
  private Slot slotInputFilterUpgrades;
  private Slot slotOutputFilterUpgrades;

  private final List<Point> slotLocations = new ArrayList<Point>();

  final List<FilterChangeListener> filterListeners = new ArrayList<FilterChangeListener>();
  final List<GhostBackgroundItemSlot> bgSlots = new ArrayList<GhostBackgroundItemSlot>();

  public ExternalConnectionContainer(InventoryPlayer playerInv, IConduitBundle bundle, EnumFacing dir) {
    super(playerInv, new InventoryUpgrades(bundle.getConduit(IItemConduit.class), dir));
    this.itemConduit = bundle.getConduit(IItemConduit.class);
    slotLocations.addAll(playerSlotLocations.values());

    int x;
    int y;

    if (itemConduit != null) {
      x = 10;
      y = 47;
      slotOutputFilterUpgrades = addSlotToContainer(new FilterSlot(getInv(), 3, x, y));
      slotLocations.add(new Point(x, y));
      bgSlots.add(new GhostBackgroundItemSlot(itemBasicFilterUpgrade.getItem(), slotOutputFilterUpgrades));

      x = 10;
      y = 47;
      slotInputFilterUpgrades = addSlotToContainer(new FilterSlot(getInv(), 2, x, y));
      slotLocations.add(new Point(x, y));
      bgSlots.add(new GhostBackgroundItemSlot(itemBasicFilterUpgrade.getItem(), slotInputFilterUpgrades));

      x = 28;
      y = 47;
      slotSpeedUpgrades = addSlotToContainer(new Slot(getInv(), 0, x, y) {
        @Override
        public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
          return getInv().isItemValidForSlot(0, par1ItemStack);
        }

        @Override
        public int getSlotStackLimit() {
          return speedUpgradeSlotLimit;
        }
      });
      slotLocations.add(new Point(x, y));
      bgSlots.add(new GhostBackgroundItemSlot(itemExtractSpeedUpgrade.getItem(), slotSpeedUpgrades));

      x = 10;
      y = 65;
      slotFunctionUpgrades = addSlotToContainer(new Slot(getInv(), 1, x, y) {
        @Override
        public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
          return getInv().isItemValidForSlot(1, par1ItemStack);
        }

        @Override
        public int getSlotStackLimit() {
          return 1;
        }
      });
      slotLocations.add(new Point(x, y));
      bgSlots.add(new GhostBackgroundItemSlot(itemFunctionUpgrade.getItem(), slotFunctionUpgrades));
    }
  }

  public void createGhostSlots(GhostSlotHandler slots) {
//    slots.addAll(bgSlots);
  }

  @Override
  public Point getPlayerInventoryOffset() {
    return new Point(23, 113);
  }
  
  public void addFilterListener(FilterChangeListener list) {
    filterListeners.add(list);
  }

  protected void filterChanged() {
    for (FilterChangeListener list : filterListeners) {
      list.onFilterChanged();
    }
  }

  public boolean hasSpeedUpgrades() {
    return slotSpeedUpgrades != null && slotSpeedUpgrades.getHasStack();
  }

  public boolean hasFunctionUpgrades() {
    return slotFunctionUpgrades != null && slotFunctionUpgrades.getHasStack();
  }

  public boolean hasFilterUpgrades(boolean input) {
    Slot slot = input ? slotInputFilterUpgrades : slotOutputFilterUpgrades;
    return slot != null && slot.getHasStack();
  }

  public void setInoutSlotsVisible(boolean inputVisible, boolean outputVisible) {
    if(itemConduit == null) {
      return;
    }
    setSlotsVisible(inputVisible, inputFilterUpgradeSlot, inputFilterUpgradeSlot + 1);
    setSlotsVisible(inputVisible, speedUpgradeSlot, speedUpgradeSlot + 1);
    setSlotsVisible(outputVisible, outputFilterUpgradeSlot, outputFilterUpgradeSlot + 1);
    setSlotsVisible(inputVisible || outputVisible, functionUpgradeSlot, functionUpgradeSlot + 1);
    World world = itemConduit.getBundle().getBundleworld();
    if(world.isRemote) {
      PacketHandler.INSTANCE.sendToServer(new PacketSlotVisibility(inputVisible, outputVisible));
    }
  }

  public void setInventorySlotsVisible(boolean visible) {
    setSlotsVisible(visible, 0, 36);
  }

  private void setSlotsVisible(boolean visible, int startIndex, int endIndex) {
    for (int i = startIndex; i < endIndex; i++) {
      Slot s = inventorySlots.get(i);
      if(visible) {
        s.xPos = slotLocations.get(i).x;
        s.yPos = slotLocations.get(i).y;
      } else {
        s.xPos = -3000;
        s.yPos = -3000;
      }
    }
  }

  @Override
  public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
    ItemStack st = player.inventory.getItemStack();
    setSpeedUpgradeSlotLimit(st);
    try {
      return super.slotClick(slotId, dragType, clickTypeIn, player);
    } catch (Exception e) {
      //Horrible work around for a bug when double clicking on a stack in inventory which matches a filter item
      //This does does double clicking to fill a stack from working with this GUI open.
      return null;
    }
  }

  private void setSpeedUpgradeSlotLimit(ItemStack st) {
    if (st != null && st.getItem() == itemExtractSpeedUpgrade.getItem()) {
      SpeedUpgrade speedUpgrade = ItemExtractSpeedUpgrade.getSpeedUpgrade(st);
      speedUpgradeSlotLimit = speedUpgrade.maxStackSize;
    }
  }

  private boolean mergeItemStackSpecial(ItemStack origStack, Slot targetSlot) {
    if (!targetSlot.isItemValid(origStack)) {
      return false;
    }

    setSpeedUpgradeSlotLimit(origStack);
    ItemStack curStack = targetSlot.getStack();
    int maxStackSize =  Math.min(origStack.getMaxStackSize(), targetSlot.getSlotStackLimit());

    if(curStack == null) {
      curStack = origStack.copy();
      curStack.setCount(Math.min(origStack.getCount(), maxStackSize));
      origStack.shrink(curStack.getCount());
      targetSlot.putStack(curStack);
      targetSlot.onSlotChanged();
      return true;
    } else if(ItemUtil.areStackMergable(curStack, origStack)) {
      int mergedSize = curStack.getCount() + origStack.getCount();
      if(mergedSize <= maxStackSize) {
        origStack.setCount(0);
        curStack.setCount(mergedSize);
        targetSlot.onSlotChanged();
        return true;
      } else if(curStack.getCount() < maxStackSize) {
        origStack.shrink(maxStackSize - curStack.getCount());
        curStack.setCount(maxStackSize);
        targetSlot.onSlotChanged();
        return true;
      }
    }

    return false;
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
    ItemStack copystack = null;
    Slot slot = inventorySlots.get(slotIndex);
    if(slot != null && slot.getHasStack()) {
      ItemStack origStack = slot.getStack();
      copystack = origStack.copy();

      boolean merged = false;
      if (slotIndex < outputFilterUpgradeSlot) {
        for (int targetSlotIdx = outputFilterUpgradeSlot; targetSlotIdx <= functionUpgradeSlot; targetSlotIdx++) {
          Slot targetSlot = inventorySlots.get(targetSlotIdx);
          if(targetSlot.xPos >= 0 && mergeItemStackSpecial(origStack, targetSlot)) {
            merged = true;
            break;
          }
        }
      } else {
        merged = mergeItemStack(origStack, 0, outputFilterUpgradeSlot, false);
      }

      if(!merged) {
        return null;
      }

      slot.onSlotChange(origStack, copystack);

      if(origStack.getCount() == 0) {
        slot.putStack((ItemStack) null);
      } else {
        slot.onSlotChanged();
      }

      if(origStack.getCount() == copystack.getCount()) {
        return null;
      }

      slot.onTake(entityPlayer, origStack);
    }

    return copystack;
  }

  private class FilterSlot extends Slot {
    public FilterSlot(IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
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
    public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
      return inventory.isItemValidForSlot(getSlotIndex(), par1ItemStack);
    }
    
  }

}
