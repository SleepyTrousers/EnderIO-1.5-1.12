package crazypants.enderio.powertools.recipes;

import crazypants.enderio.powertools.machine.capbank.BlockItemCapBank;
import crazypants.enderio.powertools.machine.capbank.CapBankType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static crazypants.enderio.base.init.ModObject.itemBasicCapacitor;
import static crazypants.enderio.base.material.alloy.Alloy.ENERGETIC_ALLOY;
import static crazypants.enderio.base.material.alloy.Alloy.VIBRANT_ALLOY;
import static crazypants.enderio.base.material.material.Material.VIBRANT_CYSTAL;

public class PowerToolRecipes {

  public static void addRecipes() {
    ItemStack capacitor2 = new ItemStack(itemBasicCapacitor.getItemNN(), 1, 1);
    ItemStack capacitor3 = new ItemStack(itemBasicCapacitor.getItemNN(), 1, 2);
    String energeticAlloy = ENERGETIC_ALLOY.getOreIngot();
    String phasedGold = VIBRANT_ALLOY.getOreIngot();
    String vibCry = VIBRANT_CYSTAL.getOreDict();

    ItemStack capBank1 = BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(CapBankType.SIMPLE), 0);
    ItemStack capBank2 = BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(CapBankType.ACTIVATED), 0);
    ItemStack capBank3 = BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(CapBankType.VIBRANT), 0);

    GameRegistry.addRecipe(new UpgradeCapBankRecipe(capBank2, "eee", "bcb", "eee", 'e', energeticAlloy, 'b', capBank1, 'c', capacitor2));
    GameRegistry.addRecipe(new UpgradeCapBankRecipe(capBank3, "vov", "NcN", "vov", 'v', phasedGold, 'o', capacitor3, 'N', capBank2, 'c', vibCry));
    // Note: These cannot be handled with a xml upgrade recipe. They combine 2 items with NBT.
  }

}
