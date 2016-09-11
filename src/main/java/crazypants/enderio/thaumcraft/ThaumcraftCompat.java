package crazypants.enderio.thaumcraft;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import cpw.mods.fml.common.Loader;
import crazypants.enderio.EnderIO;
import crazypants.enderio.item.darksteel.upgrade.IDarkSteelUpgrade;
import crazypants.enderio.item.skull.BlockEndermanSkull.SkullType;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.FrankenSkull;
import crazypants.enderio.material.Material;
import crazypants.enderio.power.Capacitors;

public class ThaumcraftCompat {

  public static void load() {
    if(Loader.isModLoaded("Thaumcraft")) {
      loadAspects();
    }
  }

  private static void loadAspects() {

    ThaumcraftApi.registerObjectTag("dustCoal", new AspectList().add(getAspects(Items.coal)));
    ThaumcraftApi.registerObjectTag("itemSilicon", new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.SENSES, 1));

    ThaumcraftApi.registerObjectTag(Alloy.ELECTRICAL_STEEL.getOreIngot(), new AspectList()
        .add(getAspects("dustCoal"))
        .add(getAspects(Items.iron_ingot))
        .add(getAspects("itemSilicon")));

    ThaumcraftApi.registerObjectTag(Alloy.ENERGETIC_ALLOY.getOreIngot(), new AspectList()
        .add(getAspects(Items.glowstone_dust))
        .add(getAspects(Items.redstone))
        .add(getAspects(Items.gold_ingot)));

    ThaumcraftApi.registerObjectTag(Alloy.PHASED_GOLD.getOreIngot(), new AspectList()
        .add(getAspects(Items.ender_pearl))
        .add(getAspects("ingotEnergeticAlloy")));

    ThaumcraftApi.registerObjectTag(Alloy.REDSTONE_ALLOY.getOreIngot(), new AspectList()
        .add(getAspects(Items.redstone))
        .add(getAspects("itemSilicon")));

    ThaumcraftApi.registerObjectTag(Alloy.CONDUCTIVE_IRON.getOreIngot(), new AspectList()
        .add(getAspects(Items.redstone))
        .add(getAspects(Items.iron_ingot)));

    ThaumcraftApi.registerObjectTag(Alloy.PHASED_IRON.getOreIngot(), new AspectList()
        .add(getAspects(Items.ender_pearl))
        .add(getAspects(Items.iron_ingot)));

    ThaumcraftApi.registerObjectTag(Alloy.DARK_STEEL.getOreIngot(), new AspectList()
        .add(getAspects(Items.iron_ingot))
        .add(getAspects("dustCoal"))
        .add(getAspects(Blocks.obsidian)));

    ThaumcraftApi.registerObjectTag(Alloy.SOULARIUM.getOreIngot(), new AspectList()
        .add(getAspects(Blocks.soul_sand))
        .add(getAspects(Items.gold_ingot)));

    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockEndermanSkull), new AspectList()
        .add(Aspect.MAGIC, 3)
        .add(Aspect.TRAVEL, 4)
        .add(Aspect.ELDRITCH, 4));

    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockEndermanSkull, 1, SkullType.TORMENTED.ordinal()), new AspectList()
        .add(getAspects(EnderIO.blockEndermanSkull))
        .add(getAspects(Items.potionitem)).add(getAspects(Items.potionitem))
        .add(getAspects(new ItemStack(EnderIO.itemBasicCapacitor, 1, Capacitors.BASIC_CAPACITOR.ordinal())))
        .add(getAspects(Alloy.SOULARIUM.getOreIngot())).add(getAspects(Alloy.SOULARIUM.getOreIngot())));

    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ZOMBIE_ELECTRODE.ordinal()), new AspectList()
        .add(getAspects(new ItemStack(Items.skull, 1, 2)))
        .add(getAspects("itemSilicon")).add(getAspects("itemSilicon"))
        .add(getAspects(new ItemStack(EnderIO.itemBasicCapacitor, 1, Capacitors.BASIC_CAPACITOR.ordinal())))
        .add(getAspects(Alloy.ENERGETIC_ALLOY.getOreIngot())).add(getAspects(Alloy.ENERGETIC_ALLOY.getOreIngot())));

    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ZOMBIE_CONTROLLER.ordinal()), new AspectList()
        .add(getAspects(new ItemStack(Items.skull, 1, 2)))
        .add(getAspects("itemSilicon")).add(getAspects("itemSilicon"))
        .add(getAspects(new ItemStack(EnderIO.itemBasicCapacitor, 1, Capacitors.BASIC_CAPACITOR.ordinal())))
        .add(getAspects(Alloy.SOULARIUM.getOreIngot())).add(getAspects(Alloy.SOULARIUM.getOreIngot())));

    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.FRANKEN_ZOMBIE.ordinal()), new AspectList()
        .add(Aspect.UNDEAD, 2)
        .add(Aspect.MAN, 1)
        .add(Aspect.EARTH, 1)
        .add(getAspects(new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ZOMBIE_CONTROLLER.ordinal()))));

    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ENDER_RESONATOR.ordinal()), new AspectList()
        .add(getAspects(EnderIO.blockEndermanSkull))
        .add(getAspects("itemSilicon")).add(getAspects("itemSilicon"))
        .add(getAspects(Alloy.PHASED_GOLD.getOreIngot()))
        .add(getAspects(Alloy.SOULARIUM.getOreIngot())).add(getAspects(Alloy.SOULARIUM.getOreIngot())));

    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.itemMaterial, 1, Material.ENDER_CRYSTAL.ordinal()), new AspectList()
        .add(Aspect.AIR, 2)
        .add(Aspect.ELDRITCH, 4)
        .add(Aspect.TRAVEL, 2)
        .add(getAspects(Items.emerald)));

    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.itemMaterial, 1, Material.ATTRACTOR_CRYSTAL.ordinal()), new AspectList()
        .add(Aspect.AIR, 2)
        .add(Aspect.MAN, 3)
        .add(getAspects(new ItemStack(EnderIO.itemMaterial, 1, Material.VIBRANT_CYSTAL.ordinal()))));

    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockFusedQuartz, 1, BlockFusedQuartz.Type.FUSED_QUARTZ.ordinal()), new AspectList()
        .add(getAspects(new ItemStack(Items.quartz, 4)))
        .add(Aspect.CRYSTAL, 1));

    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockFusedQuartz, 1, BlockFusedQuartz.Type.GLASS.ordinal()), new AspectList()
        .add(Aspect.CRYSTAL, 2));

    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockFusedQuartz, 1, BlockFusedQuartz.Type.ENLIGHTENED_FUSED_QUARTZ.ordinal()), new AspectList()
        .add(getAspects(new ItemStack(Items.quartz, 4)))
        .add(Aspect.LIGHT, 8)
        .add(Aspect.SENSES, 2));

    ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockFusedQuartz, 1, BlockFusedQuartz.Type.ENLIGHTENED_GLASS.ordinal()), new AspectList()
        .add(Aspect.CRYSTAL, 2)
        .add(Aspect.LIGHT, 8)
        .add(Aspect.SENSES, 2));

    // This is a horrible hack due to the fact that I am assembling my aspects after TC does recipe parsing
    // Therefore I redo all EIO items
    for (Object o : Item.itemRegistry.getKeys()) {
      if(o instanceof String) {
        String ownermod = ((String) o).substring(0, ((String) o).indexOf(':'));
        if(EnderIO.MODID.equals(ownermod)) {
          for (int idx = 0; idx < 16; idx++) {
            addAspectsFromRecipes((Item) Item.itemRegistry.getObject((String) o), idx);
          }
        }
      }
    }
  }

  private static AspectList getAspects(Block block) {
    return getAspects(Item.getItemFromBlock(block));
  }

  private static AspectList getAspects(String ore) {
    ArrayList<ItemStack> ores = OreDictionary.getOres(ore);
    return ores.isEmpty() ? new AspectList() : getAspects(ores.get(0));
  }

  private static AspectList getAspects(Item item) {
    return getAspects(new ItemStack(item));
  }

  private static AspectList getAspects(ItemStack item) {
    return new AspectList(item);
  }

  private static void addAspectsFromRecipes(Item item, int meta) {
    ThaumcraftApi.registerObjectTag(new ItemStack(item, 1, meta), ThaumcraftApiHelper.generateTags(item, meta));
  }

  public static void loadUpgrades(List<IDarkSteelUpgrade> upgrades) {
    upgrades.add(GogglesOfRevealingUpgrade.INSTANCE);
  }
}
