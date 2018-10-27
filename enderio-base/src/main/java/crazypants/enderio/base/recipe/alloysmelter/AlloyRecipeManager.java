package crazypants.enderio.base.recipe.alloysmelter;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.base.recipe.BasicManyToOneRecipe;
import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.ManyToOneMachineRecipe;
import crazypants.enderio.base.recipe.ManyToOneRecipeManager;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeInput;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class AlloyRecipeManager extends ManyToOneRecipeManager {

  static final @Nonnull AlloyRecipeManager instance = new AlloyRecipeManager();

  public static AlloyRecipeManager getInstance() {
    return instance;
  }

  @Nonnull
  VanillaSmeltingRecipe vanillaRecipe = new VanillaSmeltingRecipe();

  public AlloyRecipeManager() {
    super("Alloy Smelter");
  }

  public @Nonnull VanillaSmeltingRecipe getVanillaRecipe() {
    return vanillaRecipe;
  }

  public void create() {
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.ALLOYSMELTER,
        new ManyToOneMachineRecipe("AlloySmelterRecipe", MachineRecipeRegistry.ALLOYSMELTER, this));
    // vanilla alloy furnace recipes
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.ALLOYSMELTER, vanillaRecipe);
  }

  public void addRecipe(boolean prohibitDupes, @Nonnull NNList<IRecipeInput> input, @Nonnull ItemStack output, int energyCost, float xpChance) {
    RecipeOutput recipeOutput = new RecipeOutput(output, 1, xpChance);

    if (prohibitDupes && input.size() > 1) {
      // display recipe for JEI only (AlloyRecipeWrapper will de-dupe this with the same logic into a single display recipe)
      final Recipe recipe = new Recipe(recipeOutput, energyCost, RecipeBonusType.NONE, input.toArray(new IRecipeInput[input.size()])) {
        @Override
        public boolean isValidInput(int slot, @Nonnull ItemStack item) {
          return false;
        }
      };

      // split the recipe into many small recipes without duplicate inputs for multiple slots

      List<List<ItemStack>> inputStacks = recipe.getInputStackAlternatives();

      List<ItemStack> list0 = inputStacks.size() >= 1 ? inputStacks.get(0) : new NNList<>(Prep.getEmpty());
      List<ItemStack> list1 = inputStacks.size() >= 2 ? inputStacks.get(1) : new NNList<>(Prep.getEmpty());
      List<ItemStack> list2 = inputStacks.size() >= 3 ? inputStacks.get(2) : new NNList<>(Prep.getEmpty());

      int count = 0;

      for (ItemStack stack0 : list0) {
        if (stack0 != null) {
          IRecipeInput rinp0 = new RecipeInput(stack0, stack0.getItemDamage() != OreDictionary.WILDCARD_VALUE, input.get(0).getMulitplier(),
              input.get(0).getSlotNumber());
          for (ItemStack stack1 : list1) {
            if (stack1 != null && !eq(stack0, stack1)) {
              IRecipeInput rinp1 = input.size() >= 2
                  ? new RecipeInput(stack1, stack1.getItemDamage() != OreDictionary.WILDCARD_VALUE, input.get(1).getMulitplier(), input.get(1).getSlotNumber())
                  : null;
              for (ItemStack stack2 : list2) {
                if (stack2 != null && !eq(stack0, stack2) && !eq(stack1, stack2)) {
                  IRecipeInput rinp2 = input.size() >= 3 ? new RecipeInput(stack2, stack2.getItemDamage() != OreDictionary.WILDCARD_VALUE,
                      input.get(2).getMulitplier(), input.get(2).getSlotNumber()) : null;
                  addRecipe(new BasicManyToOneRecipe(new Recipe(recipeOutput, energyCost, RecipeBonusType.NONE,
                      new NNList<>().addIf(rinp0).addIf(rinp1).addIf(rinp2).toArray(new IRecipeInput[0]))).setSynthetic());
                  count++;
                }
              }
            }
          }
        }
      }

      if (count == 0) {
        Log.warn("Splitting alloying recipe for ", output, " into sub-recipes yielded nothing. Something may be wrong with this recipe.");
      } else {
        Log.debug("Split alloying recipe for ", output, " into ", count, " sub-recipes");
        addRecipe(new BasicManyToOneRecipe(recipe).setDedupeInput());
      }
    } else {
      addRecipe(new Recipe(recipeOutput, energyCost, RecipeBonusType.NONE, input.toArray(new IRecipeInput[input.size()])));
    }
  }

  private static boolean eq(@Nonnull ItemStack a, @Nonnull ItemStack b) {
    return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage();
  }

  @Override
  public void addRecipe(@Nonnull IManyToOneRecipe recipe) {
    super.addRecipe(recipe);
    if (recipe.getInputs().length >= 2 && !recipe.isDedupeInput() && !recipe.isSynthetic()) {
      NNList<Things> inputs = new NNList<>();
      for (int i = 0; i < recipe.getInputs().length; i++) {
        ItemStack input = recipe.getInputs()[i].getInput().copy();
        Things inputThing = new Things();
        inputThing.add(input);
        inputThing.setSize(input.getCount());
        inputThing.setNbt(input.getTagCompound());
        inputs.add(inputThing);
      }

      ItemStack output = recipe.getOutputs()[0].getOutput().copy();
      Things outputThing = new Things();
      outputThing.add(output);
      outputThing.setSize(output.getCount());
      outputThing.setNbt(output.getTagCompound());

      TicProxy.registerAlloyRecipe(outputThing, inputs);
    }
  }

}
