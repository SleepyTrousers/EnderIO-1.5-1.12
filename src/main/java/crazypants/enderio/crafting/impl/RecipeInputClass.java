package crazypants.enderio.crafting.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import crazypants.enderio.crafting.IRecipeInput;
import crazypants.util.Util;

public class RecipeInputClass<T> extends RecipeComponent implements IRecipeInput {

  private final List<ItemStack> equivelents;
  private final Class<T> allowedClass;

  public RecipeInputClass(ItemStack protoType, Class<T> allowedClass, ItemStack... knownSubtypes) {
    super(protoType, -1);
    this.allowedClass = allowedClass;
    if(knownSubtypes == null) {
      equivelents = Collections.emptyList();
    } else {
      equivelents = new ArrayList<ItemStack>();
      for (ItemStack subT : knownSubtypes) {
        equivelents.add(subT);
      }
    }
  }

  @Override
  public boolean isEquivalent(ItemStack candidate) {
    Block block = Util.getBlockFromItemId(candidate.itemID);
    Item item = Util.getItem(candidate.itemID);
    return (item != null && allowedClass.isAssignableFrom(item.getClass()) || (block != null && allowedClass.isAssignableFrom(block.getClass())));
  }

  @Override
  public List<ItemStack> getEquivelentInputs() {
    return equivelents;
  }

}
