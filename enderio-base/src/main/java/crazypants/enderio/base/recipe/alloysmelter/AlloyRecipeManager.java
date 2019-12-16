package crazypants.enderio.base.recipe.alloysmelter;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
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
      Log.debug("Beginning de-duping loop for recipe that outputs ", output);
      // display recipe for JEI only (AlloyRecipeWrapper will explode this into a single display recipe with rotating inputs)
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

      Log.debug("Got these inputs:");
      Log.debug(" slot 0: ", list0);
      Log.debug(" slot 1: ", list1);
      Log.debug(" slot 2: ", list2);

      NNList<Tuple> list = new NNList<>();

      for (ItemStack stack0 : list0) {
        IRecipeInput rinp0 = new RecipeInput(stack0, stack0.getItemDamage() != OreDictionary.WILDCARD_VALUE, input.get(0).getMulitplier(),
            input.get(0).getSlotNumber());
        for (ItemStack stack1 : list1) {
          IRecipeInput rinp1 = new RecipeInput(stack1, stack1.getItemDamage() != OreDictionary.WILDCARD_VALUE, input.get(1).getMulitplier(),
              input.get(1).getSlotNumber());
          for (ItemStack stack2 : list2) {
            Tuple t = new Tuple(stack0, stack1, stack2);
            if (t.isValid() && !list.contains(t)) {
              list.add(t);
              Log.debug("Found valid combination ", stack0, " / ", stack1, " / ", stack2);
              addRecipe(new BasicManyToOneRecipe(new Recipe(recipeOutput, energyCost, RecipeBonusType.NONE,
                  new NNList<>().addIf(rinp0).addIf(rinp1).addIf(input.size() >= 3 ? new RecipeInput(stack2,
                      stack2.getItemDamage() != OreDictionary.WILDCARD_VALUE, input.get(2).getMulitplier(), input.get(2).getSlotNumber()) : null)
                      .toArray(new IRecipeInput[0]))).setSynthetic());
            } else {
              Log.debug("Skipping invalid combination ", stack0, " / ", stack1, " / ", stack2);
            }
          }
        }
      }

      if (list.isEmpty()) {
        Log.warn("Splitting alloying recipe for ", output, " into sub-recipes yielded nothing. Something may be wrong with this recipe.");
      } else {
        Log.debug("Split alloying recipe for ", output, " into ", list.size(), " sub-recipes");
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
        ItemStack input = recipe.getInputs()[i].getInput();
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

  private static class Tuple {
    protected final @Nonnull ItemStack stack0, stack1, stack2;

    Tuple(@Nullable ItemStack stack0, @Nullable ItemStack stack1, @Nullable ItemStack stack2) {
      this.stack0 = NullHelper.first(stack0, Prep.getEmpty());
      this.stack1 = NullHelper.first(stack1, Prep.getEmpty());
      this.stack2 = NullHelper.first(stack2, Prep.getEmpty());
    }

    boolean isValid() {
      // Note: stack2 is optional, 0 and 1 are not
      return Prep.isValid(stack0) && Prep.isValid(stack1) && !eq(stack0, stack1) && !eq(stack0, stack2) && !eq(stack2, stack1);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Tuple)) {
        return false;
      }
      Tuple other = (Tuple) o;
      return this == other || //
          (eq(stack0, other.stack0) && eq(stack1, other.stack1) && eq(stack2, other.stack2)) || //
          (eq(stack0, other.stack1) && eq(stack1, other.stack0) && eq(stack2, other.stack2)) || //
          (eq(stack0, other.stack0) && eq(stack1, other.stack2) && eq(stack2, other.stack1)) || //
          (eq(stack0, other.stack2) && eq(stack1, other.stack1) && eq(stack2, other.stack0)) || //
          (eq(stack0, other.stack1) && eq(stack1, other.stack2) && eq(stack2, other.stack0)) || //
          (eq(stack0, other.stack0) && eq(stack1, other.stack2) && eq(stack2, other.stack1));
    }

    @Override
    public int hashCode() {
      // not used, just making the compiler happy
      return stack0.hashCode() ^ stack1.hashCode() ^ stack2.hashCode();
    }
  }

}
