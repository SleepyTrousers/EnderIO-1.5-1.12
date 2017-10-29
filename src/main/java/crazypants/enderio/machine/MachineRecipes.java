package crazypants.enderio.machine;

import static crazypants.enderio.init.ModObject.itemBasicCapacitor;
import static crazypants.enderio.material.alloy.Alloy.ENERGETIC_ALLOY;
import static crazypants.enderio.material.alloy.Alloy.VIBRANT_ALLOY;
import static crazypants.enderio.material.material.Material.VIBRANT_CYSTAL;

import crazypants.enderio.machine.capbank.BlockItemCapBank;
import crazypants.enderio.machine.capbank.CapBankType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MachineRecipes {

  public static void addRecipes() {
    ItemStack capacitor2 = new ItemStack(itemBasicCapacitor.getItem(), 1, 1);
    ItemStack capacitor3 = new ItemStack(itemBasicCapacitor.getItem(), 1, 2);
    String energeticAlloy = ENERGETIC_ALLOY.getOreIngot();
    String phasedGold = VIBRANT_ALLOY.getOreIngot();
    String vibCry = VIBRANT_CYSTAL.getOreDict();

    ItemStack capBank1 = BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(CapBankType.SIMPLE), 0);
    ItemStack capBank2 = BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(CapBankType.ACTIVATED), 0);
    ItemStack capBank3 = BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(CapBankType.VIBRANT), 0);

    GameRegistry.addRecipe(new UpgradeCapBankRecipe(capBank2, "eee", "bcb", "eee", 'e', energeticAlloy, 'b', capBank1, 'c', capacitor2));
    GameRegistry.addRecipe(new UpgradeCapBankRecipe(capBank3, "vov", "NcN", "vov", 'v', phasedGold, 'o', capacitor3, 'N', capBank2, 'c', vibCry));

    ClearConfigRecipe inst = new ClearConfigRecipe();
    MinecraftForge.EVENT_BUS.register(inst);
    GameRegistry.addRecipe(inst);
  }

}
