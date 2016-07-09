package crazypants.enderio.material;

import crazypants.enderio.EnderIO;
import crazypants.enderio.material.fusedQuartz.FusedQuartzType;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import static crazypants.util.RecipeUtil.addShaped;
import static crazypants.util.RecipeUtil.addShapeless;

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

    OreDictionary.registerOre("blockGlass", new ItemStack(FusedQuartzType.FUSED_GLASS.getBlock(), 1, OreDictionary.WILDCARD_VALUE));
    OreDictionary.registerOre("blockGlassHardened", new ItemStack(FusedQuartzType.FUSED_QUARTZ.getBlock(), 1, OreDictionary.WILDCARD_VALUE));

    // Forge names. Slightly different from vanilla names...
    String[] dyes = { "Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta",
        "Orange", "White" };

    for (int i = 0; i < 16; i++) {
      OreDictionary.registerOre("blockGlass" + dyes[i], new ItemStack(FusedQuartzType.FUSED_GLASS.getBlock(), 1, EnumDyeColor.byDyeDamage(i).getMetadata()));
      OreDictionary.registerOre("blockGlassHardened" + dyes[i],
          new ItemStack(FusedQuartzType.FUSED_QUARTZ.getBlock(), 1, EnumDyeColor.byDyeDamage(i).getMetadata()));
    }

    for (FusedQuartzType type : FusedQuartzType.values()) {
      OreDictionary.registerOre(type.getUnlocalisedName(), new ItemStack(EnderIO.blockFusedQuartz, 1, type.ordinal()));
      OreDictionary.registerOre(type.getUnlocalisedName(), new ItemStack(type.getBlock(), 1, OreDictionary.WILDCARD_VALUE));
    }

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

    for (EnumDyeColor color : EnumDyeColor.values()) {
      for (FusedQuartzType type : FusedQuartzType.values()) {
        if (color == EnumDyeColor.WHITE) {
          addShaped(new ItemStack(EnderIO.blockFusedQuartz, 8, type.ordinal()), "GGG", "GCG", "GGG", 'G', type.getUnlocalisedName(), 'C',
              new ItemStack(Items.DYE, 1, color.getDyeDamage()));
        } else {
          addShaped(new ItemStack(type.getBlock(), 8, color.getMetadata()), "GGG", "GCG", "GGG", 'G', type.getUnlocalisedName(), 'C',
              new ItemStack(Items.DYE, 1, color.getDyeDamage()));
        }
      }
    }

  }
}
