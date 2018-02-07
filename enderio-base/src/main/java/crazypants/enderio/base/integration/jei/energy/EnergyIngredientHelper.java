package crazypants.enderio.base.integration.jei.energy;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.util.Prep;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.item.ItemStack;

public class EnergyIngredientHelper implements IIngredientHelper<EnergyIngredient> {

  @Override
  public @Nonnull List<EnergyIngredient> expandSubtypes(@Nonnull List<EnergyIngredient> ingredients) {
    return ingredients;
  }

  @Override
  @Nullable
  public EnergyIngredient getMatch(@Nonnull Iterable<EnergyIngredient> ingredients, @Nonnull EnergyIngredient ingredientToMatch) {
    for (EnergyIngredient energyIngredient : ingredients) {
      if (energyIngredient.getAmount() == ingredientToMatch.getAmount()) {
        return energyIngredient;
      }
    }
    return null;
  }

  @Override
  public @Nonnull String getDisplayName(@Nonnull EnergyIngredient ingredient) {
    return LangPower.RF(ingredient.getAmount());
  }

  @Override
  public @Nonnull String getUniqueId(@Nonnull EnergyIngredient ingredient) {
    return "enderio:energy";
  }

  @Override
  public @Nonnull String getWildcardId(@Nonnull EnergyIngredient ingredient) {
    return "enderio:energy";
  }

  @Override
  public @Nonnull String getModId(@Nonnull EnergyIngredient ingredient) {
    return EnderIO.DOMAIN;
  }

  @Override
  public @Nonnull Iterable<Color> getColors(@Nonnull EnergyIngredient ingredient) {
    return Collections.emptyList();
  }

  @Override
  public @Nonnull String getResourceId(@Nonnull EnergyIngredient ingredient) {
    return "enderio:energy";
  }

  @Override
  public @Nonnull ItemStack cheatIngredient(@Nonnull EnergyIngredient ingredient, boolean fullStack) {
    return Prep.getEmpty();
  }

  @Override
  public @Nonnull EnergyIngredient copyIngredient(@Nonnull EnergyIngredient ingredient) {
    return ingredient;
  }

  @Override
  public @Nonnull String getErrorInfo(@Nullable EnergyIngredient ingredient) {
    if (ingredient == null) {
      return "Obviously 'null' is not a valid ingredient...";
    }
    return LangPower.RF(ingredient.getAmount());
  }

}
