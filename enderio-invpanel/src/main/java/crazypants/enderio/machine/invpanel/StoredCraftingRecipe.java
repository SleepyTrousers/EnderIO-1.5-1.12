package crazypants.enderio.machine.invpanel;

import java.util.List;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NullHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;

public class StoredCraftingRecipe {

  private final ItemStack[] slots;

  private boolean updateResult;
  private ItemStack result;

  public StoredCraftingRecipe() {
    slots = new ItemStack[9];
  }

  public boolean loadFromCraftingGrid(List<Slot> craftingGrid) {
    if (craftingGrid.size() != 9) {
      return false;
    }
    int count = 0;
    for(int slotIdx = 0; slotIdx < 9 && slotIdx < craftingGrid.size(); slotIdx++) {
      Slot slot = craftingGrid.get(slotIdx);
      ItemStack stack = slot.getStack();
      if(stack != null) {
        stack = stack.copy();
        stack.setCount(1);
        slots[slotIdx] = stack;
        count++;
      }
    }
    return count > 0;
  }

  public ItemStack get(int index) {
    return slots[index];
  }

  public boolean isEqual(List<Slot> craftingGrid) {
    for(int slotIdx = 0; slotIdx < 9; slotIdx++) {
      ItemStack a = slots[slotIdx];
      ItemStack b = craftingGrid.get(slotIdx).getStack();
      if((a != null || b != null) && !ItemUtil.areStacksEqual(a, b)) {
        return false;
      }
    }
    return true;
  }

  public void writeToNBT(NBTTagCompound nbtRoot) {
    for(int slotIdx = 0; slotIdx < 9; slotIdx++) {
      ItemStack itemStack = slots[slotIdx];
      if(itemStack != null) {
        NBTTagCompound itemStackNBT = new NBTTagCompound();
        itemStack.writeToNBT(itemStackNBT);
        nbtRoot.setTag(Integer.toString(slotIdx), itemStackNBT);
      }
    }
  }

  public boolean readFromNBT(NBTTagCompound nbtRoot) {
    boolean hasSlots = false;
    for(int slotIdx = 0; slotIdx < 9; slotIdx++) {
      NBTTagCompound itemStackNBT = (NBTTagCompound) NullHelper.untrust(nbtRoot.getTag(Integer.toString(slotIdx)));
      if(itemStackNBT != null) {
        slots[slotIdx] = new ItemStack(itemStackNBT);
        hasSlots = true;
      } else {
        slots[slotIdx] = null;
      }
    }
    updateResult = true;
    result = null;
    return hasSlots;
  }

  public ItemStack getResult(TileInventoryPanel te) {
    if(updateResult) {
      findCraftingResult(te);
    }
    return result;
  }

  private void findCraftingResult(TileInventoryPanel te) {
    InventoryCrafting tmp = new InventoryCrafting(new Container() {
      @Override
      public boolean canInteractWith(EntityPlayer ep) {
        return false;
      }
    }, 3, 3);

    for (int i = 0; i < 9; i++) {
      tmp.setInventorySlotContents(i, slots[i]);
    }

    result = CraftingManager.findMatchingResult(tmp, te.getWorld());
    if(result != null) {
      result = result.copy();
      result.setCount(1);
    }
    updateResult = false;
  }
}
