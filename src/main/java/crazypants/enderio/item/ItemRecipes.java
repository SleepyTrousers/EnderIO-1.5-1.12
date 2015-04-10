package crazypants.enderio.item;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.init.DarkSteelItems;
import crazypants.enderio.init.EIOBlocks;
import crazypants.enderio.init.EIOItems;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.MachinePart;
import crazypants.enderio.material.Material;

public class ItemRecipes {

  public static void addRecipes() {
    ItemStack basicGear = new ItemStack(EIOItems.itemMachinePart, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack electricalSteel = new ItemStack(EIOItems.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    ItemStack conductiveIron = new ItemStack(EIOItems.itemAlloy, 1, Alloy.CONDUCTIVE_IRON.ordinal());
    ItemStack vibCry = new ItemStack(EIOItems.itemMaterial, 1, Material.VIBRANT_CYSTAL.ordinal());
    ItemStack vibAlloy = new ItemStack(EIOItems.itemAlloy, 1, Alloy.PHASED_GOLD.ordinal());
    ItemStack enAlloy = new ItemStack(EIOItems.itemAlloy, 1, Alloy.ENERGETIC_ALLOY.ordinal());
    ItemStack darkSteel = new ItemStack(EIOItems.itemAlloy, 1, Alloy.DARK_STEEL.ordinal());
    ItemStack soularium = new ItemStack(EIOItems.itemAlloy, 1, Alloy.SOULARIUM.ordinal());

    // Wrench
    ItemStack wrench = new ItemStack(EIOItems.itemYetaWench, 1, 0);
    GameRegistry.addShapedRecipe(wrench, "s s", " b ", " s ", 's', electricalSteel, 'b', basicGear);

    //Magnet
    ItemStack magnet = new ItemStack(EIOItems.itemMagnet, 1, 0);
    EIOItems.itemMagnet.setEnergy(magnet, 0);
    GameRegistry.addShapedRecipe(magnet, "scc", "  v", "scc", 's', electricalSteel, 'c', conductiveIron, 'v', vibCry);

    //Dark Steel
    GameRegistry.addShapedRecipe(DarkSteelItems.itemDarkSteelHelmet.createItemStack(), "sss", "s s", 's', darkSteel);
    GameRegistry.addShapedRecipe(DarkSteelItems.itemDarkSteelChestplate.createItemStack(), "s s", "sss", "sss", 's', darkSteel);
    GameRegistry.addShapedRecipe(DarkSteelItems.itemDarkSteelLeggings.createItemStack(), "sss", "s s", "s s", 's', darkSteel);
    GameRegistry.addShapedRecipe(DarkSteelItems.itemDarkSteelBoots.createItemStack(), "s s", "s s", 's', darkSteel);

    ItemStack wing = new ItemStack(DarkSteelItems.itemGliderWing,1,0);
    GameRegistry.addShapedRecipe(wing, "  s", " sl", "sll", 's', darkSteel, 'l', Items.leather);
    GameRegistry.addShapedRecipe(new ItemStack(DarkSteelItems.itemGliderWing,1,1), " s ", "wsw", "   ", 's', darkSteel, 'w', wing);

    GameRegistry.addShapedRecipe(DarkSteelItems.itemDarkSteelShears.createItemStack(), " s", "s ", 's', darkSteel);

    ItemStack dspp = new ItemStack(EIOBlocks.blockDarkSteelPressurePlate);
    GameRegistry.addShapedRecipe(dspp, "ss", 's', darkSteel);

    ItemStack dsppSilent = new ItemStack(EIOBlocks.blockDarkSteelPressurePlate, 1, 1);
    GameRegistry.addShapedRecipe(dsppSilent, "p", "w", 'p', dspp, 'w', Blocks.wool);

    //Soul Vessel
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemSoulVessel), " s ", "q q", " q ", 's', soularium, 'q', new ItemStack(
        EIOBlocks.blockFusedQuartz, 1, 0));

    //XP Rod
    GameRegistry.addShapedRecipe(new ItemStack(EIOItems.itemXpTransfer), "  s", " v ", "s  ", 's', soularium, 'v', enAlloy);

  }

  public static void addOreDictionaryRecipes() {
    ItemStack darkSteel = new ItemStack(EIOItems.itemAlloy, 1, Alloy.DARK_STEEL.ordinal());
    GameRegistry.addRecipe(new ShapedOreRecipe(DarkSteelItems.itemDarkSteelSword.createItemStack(),  " s ", " s ", " w ", 's', darkSteel, 'w', "stickWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(DarkSteelItems.itemDarkSteelSword.createItemStack(),  " s ", " s ", " w ", 's', darkSteel, 'w', "woodStick"));
    GameRegistry.addRecipe(new ShapedOreRecipe(DarkSteelItems.itemDarkSteelPickaxe.createItemStack(), "sss", " w ", " w ", 's', darkSteel, 'w', "stickWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(DarkSteelItems.itemDarkSteelPickaxe.createItemStack(), "sss", " w ", " w ", 's', darkSteel, 'w', "woodStick"));
    GameRegistry.addRecipe(new ShapedOreRecipe(DarkSteelItems.itemDarkSteelAxe.createItemStack(), "ss ", "sw ", " w ", 's', darkSteel, 'w', "woodStick"));
    GameRegistry.addRecipe(new ShapedOreRecipe(DarkSteelItems.itemDarkSteelAxe.createItemStack(), "ss ", "sw ", " w ", 's', darkSteel, 'w', "stickWood"));
  }
}
