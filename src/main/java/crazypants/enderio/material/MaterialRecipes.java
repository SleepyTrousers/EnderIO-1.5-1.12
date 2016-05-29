package crazypants.enderio.material;

import static crazypants.util.RecipeUtil.addShaped;
import static crazypants.util.RecipeUtil.addShapeless;

import crazypants.enderio.EnderIO;
import crazypants.enderio.material.fusedQuartz.FusedQuartzType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MaterialRecipes {

  public static void registerDependantOresInDictionary() {
    // late registration for powders that only exist if the ingot from another
    // mod exists
    for (PowderIngot powder : PowderIngot.values()) {
      if (powder.hasDependancy() && powder.isDependancyMet()) {
        OreDictionary.registerOre(powder.oreDictName, new ItemStack(EnderIO.itemPowderIngot, 1, powder.ordinal()));
        powder.setRegistered();
      }
    }
  }

  public static void registerOresInDictionary() {
    //Ore Dictionary Registration
    for (PowderIngot powder : PowderIngot.values()) {
      if (!powder.hasDependancy()) {
        OreDictionary.registerOre(powder.oreDictName, new ItemStack(EnderIO.itemPowderIngot, 1, powder.ordinal()));
      }
    }
    
    for (Alloy alloy : Alloy.values()) {
      for (String oreDictName : alloy.getOreIngots()) {
        OreDictionary.registerOre(oreDictName, alloy.getStackIngot());
      }
      for (String oreDictName : alloy.getOreBlocks()) {
        OreDictionary.registerOre(oreDictName, alloy.getStackBlock());
      }
    }

    OreDictionary.registerOre("nuggetPulsatingIron", new ItemStack(EnderIO.itemMaterial, 1, Material.PULSATING_IRON_NUGGET.ordinal()));
    OreDictionary.registerOre("nuggetVibrantAlloy", new ItemStack(EnderIO.itemMaterial, 1, Material.VIBRANT_NUGGET.ordinal()));

    ItemStack pureGlass = new ItemStack(EnderIO.blockFusedQuartz, 1, FusedQuartzType.FUSED_GLASS.ordinal());
    OreDictionary.registerOre("blockGlass", pureGlass);
    OreDictionary.registerOre("blockGlassColorless", pureGlass);
    OreDictionary.registerOre("blockGlassHardened", new ItemStack(EnderIO.blockFusedQuartz, 1, FusedQuartzType.FUSED_QUARTZ.ordinal()));

    //Skulls
    ItemStack skull = new ItemStack(Items.SKULL, 1, OreDictionary.WILDCARD_VALUE);
    OreDictionary.registerOre("itemSkull", skull);
    OreDictionary.registerOre("itemSkull", new ItemStack(EnderIO.blockEndermanSkull));

    Material.registerOres(EnderIO.itemMaterial);
    MachinePart.registerOres(EnderIO.itemMachinePart);
  }

  public static void addRecipes() {

    for (Alloy alloy : Alloy.values()) {
      addShaped(alloy.getStackBlock(), "iii", "iii", "iii", 'i', alloy.getOreIngot());
      addShapeless(alloy.getStackIngot(9), alloy.getOreBlock());
    }


  }
}
