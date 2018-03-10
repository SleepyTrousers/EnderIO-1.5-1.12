package crazypants.enderio.machine.invpanel;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class SlotCraftingWrapper extends SlotCrafting {

  TileInventoryPanel inventory; // Shadow super field
  private final InventoryCrafting craftMatrix;

  public SlotCraftingWrapper(EntityPlayer player, InventoryCrafting craftingInventory, IInventory inventory, int slotIndex, int xPosition, int yPosition) {
    super(player, craftingInventory, inventory, slotIndex, xPosition, yPosition);
    craftMatrix = craftingInventory;  
  }

  @Override
  public @Nonnull ItemStack onTake(@Nonnull EntityPlayer playerIn, @Nonnull ItemStack stack) {

    net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, craftMatrix);
    this.onCrafting(stack);
    net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);
    NonNullList<ItemStack> containeritems = CraftingManager.getRemainingItems(this.craftMatrix, playerIn.world);
    net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

    for (int i = 0; i < containeritems.size(); ++i) {
      ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
      ItemStack containeritemstack = containeritems.get(i);

      if (!itemstack.isEmpty()) {
        this.craftMatrix.decrStackSize(i, 1);
      }

      if (!containeritemstack.isEmpty()) {
        if (this.craftMatrix.getStackInSlot(i).isEmpty()) {
          this.craftMatrix.setInventorySlotContents(i, containeritemstack);
        } else {
          ItemStack remainder = ItemHandlerHelper.insertItem(inventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), containeritemstack, false);
          if (!remainder.isEmpty()) {
            if (!playerIn.inventory.addItemStackToInventory(remainder)) {
              playerIn.dropItem(remainder, false);
            }
          }
        }
      }
    }
    return stack;
  }

  @Override
  public @Nonnull ItemStack decrStackSize(int p_75209_1_) {
    if (this.getHasStack()) {
      // on a right click we are asked to craft half a result. Ignore that.
      return super.decrStackSize(this.getStack().getCount());
    }
    return super.decrStackSize(p_75209_1_);
  }
}
