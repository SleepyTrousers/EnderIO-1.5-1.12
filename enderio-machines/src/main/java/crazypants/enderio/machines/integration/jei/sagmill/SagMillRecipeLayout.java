package crazypants.enderio.machines.integration.jei.sagmill;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IFocus;

public class SagMillRecipeLayout implements IRecipeLayout {

  private final @Nonnull IRecipeLayout recipeLayout;

  public SagMillRecipeLayout(@Nonnull IRecipeLayout recipeLayout) {
    this.recipeLayout = recipeLayout;
  }

  @Override
  public @Nonnull IGuiItemStackGroup getItemStacks() {
    return new SagMillGuiItemStackGroup(recipeLayout.getItemStacks());
  }

  @Override
  public @Nonnull IGuiFluidStackGroup getFluidStacks() {
    return recipeLayout.getFluidStacks();
  }

  @Override
  public @Nonnull <T> IGuiIngredientGroup<T> getIngredientsGroup(@Nonnull Class<T> ingredientClass) {
    return recipeLayout.getIngredientsGroup(ingredientClass);
  }

  @Override
  @Nullable
  public IFocus<?> getFocus() {
    return recipeLayout.getFocus();
  }

  @Override
  public void setRecipeTransferButton(int posX, int posY) {
    recipeLayout.setRecipeTransferButton(posX, posY);
  }

  @Override
  public void setShapeless() {
    recipeLayout.setShapeless();
  }
}