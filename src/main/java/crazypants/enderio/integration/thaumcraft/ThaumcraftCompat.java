package crazypants.enderio.integration.thaumcraft;

import java.util.List;

import crazypants.enderio.item.darksteel.upgrade.IDarkSteelUpgrade;
import net.minecraftforge.fml.common.Loader;

public class ThaumcraftCompat {

  public static void load() {
    if(Loader.isModLoaded("Thaumcraft")) {
      loadAspects();
    }
  }

  private static void loadAspects() {

    //TODO: Mod Thaumcraft
//    ThaumcraftApi.registerObjectTag("dustCoal", new AspectList().add(getAspects(Items.COAL)));
//    ThaumcraftApi.registerObjectTag("itemSilicon", new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.SENSES, 1));
//
//    ThaumcraftApi.registerObjectTag(Alloy.ELECTRICAL_STEEL.getOreIngot(), new AspectList()
//        .add(getAspects("dustCoal"))
//        .add(getAspects(Items.IRON_INGOT))
//        .add(getAspects("itemSilicon")));
//
//    ThaumcraftApi.registerObjectTag(Alloy.ENERGETIC_ALLOY.getOreIngot(), new AspectList()
//        .add(getAspects(Items.GLOWSTONE_DUST))
//        .add(getAspects(Items.REDSTONE))
//        .add(getAspects(Items.GOLD_INGOT)));
//
//    ThaumcraftApi.registerObjectTag(Alloy.VIBRANT_ALLOY.getOreIngot(), new AspectList()
//        .add(getAspects(Items.ENDER_PEARL))
//        .add(getAspects("ingotEnergeticAlloy")));
//
//    ThaumcraftApi.registerObjectTag(Alloy.REDSTONE_ALLOY.getOreIngot(), new AspectList()
//        .add(getAspects(Items.REDSTONE))
//        .add(getAspects("itemSilicon")));
//
//    ThaumcraftApi.registerObjectTag(Alloy.CONDUCTIVE_IRON.getOreIngot(), new AspectList()
//        .add(getAspects(Items.REDSTONE))
//        .add(getAspects(Items.IRON_INGOT)));
//
//    ThaumcraftApi.registerObjectTag(Alloy.PULSATING_IRON.getOreIngot(), new AspectList()
//        .add(getAspects(Items.ENDER_PEARL))
//        .add(getAspects(Items.IRON_INGOT)));
//
//    ThaumcraftApi.registerObjectTag(Alloy.DARK_STEEL.getOreIngot(), new AspectList()
//        .add(getAspects(Items.IRON_INGOT))
//        .add(getAspects("dustCoal"))
//        .add(getAspects(Blocks.OBSIDIAN)));
//
//    ThaumcraftApi.registerObjectTag(Alloy.SOULARIUM.getOreIngot(), new AspectList()
//        .add(getAspects(Blocks.SOUL_SAND))
//        .add(getAspects(Items.GOLD_INGOT)));
//
////    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockEndermanSkull), new AspectList()
////        .add(Aspect.MAGIC, 3)
////        .add(Aspect.TRAVEL, 4)
////        .add(Aspect.ELDRITCH, 4));
//
//    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockEndermanSkull, 1, SkullType.TORMENTED.ordinal()), new AspectList()
//        .add(getAspects(EnderIO.blockEndermanSkull))
//        .add(getAspects(Items.POTIONITEM)).add(getAspects(Items.POTIONITEM))
//            .add(getAspects(new ItemStack(EnderIO.itemBasicCapacitor, 1, 0)))
//        .add(getAspects(Alloy.SOULARIUM.getOreIngot())).add(getAspects(Alloy.SOULARIUM.getOreIngot())));
//
//    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ZOMBIE_ELECTRODE.ordinal()), new AspectList()
//        .add(getAspects(new ItemStack(Items.SKULL, 1, 2)))
//        .add(getAspects("itemSilicon")).add(getAspects("itemSilicon"))
//            .add(getAspects(new ItemStack(EnderIO.itemBasicCapacitor, 1, 0)))
//        .add(getAspects(Alloy.ENERGETIC_ALLOY.getOreIngot())).add(getAspects(Alloy.ENERGETIC_ALLOY.getOreIngot())));
//
//    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ZOMBIE_CONTROLLER.ordinal()), new AspectList()
//        .add(getAspects(new ItemStack(Items.SKULL, 1, 2)))
//        .add(getAspects("itemSilicon")).add(getAspects("itemSilicon"))
//            .add(getAspects(new ItemStack(EnderIO.itemBasicCapacitor, 1, 0)))
//        .add(getAspects(Alloy.SOULARIUM.getOreIngot())).add(getAspects(Alloy.SOULARIUM.getOreIngot())));
//
//    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.FRANKEN_ZOMBIE.ordinal()), new AspectList()
//        .add(Aspect.UNDEAD, 2)
//        .add(Aspect.MAN, 1)
//        .add(Aspect.EARTH, 1)
//        .add(getAspects(new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ZOMBIE_CONTROLLER.ordinal()))));
//
//    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ENDER_RESONATOR.ordinal()), new AspectList()
//        .add(getAspects(EnderIO.blockEndermanSkull))
//        .add(getAspects("itemSilicon")).add(getAspects("itemSilicon"))
//        .add(getAspects(Alloy.VIBRANT_ALLOY.getOreIngot()))
//        .add(getAspects(Alloy.SOULARIUM.getOreIngot())).add(getAspects(Alloy.SOULARIUM.getOreIngot())));
//
//    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.itemMaterial, 1, Material.ENDER_CRYSTAL.ordinal()), new AspectList()
//        .add(Aspect.AIR, 2)
//        .add(Aspect.ELDRITCH, 4)
//        //.add(Aspect.TRAVEL, 2)
//        .add(getAspects(Items.EMERALD)));
//
//    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.itemMaterial, 1, Material.ATTRACTOR_CRYSTAL.ordinal()), new AspectList()
//        .add(Aspect.AIR, 2)
//        .add(Aspect.MAN, 3)
//        .add(getAspects(new ItemStack(EnderIO.itemMaterial, 1, Material.VIBRANT_CYSTAL.ordinal()))));
//
//    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockFusedQuartz, 1, FusedQuartzType.FUSED_QUARTZ.ordinal()), new AspectList()
//        .add(getAspects(new ItemStack(Items.QUARTZ, 4)))
//        .add(Aspect.CRYSTAL, 1));
//
//    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockFusedQuartz, 1, FusedQuartzType.FUSED_GLASS.ordinal()), new AspectList()
//        .add(Aspect.CRYSTAL, 2));
//
//    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockFusedQuartz, 1, FusedQuartzType.ENLIGHTENED_FUSED_QUARTZ.ordinal()), new AspectList()
//        .add(getAspects(new ItemStack(Items.QUARTZ, 4)))
//        .add(Aspect.LIGHT, 8)
//        .add(Aspect.SENSES, 2));
//
//    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockFusedQuartz, 1, FusedQuartzType.ENLIGHTENED_FUSED_GLASS.ordinal()), new AspectList()
//        .add(Aspect.CRYSTAL, 2)
//        .add(Aspect.LIGHT, 8)
//        .add(Aspect.SENSES, 2));
//
//    // This is a horrible hack due to the fact that I am assembling my aspects after TC does recipe parsing
//    // Therefore I redo all EIO items
//    for (Object o : Item.REGISTRY.getKeys()) {
//      if(o instanceof String) {
//        String ownermod = ((String) o).substring(0, ((String) o).indexOf(':'));
//        if(EnderIO.MODID.equals(ownermod)) {
//          for (int idx = 0; idx < 16; idx++) {
//            addAspectsFromRecipes(Item.REGISTRY.getObject(new ResourceLocation((String)o)), idx);
//          }
//        }
//      }
//    }
  }

//  private static AspectList getAspects(Block block) {
//    return getAspects(Item.getItemFromBlock(block));
//  }
//
//  private static AspectList getAspects(String ore) {
//    List<ItemStack> ores = OreDictionary.getOres(ore);
//    return ores.isEmpty() ? new AspectList() : getAspects(ores.get(0));
//  }
//
//  private static AspectList getAspects(Item item) {
//    return getAspects(new ItemStack(item));
//  }
//
//  private static AspectList getAspects(ItemStack item) {
//    return new AspectList(item);
//  }

//  private static void addAspectsFromRecipes(Item item, int meta) {
//    //ThaumcraftApi.registerObjectTag(new ItemStack(item, 1, meta), ThaumcraftApiHelper.generateTags(item, meta));
//  }

  public static void loadUpgrades(List<IDarkSteelUpgrade> upgrades) {
    upgrades.add(GogglesOfRevealingUpgrade.INSTANCE);
  }
}
