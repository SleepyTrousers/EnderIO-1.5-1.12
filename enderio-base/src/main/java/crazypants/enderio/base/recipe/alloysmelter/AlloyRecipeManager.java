package crazypants.enderio.base.recipe.alloysmelter;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.RecipeConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.base.recipe.AbstractMachineRecipe;
import crazypants.enderio.base.recipe.BasicManyToOneRecipe;
import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeInput;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.base.recipe.lookup.TriItemLookup;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber(modid = EnderIO.MODID)
public class AlloyRecipeManager extends AbstractMachineRecipe {

  private static final @Nonnull String NAME = "Alloy Smelter";

  private static final @Nonnull AlloyRecipeManager instance = new AlloyRecipeManager();

  private @Nonnull TriItemLookup<IManyToOneRecipe> lookup = new TriItemLookup<>();

  public static AlloyRecipeManager getInstance() {
    return instance;
  }

  private AlloyRecipeManager() {
  }

  @SubscribeEvent
  public static void create(EnderIOLifecycleEvent.PostInit.Pre event) {
    MachineRecipeRegistry.instance.registerRecipe(instance);
  }

  @SubscribeEvent
  public static void remap(EnderIOLifecycleEvent.ModIdMappingEvent event) {
    Log.debug("Rebuilding recipe lookup table");
    Log.debug("Recipe lookup table has been rebuilt with ", instance.rebuild(), " primary entries");
  }

  public void addRecipe(boolean prohibitDupes, @Nonnull NNList<IRecipeInput> input, @Nonnull ItemStack output, int energyCost, float xpChance,
      @Nonnull RecipeLevel recipeLevel) {
    Recipe recipe = new Recipe(new RecipeOutput(output, 1, xpChance), energyCost, RecipeBonusType.NONE, recipeLevel, input.toArray(new IRecipeInput[0]));

    if (prohibitDupes && input.size() > 1) {
      addDedupedRecipe(recipe);
    } else if (needsSynthetics(recipe)) {
      addSyntheticRecipe(recipe);
    } else {
      addRecipe(new BasicManyToOneRecipe(recipe));
    }
  }

  private void addDedupedRecipe(@Nonnull Recipe recipe) {
    ItemStack output = recipe.getOutputs()[0].getOutput();
    Log.debug("Beginning de-duping loop for recipe that outputs ", output);
    RecipeOutput recipeOutput = recipe.getOutputs()[0];

    // split the recipe into many small recipes without duplicate inputs for multiple slots

    List<List<ItemStack>> inputStacks = recipe.getInputStackAlternatives();

    List<ItemStack> list0 = inputStacks.size() >= 1 ? inputStacks.get(0) : new NNList<>(Prep.getEmpty());
    List<ItemStack> list1 = inputStacks.size() >= 2 ? inputStacks.get(1) : new NNList<>(Prep.getEmpty());
    List<ItemStack> list2 = inputStacks.size() >= 3 ? inputStacks.get(2) : new NNList<>(Prep.getEmpty());

    Log.debug("Got these inputs:");
    Log.debug(" slot 0: ", list0);
    Log.debug(" slot 1: ", list1);
    Log.debug(" slot 2: ", list2);

    NNList<Tuple> seen = new NNList<>();

    for (ItemStack stack0 : list0) {
      IRecipeInput rinp0 = new RecipeInput(stack0, stack0.getItemDamage() != OreDictionary.WILDCARD_VALUE, recipe.getInputs()[0].getMulitplier(),
          recipe.getInputs()[1].getSlotNumber());
      for (ItemStack stack1 : list1) {
        IRecipeInput rinp1 = new RecipeInput(stack1, stack1.getItemDamage() != OreDictionary.WILDCARD_VALUE, recipe.getInputs()[1].getMulitplier(),
            recipe.getInputs()[1].getSlotNumber());
        for (ItemStack stack2 : list2) {
          Tuple t = new Tuple(stack0, stack1, stack2);
          if (t.isValid() && !seen.contains(t)) {
            seen.add(t);
            Log.debug("Found valid combination ", stack0, " / ", stack1, " / ", stack2);
            addRecipe(
                new BasicManyToOneRecipe(new Recipe(recipeOutput, recipe.getEnergyRequired(), recipe.getBonusType(), recipe.getRecipeLevel(),
                    new NNList<>().addIf(rinp0).addIf(rinp1)
                        .addIf(recipe.getInputs().length >= 3 ? new RecipeInput(stack2, stack2.getItemDamage() != OreDictionary.WILDCARD_VALUE,
                            recipe.getInputs()[2].getMulitplier(), recipe.getInputs()[2].getSlotNumber()) : null)
                        .toArray(new IRecipeInput[0]))).setSynthetic());
          } else {
            Log.debug("Skipping invalid combination ", stack0, " / ", stack1, " / ", stack2);
          }
        }
      }
    }

    if (seen.isEmpty()) {
      Log.warn("Splitting alloying recipe for ", output, " into sub-recipes yielded nothing. Something may be wrong with this recipe.");
    } else {
      Log.debug("Split alloying recipe for ", output, " into ", seen.size(), " sub-recipes");
    }
    // display recipe for JEI only (AlloyRecipeWrapper will explode this into a single display recipe with rotating inputs)
    addRecipe(new BasicManyToOneRecipe(recipe).setDedupeInput());
  }

  private void addSyntheticRecipe(@Nonnull Recipe recipe) {
    int er = recipe.getEnergyRequired();
    RecipeBonusType bns = recipe.getBonusType();
    RecipeLevel lvl = recipe.getRecipeLevel();
    RecipeOutput out = recipe.getOutputs()[0];
    IRecipeInput in = recipe.getInputs()[0];

    IRecipeInput in2 = in.copy();
    in2.shrinkStack(-in.getStackSize());
    RecipeOutput out2 = new RecipeOutput(out.getOutput(), out.getChance(), out.getExperiance());
    out2.getOutput().grow(out.getOutput().getCount());

    IRecipeInput in3 = in.copy();
    in3.shrinkStack(-in.getStackSize());
    in3.shrinkStack(-in.getStackSize());
    RecipeOutput out3 = new RecipeOutput(out.getOutput(), out.getChance(), out.getExperiance());
    out3.getOutput().grow(out.getOutput().getCount());
    out3.getOutput().grow(out.getOutput().getCount());

    addRecipe(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, lvl, new IRecipeInput[] { in.copy(), in.copy(), in.copy() })).setSynthetic());
    addRecipe(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, lvl, new IRecipeInput[] { in.copy(), in2.copy() })).setSynthetic());
    addRecipe(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, lvl, new IRecipeInput[] { in2.copy(), in.copy() })).setSynthetic());
    addRecipe(new BasicManyToOneRecipe(new Recipe(out2, er * 2, bns, lvl, new IRecipeInput[] { in.copy(), in.copy() })).setSynthetic());
    addRecipe(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, lvl, new IRecipeInput[] { in3.copy() })).setSynthetic());
    addRecipe(new BasicManyToOneRecipe(new Recipe(out2, er * 2, bns, lvl, new IRecipeInput[] { in2.copy() })).setSynthetic());
    addRecipe(new BasicManyToOneRecipe(recipe));
    Log.debug("Created 6 synthetic recipes for " + in.getInput() + " => " + out.getOutput());
  }

  private boolean needsSynthetics(Recipe recipe) {
    return RecipeConfig.createSyntheticRecipes.get() //
        && recipe.getInputs().length == 1 && recipe.getInputs()[0].getStackSize() <= (recipe.getInputs()[0].getInput().getMaxStackSize() / 3)
        && recipe.getOutputs()[0].getOutput().getCount() <= (recipe.getOutputs()[0].getOutput().getMaxStackSize() / 3);
  }

  private void addRecipe(@Nonnull IManyToOneRecipe recipe) {
    dupeCheckRecipe(recipe);
    addRecipeToLookup(lookup, recipe);
    addJEIIntegration(recipe);
  }

  private static void addRecipeToLookup(@Nonnull TriItemLookup<IManyToOneRecipe> lookup, @Nonnull IManyToOneRecipe recipe) {
    NNList<List<ItemStack>> list = recipe.getInputStackAlternatives();
    if (list.size() == 3) {
      for (ItemStack i0 : list.get(0)) {
        for (ItemStack i1 : list.get(1)) {
          for (ItemStack i2 : list.get(2)) {
            lookup.addRecipe(recipe, i0.getItem(), i1.getItem(), i2.getItem());
            lookup.addRecipe(recipe, i0.getItem(), i2.getItem(), i1.getItem());
            lookup.addRecipe(recipe, i1.getItem(), i0.getItem(), i2.getItem());
            lookup.addRecipe(recipe, i1.getItem(), i2.getItem(), i0.getItem());
            lookup.addRecipe(recipe, i2.getItem(), i0.getItem(), i1.getItem());
            lookup.addRecipe(recipe, i2.getItem(), i1.getItem(), i0.getItem());
          }
        }
      }
    } else if (list.size() == 2) {
      for (ItemStack i0 : list.get(0)) {
        for (ItemStack i1 : list.get(1)) {
          lookup.addRecipe(recipe, i0.getItem(), i1.getItem());
          lookup.addRecipe(recipe, i1.getItem(), i0.getItem());
        }
      }
    } else if (list.size() == 1) {
      for (ItemStack i0 : list.get(0)) {
        lookup.addRecipe(recipe, i0.getItem());
      }
    }
  }

  // Note:
  // * Recipes that require input de-duplication are generally not well suited for the smeltery
  // * Synthetic recipe are copies of some master recipe that was already added
  // * Recipes with only one input don't alloy well ;)
  private void addJEIIntegration(@Nonnull IManyToOneRecipe recipe) {
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

  private void dupeCheckRecipe(IRecipe recipe) {
    if (!recipe.isSynthetic()) {
      NNList<MachineRecipeInput> ins = new NNList<>();
      for (ItemStack stack : recipe.getInputStacks()) {
        ins.add(new MachineRecipeInput(-1, NullHelper.notnullM(stack, "NNList iterated with null")));
      }
      IRecipe rec = getRecipeForInputs(RecipeLevel.IGNORE, ins);
      if (rec != null && !rec.isSynthetic()) {
        Log.warn("The supplied recipe " + recipe + " for " + NAME + " may be a duplicate to: " + rec);
      }
    }
  }

  // a complete set of inputs => one recipe
  @Override
  public IRecipe getRecipeForInputs(@Nonnull RecipeLevel machineLevel, @Nonnull NNList<MachineRecipeInput> inputs) {
    for (IManyToOneRecipe rec : lookup.getRecipesLMRI(inputs)) {
      if (machineLevel.canMake(rec.getRecipeLevel()) && rec.isInputForRecipe(inputs)) {
        return rec;
      }
    }
    return null;
  }

  // a single input => true if it fits into any recipe
  @Override
  public boolean isValidInput(@Nonnull RecipeLevel machineLevel, @Nonnull MachineRecipeInput input) {
    for (IManyToOneRecipe recipe : lookup.getRecipes(input.item.getItem())) {
      if (machineLevel.canMake(recipe.getRecipeLevel())) {
        for (IRecipeInput ri : recipe.getInputs()) {
          if (ri.isInput(input.item)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  // some inputs => true if they fit into any recipe
  public boolean isValidRecipeComponents(@Nonnull RecipeLevel machineLevel, @Nonnull NNList<ItemStack> input) {
    for (IManyToOneRecipe recipe : lookup.getRecipesL(input)) {
      if (machineLevel.canMake(recipe.getRecipeLevel()) && recipe.isValidRecipeComponents(input)) {
        return true;
      }
    }
    return false;
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

    // nicer formatting above
    private static boolean eq(@Nonnull ItemStack a, @Nonnull ItemStack b) {
      return ItemStack.areItemsEqual(a, b);
    }

    @Override
    public int hashCode() {
      // not used, just making the compiler happy
      return stack0.hashCode() ^ stack1.hashCode() ^ stack2.hashCode();
    }
  }

  // only called when a player takes the output from the slot manually. Doesn't matter if it's a bit slow. Not nice that it has a good chance of producing wrong
  // data, but the vanilla furnace does the exact same thing, so...
  @Override
  public float getExperienceForOutput(@Nonnull ItemStack output) {
    for (IManyToOneRecipe recipe : lookup) {
      if (recipe.getOutput().getItem() == output.getItem() && recipe.getOutput().getItemDamage() == output.getItemDamage()) {
        return recipe.getOutputs()[0].getExperiance();
      }
    }
    return 0;
  }

  // only used by JEI integration, can be slow and wasteful
  public @Nonnull NNList<IManyToOneRecipe> getRecipes() {
    NNList<IManyToOneRecipe> result = new NNList<>();
    for (IManyToOneRecipe recipe : lookup) {
      result.add(recipe);
    }
    return result;
  }

  /**
   * Rebuilds the internal recipe lookup tree. This should be called when the {@link Things} used in the recipes have changed so the item lists are re-read.
   */
  public int rebuild() {
    int count = 0;
    TriItemLookup<IManyToOneRecipe> newLookup = new TriItemLookup<>();
    for (NNIterator<IManyToOneRecipe> iterator = lookup.iterator(); iterator.hasNext();) {
      addRecipeToLookup(newLookup, iterator.next());
      count++;
    }
    lookup = newLookup;
    return count;
  }

  @Override
  public @Nonnull String getUid() {
    return "AlloySmelterRecipe";
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.ALLOYSMELTER;
  }

}
