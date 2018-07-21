package crazypants.enderio.base.integration.thaumcraft;

import java.util.List;

import javax.annotation.Nonnull;

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

  static void loadAspects() {

    // workaround for not knowing whether azanor's code has built aspects yet
    AspectList coal = new AspectList().add(Aspect.FIRE, 10).add(Aspect.ENERGY, 10);
    AspectList glowstoneDust = new AspectList().add(Aspect.LIGHT, 10).add(Aspect.SENSES, 5);
    AspectList ironIngot = new AspectList().add(Aspect.METAL, 10);
    AspectList redstone = new AspectList().add(Aspect.ENERGY, 10);
    AspectList goldIngot = new AspectList().add(Aspect.METAL, 10).add(Aspect.DESIRE, 10);
    AspectList enderPearl = new AspectList().add(Aspect.MOTION, 15).add(Aspect.ELDRITCH, 10);
    AspectList skull = new AspectList().add(Aspect.UNDEAD, 10).add(Aspect.DEATH, 10).add(Aspect.SOUL, 10);
    AspectList potion = new AspectList().add(Aspect.WATER, 5).add(Aspect.ALCHEMY, 3).add(Aspect.METAL, 3).add(Aspect.DESIRE, 3);
    AspectList emerald = new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.DESIRE, 10);
    AspectList quartz = new AspectList().add(Aspect.CRYSTAL, 5);
    AspectList obsidian = new AspectList().add(Aspect.EARTH, 5).add(Aspect.FIRE, 5).add(Aspect.DARKNESS, 5);
    AspectList soulSand = new AspectList().add(Aspect.EARTH, 3).add(Aspect.SOUL, 3).add(Aspect.TRAP, 3);


    // TODO: Mod Thaumcraft
    ThaumcraftApi.registerObjectTag("dustCoal", new AspectList().add(coal));
    ThaumcraftApi.registerObjectTag("itemSilicon", new AspectList().add(Aspect.FIRE, 1).add(Aspect.ORDER, 1).add(Aspect.SENSES, 1));

    ThaumcraftApi.registerObjectTag(Alloy.ELECTRICAL_STEEL.getOreIngot(),
        new AspectList().add(getAspects("dustCoal")).add(ironIngot).add(getAspects("itemSilicon")));

    ThaumcraftApi.registerObjectTag(Alloy.ENERGETIC_ALLOY.getOreIngot(),
        new AspectList().add(glowstoneDust).add(redstone).add(goldIngot));

    ThaumcraftApi.registerObjectTag(Alloy.VIBRANT_ALLOY.getOreIngot(),
        new AspectList().add(enderPearl).add(getAspects("ingotEnergeticAlloy")));

    ThaumcraftApi.registerObjectTag(Alloy.REDSTONE_ALLOY.getOreIngot(), new AspectList().add(redstone).add(getAspects("itemSilicon")));

    ThaumcraftApi.registerObjectTag(Alloy.CONDUCTIVE_IRON.getOreIngot(), new AspectList().add(redstone).add(ironIngot));

    ThaumcraftApi.registerObjectTag(Alloy.PULSATING_IRON.getOreIngot(), new AspectList().add(enderPearl).add(ironIngot));

    ThaumcraftApi.registerObjectTag(Alloy.DARK_STEEL.getOreIngot(),
        new AspectList().add(ironIngot).add(getAspects("dustCoal")).add(obsidian));

    ThaumcraftApi.registerObjectTag(Alloy.SOULARIUM.getOreIngot(), new AspectList().add(soulSand).add(goldIngot));

    // ThaumcraftApi.registerObjectTag(new ItemStack(EnderIO.blockEndermanSkull), new AspectList()
    // .add(Aspect.MAGIC, 3)
    // .add(Aspect.TRAVEL, 4)
    // .add(Aspect.ELDRITCH, 4));

    ThaumcraftApi.registerObjectTag(new ItemStack(ModObject.blockEndermanSkull.getBlockNN(), 1, SkullType.TORMENTED.ordinal()),
        new AspectList().add(getAspects(ModObject.blockEndermanSkull.getBlockNN())).add(potion).add(potion)
            .add(getAspects(new ItemStack(ModObject.itemBasicCapacitor.getItemNN(), 1, 0))).add(getAspects(Alloy.SOULARIUM.getOreIngot()))
            .add(getAspects(Alloy.SOULARIUM.getOreIngot())));

    ThaumcraftApi.registerObjectTag(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.FRANKEN_ZOMBIE.ordinal()),
        new AspectList().add(skull).add(skull).add(getAspects("itemSilicon")).add(getAspects("itemSilicon"))
            .add(getAspects(new ItemStack(ModObject.itemBasicCapacitor.getItemNN(), 1, 0))).add(getAspects(Alloy.ENERGETIC_ALLOY.getOreIngot()))
            .add(getAspects(Alloy.ENERGETIC_ALLOY.getOreIngot())));

    ThaumcraftApi.registerObjectTag(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.FRANKEN_ZOMBIE.ordinal()),
        new AspectList().add(skull).add(skull).add(getAspects("itemSilicon")).add(getAspects("itemSilicon"))
            .add(getAspects(new ItemStack(ModObject.itemBasicCapacitor.getItemNN(), 1, 0))).add(getAspects(Alloy.SOULARIUM.getOreIngot()))
            .add(getAspects(Alloy.SOULARIUM.getOreIngot())));

    ThaumcraftApi.registerObjectTag(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.FRANKEN_ZOMBIE.ordinal()),
        new AspectList().add(Aspect.UNDEAD, 2).add(Aspect.MAN, 1).add(Aspect.EARTH, 1)
            .add(getAspects(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.ZOMBIE_CONTROLLER.ordinal()))));

    ThaumcraftApi.registerObjectTag(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.ENDER_RESONATOR.ordinal()),
        new AspectList().add(getAspects(ModObject.itemMaterial.getItemNN())).add(getAspects("itemSilicon")).add(getAspects("itemSilicon"))
            .add(getAspects(Alloy.VIBRANT_ALLOY.getOreIngot())).add(getAspects(Alloy.SOULARIUM.getOreIngot())).add(getAspects(Alloy.SOULARIUM.getOreIngot())));

    ThaumcraftApi.registerObjectTag(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.ENDER_CRYSTAL.ordinal()),
        new AspectList().add(Aspect.AIR, 2).add(Aspect.ELDRITCH, 4)
            // .add(Aspect.TRAVEL, 2)
            .add(emerald));

    ThaumcraftApi.registerObjectTag(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.ATTRACTOR_CRYSTAL.ordinal()), new AspectList()
        .add(Aspect.AIR, 2).add(Aspect.MAN, 3).add(getAspects(new ItemStack(ModObject.itemMaterial.getItemNN(), 1, Material.VIBRANT_CYSTAL.ordinal()))));

    ThaumcraftApi.registerObjectTag(new ItemStack(ModObject.blockFusedQuartz.getBlockNN(), 1),
        new AspectList().add(quartz).add(quartz).add(quartz).add(quartz).add(Aspect.CRYSTAL, 1));

    ThaumcraftApi.registerObjectTag(new ItemStack(ModObject.blockFusedGlass.getBlockNN(), 1), new AspectList().add(Aspect.CRYSTAL, 2));

    ThaumcraftApi.registerObjectTag(new ItemStack(ModObject.blockEnlightenedFusedQuartz.getBlockNN(), 1),
        new AspectList().add(quartz).add(quartz).add(quartz).add(quartz).add(Aspect.LIGHT, 8).add(Aspect.SENSES, 2));

    ThaumcraftApi.registerObjectTag(new ItemStack(ModObject.blockEnlightenedFusedGlass.getBlockNN(), 1),
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
  }

  private static AspectList getAspects(@Nonnull Block block) {
    return getAspects(Item.getItemFromBlock(block));
  }

  private static AspectList getAspects(String ore) {
    List<ItemStack> ores = OreDictionary.getOres(ore);
    return ores.isEmpty() ? new AspectList() : getAspects(ores.get(0));
  }

  private static AspectList getAspects(@Nonnull Item item) {
    return getAspects(new ItemStack(item));
  }

  private static AspectList getAspects(@Nonnull ItemStack item) {
    return new AspectList(item);
  }

}
