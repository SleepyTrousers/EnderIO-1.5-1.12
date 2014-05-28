package crazypants.enderio.material;

import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;

public class MaterialRecipes {

  public static void registerOresInDictionary() {
    //Ore Dictionary Registeration
    OreDictionary.registerOre("dustCoal", new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_COAL.ordinal()));
    OreDictionary.registerOre("dustIron", new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_IRON.ordinal()));
    OreDictionary.registerOre("dustGold", new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_GOLD.ordinal()));
    OreDictionary.registerOre("dustCopper", new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_COPPER.ordinal()));
    OreDictionary.registerOre("dustTin", new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_TIN.ordinal()));
    OreDictionary.registerOre("dustEnderPearl", new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_ENDER.ordinal()));
    OreDictionary.registerOre("itemSilicon", new ItemStack(EnderIO.itemMaterial, 1, Material.SILICON.ordinal()));
    OreDictionary.registerOre("gearStone", new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.BASIC_GEAR.ordinal()));

    OreDictionary.registerOre("glass", Blocks.glass);
    OreDictionary.registerOre("stickWood", Items.stick);
    OreDictionary.registerOre("woodStick", Items.stick);
    OreDictionary.registerOre("sand", Blocks.sand);
    OreDictionary.registerOre("ingotIron", Items.iron_ingot);
    OreDictionary.registerOre("ingotGold", Items.gold_ingot);
    

    ItemStack pureGlass = new ItemStack(EnderIO.blockFusedQuartz, 1, BlockFusedQuartz.Type.GLASS.ordinal());
    OreDictionary.registerOre("glass", pureGlass);
    OreDictionary.registerOre("glassHardened", new ItemStack(EnderIO.blockFusedQuartz, 1, BlockFusedQuartz.Type.FUSED_QUARTZ.ordinal()));

    //Glass stuff for compatability
    GameRegistry.addShapedRecipe(new ItemStack(Blocks.glass_pane,16,0), "   ", "eee", "eee", 'e', pureGlass);
    GameRegistry.addShapelessRecipe(new ItemStack(Blocks.glass), pureGlass);
    GameRegistry.addShapedRecipe(new ItemStack(Items.glass_bottle,3,0), "   ", "g g", " g ", 'g', pureGlass);

  }

  public static void addRecipes() {

    //Common Ingredients
    ItemStack conduitBinder = new ItemStack(EnderIO.itemMaterial, 4, Material.CONDUIT_BINDER.ordinal());
    ItemStack basicGear = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack binderComposite = new ItemStack(EnderIO.itemMaterial, 1, Material.BINDER_COMPOSITE.ordinal());

    ItemStack fusedQuartzFrame = new ItemStack(EnderIO.itemFusedQuartzFrame, 1, 0);
    ItemStack machineChassi = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.MACHINE_CHASSI.ordinal());

    ItemStack phasedGold = new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_GOLD.ordinal());
    ItemStack phasedIron = new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_IRON.ordinal());
    ItemStack electricalSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    ItemStack darkSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.DARK_STEEL.ordinal());

    //Conduit Binder
    ItemStack cbc = binderComposite.copy();
    cbc.stackSize = 8;
    if(Config.useAlternateBinderRecipe) {
      GameRegistry.addShapedRecipe(cbc, "gcg", "sgs", "gcg", 'g', Blocks.gravel, 's', Blocks.sand, 'c', Items.clay_ball);
    } else {
      GameRegistry.addShapedRecipe(cbc, "ggg", "scs", "ggg", 'g', Blocks.gravel, 's', Blocks.sand, 'c', Items.clay_ball);
    }
    FurnaceRecipes.smelting().func_151394_a(binderComposite, conduitBinder, 0);

    //Nuggets
    ItemStack phasedIronNugget = new ItemStack(EnderIO.itemMaterial, 9, Material.PHASED_IRON_NUGGET.ordinal());
    GameRegistry.addShapelessRecipe(phasedIronNugget, phasedIron);
    phasedIronNugget = phasedIronNugget.copy();
    phasedIronNugget.stackSize = 1;
    GameRegistry.addShapedRecipe(phasedIron, "eee", "eee", "eee", 'e', phasedIronNugget);

    ItemStack vibrantNugget = new ItemStack(EnderIO.itemMaterial, 9, Material.VIBRANT_NUGGET.ordinal());
    GameRegistry.addShapelessRecipe(vibrantNugget, phasedGold);
    vibrantNugget = vibrantNugget.copy();
    vibrantNugget.stackSize = 1;
    GameRegistry.addShapedRecipe(phasedGold, "eee", "eee", "eee", 'e', vibrantNugget);

    //Crystals
    ItemStack pulsCry = new ItemStack(EnderIO.itemMaterial, 1, Material.PULSATING_CYSTAL.ordinal());
    GameRegistry.addShapedRecipe(pulsCry, "nnn", "ngn", "nnn", 'n', phasedIronNugget, 'g', Items.diamond);

    ItemStack vibCry = new ItemStack(EnderIO.itemMaterial, 1, Material.VIBRANT_CYSTAL.ordinal());
    GameRegistry.addShapedRecipe(vibCry, "nnn", "ngn", "nnn", 'n', vibrantNugget, 'g', Items.emerald);

    //Balls
    ItemStack darkBall = new ItemStack(EnderIO.itemMaterial, 5, Material.DRAK_GRINDING_BALL.ordinal());
    GameRegistry.addShapedRecipe(darkBall, " s ", "sss", " s ", 's', darkSteel);


    //Smelting
    FurnaceRecipes.smelting().func_151394_a(new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_IRON.ordinal()), new ItemStack(Items.iron_ingot), 0);
    FurnaceRecipes.smelting().func_151394_a(new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_GOLD.ordinal()), new ItemStack(Items.gold_ingot), 0);

    //Ender Dusts
    ItemStack enderDust = new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_ENDER.ordinal());
    GameRegistry.addShapedRecipe(new ItemStack(Items.ender_pearl), "eee", "eee", "eee", 'e', enderDust);

    //Dark Iron Bars
    ItemStack diBars = new ItemStack(EnderIO.blockDarkIronBars, 16, 0);
    GameRegistry.addShapedRecipe(diBars, "ddd", "ddd", "   ", 'd', darkSteel);
    
    // Fused Quartz Frame
    GameRegistry.addRecipe(new ShapedOreRecipe(fusedQuartzFrame, "bsb", "s s", "bsb", 'b', conduitBinder, 's', "stickWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(fusedQuartzFrame, "bsb", "s s", "bsb", 'b', conduitBinder, 's', "woodStick"));

    // Machine Chassi
    GameRegistry.addShapedRecipe(machineChassi, "fif", "i i", "fif", 'f', Blocks.iron_bars, 'i', Items.iron_ingot);

    // Basic Gear
    GameRegistry.addRecipe(new ShapedOreRecipe(basicGear, "scs", "c c", "scs", 's', "stickWood", 'c', Blocks.cobblestone));
    GameRegistry.addRecipe(new ShapedOreRecipe(basicGear, "scs", "c c", "scs", 's', "woodStick", 'c', Blocks.cobblestone));

    //Ender Capacitor
    ItemStack enderCapacitor = new ItemStack(EnderIO.itemBasicCapacitor, 1, 2);
    ItemStack activatedCapacitor = new ItemStack(EnderIO.itemBasicCapacitor, 1, 1);
    if(Config.useHardRecipes) {
      GameRegistry.addShapedRecipe(enderCapacitor, "eee", "cgc", "eee", 'e', phasedGold, 'c', activatedCapacitor, 'g', Blocks.glowstone);
    } else {
      GameRegistry.addShapedRecipe(enderCapacitor, " e ", "cgc", " e ", 'e', phasedGold, 'c', activatedCapacitor, 'g', Blocks.glowstone);
    }

  }

  public static void addOreDictionaryRecipes() {
    int oreId = OreDictionary.getOreID("ingotCopper");
    ArrayList<ItemStack> ingots = OreDictionary.getOres(oreId);
    if(!ingots.isEmpty()) {
      FurnaceRecipes.smelting().func_151394_a(new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_COPPER.ordinal()), ingots.get(0), 0);
    }
    oreId = OreDictionary.getOreID("ingotTin");
    ingots = OreDictionary.getOres(oreId);
    if(!ingots.isEmpty()) {
      FurnaceRecipes.smelting().func_151394_a(new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_TIN.ordinal()), ingots.get(0), 0);
    }

    ItemStack capacitor = new ItemStack(EnderIO.itemBasicCapacitor, 1, 0);
    ArrayList<ItemStack> copperIngots = OreDictionary.getOres("ingotCopper");
    Item gold;
    if(Config.useHardRecipes) {
      gold = Items.gold_ingot;
    } else {
      gold = Items.gold_nugget;
    }
    if(copperIngots != null && !copperIngots.isEmpty()) {
      GameRegistry.addRecipe(new ShapedOreRecipe(capacitor, " gr", "gcg", "rg ", 'r', Items.redstone, 'g', gold, 'c', "ingotCopper"));
    } else {
      GameRegistry.
      addShapedRecipe(capacitor, " gr", "gig", "rg ", 'r', Items.redstone, 'g', gold, 'i', Items.iron_ingot);
    }

    int dustCoal = OreDictionary.getOreID("dustCoal");
    ItemStack activatedCapacitor = new ItemStack(EnderIO.itemBasicCapacitor, 1, 1);
    ItemStack electricalSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    if(Config.useHardRecipes) {
      GameRegistry.addRecipe(new ShapedOreRecipe(activatedCapacitor, "eee", "cCc", "eee", 'e', electricalSteel, 'c', capacitor, 'C', "dustCoal"));
    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(activatedCapacitor, " e ", "cCc", " e ", 'e', electricalSteel, 'c', capacitor, 'C', "dustCoal"));
    }
  }
}