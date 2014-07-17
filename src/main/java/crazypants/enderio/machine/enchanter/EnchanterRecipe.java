package crazypants.enderio.machine.enchanter;

import crazypants.enderio.machine.recipe.RecipeInput;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public class EnchanterRecipe {

  private final RecipeInput input;
  private final Enchantment enchantment;
  
  public static Enchantment getEnchantmentFromName(String enchantmentName) {    
    for(Enchantment ench : Enchantment.enchantmentsList) {
      if(ench != null && ench.getName() != null && ench.getName().equals(enchantmentName)) {        
        return ench;
      }
    }
    return null;
  }
  
  public EnchanterRecipe(crazypants.enderio.machine.recipe.RecipeInput curInput, String enchantmentName) {
    this.input = curInput;
    enchantment = getEnchantmentFromName(enchantmentName);
  }

  public EnchanterRecipe(crazypants.enderio.machine.recipe.RecipeInput input, Enchantment enchantment) {  
    this.input = input;
    this.enchantment = enchantment;
  }
  
  public boolean isInput(ItemStack stack) {
    if(stack == null || !isValid()) {
      return false;
    }
    return input.isInput(stack);    
  }
  
  public boolean isValid() {
    return enchantment != null && input != null && input.getInput() != null;
  }

  public Enchantment getEnchantment() {
    return enchantment;
  }

  public RecipeInput getInput() {
    return input;
  }
  
  
  
  
}
