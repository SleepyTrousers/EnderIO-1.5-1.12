package crazypants.enderio.machine;

import crazypants.enderio.power.PowerHandlerUtil;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class UpgradeCapBankRecipe extends ShapedOreRecipe {

    static {
        RecipeSorter.register(
                "EnderIO:upgradeCapBankRecipe",
                UpgradeCapBankRecipe.class,
                RecipeSorter.Category.SHAPED,
                "after:minecraft:shaped");
    }

    public UpgradeCapBankRecipe(ItemStack result, Object... recipe) {
        super(result, recipe);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                ItemStack st = inv.getStackInRowAndColumn(x, y);
                if (st != null && st.stackTagCompound != null && st.stackTagCompound.hasKey("Items")) {
                    return false;
                }
            }
        }
        return super.matches(inv, world);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        long energy = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                ItemStack st = var1.getStackInRowAndColumn(x, y);
                if (st != null) {
                    energy += PowerHandlerUtil.getStoredEnergyForItem(st);
                }
            }
        }

        ItemStack res = super.getCraftingResult(var1);
        PowerHandlerUtil.setStoredEnergyForItem(res, (int) Math.min(Integer.MAX_VALUE, energy));
        return res;
    }
}
