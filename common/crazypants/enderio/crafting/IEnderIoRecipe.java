package crazypants.enderio.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;

public interface IEnderIoRecipe {

  public static final String PAINTER_ID = ModObject.blockPainter.unlocalisedName;

  public static final String ALLOY_SMELTER_ID = ModObject.blockAlloySmelter.unlocalisedName;

  public static final String SAG_MILL_ID = ModObject.blockCrusher.unlocalisedName;

  String getCrafterId();

  List<IRecipeInput> getInputs();

  List<IRecipeOutput> getOutputs();

  boolean isInput(ItemStack input);

  boolean isOutput(ItemStack output);

  float getRequiredEnergy();

}
