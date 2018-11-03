package crazypants.enderio.base.integration.thaumcraft;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.block.skull.SkullType;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.material.material.Material;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ThaumcraftAspects {

  private static int countItem = 0, countOredict = 0;

  static void loadAspects() {

    Log.info("Thaumcraft integration: Adding aspects...");

    registerObjectTag("dustCoal", new AspectList().add(getAspects(Items.COAL)));
    registerObjectTag("itemSilicon", new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.SENSES, 1));

    registerObjectTag(Alloy.ELECTRICAL_STEEL.getOreIngot(),
        new AspectList().add(getAspects("dustCoal")).add(getAspects(Items.IRON_INGOT)).add(getAspects("itemSilicon")));

    registerObjectTag(Alloy.ENERGETIC_ALLOY.getOreIngot(),
        new AspectList().add(getAspects(Items.GLOWSTONE_DUST)).add(getAspects(Items.REDSTONE)).add(getAspects(Items.GOLD_INGOT)));

    registerObjectTag(Alloy.VIBRANT_ALLOY.getOreIngot(), new AspectList().add(getAspects(Items.ENDER_PEARL)).add(getAspects("ingotEnergeticAlloy")));

    registerObjectTag(Alloy.REDSTONE_ALLOY.getOreIngot(), new AspectList().add(getAspects(Items.REDSTONE)).add(getAspects("itemSilicon")));

    registerObjectTag(Alloy.CONDUCTIVE_IRON.getOreIngot(), new AspectList().add(getAspects(Items.REDSTONE)).add(getAspects(Items.IRON_INGOT)));

    registerObjectTag(Alloy.PULSATING_IRON.getOreIngot(), new AspectList().add(getAspects(Items.ENDER_PEARL)).add(getAspects(Items.IRON_INGOT)));

    registerObjectTag(Alloy.DARK_STEEL.getOreIngot(),
        new AspectList().add(getAspects(Items.IRON_INGOT)).add(getAspects("dustCoal")).add(getAspects(Blocks.OBSIDIAN)));

    registerObjectTag(Alloy.SOULARIUM.getOreIngot(), new AspectList().add(getAspects(Blocks.SOUL_SAND)).add(getAspects(Items.GOLD_INGOT)));

    // ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockEndermanSkull), new AspectList()
    // .add(Aspect.MAGIC, 3)
    // .add(Aspect.TRAVEL, 4)
    // .add(Aspect.ELDRITCH, 4));

    registerObjectTag(new ItemStack(ModObject.blockEndermanSkull.getBlockNN(), 1, SkullType.TORMENTED.ordinal()),
        new AspectList().add(getAspects(ModObject.blockEndermanSkull.getBlockNN())).add(getAspects(Items.POTIONITEM)).add(getAspects(Items.POTIONITEM))
            .add(getAspects(new ItemStack(ModObject.itemBasicCapacitor.getItemNN(), 1, 0))).add(getAspects(Alloy.SOULARIUM.getOreIngot()))
            .add(getAspects(Alloy.SOULARIUM.getOreIngot())));

    registerObjectTag(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.FRANKEN_ZOMBIE.ordinal()),
        new AspectList().add(getAspects(new ItemStack(Items.SKULL, 1, 2))).add(getAspects("itemSilicon")).add(getAspects("itemSilicon"))
            .add(getAspects(new ItemStack(ModObject.itemBasicCapacitor.getItemNN(), 1, 0))).add(getAspects(Alloy.ENERGETIC_ALLOY.getOreIngot()))
            .add(getAspects(Alloy.ENERGETIC_ALLOY.getOreIngot())));

    registerObjectTag(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.FRANKEN_ZOMBIE.ordinal()),
        new AspectList().add(getAspects(new ItemStack(Items.SKULL, 1, 2))).add(getAspects("itemSilicon")).add(getAspects("itemSilicon"))
            .add(getAspects(new ItemStack(ModObject.itemBasicCapacitor.getItemNN(), 1, 0))).add(getAspects(Alloy.SOULARIUM.getOreIngot()))
            .add(getAspects(Alloy.SOULARIUM.getOreIngot())));

    registerObjectTag(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.FRANKEN_ZOMBIE.ordinal()), new AspectList().add(Aspect.UNDEAD, 2)
        .add(Aspect.MAN, 1).add(Aspect.EARTH, 1).add(getAspects(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.ZOMBIE_CONTROLLER.ordinal()))));

    registerObjectTag(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.ENDER_RESONATOR.ordinal()),
        new AspectList().add(getAspects(ModObject.itemMaterial.getItemNN())).add(getAspects("itemSilicon")).add(getAspects("itemSilicon"))
            .add(getAspects(Alloy.VIBRANT_ALLOY.getOreIngot())).add(getAspects(Alloy.SOULARIUM.getOreIngot())).add(getAspects(Alloy.SOULARIUM.getOreIngot())));

    registerObjectTag(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.ENDER_CRYSTAL.ordinal()),
        new AspectList().add(Aspect.AIR, 2).add(Aspect.ELDRITCH, 4)
            // .add(Aspect.TRAVEL, 2)
            .add(getAspects(Items.EMERALD)));

    registerObjectTag(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.ATTRACTOR_CRYSTAL.ordinal()), new AspectList().add(Aspect.AIR, 2)
        .add(Aspect.MAN, 3).add(getAspects(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.VIBRANT_CRYSTAL.ordinal()))));

    registerObjectTag(new ItemStack(ModObject.blockFusedQuartz.getBlockNN(), 1),
        new AspectList().add(getAspects(new ItemStack(Items.QUARTZ, 4))).add(Aspect.CRYSTAL, 1));

    registerObjectTag(new ItemStack(ModObject.blockFusedGlass.getBlockNN(), 1), new AspectList().add(Aspect.CRYSTAL, 2));

    registerObjectTag(new ItemStack(ModObject.blockEnlightenedFusedQuartz.getBlockNN(), 1),
        new AspectList().add(getAspects(new ItemStack(Items.QUARTZ, 4))).add(Aspect.LIGHT, 8).add(Aspect.SENSES, 2));

    registerObjectTag(new ItemStack(ModObject.blockEnlightenedFusedGlass.getBlockNN(), 1),
        new AspectList().add(Aspect.CRYSTAL, 2).add(Aspect.LIGHT, 8).add(Aspect.SENSES, 2));

    // This is a horrible hack due to the fact that I am assembling my aspects after TC does recipe parsing
    // Therefore I redo all EIO items
    // for (Object o : Item.REGISTRY.getKeys()) {
    // if (o instanceof String) {
    // String ownermod = ((String) o).substring(0, ((String) o).indexOf(':'));
    // if (EnderIO.MODID.equals(ownermod)) {
    // for (int idx = 0; idx < 16; idx++) {
    // addAspectsFromRecipes(Item.REGISTRY.getObject(new ResourceLocation((String) o)), idx);
    // }
    // }
    // }
    // }

    Log.info("Thaumcraft integration: Added aspects for " + countItem + " items and " + countOredict + " ore dictionary entrties");
  }

  private static @Nonnull AspectList getAspects(@Nonnull Block block) {
    return getAspects(Item.getItemFromBlock(block));
  }

  private static @Nonnull AspectList getAspects(@Nonnull String ore) {
    List<ItemStack> ores = OreDictionary.getOres(ore);
    return ores.isEmpty() ? new AspectList() : getAspects(NullHelper.notnullF(ores.get(0), "oreDict " + ore + " has null stack"));
  }

  private static @Nonnull AspectList getAspects(@Nonnull Item item) {
    return getAspects(new ItemStack(item));
  }

  private static @Nonnull AspectList getAspects(@Nonnull ItemStack item) {
    return new AspectList(item);
  }

  private static void registerObjectTag(@Nonnull String oreDict, AspectList aspects) {
    if (aspects.visSize() == 0) {
      Log.warn("Thaumcraft integration: No vis when adding aspects for " + oreDict);
    }
    ThaumcraftApi.registerObjectTag(oreDict, aspects);
    countOredict++;
  }

  private static void registerObjectTag(@Nonnull ItemStack item, AspectList aspects) {
    if (aspects.visSize() == 0) {
      Log.warn("Thaumcraft integration: No vis when adding aspects for " + item);
    }
    ThaumcraftApi.registerObjectTag(item, aspects);
    countItem++;
  }

}
