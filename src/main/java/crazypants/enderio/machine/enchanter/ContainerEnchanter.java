package crazypants.enderio.machine.enchanter;

import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerEnchanter extends Container {

  private TileEnchanter enchanter;

  public ContainerEnchanter(EntityPlayer player, InventoryPlayer playerInv, TileEnchanter te) {

    enchanter = te;

    addSlotToContainer(new Slot(te, 0, 27, 35) {

      @Override
      public int getSlotStackLimit() {
        return 1;
      }

      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return enchanter.isItemValidForSlot(0, itemStack);
      }

      @Override
      public void onSlotChanged() {
        updateOutput();
      }

    });

    addSlotToContainer(new Slot(te, 1, 76, 35) {

      @Override
      public int getSlotStackLimit() {
        return 5;
      }

      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return enchanter.isItemValidForSlot(1, itemStack);
      }

      @Override
      public void onSlotChanged() {
        updateOutput();
      }

    });

    addSlotToContainer(new Slot(te, 2, 134, 35) {

      @Override
      public int getSlotStackLimit() {
        return 1;
      }

      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return false;
      }

      public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        if(!player.capabilities.isCreativeMode) {
          player.addExperienceLevel(-enchanter.getCurrentEnchantmentCost());
        }
        enchanter.setInventorySlotContents(0, (ItemStack) null);
        enchanter.setInventorySlotContents(1, (ItemStack) null);
        //          if (!p_i1800_2_.isRemote) {
        //              p_i1800_2_.playAuxSFX(1021, p_i1800_3_, p_i1800_4_, p_i1800_5_, 0);
        //          }
      }

      @Override
      public boolean canTakeStack(EntityPlayer player) {        
        return playerHasEnoughLevels(player);
      }

    });

    int x = 8;
    int y = 84;
    // add players inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, x + j * 18, y + i * 18));
      }
    }

    for (int i = 0; i < 9; ++i) {
      addSlotToContainer(new Slot(playerInv, i, x + i * 18, y + 58));
    }
  }

  @Override
  public boolean canInteractWith(EntityPlayer p_75145_1_) {
    return true;
  }
  
  public boolean playerHasEnoughLevels(EntityPlayer player) {  
    if(player.capabilities.isCreativeMode) {
      return true;
    }
    return player.experienceLevel >= enchanter.getCurrentEnchantmentCost();
  }

  private void updateOutput() {
    ItemStack output = null;
    EnchantmentData enchantment = enchanter.getCurrentEnchantment();
    if(enchantment != null) {
      output = new ItemStack(Items.enchanted_book);
      Items.enchanted_book.addEnchantment(output, enchantment);
    }
    enchanter.setOutput(output);
  }

  public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
    ItemStack copyStack = null;
    Slot slot = (Slot) this.inventorySlots.get(par2);

    if(slot != null && slot.getHasStack()) {
      ItemStack origStack = slot.getStack();
      copyStack = origStack.copy();

      if(par2 < 2) {
        if(!mergeItemStack(origStack, 2, inventorySlots.size(), true)) {
          return null;
        }
      } else {
        if(!enchanter.isItemValidForSlot(0, origStack) || !mergeItemStack(origStack, 0, 1, false)) {
          if(!enchanter.isItemValidForSlot(1, origStack) || !mergeItemStack(origStack, 1, 2, false)) {
            return null;
          }
        }
      }
      if(origStack.stackSize == 0) {
        slot.putStack((ItemStack) null);
      } else {
        slot.onSlotChanged();
      }
    }
    return copyStack;
  }

}
