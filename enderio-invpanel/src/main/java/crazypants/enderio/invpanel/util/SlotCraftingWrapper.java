package crazypants.enderio.invpanel.util;

import javax.annotation.Nonnull;

import crazypants.enderio.invpanel.invpanel.TileInventoryPanel;
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

  public TileInventoryPanel inventory; // Shadow super field
  protected final @Nonnull EntityPlayer player; // Shadow super field
  protected int amountCrafted; // Shadow super field
  private final InventoryCrafting craftMatrix;

  public SlotCraftingWrapper(@Nonnull EntityPlayer player, @Nonnull InventoryCrafting craftingInventory, @Nonnull IInventory inventory, int slotIndex,
      int xPosition, int yPosition) {
    super(player, craftingInventory, inventory, slotIndex, xPosition, yPosition);
    craftMatrix = craftingInventory;
    this.player = player;
  }

  @Override
  @Nonnull
  public ItemStack onTake(@Nonnull EntityPlayer playerIn, @Nonnull ItemStack stack) {

    net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, craftMatrix);
    this.onCrafting(stack);
    net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);
    NonNullList<ItemStack> containeritems = CraftingManager.getRemainingItems(craftMatrix, playerIn.world);
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
          ItemStack remainder = ItemHandlerHelper.insertItem(inventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), containeritemstack,
              false);
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
  protected void onCrafting(@Nonnull ItemStack stack, int amount) {
    amountCrafted += amount;
    onCrafting(stack);
  }

  @Override
  protected void onCrafting(@Nonnull ItemStack stack) {
    if (amountCrafted > 0) {
      stack.onCrafting(player.world, player, amountCrafted);
      net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, craftMatrix);
    }

    amountCrafted = 0;
    // InventoryCraftResult inventorycraftresult = (InventoryCraftResult)this.inventory;
    // IRecipe irecipe = inventorycraftresult.getRecipeUsed();
    //
    // if (irecipe != null && !irecipe.isDynamic())
    // {
    // this.player.unlockRecipes(Lists.newArrayList(irecipe));
    // inventorycraftresult.setRecipeUsed((IRecipe)null);
    // }
  }

  @Override
  @Nonnull
  public ItemStack decrStackSize(int amount) {
    if (this.getHasStack()) {
      // on a right click we are asked to craft half a result. Ignore that.
      amountCrafted += getStack().getCount();
      return super.decrStackSize(getStack().getCount());
    }
    return super.decrStackSize(amount);
  }
}
