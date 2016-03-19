package crazypants.enderio.item;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import crazypants.enderio.EnderIO;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.machine.painter.blocks.EnumPressurePlateType;
import crazypants.enderio.machine.painter.blocks.RecipePaintedPressurePlate;
import crazypants.enderio.material.MachinePart;

import static crazypants.enderio.material.Alloy.CONDUCTIVE_IRON;
import static crazypants.enderio.material.Alloy.DARK_STEEL;
import static crazypants.enderio.material.Alloy.ELECTRICAL_STEEL;
import static crazypants.enderio.material.Alloy.ENERGETIC_ALLOY;
import static crazypants.enderio.material.Alloy.SOULARIUM;
import static crazypants.enderio.material.Material.VIBRANT_CYSTAL;
import static crazypants.util.RecipeUtil.addShaped;

public class ItemRecipes {

  public static void addRecipes() {
    ItemStack basicGear = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.BASIC_GEAR.ordinal());
    String electricalSteel = ELECTRICAL_STEEL.getOreIngot();
    String conductiveIron = CONDUCTIVE_IRON.getOreIngot();
    String vibCry = VIBRANT_CYSTAL.oreDict;
    String enAlloy = ENERGETIC_ALLOY.getOreIngot();
    String darkSteel = DARK_STEEL.getOreIngot();
    String soularium = SOULARIUM.getOreIngot();

    // Wrench
    ItemStack wrench = new ItemStack(EnderIO.itemYetaWench, 1, 0);
    addShaped(wrench, "s s", " b ", " s ", 's', electricalSteel, 'b', basicGear);

    //Magnet
    ItemStack magnet = new ItemStack(DarkSteelItems.itemMagnet, 1, 0);
    DarkSteelItems.itemMagnet.setEnergy(magnet, 0);
    addShaped(magnet, "scc", "  v", "scc", 's', electricalSteel, 'c', conductiveIron, 'v', vibCry);

    //Dark Steel
    addShaped(DarkSteelItems.itemDarkSteelHelmet.createItemStack(), "sss", "s s", 's', darkSteel);
    addShaped(DarkSteelItems.itemDarkSteelChestplate.createItemStack(), "s s", "sss", "sss", 's', darkSteel);
    addShaped(DarkSteelItems.itemDarkSteelLeggings.createItemStack(), "sss", "s s", "s s", 's', darkSteel);
    addShaped(DarkSteelItems.itemDarkSteelBoots.createItemStack(), "s s", "s s", 's', darkSteel);

    ItemStack wing = new ItemStack(DarkSteelItems.itemGliderWing, 1, 0);
    addShaped(wing, "  s", " sl", "sll", 's', darkSteel, 'l', Items.leather);
    addShaped(new ItemStack(DarkSteelItems.itemGliderWing, 1, 1), " s ", "wsw", "   ", 's', darkSteel, 'w', wing);

    addShaped(DarkSteelItems.itemDarkSteelShears.createItemStack(), " s", "s ", 's', darkSteel);

    ItemStack pp5 = new ItemStack(EnderIO.blockPaintedPressurePlate, 1, EnumPressurePlateType.DARKSTEEL.getMetaFromType(false));
    ItemStack pp6 = new ItemStack(EnderIO.blockPaintedPressurePlate, 1, EnumPressurePlateType.SOULARIUM.getMetaFromType(false));

    addShaped(pp5, "ss", 's', darkSteel);
    addShaped(pp6, "ss", 's', soularium);

    ItemStack pp1s = new ItemStack(EnderIO.blockPaintedPressurePlate, 1, EnumPressurePlateType.WOOD.getMetaFromType(true));
    ItemStack pp2s = new ItemStack(EnderIO.blockPaintedPressurePlate, 1, EnumPressurePlateType.STONE.getMetaFromType(true));
    ItemStack pp3s = new ItemStack(EnderIO.blockPaintedPressurePlate, 1, EnumPressurePlateType.GOLD.getMetaFromType(true));
    ItemStack pp4s = new ItemStack(EnderIO.blockPaintedPressurePlate, 1, EnumPressurePlateType.IRON.getMetaFromType(true));

    GameRegistry.addShapedRecipe(pp1s, "p", "w", 'p', Blocks.wooden_pressure_plate, 'w', Blocks.wool);
    GameRegistry.addShapedRecipe(pp2s, "p", "w", 'p', Blocks.stone_pressure_plate, 'w', Blocks.wool);
    GameRegistry.addShapedRecipe(pp3s, "p", "w", 'p', Blocks.light_weighted_pressure_plate, 'w', Blocks.wool);
    GameRegistry.addShapedRecipe(pp4s, "p", "w", 'p', Blocks.heavy_weighted_pressure_plate, 'w', Blocks.wool);

    GameRegistry.addRecipe(new RecipePaintedPressurePlate()); // plate + wool = silent plate (keeps nbt)
    for (EnumPressurePlateType type : EnumPressurePlateType.values()) {
      // these are just for JEI, the RecipePaintedPressurePlate has higher priority
      GameRegistry.addShapedRecipe(new ItemStack(EnderIO.blockPaintedPressurePlate, 1, type.getMetaFromType(true)), "p", "w", 'p', new ItemStack(
          EnderIO.blockPaintedPressurePlate, 1, type.getMetaFromType(false)), 'w', Blocks.wool);
    }

    //Soul Vessel
    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EnderIO.itemSoulVessel), " s ", "q q", " q ", 's', soularium, 'q', new ItemStack(
        EnderIO.blockFusedQuartz, 1, 0)));

    //XP Rod
    addShaped(new ItemStack(EnderIO.itemXpTransfer), "  s", " v ", "s  ", 's', soularium, 'v', enAlloy);

    // DS Tools
    addShaped(DarkSteelItems.itemDarkSteelSword.createItemStack(), " s ", " s ", " w ", 's', darkSteel, 'w', "stickWood");
    addShaped(DarkSteelItems.itemDarkSteelSword.createItemStack(), " s ", " s ", " w ", 's', darkSteel, 'w', "woodStick");
    addShaped(DarkSteelItems.itemDarkSteelPickaxe.createItemStack(), "sss", " w ", " w ", 's', darkSteel, 'w', "stickWood");
    addShaped(DarkSteelItems.itemDarkSteelPickaxe.createItemStack(), "sss", " w ", " w ", 's', darkSteel, 'w', "woodStick");
    addShaped(DarkSteelItems.itemDarkSteelAxe.createItemStack(), "ss ", "sw ", " w ", 's', darkSteel, 'w', "woodStick");
    addShaped(DarkSteelItems.itemDarkSteelAxe.createItemStack(), "ss ", "sw ", " w ", 's', darkSteel, 'w', "stickWood");
  }
}
