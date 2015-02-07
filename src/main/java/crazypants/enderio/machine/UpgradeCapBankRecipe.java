package crazypants.enderio.machine;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import crazypants.enderio.power.PowerHandlerUtil;

public class UpgradeCapBankRecipe extends ShapedOreRecipe {

  public UpgradeCapBankRecipe(ItemStack result, Object... recipe) {
    super(result, recipe);
  }

  @Override
  public ItemStack getCraftingResult(InventoryCrafting var1) {
    long energy = 0;
    for(int y=0 ; y<3 ; y++) {
      for(int x=0 ; x<3 ; x++) {
        ItemStack st = var1.getStackInRowAndColumn(x, y);
        if(st != null) {
          energy += PowerHandlerUtil.getStoredEnergyForItem(st);
        }
      }
    }

    ItemStack res = super.getCraftingResult(var1);
    PowerHandlerUtil.setStoredEnergyForItem(res, (int)Math.min(Integer.MAX_VALUE, energy));
    return res;
  }
}
