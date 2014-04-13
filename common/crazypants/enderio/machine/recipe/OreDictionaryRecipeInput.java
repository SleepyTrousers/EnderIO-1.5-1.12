package crazypants.enderio.machine.recipe;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryRecipeInput extends RecipeInput {

  private int oreId;

  public OreDictionaryRecipeInput(ItemStack itemStack, int oreId) {
    super(itemStack);
    this.oreId = oreId;
  }

  @Override
  public boolean isInput(ItemStack test) {
    if(test == null || oreId < 0) {
      return false;
    }
    try { //work around for issue #591
      return OreDictionary.getOreID(test) == oreId;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public ItemStack[] getEquivelentInputs() {
    ArrayList<ItemStack> res = OreDictionary.getOres(oreId);
    if(res == null || res.isEmpty()) {
      return null;
    }
    return res.toArray(new ItemStack[res.size()]);
  }

  @Override
  public String toString() {
    return "OreDictionaryRecipeInput [oreId=" + oreId + " name=" + OreDictionary.getOreName(oreId) + "]";
  }

}
