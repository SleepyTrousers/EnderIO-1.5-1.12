package crazypants.enderio.integration.tic.materials;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.integration.tic.traits.TraitPickup;
import crazypants.enderio.integration.tic.traits.TraitTeleport;
import net.minecraftforge.fluids.Fluid;
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
  private static final Map<Alloy, Fluid> FLUIDS = new EnumMap<>(Alloy.class);
  private static final Map<Alloy, Material> MATERIALS = new EnumMap<>(Alloy.class);

  public static @Nonnull Data getData(Alloy alloy) {
    return NullHelper.notnull(TRAITS.get(alloy), "TRAIT AWOL");
  }

  public static @Nonnull Fluid getFluid(Alloy alloy) {
    return NullHelper.notnull(FLUIDS.get(alloy), "FLUID AWOL");
  }

  public static @Nonnull Material getMaterial(Alloy alloy) {
    return NullHelper.notnull(MATERIALS.get(alloy), "MATERIAL AWOL");
  }

  public static void setFluid(Alloy alloy, Fluid fluid) {
    FLUIDS.put(alloy, fluid);
  }

  public static void setMaterial(Alloy alloy, Material material) {
    MATERIALS.put(alloy, material);
  }

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
        material.addTrait(TraitTeleport.instance4, MaterialTypes.HEAD);
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
        material.addTrait(TraitTeleport.instance1);
        material.addTrait(TraitTeleport.instance2, MaterialTypes.HANDLE);
        material.addTrait(TraitTeleport.instance3, MaterialTypes.EXTRA);
        material.addTrait(TraitTeleport.instance0, MaterialTypes.HEAD);
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

  public static interface Data {
    void traits(@Nonnull Material material);

    void stats(@Nonnull Material material);
  }
}
