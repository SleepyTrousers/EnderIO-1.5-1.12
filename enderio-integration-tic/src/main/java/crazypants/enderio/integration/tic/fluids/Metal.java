package crazypants.enderio.integration.tic.fluids;

import javax.annotation.Nonnull;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.material.alloy.IAlloy;
import crazypants.enderio.integration.tic.materials.TicMaterials;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;

public class Metal {

  private static final ResourceLocation TEX_STILL = new ResourceLocation("tconstruct:blocks/fluids/molten_metal_flow");
  private static final ResourceLocation TEX_FLOWING = new ResourceLocation("tconstruct:blocks/fluids/molten_metal");

  public static void createFluidMaterial(final @Nonnull IAlloy alloy) {
    final Fluid fluid = new Fluid(alloy.getFluidName(), TEX_FLOWING, TEX_STILL) {
      @Override
      public int getColor() {
        return 0xFF000000 | alloy.getColor();
      }
    }.setDensity(9000).setLuminosity(6).setTemperature(alloy.getMeltingPoint() + 273).setViscosity(3000);

    FluidRegistry.registerFluid(fluid);
    FluidRegistry.addBucketForFluid(fluid);

    final Material material = new Material(alloy.getBaseName(), alloy.getColor());
    material.addCommonItems(alloy.getOreName());
    TinkerRegistry.integrate(new MaterialIntegration(material, fluid, alloy.getOreName()) {
      @Override
      public void registerFluidBlock(IForgeRegistry<Block> registry) {
      };
    }.toolforge()).preInit(); // preInit needed only for correct mod identification

    if (!TicMaterials.hasIntegration(alloy)) {
      TicMaterials.addIntegration(alloy);
    }

    TicMaterials.getData(alloy).setFluid(fluid);
    TicMaterials.getData(alloy).setMaterial(material);
    TicMaterials.getData(alloy).stats();
  }

  public static void createMaterial(final @Nonnull IAlloy alloy) {
    if (!TicMaterials.hasIntegration(alloy)) {
      return;
    }
    TicMaterials.getData(alloy).setMaterial(new Material(alloy.getBaseName(), alloy.getColor()));
    TicMaterials.getData(alloy).getMaterial().addCommonItems(alloy.getOreName());
    TinkerRegistry.integrate(new MaterialIntegration(TicMaterials.getData(alloy).getMaterial(), null, alloy.getOreName()) {
      @Override
      public void registerFluidBlock(IForgeRegistry<Block> registry) {
      };
    }).preInit(); // preInit needed only for correct mod identification
    TicMaterials.getData(alloy).stats();
  }

  public static void createFluidBlock(IForgeRegistry<Block> registry, final @Nonnull IAlloy alloy) {
    if (TicMaterials.hasIntegration(alloy)) {
      registry.register(MoltenMetal.create(TicMaterials.getData(alloy).getFluid(), alloy.getColor()));
    }
  }

  public static void createTraits(final @Nonnull IAlloy alloy) {
    if (TicMaterials.hasIntegration(alloy)) {
      TicMaterials.getData(alloy).traits();
    }
  }

  @SideOnly(Side.CLIENT)
  public static void registerRenderers(final @Nonnull IAlloy alloy) {
    if (TicMaterials.hasIntegration(alloy)) {
      Fluids.registerFluidBlockRendering(TicMaterials.getData(alloy).getFluid());
    }
  }

}
