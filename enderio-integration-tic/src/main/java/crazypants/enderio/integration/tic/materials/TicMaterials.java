package crazypants.enderio.integration.tic.materials;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.integration.tic.traits.TraitPickup;
import crazypants.enderio.integration.tic.traits.TraitTeleport;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.tools.TinkerTraits;

import static slimeknights.tconstruct.library.utils.HarvestLevels.STONE;

public class TicMaterials {

  private static final Map<Alloy, Data> TRAITS = new EnumMap<>(Alloy.class);

  static {
    TRAITS.put(Alloy.ELECTRICAL_STEEL, new Data() {
      @Override
      public void traits(@Nonnull Material material) {
        material.addTrait(TinkerTraits.lightweight);
        material.addTrait(TinkerTraits.shocking, MaterialTypes.HEAD);
      }

      @Override
      public void stats(@Nonnull Material material) {
        TinkerRegistry.addMaterialStats(material, new HeadMaterialStats(306, 6.50f, 2.25f, HarvestLevels.DIAMOND), new HandleMaterialStats(0.75f, 80),
            new ExtraMaterialStats(75), new BowMaterialStats(1.5f, 0.9f, 1f));
      }
    });

    TRAITS.put(Alloy.ENERGETIC_ALLOY, new Data() {
      @Override
      public void traits(@Nonnull Material material) {
        material.addTrait(TinkerTraits.petramor);
        material.addTrait(TinkerTraits.unnatural, MaterialTypes.HEAD);
        material.addTrait(TinkerTraits.holy, MaterialTypes.HANDLE);
      }

      @Override
      public void stats(@Nonnull Material material) {
        TinkerRegistry.addMaterialStats(material, new HeadMaterialStats(690, 2.50f, 5.60f, HarvestLevels.OBSIDIAN), new HandleMaterialStats(2.00f, -800),
            new ExtraMaterialStats(400), new BowMaterialStats(0.50f, 0.8f, 1f));
      }
    });

    TRAITS.put(Alloy.VIBRANT_ALLOY, new Data() {
      @Override
      public void traits(@Nonnull Material material) {
        material.addTrait(TraitPickup.instance);
        material.addTrait(new TraitTeleport(2, 4), MaterialTypes.HEAD);
      }

      @Override
      public void stats(@Nonnull Material material) {
        TinkerRegistry.addMaterialStats(material, new HeadMaterialStats(220, 3.50f, 9.00f, HarvestLevels.COBALT), new HandleMaterialStats(0.50f, -50),
            new ExtraMaterialStats(60), new BowMaterialStats(0.75f, 1.0f, 5f));
      }
    });

    TRAITS.put(Alloy.REDSTONE_ALLOY, new Data() {
      @Override
      public void traits(@Nonnull Material material) {
        material.addTrait(TinkerTraits.crude);
        material.addTrait(TinkerTraits.shocking, MaterialTypes.HEAD);
        material.addTrait(TinkerTraits.writable, MaterialTypes.HANDLE);
      }

      @Override
      public void stats(@Nonnull Material material) {
        TinkerRegistry.addMaterialStats(material, new HeadMaterialStats(120, 2.50f, 1.50f, STONE), new HandleMaterialStats(1.00f, -5),
            new ExtraMaterialStats(150), new BowMaterialStats(2.5f, 0.4f, 0f));
      }
    });

    TRAITS.put(Alloy.CONDUCTIVE_IRON, new Data() {
      @Override
      public void traits(@Nonnull Material material) {
        material.addTrait(TinkerTraits.lightweight);
        material.addTrait(TinkerTraits.crude2, MaterialTypes.HEAD);
      }

      @Override
      public void stats(@Nonnull Material material) {
        TinkerRegistry.addMaterialStats(material, new HeadMaterialStats(106, 6.75f, 1.25f, HarvestLevels.DIAMOND), new HandleMaterialStats(1.25f, 100),
            new ExtraMaterialStats(250), new BowMaterialStats(1.5f, 0.9f, 1.25f));
      }
    });

    TRAITS.put(Alloy.PULSATING_IRON, new Data() {
      @Override
      public void traits(@Nonnull Material material) {
        material.addTrait(new TraitTeleport(1, 1));
        material.addTrait(new TraitTeleport(1, 2), MaterialTypes.HANDLE);
        material.addTrait(new TraitTeleport(1, 3), MaterialTypes.EXTRA);
        material.addTrait(new TraitTeleport(3, 0), MaterialTypes.HEAD);
        material.addTrait(TinkerTraits.poisonous, MaterialTypes.PROJECTILE);
      }

      @Override
      public void stats(@Nonnull Material material) {
        TinkerRegistry.addMaterialStats(material, new HeadMaterialStats(920, 6.00f, 2.00f, HarvestLevels.IRON), new HandleMaterialStats(1.05f, 250),
            new ExtraMaterialStats(250), new BowMaterialStats(0.25f, 3.5f, 6f));
      }
    });

    TRAITS.put(Alloy.DARK_STEEL, new Data() {
      @Override
      public void traits(@Nonnull Material material) {
        material.addTrait(TinkerTraits.unnatural);
        material.addTrait(TinkerTraits.enderference, MaterialTypes.HEAD);
        material.addTrait(TinkerTraits.dense, MaterialTypes.EXTRA);
        material.addTrait(TinkerTraits.dense, MaterialTypes.BOW);
      }

      @Override
      public void stats(@Nonnull Material material) {
        TinkerRegistry.addMaterialStats(material, new HeadMaterialStats(550, 7.00f, 6.00f, HarvestLevels.COBALT), new HandleMaterialStats(0.9f, 150),
            new ExtraMaterialStats(250), new BowMaterialStats(0.3f, 2.5f, 9f));
      }
    });

    TRAITS.put(Alloy.SOULARIUM, new Data() {
      @Override
      public void traits(@Nonnull Material material) {
        material.addTrait(TinkerTraits.duritos);
        material.addTrait(TinkerTraits.hellish, MaterialTypes.HEAD);
        material.addTrait(TinkerTraits.splinters, MaterialTypes.EXTRA);
        material.addTrait(TinkerTraits.flammable, MaterialTypes.HANDLE);
        material.addTrait(TinkerTraits.writable, MaterialTypes.BOWSTRING);
      }

      @Override
      public void stats(@Nonnull Material material) {
        TinkerRegistry.addMaterialStats(material, new HeadMaterialStats(1555, 1.00f, 1.00f, HarvestLevels.STONE), new HandleMaterialStats(0.5f, 1500),
            new ExtraMaterialStats(1250), new BowMaterialStats(0.1f, 0.5f, 0f), new BowStringMaterialStats(0.75f));
      }
    });

  }

  public static void integrate(Alloy alloy, Fluid fluid) {
    if (TRAITS.get(alloy) != null) {
      Material material = new Material(alloy.getBaseName(), alloy.getColor());
      material.addCommonItems(alloy.getOreName());
      TRAITS.get(alloy).traits(material);
      TinkerRegistry.integrate(new MaterialIntegration(material, fluid, alloy.getOreName()).toolforge());
      TRAITS.get(alloy).stats(material);
    } else {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("fluid", fluid.getName());
      tag.setString("ore", alloy.getOreName());
      tag.setBoolean("toolforge", true);
      FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);
    }
  }

  private static interface Data {
    void traits(@Nonnull Material material);

    void stats(@Nonnull Material material);
  }
}
