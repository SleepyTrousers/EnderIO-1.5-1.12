package crazypants.enderio.material;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.material.alloy.Alloy;
import crazypants.enderio.material.glass.FusedQuartzType;
import crazypants.enderio.material.material.Material;
import crazypants.enderio.material.material.NutritiousStickRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import static crazypants.enderio.init.ModObject.blockEndermanSkull;

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
  }

  public static void init(@Nonnull FMLPreInitializationEvent event) {
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

    // Forge names. Slightly different from vanilla names...
    String[] dyes = { "Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta",
        "Orange", "White" };

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

  public static void addRecipes() {

    for (Alloy alloy : Alloy.values()) {
      Object[] recipe = { "iii", "iii", "iii", 'i', alloy.getOreIngot() };
      GameRegistry.addRecipe(new ShapedOreRecipe(alloy.getStackBlock(), recipe));
      GameRegistry.addRecipe(new ShapelessOreRecipe(alloy.getStackIngot(9), alloy.getOreBlock()));
    }

    for (EnumDyeColor color : EnumDyeColor.values()) {
      for (FusedQuartzType type : FusedQuartzType.values()) {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(type.getBlock(), 8, color.getMetadata()), "GGG", "CGG", "GGG", 'G', type.getOreDictName(),
            'C', new ItemStack(Items.DYE, 1, color.getDyeDamage())));
      }
    }

    GameRegistry.addRecipe(new NutritiousStickRecipe());
  }

}
