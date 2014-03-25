package crazypants.enderio.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.MachinePart;
import crazypants.enderio.material.Material;

public class ItemRecipes {

  public static void addRecipes() {
    ItemStack basicGear = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack electricalSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    ItemStack wrench = new ItemStack(EnderIO.itemYetaWench, 1, 0);

    // Wrench
    GameRegistry.addShapedRecipe(wrench, "s s", " b ", " s ", 's', electricalSteel, 'b', basicGear);

    ItemStack darkSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.DARK_STEEL.ordinal());
    ItemStack vibCry = new ItemStack(EnderIO.itemMaterial, 1, Material.VIBRANT_CYSTAL.ordinal());

    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelHelmet.createItemStack(), "scs", "s s", "   ", 's', darkSteel, 'c', vibCry);
    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelHelmet.createItemStack(), "   ", "scs", "s s", 's', darkSteel, 'c', vibCry);

    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelChestplate.createItemStack(), "s s", "scs", "sss", 's', darkSteel, 'c', vibCry);

    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelLeggings.createItemStack(), "csc", "s s", "s s", 's', darkSteel, 'c', vibCry);

    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelBoots.createItemStack(), "s s", "c c", "   ", 's', darkSteel, 'c', vibCry);
    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelBoots.createItemStack(), "   ", "s s", "c c", 's', darkSteel, 'c', vibCry);

    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelBoots.createItemStack(), "   ", "s s", "c c", 's', darkSteel, 'c', vibCry);

    GameRegistry.addShapedRecipe(EnderIO.itemDarkSteelSword.createItemStack(), " s ", " s ", " c ", 's', darkSteel, 'c', vibCry);

    GameRegistry.addRecipe(new ShapedOreRecipe(EnderIO.itemDarkSteelPickaxe.createItemStack(), "scs", " w ", " w ", 's', darkSteel, 'c', vibCry, 'w',
        "stickWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(EnderIO.itemDarkSteelPickaxe.createItemStack(), "scs", " w ", " w ", 's', darkSteel, 'c', vibCry, 'w',
        "woodStick"));

  }
}
