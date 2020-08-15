package crazypants.enderio.invpanel.util;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.invpanel.invpanel.TileInventoryPanel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;

public class StoredCraftingRecipe {

  private final @Nonnull NNList<ItemStack> slots;

  private boolean updateResult;
  private @Nonnull ItemStack result;

  public StoredCraftingRecipe() {
    slots = new NNList<ItemStack>(9, ItemStack.EMPTY);
    result = ItemStack.EMPTY;
  }

  public boolean loadFromCraftingGrid(List<Slot> craftingGrid) {
    if (craftingGrid.size() != 9) {
      return false;
    }
    int count = 0;
    for (int slotIdx = 0; slotIdx < 9 && slotIdx < craftingGrid.size(); slotIdx++) {
      Slot slot = craftingGrid.get(slotIdx);
      ItemStack stack = slot.getStack();
      if (!stack.isEmpty()) {
        stack = stack.copy();
        stack.setCount(1);
        slots.set(slotIdx, stack);
        count++;
      }
    }
    updateResult = true;
    return count > 0;
  }

  @Nonnull
  public ItemStack get(int index) {
    return slots.get(index);
  }

  public boolean isEqual(List<Slot> craftingGrid) {
    for (int slotIdx = 0; slotIdx < 9; slotIdx++) {
      ItemStack a = slots.get(slotIdx);
      ItemStack b = craftingGrid.get(slotIdx).getStack();
      if ((!a.isEmpty() || !b.isEmpty()) && !ItemUtil.areStacksEqual(a, b)) {
        return false;
      }
    }
    return true;
  }

  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    for (int slotIdx = 0; slotIdx < 9; slotIdx++) {
      ItemStack itemStack = slots.get(slotIdx);
      if (!itemStack.isEmpty()) {
        NBTTagCompound itemStackNBT = new NBTTagCompound();
        itemStack.writeToNBT(itemStackNBT);
        nbtRoot.setTag(Integer.toString(slotIdx), itemStackNBT);
      }
    }
  }

  public boolean readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    boolean hasSlots = false;
    for (int slotIdx = 0; slotIdx < 9; slotIdx++) {
      NBTTagCompound itemStackNBT = (NBTTagCompound) NullHelper.untrust(nbtRoot.getTag(Integer.toString(slotIdx)));
      if (itemStackNBT != null) {
        slots.set(slotIdx, new ItemStack(itemStackNBT));
        hasSlots = true;
      } else {
        slots.set(slotIdx, ItemStack.EMPTY);
      }
    }
    updateResult = true;
    result = ItemStack.EMPTY;
    return hasSlots;
  }

  @Nonnull
  public ItemStack getResult(@Nonnull TileInventoryPanel te) {
    if (updateResult) {
      findCraftingResult(te);
    }
    return result;
  }

  private void findCraftingResult(@Nonnull TileInventoryPanel te) {
    InventoryCrafting tmp = new InventoryCrafting(new Container() {
      @Override
      public boolean canInteractWith(@Nonnull EntityPlayer ep) {
        return false;
      }
    }, 3, 3);

    for (int i = 0; i < 9; i++) {
      tmp.setInventorySlotContents(i, slots.get(i));
    }

    result = CraftingManager.findMatchingResult(tmp, te.getWorld());
    if (!result.isEmpty()) {
      result = result.copy();
      result.setCount(1);
    }
    updateResult = false;
  }

  public @Nonnull NNList<ItemStack> getIngredients() {
    return this.slots;
  }
}
