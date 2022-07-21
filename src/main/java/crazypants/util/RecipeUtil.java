package crazypants.util;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeUtil {

    public static void addShaped(Item res, Object... recipe) {
        addShaped(new ItemStack(res), recipe);
    }

    public static void addShaped(Block res, Object... recipe) {
        addShaped(new ItemStack(res), recipe);
    }

    public static void addShaped(ItemStack res, Object... recipe) {
        GameRegistry.addRecipe(new ShapedOreRecipe(res, recipe));
    }

    public static void addShapeless(Item res, Object... recipe) {
        addShapeless(new ItemStack(res), recipe);
    }

    public static void addShapeless(Block res, Object... recipe) {
        addShapeless(new ItemStack(res), recipe);
    }

    public static void addShapeless(ItemStack res, Object... recipe) {
        GameRegistry.addRecipe(new ShapelessOreRecipe(res, recipe));
    }
}
