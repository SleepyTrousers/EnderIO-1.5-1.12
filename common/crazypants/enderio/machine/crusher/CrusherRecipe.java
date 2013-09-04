package crazypants.enderio.machine.crusher;

import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.crusher.CrusherRecipeManager.Type;

class CrusherRecipe {

  final ItemStack input;
  final ItemStack output;
  final Type type;
  
  CrusherRecipe(ItemStack input, ItemStack outupt, Type type) {      
    this.input = input;
    this.output = outupt;
    this.type = type;
  }
  
  boolean isInput(ItemStack test) {
    if(test == null) {
      return false;
    }
    return test.itemID == input.itemID && test.getItemDamage() == input.getItemDamage();
  }
  
}
