package crazypants.enderio.base.material.recipes;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.material.alloy.endergy.AlloyEndergy;
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

  private static final NNList<String> REGISTERED = new NNList<>();

  public static void init(IMCEvent event) {
    // we register late so we can properly check for dependencies
    Material.getActiveMaterials().apply(new Callback<Material>() {
      @Override
      public void apply(@Nonnull Material material) {
        if (material.hasDependency()) {
          registerOre(material.getOreDict(), material.getStack());
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
          registerOre(material.getOreDict(), material.getStack());
        }
      }
    });
    NNList.of(Alloy.class).apply(new Callback<Alloy>() {
      @Override
      public void apply(@Nonnull Alloy alloy) {
        registerOre(alloy.getOreBlock(), alloy.getStackBlock());
        registerOre(alloy.getOreIngot(), alloy.getStackIngot());
        registerOre(alloy.getOreNugget(), alloy.getStackNugget());
        registerOre(alloy.getOreBall(), alloy.getStackBall());
      }
    });

    NNList.of(AlloyEndergy.class).apply(new Callback<AlloyEndergy>() {
      @Override
      public void apply(@Nonnull AlloyEndergy alloy) {
        registerOre(alloy.getOreBlock(), alloy.getStackBlock());
        registerOre(alloy.getOreIngot(), alloy.getStackIngot());
        registerOre(alloy.getOreNugget(), alloy.getStackNugget());
        registerOre(alloy.getOreBall(), alloy.getStackBall());
      }
    });

    registerOre("blockGlass", new ItemStack(FusedQuartzType.FUSED_GLASS.getBlock(), 1, OreDictionary.WILDCARD_VALUE));
    registerOre("blockGlassColorless", new ItemStack(FusedQuartzType.FUSED_GLASS.getBlock(), 1, EnumDyeColor.WHITE.getMetadata()));
    registerOre("blockGlassHardened", new ItemStack(FusedQuartzType.FUSED_QUARTZ.getBlock(), 1, OreDictionary.WILDCARD_VALUE));

    for (int i = 0; i < dyes.length; i++) {
      registerOre("blockGlass" + dyes[i], new ItemStack(FusedQuartzType.FUSED_GLASS.getBlock(), 1, EnumDyeColor.byDyeDamage(i).getMetadata()));
      registerOre("blockGlassHardened" + dyes[i], new ItemStack(FusedQuartzType.FUSED_QUARTZ.getBlock(), 1, EnumDyeColor.byDyeDamage(i).getMetadata()));
    }

    for (FusedQuartzType type : FusedQuartzType.values()) {
      registerOre(type.getOreDictName(), new ItemStack(type.getBlock(), 1, OreDictionary.WILDCARD_VALUE));
    }

    // Skulls
    registerOre("itemSkull", new ItemStack(Items.SKULL, 1, OreDictionary.WILDCARD_VALUE));
    registerOre("itemSkull", new ItemStack(blockEndermanSkull.getBlockNN()));

    Things.addAlias(Material.DYE_GREEN.getBaseName().toUpperCase(Locale.ENGLISH),
        itemMaterial.getItemNN().getRegistryName() + ":" + Material.DYE_GREEN.ordinal());
    Things.addAlias(Material.DYE_BROWN.getBaseName().toUpperCase(Locale.ENGLISH),
        itemMaterial.getItemNN().getRegistryName() + ":" + Material.DYE_BROWN.ordinal());
    Things.addAlias(Material.DYE_BLACK.getBaseName().toUpperCase(Locale.ENGLISH),
        itemMaterial.getItemNN().getRegistryName() + ":" + Material.DYE_BLACK.ordinal());

    // Hoes
    registerOre("toolHoe", new ItemStack(Items.WOODEN_HOE, 1, OreDictionary.WILDCARD_VALUE));
    registerOre("toolHoe", new ItemStack(Items.IRON_HOE, 1, OreDictionary.WILDCARD_VALUE));
    registerOre("toolHoe", new ItemStack(Items.STONE_HOE, 1, OreDictionary.WILDCARD_VALUE));
    registerOre("toolHoe", new ItemStack(Items.DIAMOND_HOE, 1, OreDictionary.WILDCARD_VALUE));
    registerOre("toolHoe", new ItemStack(Items.GOLDEN_HOE, 1, OreDictionary.WILDCARD_VALUE));

    // Zoo
    registerOre("egg", new ItemStack(ModObject.item_owl_egg.getItemNN()));

    // Shears
    registerOre("toolShears", new ItemStack(Items.SHEARS, 1, OreDictionary.WILDCARD_VALUE));
    registerOre("toolShears", new ItemStack(ModObject.itemDarkSteelShears.getItemNN(), 1, OreDictionary.WILDCARD_VALUE));

    // Treetap
    registerOre("toolTreetap", new ItemStack(ModObject.itemDarkSteelTreetap.getItemNN(), 1, OreDictionary.WILDCARD_VALUE));
  }

  public static void registerOre(@Nonnull String name, @Nonnull ItemStack ore) {
    OreDictionary.registerOre(name, ore);
    REGISTERED.add(name);
  }

  public static void checkOreRegistrations() {
    NNList<String> failed = new NNList<>();
    for (String name : REGISTERED) {
      if (OreDictionary.getOres(name).isEmpty()) {
        failed.add(name);
      }
    }
    if (!failed.isEmpty()) {
      Log.warn("=========================================================================");
      Log.warn("= Dear other mod author, ================================================");
      Log.warn("= while we do not care what is in those oredict entries, our mod is =====");
      Log.warn("= coded to assume that there is at least one item in them. Feel free to =");
      Log.warn("= put in some invisible unobtainable dummy item, but please don't empty =");
      Log.warn("= them completely. ======================================================");
      Log.warn("= Thank you! ============================================================");
      Log.warn("=========================================================================");
      EnderIO.proxy.stopWithErrorScreen( //
          "=======================================================================", //
          "== ENDER IO FATAL ERROR ==", //
          "=======================================================================", //
          "We registered some items with the Ore Dictionary but they are now gone.", //
          "That means that some other mod has messed with our stuff.", //
          "=======================================================================", //
          "This is NOT a bug in Ender IO.", //
          "=======================================================================", //
          "Find out which mod deletes our items and report this to THEM.", //
          "=======================================================================", //
          "Missing registration(s):", //
          String.join(", ", failed), //
          "=======================================================================", //
          "", "" //
      );
    }
  }

}
