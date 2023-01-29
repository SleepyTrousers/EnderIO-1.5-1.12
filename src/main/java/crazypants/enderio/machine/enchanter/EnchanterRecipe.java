package crazypants.enderio.machine.enchanter;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import crazypants.enderio.machine.recipe.RecipeInput;

public class EnchanterRecipe {

    private final RecipeInput input;
    private final Enchantment enchantment;
    private final int costPerLevel;
    private final int stackSizePerLevel;

    public static Enchantment getEnchantmentFromName(String enchantmentName) {
        for (Enchantment ench : Enchantment.enchantmentsList) {
            if (ench != null && ench.getName() != null && ench.getName().equals(enchantmentName)) {
                return ench;
            }
        }
        return null;
    }

    public EnchanterRecipe(RecipeInput curInput, String enchantmentName, int costPerLevel) {
        input = curInput;
        enchantment = getEnchantmentFromName(enchantmentName);
        this.costPerLevel = costPerLevel;
        stackSizePerLevel = curInput.getInput().stackSize;
    }

    public EnchanterRecipe(RecipeInput input, Enchantment enchantment, int costPerLevel) {
        this.input = input;
        this.enchantment = enchantment;
        this.costPerLevel = costPerLevel;
        stackSizePerLevel = input.getInput().stackSize;
    }

    public boolean isInput(ItemStack stack) {
        if (stack == null || !isValid()) {
            return false;
        }
        return input.isInput(stack);
    }

    public boolean isValid() {
        return enchantment != null && input != null && input.getInput() != null && costPerLevel > -1;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public RecipeInput getInput() {
        return input;
    }

    public int getCostPerLevel() {
        return costPerLevel;
    }

    public int getLevelForStackSize(int size) {
        return Math.min(size / stackSizePerLevel, enchantment.getMaxLevel());
    }

    public int getItemsPerLevel() {
        return stackSizePerLevel;
    }
}
