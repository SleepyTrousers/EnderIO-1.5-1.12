package crazypants.enderio.crafting.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import crazypants.enderio.crafting.IRecipeInput;
import crazypants.util.Util;

public class RecipeInput extends RecipeComponent implements IRecipeInput {

  private final List<ItemStack> equivelents;

  public RecipeInput(ItemStack itemPrototype, int slot, boolean checkNBT, ItemStack... equivs) {
    super(itemPrototype, slot);
    if(equivs == null) {
      equivelents = Collections.emptyList();
    } else {
      equivelents = new ArrayList<ItemStack>(equivs.length);
      for (ItemStack st : equivs) {
        equivelents.add(st);
      }
    }
  }

  public RecipeInput(ItemStack input) {
    this(input, input.getItemDamage() == 0);
  }

  public RecipeInput(ItemStack input, boolean addSubtypes) {
    super(input, -1);
    Item inputItem = Util.getItem(input.itemID);
    if(addSubtypes && inputItem != null && inputItem.getHasSubtypes()) {
      equivelents = new ArrayList<ItemStack>();
      ArrayList<ItemStack> sublist = new ArrayList<ItemStack>();
      inputItem.getSubItems(inputItem.itemID, null, sublist);
      for (ItemStack st : sublist) {
        if(itemPrototype.getItemDamage() != st.getItemDamage()) {
          equivelents.add(st.copy());
        }
      }
    } else {
      equivelents = Collections.emptyList();
    }
  }

  @Override
  public boolean isEquivalent(ItemStack candidate) {
    if(isEqual(itemPrototype, candidate)) {
      return true;
    }
    for (ItemStack stack : equivelents) {
      if(isEqual(stack, candidate)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<ItemStack> getEquivelentInputs() {
    return equivelents;
  }

}
