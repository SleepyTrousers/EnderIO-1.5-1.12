package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.RecipeInput;
import crazypants.enderio.machine.alloy.BasicAlloyRecipe;

public class FusedQuartzRecipe implements IMachineRecipe {

  private static final int NUM_QUARTZ = 4;

  @Override
  public String getUid() {
    return ModObject.blockFusedQuartz.unlocalisedName;
  }

  @Override
  public float getEnergyRequired(RecipeInput... inputs) {
    return BasicAlloyRecipe.DEFAULT_ENERGY_USE;
  }

  @Override
  public boolean isRecipe(RecipeInput... inputs) {
    int numQuartz = 0;
    for (RecipeInput input : inputs) {
      if (input != null && input.item != null && input.item.itemID == Item.netherQuartz.itemID) {
        numQuartz += input.item.stackSize;
      }
    }
    return numQuartz >= NUM_QUARTZ;
  }

  @Override
  public ItemStack[] getCompletedResult(RecipeInput... inputs) {
    return new ItemStack[] { new ItemStack(ModObject.blockFusedQuartz.actualId, 1, 0) };
  }

  @Override
  public boolean isValidInput(int slotNumber, ItemStack item) {
    if (item != null && item.itemID == Item.netherQuartz.itemID) {
      return true;
    }
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockFusedQuartz.unlocalisedName;
  }

  @Override
  public RecipeInput[] getQuantitiesConsumed(RecipeInput[] inputs) {
    int[] numPerInput = new int[inputs.length];
    int numFound = 0;

    for (int i = 0; i < inputs.length; i++) {
      numPerInput[i] = getQuartzQuanity(inputs[i]);
    }

    int total = 0;
    int[] consumedPerInput = new int[inputs.length];
    for (int i = 0; i < NUM_QUARTZ; i++) {
      for (int j = 0; j < consumedPerInput.length; j++) {
        if (total < NUM_QUARTZ && numPerInput[j] - consumedPerInput[j] > 0) {
          total++;
          consumedPerInput[j]++;
        }
      }
    }

    if (total < NUM_QUARTZ) {
      System.out.println("FusedQuartzRecipe.getQuantitiesConsumed: Error!! No QuartzConsumed Consumed.");
      return new RecipeInput[0];
    }
    if (total > NUM_QUARTZ) {
      System.out.println("FusedQuartzRecipe.getQuantitiesConsumed: Error!! Consumed more than we should have.");
    }

    List<RecipeInput> res = new ArrayList<RecipeInput>();
    for (int i = 0; i < consumedPerInput.length; i++) {
      if (consumedPerInput[i] > 0) {
        RecipeInput consumed = new RecipeInput(inputs[i].slotNumber, new ItemStack(Item.netherQuartz, consumedPerInput[i]));
        res.add(consumed);
      }
    }
    return res.toArray(new RecipeInput[res.size()]);
  }

  private int getQuartzQuanity(RecipeInput ri) {
    if (ri != null && ri.item != null && ri.item.itemID == Item.netherQuartz.itemID) {
      return ri.item.stackSize;
    }
    return 0;
  }

  @Override
  public float getExperianceForOutput(ItemStack output) {
    if(output == null) {
      return 0;
    }
    return 0.2F * output.stackSize;
  }

}
