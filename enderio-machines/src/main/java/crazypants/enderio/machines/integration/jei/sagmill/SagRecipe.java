package crazypants.enderio.machines.integration.jei.sagmill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.integration.jei.RecipeWrapper;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.base.recipe.sagmill.GrindingBall;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.util.Prep;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;

public class SagRecipe extends RecipeWrapper {

  public SagRecipe(IRecipe recipe) {
    super(recipe);
  }

  @Override
  public void getIngredients(@Nonnull IIngredients ingredients) {
    List<List<ItemStack>> inputStacks = recipe.getInputStackAlternatives();

    List<EnergyIngredient> energies = new ArrayList<>();
    if (recipe.getBonusType().doChances()) {
      final List<ItemStack> balls = SagRecipe.getBalls();
      inputStacks.add(balls);

      for (ItemStack ball : balls) {
        if (ball != null && Prep.isValid(ball)) {
          energies.add(
              new EnergyIngredient((int) (recipe.getEnergyRequired() * SagMillRecipeManager.getInstance().getGrindballFromStack(ball).getPowerMultiplier())));
        } else {
          energies.add(new EnergyIngredient(recipe.getEnergyRequired()));
        }
      }
    } else {
      inputStacks.add(Collections.emptyList()); // no balls
      energies.add(new EnergyIngredient(recipe.getEnergyRequired()));
    }

    ingredients.setInputLists(ItemStack.class, inputStacks);
    ingredients.setInputLists(EnergyIngredient.class, Collections.singletonList(energies));

    List<ItemStack> outputs = new ArrayList<ItemStack>();
    for (RecipeOutput out : recipe.getOutputs()) {
      if (Prep.isValid(out.getOutput())) {
        outputs.add(out.getOutput());
      }
    }
    ingredients.setOutputs(ItemStack.class, outputs);
  }

  static @Nonnull List<ItemStack> getBalls() {
    NNList<GrindingBall> daBalls = SagMillRecipeManager.getInstance().getBalls();
    List<ItemStack> res = new ArrayList<ItemStack>();
    res.add(null); // sic! JEI will display null as an empty slot but ignore empty stacks completely
    for (GrindingBall ball : daBalls) {
      res.add(ball.getInput());
    }
    return res;
  }

}