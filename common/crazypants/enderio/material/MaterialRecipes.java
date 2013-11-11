package crazypants.enderio.material;

import static crazypants.enderio.ModObject.itemBasicCapacitor;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Config;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.alloy.BasicAlloyRecipe;
import crazypants.enderio.machine.alloy.VanillaSmeltingRecipe;

public class MaterialRecipes {

  public static void registerOresInDictionary() {
    //Ore Dictionary Registeration
    OreDictionary.registerOre("dustCoal", new ItemStack(ModObject.itemPowderIngot.actualId, 1, PowderIngot.POWDER_COAL.ordinal()));
    OreDictionary.registerOre("dustIron", new ItemStack(ModObject.itemPowderIngot.actualId, 1, PowderIngot.POWDER_IRON.ordinal()));
    OreDictionary.registerOre("dustGold", new ItemStack(ModObject.itemPowderIngot.actualId, 1, PowderIngot.POWDER_GOLD.ordinal()));
    OreDictionary.registerOre("dustCopper", new ItemStack(ModObject.itemPowderIngot.actualId, 1, PowderIngot.POWDER_COPPER.ordinal()));
    OreDictionary.registerOre("dustTin", new ItemStack(ModObject.itemPowderIngot.actualId, 1, PowderIngot.POWDER_TIN.ordinal()));
    OreDictionary.registerOre("dustEnderPearl", new ItemStack(ModObject.itemPowderIngot.actualId, 1, PowderIngot.POWDER_ENDER.ordinal()));
  }

  public static void addRecipes() {

    //Common Ingredients
    ItemStack conduitBinder = new ItemStack(ModObject.itemMaterial.actualId, 4, Material.CONDUIT_BINDER.ordinal());
    ItemStack basicGear = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack binderComposite = new ItemStack(ModObject.itemMaterial.actualId, 1, Material.BINDER_COMPOSITE.ordinal());
    ItemStack industrialBinder = new ItemStack(ModObject.itemMaterial.actualId, 1, Material.CONDUIT_BINDER.ordinal());
    ItemStack enderCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 2);
    ItemStack fusedQuartzFrame = new ItemStack(ModObject.itemFusedQuartzFrame.actualId, 1, 0);
    ItemStack machineChassi = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.MACHINE_CHASSI.ordinal());
    ItemStack activatedCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 1);
    ItemStack endergold = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.PHASED_GOLD.ordinal());
    ItemStack electricalSteel = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ELECTRICAL_STEEL.ordinal());

    //Conduit Binder
    ItemStack cbc = binderComposite.copy();
    cbc.stackSize = 8;
    if(Config.useAlternateBinderRecipe) {
      GameRegistry.addShapedRecipe(cbc, "gcg", "sgs", "gcg", 'g', Block.gravel, 's', Block.sand, 'c', Item.clay);
    } else {
      GameRegistry.addShapedRecipe(cbc, "ggg", "scs", "ggg", 'g', Block.gravel, 's', Block.sand, 'c', Item.clay);
    }
    FurnaceRecipes.smelting().addSmelting(binderComposite.itemID, binderComposite.getItemDamage(), conduitBinder, 0);

    //Aloys
    int meta = 0;
    for (Alloy alloy : Alloy.values()) {
      ItemStack ingot = new ItemStack(ModObject.itemAlloy.actualId, 1, meta);
      IMachineRecipe recipe = new BasicAlloyRecipe(ingot, alloy.unlocalisedName, alloy.ingrediants);
      if(ItemAlloy.useNuggets) {
        ItemStack nugget = new ItemStack(ModObject.itemAlloy.actualId, 9, meta + Alloy.values().length);
        GameRegistry.addShapelessRecipe(nugget, ingot);
        nugget = nugget.copy();
        nugget.stackSize = 1;
        GameRegistry.addShapedRecipe(ingot, "nnn", "nnn", "nnn", 'n', nugget);
      }
      MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, recipe);
      meta++;
    }

    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, new FusedQuartzRecipe());
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, new VanillaSmeltingRecipe());

    //Smelting
    FurnaceRecipes.smelting().addSmelting(ModObject.itemPowderIngot.actualId, PowderIngot.POWDER_IRON.ordinal(), new ItemStack(Item.ingotIron), 0);
    FurnaceRecipes.smelting().addSmelting(ModObject.itemPowderIngot.actualId, PowderIngot.POWDER_GOLD.ordinal(), new ItemStack(Item.ingotGold), 0);

    //Ender Dusts
    ItemStack enderDust = new ItemStack(ModObject.itemPowderIngot.actualId, 1, PowderIngot.POWDER_ENDER.ordinal());
    GameRegistry.addShapedRecipe(new ItemStack(Item.enderPearl), "eee", "eee", "eee", 'e', enderDust);

    // Fused Quartz Frame
    GameRegistry.addRecipe(new ShapedOreRecipe(fusedQuartzFrame, "bsb", "s s", "bsb", 'b', conduitBinder, 's', "stickWood"));
    GameRegistry.addRecipe(new ShapedOreRecipe(fusedQuartzFrame, "bsb", "s s", "bsb", 'b', conduitBinder, 's', "woodStick"));

    // Machine Chassi
    GameRegistry.addShapedRecipe(machineChassi, "fif", "i i", "fif", 'f', Block.fenceIron, 'i', Item.ingotIron);

    // Basic Gear
    GameRegistry.addRecipe(new ShapedOreRecipe(basicGear, "scs", "c c", "scs", 's', "stickWood", 'c', Block.cobblestone));
    GameRegistry.addRecipe(new ShapedOreRecipe(basicGear, "scs", "c c", "scs", 's', "woodStick", 'c', Block.cobblestone));

    // Ender Capacitor
    if(Config.useHardRecipes) {
      GameRegistry.addShapedRecipe(enderCapacitor, "eee", "cgc", "eee", 'e', endergold, 'c', activatedCapacitor, 'g', Block.glowStone);
    } else {
      GameRegistry.addShapedRecipe(enderCapacitor, " e ", "cgc", " e ", 'e', endergold, 'c', activatedCapacitor, 'g', Block.glowStone);
    }

  }

  public static void addOreDictionaryRecipes() {
    int oreId = OreDictionary.getOreID("ingotCopper");
    ArrayList<ItemStack> ingots = OreDictionary.getOres(oreId);
    if(!ingots.isEmpty()) {
      FurnaceRecipes.smelting().addSmelting(ModObject.itemPowderIngot.actualId, PowderIngot.POWDER_COPPER.ordinal(), ingots.get(0), 0);
    }
    oreId = OreDictionary.getOreID("ingotTin");
    ingots = OreDictionary.getOres(oreId);
    if(!ingots.isEmpty()) {
      FurnaceRecipes.smelting().addSmelting(ModObject.itemPowderIngot.actualId, PowderIngot.POWDER_TIN.ordinal(), ingots.get(0), 0);
    }

    ItemStack capacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 0);
    ArrayList<ItemStack> copperIngots = OreDictionary.getOres("ingotCopper");
    Item gold;
    if(Config.useHardRecipes) {
      gold = Item.ingotGold;
    } else {
      gold = Item.goldNugget;
    }
    if(copperIngots != null && !copperIngots.isEmpty()) {
      GameRegistry.addRecipe(new ShapedOreRecipe(capacitor, " gr", "gcg", "rg ", 'r', Item.redstone, 'g', gold, 'c', "ingotCopper"));
    } else {
      GameRegistry.
          addShapedRecipe(capacitor, " gr", "gig", "rg ", 'r', Item.redstone, 'g', gold, 'i', Item.ingotIron);
    }

    int dustCoal = OreDictionary.getOreID("dustCoal");
    ItemStack activatedCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 1);
    ItemStack electricalSteel = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    if(Config.useHardRecipes) {
      GameRegistry.addRecipe(new ShapedOreRecipe(activatedCapacitor, "eee", "cCc", "eee", 'e', electricalSteel, 'c', capacitor, 'C', "dustCoal"));
    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(activatedCapacitor, " e ", "cCc", " e ", 'e', electricalSteel, 'c', capacitor, 'C', "dustCoal"));
    }
  }
}