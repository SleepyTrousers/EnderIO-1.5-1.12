package crazypants.enderio.conduit.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.gui.item.InventoryUpgrades;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.SpeedUpgrade;

public class ExternalConnectionContainer extends Container {

  private final IItemConduit itemConduit;

  private int speedUpgradeSlotLimit = 15;

  private static final int outputFilterUpgradeSlot = 36;
  private static final int inputFilterUpgradeSlot = 37;
  private static final int speedUpgradeSlot = 38;

  private Slot slotSpeedUpgrades;
  private Slot slotFunctionUpgrades;
  private Slot slotInputFilterUpgrades;
  private Slot slotOutputFilterUpgrades;

  private final List<Point> slotLocations = new ArrayList<Point>();

  final List<FilterChangeListener> filterListeners = new ArrayList<FilterChangeListener>();

  public ExternalConnectionContainer(InventoryPlayer playerInv, IConduitBundle bundle, ForgeDirection dir) {
    int x;
    int y;

    int topY = 113;
    // add players inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        x = 23 + j * 18;
        y = topY + i * 18;
        addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, x, y));
        slotLocations.add(new Point(x, y));
      }
    }

    y = 171;
    for (int i = 0; i < 9; ++i) {
      x = 23 + i * 18;
      addSlotToContainer(new Slot(playerInv, i, x, y));
      slotLocations.add(new Point(x, y));
    }

    itemConduit = bundle.getConduit(IItemConduit.class);
    if(itemConduit != null) {
      final InventoryUpgrades ui = new InventoryUpgrades(itemConduit, dir);

      x = 10;
      y = 47;
      slotInputFilterUpgrades = addSlotToContainer(new FilterSlot(ui, 3, x, y));
      slotLocations.add(new Point(x, y));

      x = 10;
      y = 47;
      slotOutputFilterUpgrades = addSlotToContainer(new FilterSlot(ui, 2, x, y));
      slotLocations.add(new Point(x, y));

      x = 28;
      y = 47;      
      slotSpeedUpgrades = addSlotToContainer(new Slot(ui, 0, x, y) {
        @Override
        public boolean isItemValid(ItemStack par1ItemStack) {
          return ui.isItemValidForSlot(0, par1ItemStack);
        }

        @Override
        public int getSlotStackLimit() {
          return speedUpgradeSlotLimit;
        }
      });
      slotLocations.add(new Point(x, y));

      x = 10;
      y = 65;
      slotFunctionUpgrades = addSlotToContainer(new Slot(ui, 1, x, y) {
        @Override
        public boolean isItemValid(ItemStack par1ItemStack) {
          return ui.isItemValidForSlot(1, par1ItemStack);
        }

        @Override
        public int getSlotStackLimit() {
          return 1;
        }
      });
      slotLocations.add(new Point(x, y));
    }
  }
  
  public void addFilterListener(FilterChangeListener list) {
    filterListeners.add(list);
  }

  protected void filterChanged() {
    System.out.println("filterChanged " + filterListeners.size());
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

  public void setInputSlotsVisible(boolean visible) {
    if(itemConduit == null) {
      return;
    }
    setSlotsVisible(visible, inputFilterUpgradeSlot, inputFilterUpgradeSlot + 1);
    setSlotsVisible(visible, speedUpgradeSlot, speedUpgradeSlot + 1);
  }

  public void setOutputSlotsVisible(boolean visible) {
    if(itemConduit == null) {
      return;
    }
    
    setSlotsVisible(visible, outputFilterUpgradeSlot, outputFilterUpgradeSlot + 1);
  }

  public void setInventorySlotsVisible(boolean visible) {
    setSlotsVisible(visible, 0, 36);
  }

  private void setSlotsVisible(boolean visible, int startIndex, int endIndex) {
    for (int i = startIndex; i < endIndex; i++) {
      Slot s = (Slot) inventorySlots.get(i);
      if(visible) {
        s.xDisplayPosition = slotLocations.get(i).x;
        s.yDisplayPosition = slotLocations.get(i).y;
      } else {
        s.xDisplayPosition = -3000;
        s.yDisplayPosition = -3000;
      }
    }
  }

  @Override
  public boolean canInteractWith(EntityPlayer entityplayer) {
    return true;
  }

  @Override
  public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer) {
    ItemStack st = par4EntityPlayer.inventory.getItemStack();
    if(st != null && st.getItem() == EnderIO.itemExtractSpeedUpgrade) {
      SpeedUpgrade speedUpgrade = EnderIO.itemExtractSpeedUpgrade.getSpeedUpgrade(st);
      speedUpgradeSlotLimit = speedUpgrade.maxStackSize;
    }
    try {
      return super.slotClick(par1, par2, par3, par4EntityPlayer);
    } catch (Exception e) {
      //Horrible work around for a bug when double clicking on a stack in inventory which matches a filter item
      //This does does double clicking to fill a stack from working with this GUI open.
      return null;
    }
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
    return null;
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
    public boolean isItemValid(ItemStack par1ItemStack) {
      return inventory.isItemValidForSlot(getSlotIndex(), par1ItemStack);
    }
    
  }

}
