package crazypants.enderio.machine.invpanel;

import com.enderio.core.common.util.ItemUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class SlotCraftingWrapper extends SlotCrafting {

  private final InventoryCrafting craftMatrix;

  public SlotCraftingWrapper(EntityPlayer player, InventoryCrafting craftingInventory, IInventory p_i45790_3_, int slotIndex, int xPosition, int yPosition) {
    super(player, craftingInventory, p_i45790_3_, slotIndex, xPosition, yPosition);
    craftMatrix = craftingInventory;
  }

  @Override
  public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {

    net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, craftMatrix);
    this.onCrafting(stack);
    net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);
    ItemStack[] containeritems = CraftingManager.getInstance().getRemainingItems(this.craftMatrix, playerIn.world);
    net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

    for (int i = 0; i < containeritems.length; ++i) {
      ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
      ItemStack containeritemstack = containeritems[i];

      if (itemstack != null) {
        this.craftMatrix.decrStackSize(i, 1);
      }

      if (containeritemstack != null) {
        if (this.craftMatrix.getStackInSlot(i) == null) {
          this.craftMatrix.setInventorySlotContents(i, containeritemstack);
        } else {
          int numInserted = ItemUtil.doInsertItem(inventory, 10, 20, containeritemstack);
          if (numInserted < containeritemstack.getCount()) {
            containeritemstack.getCount() -= numInserted;
            if (!playerIn.inventory.addItemStackToInventory(containeritemstack)) {
              playerIn.dropItem(containeritemstack, false);
            }

          }
        }
      }
    }
  }

  @Override
  public ItemStack decrStackSize(int p_75209_1_) {
    if (this.getHasStack()) {
      // on a right click we are asked to craft half a result. Ignore that.
      return super.decrStackSize(this.getStack().getCount());
    }
    return super.decrStackSize(p_75209_1_);
  }
}
