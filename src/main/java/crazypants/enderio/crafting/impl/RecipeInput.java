package crazypants.enderio.crafting.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.crafting.IRecipeInput;

public class RecipeInput extends RecipeComponent implements IRecipeInput {

  private final List<ItemStack> equivelents;

  public RecipeInput(ItemStack itemPrototype, int slot, ItemStack... equivs) {
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
    this(input, input == null ? false : input.getItemDamage() == 0);
  }

  public RecipeInput(ItemStack input, boolean addSubtypes) {
    super(input, -1);
    Item inputItem = input == null ? null : input.getItem();
    if(addSubtypes && inputItem != null && inputItem.getHasSubtypes()) {
      equivelents = new ArrayList<ItemStack>();
      ArrayList<ItemStack> sublist = new ArrayList<ItemStack>();
      inputItem.getSubItems(inputItem, null, sublist);
      for (ItemStack st : sublist) {
        if(itemPrototype.getItemDamage() != st.getItemDamage()) {
          equivelents.add(st.copy());
        }
      }
    } else {
      equivelents = Collections.emptyList();
    }
  }

  public RecipeInput(FluidStack fluidInput, int slot) {
    super(fluidInput, slot);
    equivelents = Collections.emptyList();
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
