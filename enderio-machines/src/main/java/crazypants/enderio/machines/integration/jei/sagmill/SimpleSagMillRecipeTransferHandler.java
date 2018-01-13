package crazypants.enderio.machines.integration.jei.sagmill;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.machines.machine.sagmill.ContainerSagMill;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.startup.StackHelper;
import mezz.jei.transfer.BasicRecipeTransferHandler;
import mezz.jei.transfer.BasicRecipeTransferInfo;
import net.minecraft.entity.player.EntityPlayer;

public class SimpleSagMillRecipeTransferHandler extends BasicRecipeTransferHandler<ContainerSagMill.Simple> {

  public SimpleSagMillRecipeTransferHandler(IModRegistry registry, @Nonnull String uid, int recipeSlotStart, int recipeSlotCount, int inventorySlotStart,
      int inventorySlotCount) {
    super((StackHelper) registry.getJeiHelpers().getStackHelper(), registry.getJeiHelpers().recipeTransferHandlerHelper(),
        new BasicRecipeTransferInfo<ContainerSagMill.Simple>(ContainerSagMill.Simple.class, uid, recipeSlotStart, recipeSlotCount, inventorySlotStart,
            inventorySlotCount));
  }

  @Override
  @Nullable
  public IRecipeTransferError transferRecipe(@Nonnull ContainerSagMill.Simple container, @Nonnull IRecipeLayout recipeLayout, @Nonnull EntityPlayer player,
      boolean maxTransfer, boolean doTransfer) {

    return super.transferRecipe(container, new SagMillRecipeLayout(recipeLayout), player, maxTransfer, doTransfer);
  }
}