package crazypants.enderio.machines.integration.jei;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.crafter.ContainerCrafter;
import crazypants.enderio.machines.machine.crafter.GuiCrafter;
import crazypants.enderio.util.Prep;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class CrafterRecipeTransferHandler<E extends ContainerCrafter<?>> implements IRecipeTransferHandler<E> {

  public static void register(IModRegistry registry) {
    IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
    recipeTransferRegistry.addRecipeTransferHandler(new CrafterRecipeTransferHandler.Simple(), VanillaRecipeCategoryUid.CRAFTING);
    recipeTransferRegistry.addRecipeTransferHandler(new CrafterRecipeTransferHandler.Normal(), VanillaRecipeCategoryUid.CRAFTING);
    registry.addRecipeClickArea(GuiCrafter.class, 219 - 21, 43, 16, 16, VanillaRecipeCategoryUid.CRAFTING);
    registry.addRecipeCatalyst(new ItemStack(MachineObject.block_crafter.getBlockNN()), VanillaRecipeCategoryUid.CRAFTING);
    registry.addRecipeCatalyst(new ItemStack(MachineObject.block_simple_crafter.getBlockNN()), VanillaRecipeCategoryUid.CRAFTING);
  }

  public static class Simple extends CrafterRecipeTransferHandler<ContainerCrafter.Simple> {

    @Override
    public @Nonnull Class<ContainerCrafter.Simple> getContainerClass() {
      return ContainerCrafter.Simple.class;
    }

  }

  public static class Normal extends CrafterRecipeTransferHandler<ContainerCrafter.Normal> {

    @Override
    public @Nonnull Class<ContainerCrafter.Normal> getContainerClass() {
      return ContainerCrafter.Normal.class;
    }

  }

  @Override
  @Nullable
  public IRecipeTransferError transferRecipe(@Nonnull E crafter, @Nonnull IRecipeLayout recipeLayout, @Nonnull EntityPlayer player, boolean maxTransfer,
      boolean doTransfer) {

    if (!doTransfer) {
      return null;
    }

    List<? extends GhostSlot> dummySlots = crafter.getDummySlots();

    IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

    Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = itemStacks.getGuiIngredients();

    for (int i = 0; i < 9; i++) {
      if (i < dummySlots.size()) {
        if (guiIngredients.containsKey(i + 1)) {
          IGuiIngredient<ItemStack> guiIngredient = guiIngredients.get(i + 1);
          List<ItemStack> allIngredients = guiIngredient.getAllIngredients();
          if (!allIngredients.isEmpty()) {
            dummySlots.get(i).putStack(findBestMatchInInventory(crafter, allIngredients), 0);
          } else {
            dummySlots.get(i).putStack(Prep.getEmpty(), 0);
          }
        } else {
          dummySlots.get(i).putStack(Prep.getEmpty(), 0);
        }
      }
    }

    return null;
  }

  private @Nonnull ItemStack findBestMatchInInventory(E crafter, List<ItemStack> allIngredients) {
    if (allIngredients.size() > 1) {
      for (Slot slot : crafter.getPlayerSlots()) {
        if (slot.getHasStack()) {
          ItemStack stack = slot.getStack();
          for (ItemStack ingredient : allIngredients) {
            if (ingredient != null && ItemUtil.areStacksEqual(stack, ingredient)) {
              return ingredient;
            }
          }
        }
      }
    }
    return NullHelper.first(allIngredients.get(0), Prep.getEmpty());
  }

}