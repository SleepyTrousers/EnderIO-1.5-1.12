package crazypants.enderio.machine.capbank;

import crazypants.enderio.EnderIO;
import crazypants.enderio.power.PowerHandlerUtil;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class ConvertOldRecipe implements IRecipe {

    static {
        RecipeSorter.register(
                "EnderIO:convertOldCapBankRecipe",
                ConvertOldRecipe.class,
                Category.SHAPELESS,
                "after:minecraft:shapeless");
    }

    private ItemStack output;

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        ItemStack input = null;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack checkStack = inv.getStackInSlot(i);
            if (checkStack != null) {
                if (input != null) {
                    return false;
                }
                if (Block.getBlockFromItem(checkStack.getItem()) == EnderIO.blockCapacitorBank) {
                    input = checkStack;
                } else {
                    return false;
                }
            }
        }

        if (input == null) {
            return false;
        }

        output = new ItemStack(EnderIO.blockCapBank, 1, CapBankType.getMetaFromType(CapBankType.ACTIVATED));
        PowerHandlerUtil.setStoredEnergyForItem(output, PowerHandlerUtil.getStoredEnergyForItem(input));
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return output.copy();
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }
}
