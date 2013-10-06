package crazypants.enderio.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IRecipeInput extends IRecipeComponent {

  public List<ItemStack> getEquivelentInputs();

}
