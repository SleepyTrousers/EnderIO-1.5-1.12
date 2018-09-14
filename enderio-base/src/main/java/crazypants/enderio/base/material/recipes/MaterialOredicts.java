package crazypants.enderio.base.material.recipes;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.material.glass.FusedQuartzType;
import crazypants.enderio.base.material.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import static crazypants.enderio.base.init.ModObject.blockEndermanSkull;
import static crazypants.enderio.base.init.ModObject.itemMaterial;

@EventBusSubscriber(modid = EnderIO.MODID)
public class MaterialOredicts {

  // Forge names. Slightly different from vanilla names...
  static final String[] dyes = { "Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue",
      "Magenta", "Orange", "White" };

  public static void init(IMCEvent event) {
    // we register late so we can properly check for dependencies
    Material.getActiveMaterials().apply(new Callback<Material>() {
      @Override
      public void apply(@Nonnull Material material) {
        if (material.hasDependency()) {
          OreDictionary.registerOre(material.getOreDict(), material.getStack());
        }
      }
    });
  }

  // Ore Dictionary Registration
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void registerOredict(@Nonnull RegistryEvent.Register<Item> event) {
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
        OreDictionary.registerOre(alloy.getOreBall(), alloy.getStackBall());
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

    Things.addAlias(Material.DYE_GREEN.getBaseName().toUpperCase(Locale.ENGLISH),
        itemMaterial.getItemNN().getRegistryName() + ":" + Material.DYE_GREEN.ordinal());
    Things.addAlias(Material.DYE_BROWN.getBaseName().toUpperCase(Locale.ENGLISH),
        itemMaterial.getItemNN().getRegistryName() + ":" + Material.DYE_BROWN.ordinal());
    Things.addAlias(Material.DYE_BLACK.getBaseName().toUpperCase(Locale.ENGLISH),
        itemMaterial.getItemNN().getRegistryName() + ":" + Material.DYE_BLACK.ordinal());

    // Hoes
    OreDictionary.registerOre("toolHoe", new ItemStack(Items.WOODEN_HOE, 1, OreDictionary.WILDCARD_VALUE));
    OreDictionary.registerOre("toolHoe", new ItemStack(Items.IRON_HOE, 1, OreDictionary.WILDCARD_VALUE));
    OreDictionary.registerOre("toolHoe", new ItemStack(Items.STONE_HOE, 1, OreDictionary.WILDCARD_VALUE));
    OreDictionary.registerOre("toolHoe", new ItemStack(Items.DIAMOND_HOE, 1, OreDictionary.WILDCARD_VALUE));
    OreDictionary.registerOre("toolHoe", new ItemStack(Items.GOLDEN_HOE, 1, OreDictionary.WILDCARD_VALUE));

    // Zoo
    OreDictionary.registerOre("egg", new ItemStack(ModObject.item_owl_egg.getItemNN()));

    // Shears
    OreDictionary.registerOre("toolShears", new ItemStack(Items.SHEARS, 1, OreDictionary.WILDCARD_VALUE));
    OreDictionary.registerOre("toolShears", new ItemStack(ModObject.itemDarkSteelShears.getItemNN(), 1, OreDictionary.WILDCARD_VALUE));

    // Treetap
    OreDictionary.registerOre("toolTreetap", new ItemStack(ModObject.itemDarkSteelTreetap.getItemNN(), 1, OreDictionary.WILDCARD_VALUE));
  }

}
