package crazypants.enderio.integration.tic;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.material.alloy.IAlloy;
import crazypants.enderio.base.material.alloy.endergy.AlloyEndergy;
import crazypants.enderio.integration.tic.book.EioBook;
import crazypants.enderio.integration.tic.book.TicBook;
import crazypants.enderio.integration.tic.fluids.Ender;
import crazypants.enderio.integration.tic.fluids.Glowstone;
import crazypants.enderio.integration.tic.fluids.Metal;
import crazypants.enderio.integration.tic.fluids.Redstone;
import crazypants.enderio.integration.tic.materials.TicMaterials;
import crazypants.enderio.integration.tic.modifiers.TicModifierHandler;
import crazypants.enderio.integration.tic.modifiers.TicModifiers;
import crazypants.enderio.integration.tic.queues.TicRecipeHandler;
import crazypants.enderio.integration.tic.recipes.TicRegistration;
import net.minecraft.block.Block;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;

public class TicControl {

  private static final @Nonnull NNList<IAlloy> ALLOYS = new NNList<IAlloy>();
  static {
    NNList.addAllEnum(ALLOYS, Alloy.class);
    if (Loader.isModLoaded("enderioendergy")) {
      NNList.addAllEnum(ALLOYS, AlloyEndergy.class);
    }
  }

  private static boolean doFluids() {
    return TConstruct.pulseManager.isPulseLoaded(NullHelper.notnull(TinkerSmeltery.PulseId, "TiC is broken"))
        && TConstruct.pulseManager.isPulseLoaded(NullHelper.notnull(TinkerFluids.PulseId, "TiC is broken"));
  }

  private static boolean doToolMaterials() {
    return TConstruct.pulseManager.isPulseLoaded(NullHelper.notnull(TinkerTools.PulseId, "TiC is broken"))
        && TConstruct.pulseManager.isPulseLoaded(NullHelper.notnull(TinkerModifiers.PulseId, "TiC is broken"));
  }

  private static boolean doModifierChecks() {
    return TConstruct.pulseManager.isPulseLoaded(NullHelper.notnull(TinkerModifiers.PulseId, "TiC is broken"));
  }

  public static void preInitBeforeTic(FMLPreInitializationEvent event) {
    if (doModifierChecks()) {
      TicProxy.register(TicRecipeHandler.instance, TicModifierHandler.instance);
    } else {
      TicProxy.register(TicRecipeHandler.instance, TicModifierHandler.instanceWithoutModifiers);
    }
    if (doFluids()) {
      Glowstone.createFluid();
      Redstone.createFluid();
      Ender.createFluid();
      ALLOYS.apply(new Callback<IAlloy>() {
        @Override
        public void apply(@Nonnull IAlloy alloy) {
          Metal.createFluidMaterial(alloy);
        }
      });
    } else if (doToolMaterials()) {
      ALLOYS.apply(new Callback<IAlloy>() {
        @Override
        public void apply(@Nonnull IAlloy alloy) {
          Metal.createMaterial(alloy);
        }
      });
    }
  }

  public static void injectBlocks(@Nonnull IForgeRegistry<Block> registry) {
    if (doFluids()) {
      registry.register(Glowstone.createFluidBlock());
      registry.register(Redstone.createFluidBlock());
      registry.register(Ender.createFluidBlock());
      ALLOYS.apply(new Callback<IAlloy>() {
        @Override
        public void apply(@Nonnull IAlloy alloy) {
          if (TicMaterials.hasIntegration(alloy)) {
            Metal.createFluidBlock(registry, alloy);
          }
        }
      });
    }
  }

  public static void initBeforeTic(FMLInitializationEvent event) {
    if (doToolMaterials()) {
      ALLOYS.apply(new Callback<IAlloy>() {
        @Override
        public void apply(@Nonnull IAlloy alloy) {
          Metal.createTraits(alloy);
        }
      });

      TicModifiers.register();
      if (!EnderIO.proxy.isDedicatedServer()) {
        TicBook.integrate();
      }
    }

    if (!EnderIO.proxy.isDedicatedServer()) {
      EioBook.integrate();
    }

    FarmersRegistry.registerHoes("tconstruct", "mattock");
  }

  public static void postInitBeforeTic(FMLPostInitializationEvent event) {
    if (doFluids()) {
      Glowstone.registerGlowstoneRecipes();
      Redstone.registerRedstoneRecipes();
      Ender.registerEnderRecipes();
    }
  }

  public static void postInitAfterTic(FMLPostInitializationEvent event) {
    // this runs after TiC's PostInit because it queries TiC for fluids
    if (doFluids()) {
      TicRegistration.registerSmeltings();
      TicRegistration.registerAlloys();
      TicRegistration.registerTableCasting();
      TicRegistration.registerBasinCasting();
    }
  }

  @SideOnly(Side.CLIENT)
  public static void registerRenderers(ModelRegistryEvent event) {
    if (doFluids()) {
      Glowstone.registerRenderers();
      Redstone.registerRenderers();
      Ender.registerRenderers();
      ALLOYS.apply(new Callback<IAlloy>() {
        @Override
        public void apply(@Nonnull IAlloy alloy) {
          Metal.registerRenderers(alloy);
        }
      });
    }
  }

}
