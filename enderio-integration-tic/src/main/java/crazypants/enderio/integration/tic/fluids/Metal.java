package crazypants.enderio.integration.tic.fluids;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.integration.tic.materials.TicMaterials;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class Metal {

  private static final ResourceLocation TEX_STILL = new ResourceLocation("tconstruct:blocks/fluids/molten_metal_flow");
  private static final ResourceLocation TEX_FLOWING = new ResourceLocation("tconstruct:blocks/fluids/molten_metal");

  public static Block createMetal(final @Nonnull Alloy alloy) {
    Fluid f = new Fluid(alloy.getFluidName(), TEX_FLOWING, TEX_STILL) {
      @Override
      public int getColor() {
        return 0xFF000000 | alloy.getColor();
      }
    };
    f.setDensity(9000);
    f.setLuminosity(4);
    f.setTemperature(alloy.getMeltingPoint() + 273);
    f.setViscosity(3000);
    FluidRegistry.registerFluid(f);
    Block block = MoltenMetal.create(f, Material.LAVA, alloy.getColor());
    if (!EnderIO.proxy.isDedicatedServer()) {
      Fluids.registerFluidBlockRendering(f);
    }
    FluidRegistry.addBucketForFluid(f);

    TicMaterials.integrate(alloy, f);

    return block;
  }

}
