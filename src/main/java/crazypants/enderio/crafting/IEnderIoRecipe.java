package crazypants.enderio.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.ModObject;

public interface IEnderIoRecipe {

  public static final String PAINTER_ID = ModObject.blockPainter.unlocalisedName;

  public static final String ALLOY_SMELTER_ID = ModObject.blockAlloySmelter.unlocalisedName;

  public static final String SAG_MILL_ID = ModObject.blockSagMill.unlocalisedName;

  public static final String VAT_ID = ModObject.blockVat.unlocalisedName;

  String getCrafterId();

  List<IRecipeInput> getInputs();

  List<IRecipeOutput> getOutputs();

  boolean isInput(ItemStack input);

  boolean isOutput(ItemStack output);

  float getRequiredEnergy();

  boolean isOutput(FluidStack fluid);

}
