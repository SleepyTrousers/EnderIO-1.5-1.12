package crazypants.enderio.machine.recipe;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryRecipeInput extends RecipeInput {

  private int oreId;

  public OreDictionaryRecipeInput(ItemStack itemStack, int oreId, int slot) {
    this(itemStack, oreId, 1, slot);
  }

  public OreDictionaryRecipeInput(ItemStack stack, int oreId, float multiplier, int slot) {
    super(stack, true, multiplier, slot);
    this.oreId = oreId;
  }
  
  public OreDictionaryRecipeInput(OreDictionaryRecipeInput copy) {
    super(copy.getInput(), true, copy.getMulitplier(), copy.getSlotNumber());
    oreId = copy.oreId;
  }

  public RecipeInput copy() {
    return new OreDictionaryRecipeInput(this);
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
    for(ItemStack st : res) {
      st.stackSize = getInput().stackSize;
    }
    return res.toArray(new ItemStack[res.size()]);
  }

  @Override
  public String toString() {
    return "OreDictionaryRecipeInput [oreId=" + oreId + " name=" + OreDictionary.getOreName(oreId) + "]";
  }

}
