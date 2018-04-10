package crazypants.enderio.integration.tic.fluids;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.integration.tic.queues.TicHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.material.material.Material.POWDER_ENDER;

public class Ender {

  private static final ResourceLocation TEX_STILL = new ResourceLocation("tconstruct:blocks/fluids/molten_metal_flow");
  private static final ResourceLocation TEX_FLOWING = new ResourceLocation("tconstruct:blocks/fluids/molten_metal");

  private static Fluid fluid;

  public static void createFluid() {
    fluid = new Fluid(TicProxy.ENDER_FLUID_NAME, TEX_FLOWING, TEX_STILL) {
      @Override
      public int getColor() {
        return 0xFF000000 | 0x1b7b6b;
      }
    }.setUnlocalizedName(EnderIO.DOMAIN + "." + TicProxy.ENDER_FLUID_NAME).setDensity(4000);
    fluid.setLuminosity(3).setTemperature(1000 + 273).setViscosity(35);
    FluidRegistry.registerFluid(fluid);
    FluidRegistry.addBucketForFluid(fluid);
  }

  public static Block createFluidBlock() {
    @SuppressWarnings("null")
    MoltenEnder block = new MoltenEnder(fluid, Material.WATER, 0x1b7b6b);
    block.setFluidStack(new FluidStack(FluidRegistry.getFluid(fluid.getName()), Fluid.BUCKET_VOLUME));

    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("fluid", fluid.getName());
    tag.setString("ore", "Ender");
    tag.setBoolean("toolforge", true);
    FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

    return block;
  }

  @SideOnly(Side.CLIENT)
  public static void registerRenderers() {
    Fluids.registerFluidBlockRendering(fluid);
  }

  public static void registerEnderRecipes() {
    Fluid f = FluidRegistry.getFluid(TicProxy.ENDER_FLUID_NAME);
    if (f != null) {
      // Note: We match the old TE amounts
      TicHandler.instance.registerSmelterySmelting(new Things().add(Items.ENDER_PEARL), f, 250);
      // Need to do this late because of the cast
      Things cast = new Things("tconstruct:cast_custom:2");
      TicHandler.instance.registerTableCast(new Things().add(Items.ENDER_PEARL), cast, f, 250, false);
    }
    TicHandler.instance.registerSmelterySmelting(new Things().add(POWDER_ENDER.getStack()), f, 250 / 9);
  }

}
