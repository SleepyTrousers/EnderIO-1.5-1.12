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
import crazypants.enderio.conduit.item.FilterRegister;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.IItemFilter;
import crazypants.enderio.conduit.item.ItemFilter;
import crazypants.gui.TemplateSlot;

public class ExternalConnectionContainer extends Container {

  private InventoryPlayer playerInv;
  private IConduitBundle bundle;
  private ForgeDirection dir;
  private IItemConduit itemConduit;

  private IItemFilter inputFilter;
  private IItemFilter outputFilter;

  private int outputFilterUpgradeSlot = 36;
  private int inputFilterUpgradeSlot = 37;
  private int speedUpgradeSlot = 38;
  private int startFilterSlot = 39;

  private List<Point> slotLocations = new ArrayList<Point>();

  List<FilterChangeListener> filterListeners = new ArrayList<FilterChangeListener>();

  public ExternalConnectionContainer(InventoryPlayer playerInv, IConduitBundle bundle, ForgeDirection dir) {
    this.playerInv = playerInv;
    this.bundle = bundle;
    this.dir = dir;

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

      x = 10;
      y = 47;
      InventoryFilterUpgrade fi = new InventoryFilterUpgrade(itemConduit, dir, false);
      addSlotToContainer(new FilterSlot(fi, 0, x, y, false));
      slotLocations.add(new Point(x, y));

      x = 10;
      y = 47;
      fi = new InventoryFilterUpgrade(itemConduit, dir, true);
      addSlotToContainer(new FilterSlot(fi, 0, x, y, true));
      slotLocations.add(new Point(x, y));

      x = 28;
      y = 47;
      final InventorySpeedUpgrades si = new InventorySpeedUpgrades(itemConduit, dir);
      addSlotToContainer(new Slot(si, 0, x, y) {

        @Override
        public boolean isItemValid(ItemStack par1ItemStack) {
          return si.isItemValidForSlot(0, par1ItemStack);
        }
        
      });
      slotLocations.add(new Point(x, y));

      addFilterSlots(dir);
    }

  }

  private void addFilterSlots(ForgeDirection dir) {

    List<Slot> slots;
    inputFilter = itemConduit.getInputFilter(dir);
    if(inputFilter != null) {
      slots = inputFilter.getSlots();
      for (Slot slot : slots) {
        addSlotToContainer(slot);
        slotLocations.add(new Point(slot.xDisplayPosition, slot.yDisplayPosition));
      }
    }

    outputFilter = itemConduit.getOutputFilter(dir);
    if(outputFilter != null) {
      slots = outputFilter.getSlots();
      for (Slot slot : slots) {
        addSlotToContainer(slot);
        slotLocations.add(new Point(slot.xDisplayPosition, slot.yDisplayPosition));
      }
    }
  }

  protected void filterChanged() {
    int slotsToRemove = inventorySlots.size() - startFilterSlot;
    for (int i = 0; i < slotsToRemove; i++) {
      int removeIndex = inventorySlots.size() - 1;
      inventorySlots.remove(removeIndex);
      inventoryItemStacks.remove(removeIndex);
      slotLocations.remove(removeIndex);
    }
    addFilterSlots(dir);

    for (FilterChangeListener list : filterListeners) {
      list.onFilterChanged();
    }
  }

  public void setInputSlotsVisible(boolean visible) {
    if(itemConduit == null) {
      return;
    }
    setSlotsVisible(visible, inputFilterUpgradeSlot, inputFilterUpgradeSlot + 1);
    setSlotsVisible(visible, speedUpgradeSlot, speedUpgradeSlot + 1);

    if(inputFilter == null) {
      return;
    }
    int startIndex = startFilterSlot;
    int endIndex = inputFilter.getSlotCount() + startIndex;
    setSlotsVisible(visible, startIndex, endIndex);

  }

  public void setOutputSlotsVisible(boolean visible) {

    if(itemConduit == null) {
      return;
    }
    
    setSlotsVisible(visible, outputFilterUpgradeSlot, outputFilterUpgradeSlot + 1);

    if(outputFilter == null) {
      return;
    }
    int startIndex = startFilterSlot + (inputFilter == null ? 0 : inputFilter.getSlotCount());
    int endIndex = startIndex + outputFilter.getSlotCount();
    setSlotsVisible(visible, startIndex, endIndex);

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
    if(par4EntityPlayer.worldObj != null) {
      if(par1 >= startFilterSlot && itemConduit != null) {
        itemConduit.setInputFilter(dir, inputFilter);
        itemConduit.setOutputFilter(dir, outputFilter);
      }     
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

    boolean isInput;
    
    public FilterSlot(IInventory par1iInventory, int par2, int par3, int par4, boolean isInput) {
      super(par1iInventory, par2, par3, par4);
      this.isInput = isInput;
    }

    @Override
    public void onSlotChanged() {
      filterChanged();
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack) {
      return inventory.isItemValidForSlot(0, par1ItemStack);
    }
    
  }

}
