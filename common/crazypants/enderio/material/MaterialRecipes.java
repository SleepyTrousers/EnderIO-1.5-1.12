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
  }

  public static void addRecipes() {

    //Common Ingredients
    ItemStack conduitBinder = new ItemStack(ModObject.itemMaterial.actualId, 6, Material.CONDUIT_BINDER.ordinal());
    ItemStack basicGear = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack industrialBinder = new ItemStack(ModObject.itemMaterial.actualId, 1, Material.CONDUIT_BINDER.ordinal());
    ItemStack wrench = new ItemStack(ModObject.itemYetaWrench.actualId, 1, 0);
    ItemStack enderCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 2);
    ItemStack fusedQuartzFrame = new ItemStack(ModObject.itemFusedQuartzFrame.actualId, 1, 0);
    ItemStack machineChassi = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.MACHINE_CHASSI.ordinal());
    ItemStack mJReader = new ItemStack(ModObject.itemMJReader.actualId, 1, 0);
    ItemStack capacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 0);
    ItemStack activatedCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 1);
    ItemStack enderiron = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.PHASED_IRON.ordinal());
    ItemStack electricalSteel = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ELECTRICAL_STEEL.ordinal());

    //Conduit Binder
    if(Config.useAlternateBinderRecipe) {
      ItemStack cb = conduitBinder.copy();
      cb.stackSize = 4;
      GameRegistry.addShapedRecipe(cb, "gg ", "gg ", "   ", 'g', Block.gravel);
    } else {
      GameRegistry.addSmelting(Block.gravel.blockID, conduitBinder, 0);
    }

    // Ender Capacitor
    GameRegistry.addShapedRecipe(enderCapacitor, " e ", "cgc", " e ", 'e', enderiron, 'c', activatedCapacitor, 'g', Block.glowStone);

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

    FurnaceRecipes.smelting().addSmelting(ModObject.itemPowderIngot.actualId, PowderIngot.POWDER_IRON.ordinal(), new ItemStack(Item.ingotIron), 0);
    FurnaceRecipes.smelting().addSmelting(ModObject.itemPowderIngot.actualId, PowderIngot.POWDER_GOLD.ordinal(), new ItemStack(Item.ingotGold), 0);

    // Fused Quartz Frame
    GameRegistry.addShapedRecipe(fusedQuartzFrame, "bsb", "s s", "bsb", 'b', conduitBinder, 's', new ItemStack(Item.stick));

    // Wrench
    GameRegistry.addShapedRecipe(wrench, "s s", " b ", " s ", 's', electricalSteel, 'b', basicGear);

    // Machine Chassi
    GameRegistry.addShapedRecipe(machineChassi, "fif", "i i", "fif", 'f', Block.fenceIron, 'i', Item.ingotIron);

    // Basic Gear
    GameRegistry.addShapedRecipe(basicGear, "scs", "c c", "scs", 's', Item.stick, 'c', Block.cobblestone);

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
    if(copperIngots != null && !copperIngots.isEmpty()) {

      GameRegistry.addRecipe(new ShapedOreRecipe(capacitor, " gr", "gcg", "rg ", 'r', Item.redstone, 'g', Item.goldNugget, 'c', "ingotCopper"));
    } else {
      GameRegistry.
          addShapedRecipe(capacitor, " gr", "gig", "rg ", 'r', Item.redstone, 'g', Item.goldNugget, 'i', Item.ingotIron);
    }
    int dustCoal = OreDictionary.getOreID("dustCoal");
    ItemStack activatedCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 1);
    ItemStack electricalSteel = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    GameRegistry.addRecipe(new ShapedOreRecipe(activatedCapacitor, " e ", "cCc", " e ", 'e', electricalSteel, 'c', capacitor, 'C', "dustCoal"));
  }
}