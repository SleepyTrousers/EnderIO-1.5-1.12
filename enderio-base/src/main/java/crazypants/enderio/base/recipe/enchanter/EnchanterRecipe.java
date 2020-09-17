package crazypants.enderio.base.recipe.enchanter;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.config.EnchanterConfig;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.util.Prep;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;

public class EnchanterRecipe implements IMachineRecipe {

  private final @Nonnull RecipeLevel recipeLevel;
  private static final @Nonnull Things BOOK = new Things().add(Items.WRITABLE_BOOK);
  private static final @Nonnull Things LAPIS = new Things("oredict:gemLapis");
  private final @Nonnull Things input;
  private final int stackSizePerLevel;
  private final @Nonnull Enchantment enchantment;
  private final double costMultiplier;
  private final @Nonnull String uuid;

  public EnchanterRecipe(@Nonnull RecipeLevel recipeLevel, @Nonnull Things input, int stackSizePerLevel, @Nonnull Enchantment enchantment,
      double costMultiplier) {
    this.recipeLevel = recipeLevel;
    this.input = input;
    this.stackSizePerLevel = stackSizePerLevel;
    this.enchantment = enchantment;
    this.costMultiplier = costMultiplier;
    this.uuid = NullHelper.first(input.getItemStacksRaw().get(0).toString(), "invalid Recipe");
  }

  public @Nonnull Enchantment getEnchantment() {
    return enchantment;
  }

  public @Nonnull Things getBook() {
    return BOOK;
  }

  public @Nonnull Things getLapis() {
    return LAPIS;
  }

  public @Nonnull Things getInput() {
    return input;
  }

  private int getLevelForStackSize(int size) {
    return Math.min(size / stackSizePerLevel, enchantment.getMaxLevel());
  }

  public int getItemsPerLevel() {
    return stackSizePerLevel;
  }

  @Override
  @Nonnull
  public String getUid() {
    return uuid;
  }

  @Override
  public int getEnergyRequired(@Nonnull NNList<MachineRecipeInput> inputs) {
    return 0;
  }

  @Override
  public boolean isRecipe(@Nonnull RecipeLevel machineLevel, @Nonnull NNList<MachineRecipeInput> inputs) {
    if (!recipeLevel.canMake(machineLevel)) {
      return false;
    }
    ItemStack slot0 = MachineRecipeInput.getInputForSlot(0, inputs);
    ItemStack slot1 = MachineRecipeInput.getInputForSlot(1, inputs);
    ItemStack slot2 = MachineRecipeInput.getInputForSlot(2, inputs);
    if (!BOOK.contains(slot0) || !input.contains(slot1) || !LAPIS.contains(slot2)) {
      return false;
    }
    int level = getLevelForStackSize(slot1.getCount());
    if (level < 1) {
      return false;
    }
    return slot2.getCount() >= getLapizForLevel(level);
  }

  @Override
  @Nonnull
  public ResultStack[] getCompletedResult(long nextSeed, float chanceMultiplier, @Nonnull NNList<MachineRecipeInput> inputs) {
    ItemStack slot1 = MachineRecipeInput.getInputForSlot(1, inputs);
    int level = getLevelForStackSize(slot1.getCount());
    EnchantmentData enchantmentData = new EnchantmentData(enchantment, level);
    ItemStack output = new ItemStack(Items.ENCHANTED_BOOK);
    ItemEnchantedBook.addEnchantment(output, enchantmentData);
    return new ResultStack[] { new ResultStack(output) };
  }

  @Override
  public boolean isValidInput(@Nonnull RecipeLevel machineLevel, @Nonnull MachineRecipeInput inputs) {
    if (!RecipeLevel.IGNORE.canMake(machineLevel)) {
      return false;
    }
    ItemStack slot0 = MachineRecipeInput.getInputForSlot(0, inputs);
    ItemStack slot1 = MachineRecipeInput.getInputForSlot(1, inputs);
    ItemStack slot2 = MachineRecipeInput.getInputForSlot(2, inputs);
    return (Prep.isInvalid(slot0) || BOOK.contains(slot0)) //
        && (Prep.isInvalid(slot1) || input.contains(slot1)) //
        && (Prep.isInvalid(slot2) || LAPIS.contains(slot2));
  }

  @Override
  @Nonnull
  public String getMachineName() {
    return MachineRecipeRegistry.ENCHANTER;
  }

  @Override
  @Nonnull
  public NNList<MachineRecipeInput> getQuantitiesConsumed(@Nonnull NNList<MachineRecipeInput> inputs) {
    ItemStack slot0 = MachineRecipeInput.getInputForSlot(0, inputs).copy();
    ItemStack slot1 = MachineRecipeInput.getInputForSlot(1, inputs).copy();
    ItemStack slot2 = MachineRecipeInput.getInputForSlot(2, inputs).copy();

    int level = getLevelForStackSize(slot1.getCount());
    slot0.setCount(1);
    slot1.setCount(stackSizePerLevel * level);
    slot2.setCount(getLapizForLevel(level));

    NNList<MachineRecipeInput> result = new NNList<MachineRecipeInput>();
    result.add(new MachineRecipeInput(0, slot0));
    result.add(new MachineRecipeInput(1, slot1));
    result.add(new MachineRecipeInput(2, slot2));
    return result;
  }

  public int getXPCost(@Nonnull NNList<MachineRecipeInput> inputs) {
    ItemStack slot1 = MachineRecipeInput.getInputForSlot(1, inputs);
    int level = getLevelForStackSize(slot1.getCount());
    return getXPCostForLevel(level);
  }

  private int getXPCostForLevel(int level) {
    level = Math.min(level, enchantment.getMaxLevel());
    int cost = getRawXPCostForLevel(level);
    if (level < enchantment.getMaxLevel()) {
      // min cost of half the next levels XP cause books combined in anvil
      int nextCost = getXPCostForLevel(level + 1);
      cost = Math.max(nextCost / 2, cost);
    }
    return Math.max(1, cost);
  }

  private int getRawXPCostForLevel(int level) {
    // -1 cause its the index
    double min = Math.max(1, enchantment.getMinEnchantability(level));
    min *= costMultiplier; // per recipe scaling
    int cost = (int) Math.round(min * EnchanterConfig.levelCostFactor.get()); // global scaling
    cost += EnchanterConfig.baseLevelCost.get(); // add base cost
    return cost;
  }

  public int getLapizForLevel(int level) {
    int res = enchantment.getMaxLevel() == 1 ? 5 : level;
    return (int) Math.max(1, Math.round(res * EnchanterConfig.lapisCostFactor.get()));
  }

}
