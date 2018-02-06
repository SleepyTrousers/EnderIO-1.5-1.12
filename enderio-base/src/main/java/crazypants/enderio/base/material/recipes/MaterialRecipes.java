package crazypants.enderio.base.material.recipes;

import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.material.glass.FusedQuartzType;
import crazypants.enderio.base.material.material.Material;
import crazypants.enderio.base.material.material.NutritiousStickRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import static crazypants.enderio.base.init.ModObject.blockEndermanSkull;
import static crazypants.enderio.base.init.ModObject.itemMaterial;

public class MaterialRecipes {

  public static void init(@Nonnull FMLInitializationEvent event) {

    old_early();

    // we register late so we can properly check for dependencies
    Material.getActiveMaterials().apply(new Callback<Material>() {
      @Override
      public void apply(@Nonnull Material material) {
        if (material.hasDependency() && material.isDependencyMet()) {
          OreDictionary.registerOre(material.getOreDict(), material.getStack());
        }
      }
    });

    Things.addAlias(Material.DYE_GREEN.getBaseName().toUpperCase(Locale.ENGLISH),
        itemMaterial.getItemNN().getRegistryName() + ":" + Material.DYE_GREEN.ordinal());
    Things.addAlias(Material.DYE_BROWN.getBaseName().toUpperCase(Locale.ENGLISH),
        itemMaterial.getItemNN().getRegistryName() + ":" + Material.DYE_BROWN.ordinal());
    Things.addAlias(Material.DYE_BLACK.getBaseName().toUpperCase(Locale.ENGLISH),
        itemMaterial.getItemNN().getRegistryName() + ":" + Material.DYE_BLACK.ordinal());
  }

  private static void old_early() {
    // Ore Dictionary Registration
    Material.getActiveMaterials().apply(new Callback<Material>() {
      @Override
      public void apply(@Nonnull Material material) {
        if (!material.hasDependency()) {
          OreDictionary.registerOre(material.getOreDict(), material.getStack());
        }
      }
    });
    NNList.of(Alloy.class).apply(new Callback<Alloy>() {
      @Override
      public void apply(@Nonnull Alloy alloy) {
        OreDictionary.registerOre(alloy.getOreBlock(), alloy.getStackBlock());
        OreDictionary.registerOre(alloy.getOreIngot(), alloy.getStackIngot());
        OreDictionary.registerOre(alloy.getOreNugget(), alloy.getStackNugget());
      }
    });

    OreDictionary.registerOre("blockGlass", new ItemStack(FusedQuartzType.FUSED_GLASS.getBlock(), 1, OreDictionary.WILDCARD_VALUE));
    OreDictionary.registerOre("blockGlassColorless", new ItemStack(FusedQuartzType.FUSED_GLASS.getBlock(), 1, EnumDyeColor.WHITE.getMetadata()));
    OreDictionary.registerOre("blockGlassHardened", new ItemStack(FusedQuartzType.FUSED_QUARTZ.getBlock(), 1, OreDictionary.WILDCARD_VALUE));

    for (int i = 0; i < dyes.length; i++) {
      OreDictionary.registerOre("blockGlass" + dyes[i], new ItemStack(FusedQuartzType.FUSED_GLASS.getBlock(), 1, EnumDyeColor.byDyeDamage(i).getMetadata()));
      OreDictionary.registerOre("blockGlassHardened" + dyes[i],
          new ItemStack(FusedQuartzType.FUSED_QUARTZ.getBlock(), 1, EnumDyeColor.byDyeDamage(i).getMetadata()));
    }

    for (FusedQuartzType type : FusedQuartzType.values()) {
      OreDictionary.registerOre(type.getOreDictName(), new ItemStack(type.getBlock(), 1, OreDictionary.WILDCARD_VALUE));
    }

    // Skulls
    OreDictionary.registerOre("itemSkull", new ItemStack(Items.SKULL, 1, OreDictionary.WILDCARD_VALUE));
    OreDictionary.registerOre("itemSkull", new ItemStack(blockEndermanSkull.getBlockNN()));

  }

  // Forge names. Slightly different from vanilla names...
  static String[] dyes = { "Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta",
      "Orange", "White" };

  public static void addRecipes() {

    for (Alloy alloy : Alloy.values()) {
      ForgeRegistries.RECIPES.register(
          new ShapedOreRecipe(null, alloy.getStackBlock(), "iii", "iii", "iii", 'i', alloy.getOreIngot()).setRegistryName(UUID.randomUUID().toString()));
      ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, alloy.getStackIngot(9), alloy.getOreBlock()).setRegistryName(UUID.randomUUID().toString()));

      ForgeRegistries.RECIPES.register(
          new ShapedOreRecipe(null, alloy.getStackIngot(), "nnn", "nnn", "nnn", 'n', alloy.getOreNugget()).setRegistryName(UUID.randomUUID().toString()));
      ForgeRegistries.RECIPES
          .register(new ShapelessOreRecipe(null, alloy.getStackNugget(9), alloy.getStackIngot()).setRegistryName(UUID.randomUUID().toString()));
    }

    for (EnumDyeColor color : EnumDyeColor.values()) {
      for (FusedQuartzType type : FusedQuartzType.values()) {
        ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(type.getBlock(), 8, color.getMetadata()), "GGG", "CGG", "GGG", 'G',
            type.getOreDictName(), 'C', "dye" + dyes[color.getDyeDamage()]).setRegistryName(UUID.randomUUID().toString()));
      }
    }

    ForgeRegistries.RECIPES.register(new NutritiousStickRecipe().setRegistryName(UUID.randomUUID().toString()));
  }

}
