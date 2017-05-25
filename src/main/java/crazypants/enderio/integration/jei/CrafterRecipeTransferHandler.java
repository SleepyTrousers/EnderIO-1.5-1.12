package crazypants.enderio.integration.jei;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.init.ModObject;
import crazypants.enderio.machine.crafter.ContainerCrafter;
import crazypants.enderio.machine.crafter.ContainerCrafter.DummySlot;
import crazypants.enderio.machine.crafter.GuiCrafter;
import crazypants.enderio.machine.gui.AbstractMachineContainer.SlotRange;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CrafterRecipeTransferHandler implements IRecipeTransferHandler {

  public static void register(IModRegistry registry) {
    IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
    recipeTransferRegistry.addRecipeTransferHandler(new CrafterRecipeTransferHandler());
    registry.addRecipeClickArea(GuiCrafter.class, 219 - 21, 43 + 19, 16, 16, VanillaRecipeCategoryUid.CRAFTING);
    registry.addRecipeCategoryCraftingItem(new ItemStack(ModObject.blockCrafter.getBlock()), VanillaRecipeCategoryUid.CRAFTING);
  }

  private CrafterRecipeTransferHandler() {
  }

  @Override
  public Class<? extends Container> getContainerClass() {
    return ContainerCrafter.class;
  }

  @Override
  public String getRecipeCategoryUid() {
    return VanillaRecipeCategoryUid.CRAFTING;
  }

  @Override
  @Nullable
  public IRecipeTransferError transferRecipe(@Nonnull Container container, @Nonnull IRecipeLayout recipeLayout, @Nonnull EntityPlayer player,
      boolean maxTransfer, boolean doTransfer) {

    if (!doTransfer) {
      return null;
    }

    if (container instanceof ContainerCrafter) {
      ContainerCrafter crafter = (ContainerCrafter) container;

      List<DummySlot> dummySlots = crafter.getDummySlots();

      IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

      Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = itemStacks.getGuiIngredients();

      for (int i = 0; i < 9; i++) {
        if (i < dummySlots.size()) {
          if (guiIngredients.containsKey(i + 1)) {
            IGuiIngredient<ItemStack> guiIngredient = guiIngredients.get(i + 1);
            List<ItemStack> allIngredients = guiIngredient.getAllIngredients();
            if (!allIngredients.isEmpty()) {
              dummySlots.get(i).putStack(findBestMatchInInventory(crafter, allIngredients));
            } else {
              dummySlots.get(i).putStack(null);
            }
          } else {
            dummySlots.get(i).putStack(null);
          }
        }
      }
    }

    return null;
  }

  private static ItemStack findBestMatchInInventory(ContainerCrafter crafter, List<ItemStack> allIngredients) {
    if (allIngredients.size() > 1) {
      SlotRange slotRange = crafter.getPlayerInventorySlotRange(false);
      for (int i = slotRange.getStart(); i < slotRange.getEnd(); i++) {
        Slot slot = crafter.inventorySlots.get(i);
        if (slot.getHasStack()) {
          ItemStack stack = slot.getStack();
          for (ItemStack ingredient : allIngredients) {
            if (ItemUtil.areStacksEqual(stack, ingredient)) {
              return ingredient;
            }
          }
        }
      }
    }
    return allIngredients.get(0);
  }

}
