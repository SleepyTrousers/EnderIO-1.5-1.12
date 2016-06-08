package crazypants.enderio.jei;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static crazypants.enderio.machine.invpanel.InventoryPanelContainer.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machine.invpanel.InventoryPanelContainer.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machine.invpanel.InventoryPanelContainer.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machine.invpanel.InventoryPanelContainer.NUM_RECIPE_SLOT;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.invpanel.InventoryPanelContainer;
import crazypants.enderio.machine.invpanel.client.CraftingHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mezz.jei.gui.ingredients.IGuiIngredient;
import mezz.jei.transfer.BasicRecipeTransferHandler;
import mezz.jei.transfer.BasicRecipeTransferInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

/**
 * Notes:
 * <p>
 * This doesn't quite follow JEI's mechanics, we also do partial transfers here. Then we cannot check beforehand if a recipe can be transfered. And we don't mix
 * inventory and remote inventories. We also don't report failures, or if we could transfer something at all.
 *
 */
public class InventoryPanelRecipeTransferHandler implements IRecipeTransferHandler {

  private final IRecipeTransferHandler recipeTransferHelper;

  public static void register(IModRegistry registry) {
    IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
    recipeTransferRegistry.addRecipeTransferHandler(new InventoryPanelRecipeTransferHandler());
    // registry.addRecipeClickArea(GuiInventoryPanel.class, 219 - 21, 43 + 19, 16, 16, VanillaRecipeCategoryUid.CRAFTING);
    registry.addRecipeCategoryCraftingItem(new ItemStack(EnderIO.blockInventoryPanel), VanillaRecipeCategoryUid.CRAFTING);
  }

  private InventoryPanelRecipeTransferHandler() {
    recipeTransferHelper = new BasicRecipeTransferHandler(new BasicRecipeTransferInfo(InventoryPanelContainer.class, VanillaRecipeCategoryUid.CRAFTING,
        FIRST_RECIPE_SLOT, NUM_RECIPE_SLOT, FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT));

  }

  @Override
  public Class<? extends Container> getContainerClass() {
    return InventoryPanelContainer.class;
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

    if (container instanceof InventoryPanelContainer) {
      InventoryPanelContainer invPanelContainer = (InventoryPanelContainer) container;

      IRecipeTransferError transferResult = recipeTransferHelper.transferRecipe(invPanelContainer, recipeLayout, player, maxTransfer, doTransfer);
      if (transferResult == null) {
        return null;
      }

      if (invPanelContainer.clearCraftingGrid()) {
        ItemStack[][] ingredients = new ItemStack[9][];

        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = itemStacks.getGuiIngredients();

        for (int i = 0; i < 9; i++) {
          if (guiIngredients.containsKey(i + 1)) {
            IGuiIngredient<ItemStack> guiIngredient = guiIngredients.get(i + 1);
            List<ItemStack> allIngredients = guiIngredient.getAllIngredients();
            if (!allIngredients.isEmpty()) {
              ingredients[i] = allIngredients.toArray(new ItemStack[0]);
            } else {
              ingredients[i] = null;
            }
          } else {
            ingredients[i] = null;
          }
        }

        new CraftingHelper(ingredients).refill(invPanelContainer, maxTransfer ? 64 : 1);
      }
    }

    return null;
  }

}
