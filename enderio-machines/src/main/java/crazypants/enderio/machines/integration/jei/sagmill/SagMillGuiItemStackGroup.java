package crazypants.enderio.machines.integration.jei.sagmill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.item.ItemStack;

public class SagMillGuiItemStackGroup implements IGuiItemStackGroup {
  private final @Nonnull IGuiItemStackGroup itemStacks;

  public SagMillGuiItemStackGroup(@Nonnull IGuiItemStackGroup itemStacks) {
    this.itemStacks = itemStacks;
  }

  @Override
  public void set(@Nonnull IIngredients ingredients) {
    itemStacks.set(ingredients);
  }

  @Override
  public void set(int slotIndex, @Nullable List<ItemStack> ingredients) {
    itemStacks.set(slotIndex, ingredients);
  }

  @Override
  public void setBackground(int slotIndex, @Nonnull IDrawable background) {
    itemStacks.setBackground(slotIndex, background);
  }

  @Override
  public @Nonnull Map<Integer, ? extends IGuiIngredient<ItemStack>> getGuiIngredients() {
    final Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = new HashMap<>(itemStacks.getGuiIngredients());
    guiIngredients.remove(5);
    return guiIngredients;
  }

  @Override
  public void init(int slotIndex, boolean input, @Nonnull IIngredientRenderer<ItemStack> ingredientRenderer, int xPosition, int yPosition, int width,
      int height, int xPadding, int yPadding) {
    itemStacks.init(slotIndex, input, ingredientRenderer, xPosition, yPosition, width, height, xPadding, yPadding);
  }

  @Override
  public void setOverrideDisplayFocus(@Nullable IFocus<ItemStack> focus) {
    itemStacks.setOverrideDisplayFocus(focus);
  }

  @Override
  public void init(int slotIndex, boolean input, int xPosition, int yPosition) {
    itemStacks.init(slotIndex, input, xPosition, yPosition);
  }

  @Override
  public void set(int slotIndex, @Nullable ItemStack itemStack) {
    itemStacks.set(slotIndex, itemStack);
  }

  @Override
  public void addTooltipCallback(@Nonnull ITooltipCallback<ItemStack> tooltipCallback) {
    itemStacks.addTooltipCallback(tooltipCallback);
  }
}