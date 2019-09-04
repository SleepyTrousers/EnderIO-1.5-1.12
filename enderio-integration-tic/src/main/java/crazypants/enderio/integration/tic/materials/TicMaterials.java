package crazypants.enderio.integration.tic.materials;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.material.alloy.IAlloy;
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

  private static final Map<IAlloy, Data> DATA = new HashMap<>();

  public static boolean hasIntegration(IAlloy alloy) {
    return DATA.get(alloy) != null;
  }

  public static void addIntegration(IAlloy alloy) {
    DATA.put(alloy, new Data() {

      @Override
      public void traits() {
      }

      @Override
      public void stats() {
      }
    });
  }

  public static @Nonnull Data getData(IAlloy alloy) {
    return NullHelper.notnull(DATA.get(alloy), "TRAIT AWOL");
  }

  static {
    DATA.put(Alloy.ELECTRICAL_STEEL, new Data() {
      @Override
      public void traits() {
        getMaterial().addTrait(TinkerTraits.lightweight);
        getMaterial().addTrait(TinkerTraits.shocking, MaterialTypes.HEAD);
      }

      @Override
      public void stats() {
        TinkerRegistry.addMaterialStats(getMaterial(), new HeadMaterialStats(306, 6.50f, 2.25f, HarvestLevels.DIAMOND), new HandleMaterialStats(0.75f, 80),
            new ExtraMaterialStats(75), new BowMaterialStats(1.5f, 0.9f, 1f));
      }
    });

    DATA.put(Alloy.ENERGETIC_ALLOY, new Data() {
      @Override
      public void traits() {
        getMaterial().addTrait(TinkerTraits.petramor);
        getMaterial().addTrait(TinkerTraits.unnatural, MaterialTypes.HEAD);
        getMaterial().addTrait(TinkerTraits.holy, MaterialTypes.HANDLE);
      }

      @Override
      public void stats() {
        TinkerRegistry.addMaterialStats(getMaterial(), new HeadMaterialStats(690, 2.50f, 5.60f, HarvestLevels.OBSIDIAN), new HandleMaterialStats(2.00f, -800),
            new ExtraMaterialStats(400), new BowMaterialStats(0.50f, 0.8f, 1f));
      }
    });

    DATA.put(Alloy.VIBRANT_ALLOY, new Data() {
      @Override
      public void traits() {
        getMaterial().addTrait(TraitPickup.instance);
        getMaterial().addTrait(TraitTeleport.instance4, MaterialTypes.HEAD);
      }

      @Override
      public void stats() {
        TinkerRegistry.addMaterialStats(getMaterial(), new HeadMaterialStats(220, 3.50f, 9.00f, HarvestLevels.COBALT), new HandleMaterialStats(0.50f, -50),
            new ExtraMaterialStats(60), new BowMaterialStats(0.75f, 1.0f, 5f));
      }
    });

    DATA.put(Alloy.REDSTONE_ALLOY, new Data() {
      @Override
      public void traits() {
        getMaterial().addTrait(TinkerTraits.crude);
        getMaterial().addTrait(TinkerTraits.shocking, MaterialTypes.HEAD);
        getMaterial().addTrait(TinkerTraits.writable, MaterialTypes.HANDLE);
      }

      @Override
      public void stats() {
        TinkerRegistry.addMaterialStats(getMaterial(), new HeadMaterialStats(120, 2.50f, 1.50f, STONE), new HandleMaterialStats(1.00f, -5),
            new ExtraMaterialStats(150), new BowMaterialStats(2.5f, 0.4f, 0f));
      }
    });

    DATA.put(Alloy.CONDUCTIVE_IRON, new Data() {
      @Override
      public void traits() {
        getMaterial().addTrait(TinkerTraits.lightweight);
        getMaterial().addTrait(TinkerTraits.crude2, MaterialTypes.HEAD);
      }

      @Override
      public void stats() {
        TinkerRegistry.addMaterialStats(getMaterial(), new HeadMaterialStats(106, 6.75f, 1.25f, HarvestLevels.DIAMOND), new HandleMaterialStats(1.25f, 100),
            new ExtraMaterialStats(250), new BowMaterialStats(1.5f, 0.9f, 1.25f));
      }
    });

    DATA.put(Alloy.PULSATING_IRON, new Data() {
      @Override
      public void traits() {
        getMaterial().addTrait(TraitTeleport.instance1);
        getMaterial().addTrait(TraitTeleport.instance2, MaterialTypes.HANDLE);
        getMaterial().addTrait(TraitTeleport.instance3, MaterialTypes.EXTRA);
        getMaterial().addTrait(TraitTeleport.instance0, MaterialTypes.HEAD);
        getMaterial().addTrait(TinkerTraits.poisonous, MaterialTypes.PROJECTILE);
      }

      @Override
      public void stats() {
        TinkerRegistry.addMaterialStats(getMaterial(), new HeadMaterialStats(920, 6.00f, 2.00f, HarvestLevels.IRON), new HandleMaterialStats(1.05f, 250),
            new ExtraMaterialStats(250), new BowMaterialStats(0.25f, 3.5f, 6f));
      }
    });

    DATA.put(Alloy.DARK_STEEL, new Data() {
      @Override
      public void traits() {
        getMaterial().addTrait(TinkerTraits.unnatural);
        getMaterial().addTrait(TinkerTraits.enderference, MaterialTypes.HEAD);
        getMaterial().addTrait(TinkerTraits.dense, MaterialTypes.EXTRA);
        getMaterial().addTrait(TinkerTraits.dense, MaterialTypes.BOW);
      }

      @Override
      public void stats() {
        TinkerRegistry.addMaterialStats(getMaterial(), new HeadMaterialStats(550, 7.00f, 6.00f, DarkSteelConfig.miningLevel.get(0).get()),
            new HandleMaterialStats(0.9f, 150), new ExtraMaterialStats(250), new BowMaterialStats(0.3f, 2.5f, 9f));
      }
    });

    DATA.put(Alloy.SOULARIUM, new Data() {
      @Override
      public void traits() {
        getMaterial().addTrait(TinkerTraits.duritos);
        getMaterial().addTrait(TinkerTraits.hellish, MaterialTypes.HEAD);
        getMaterial().addTrait(TinkerTraits.splinters, MaterialTypes.EXTRA);
        getMaterial().addTrait(TinkerTraits.flammable, MaterialTypes.HANDLE);
        getMaterial().addTrait(TinkerTraits.writable, MaterialTypes.BOWSTRING);
      }

      @Override
      public void stats() {
        TinkerRegistry.addMaterialStats(getMaterial(), new HeadMaterialStats(1555, 1.00f, 1.00f, HarvestLevels.STONE), new HandleMaterialStats(0.5f, 1500),
            new ExtraMaterialStats(1250), new BowMaterialStats(0.1f, 0.5f, 0f), new BowStringMaterialStats(0.75f));
      }
    });

    DATA.put(Alloy.END_STEEL, new Data() {
      @Override
      public void traits() {
        getMaterial().addTrait(TinkerTraits.unnatural);
        getMaterial().addTrait(TinkerTraits.enderference, MaterialTypes.HEAD);
        getMaterial().addTrait(TinkerTraits.alien, MaterialTypes.HEAD);
      }

      @Override
      public void stats() {
        TinkerRegistry.addMaterialStats(getMaterial(), new HeadMaterialStats(400, 8.25f, 5.00f, DarkSteelConfig.miningLevel.get(1).get()),
            new HandleMaterialStats(0.9f, 50), new ExtraMaterialStats(150), new BowMaterialStats(0.3f, 2.5f, 9f));
      }
    });

    DATA.put(Alloy.CONSTRUCTION_ALLOY, new Data() {
      @Override
      public void traits() {
        getMaterial().addTrait(TinkerTraits.cheapskate);
      }

      @Override
      public void stats() {
        TinkerRegistry.addMaterialStats(getMaterial(), new HeadMaterialStats(50, 1.25f, 1.00f, HarvestLevels.IRON), new HandleMaterialStats(0.5f, 10),
            new ExtraMaterialStats(10), new BowMaterialStats(3f, 0.5f, 0f));
      }
    });

  }

  public static abstract class Data {
    private Fluid fluid;

    private Material material;

    abstract public void traits();

    abstract public void stats();

    public @Nonnull Fluid getFluid() {
      return NullHelper.notnull(fluid, "FLUID AWOL");
    }

    public void setFluid(Fluid fluid) {
      this.fluid = fluid;
    }

    public @Nonnull Material getMaterial() {
      return NullHelper.notnull(material, "MATERIAL AWOL");
    }

    public void setMaterial(Material material) {
      this.material = material;
    };

  }
}
