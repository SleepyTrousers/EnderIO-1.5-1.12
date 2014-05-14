package crazypants.enderio.machine.farm;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public final class ModdedCrops {

  //System.out.println("FarmersComune.enclosing_method: ");

  public static void addModdedFarmers() {
    String natura = "Natura";
    String cropsItem = "N Crops";

    Block natCropBlock = GameRegistry.findBlock(natura, cropsItem);
    if(natCropBlock != null) {      
      Item natSeedItem = GameRegistry.findItem(natura, "barley.seed");
      if(natSeedItem != null) {
        //barley
        FarmersComune.instance.joinComune(new SeedFarmer(natCropBlock, 3, new ItemStack(natSeedItem)));
        // cotton
        FarmersComune.instance.joinComune(new PickableFarmer(natCropBlock, 4, 8, new ItemStack(natSeedItem, 1, 1)));
      }
      
      
    }

  }

  private ModdedCrops() {
  }

}
