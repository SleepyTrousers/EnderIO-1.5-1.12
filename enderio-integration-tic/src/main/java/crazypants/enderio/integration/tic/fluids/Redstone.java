package crazypants.enderio.integration.tic.fluids;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.integration.tic.queues.TicRecipeHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Redstone {

  private static final ResourceLocation TEX_STILL = new ResourceLocation("tconstruct:blocks/fluids/molten_metal_flow");
  private static final ResourceLocation TEX_FLOWING = new ResourceLocation("tconstruct:blocks/fluids/molten_metal");

  private static Fluid fluid;

  public static void createFluid() {
    fluid = new Fluid(TicProxy.REDSTONE_FLUID_NAME, TEX_FLOWING, TEX_STILL) {
      @Override
      public int getColor() {
        return 0xFF000000 | 0xff0000;
      }
    }.setUnlocalizedName(EnderIO.DOMAIN + "." + TicProxy.REDSTONE_FLUID_NAME).setDensity(1200).setTemperature(1700 + 273).setViscosity(1500);
    FluidRegistry.registerFluid(fluid);
    FluidRegistry.addBucketForFluid(fluid);
  }

  public static Block createFluidBlock() {
    fluid.setLuminosity(Blocks.LIT_REDSTONE_ORE.getDefaultState().getLightValue() * 7 / 10);
    @SuppressWarnings("null")
    MoltenRedstone block = new MoltenRedstone(fluid, Material.WATER, 0xff0000);
    block.setFluidStack(new FluidStack(FluidRegistry.getFluid(fluid.getName()), Fluid.BUCKET_VOLUME));

    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("fluid", fluid.getName());
    tag.setString("ore", "Redstone");
    tag.setBoolean("toolforge", true);
    FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

    return block;
  }

  @SideOnly(Side.CLIENT)
  public static void registerRenderers() {
    Fluids.registerFluidBlockRendering(fluid);
  }

  public static void registerRedstoneRecipes() {
    Fluid f = FluidRegistry.getFluid(TicProxy.REDSTONE_FLUID_NAME);
    if (f != null) {
      // Note: We match the old TE amounts
      TicRecipeHandler.instance.registerSmelterySmelting(new Things().add(Items.REDSTONE), f, 100);
      TicRecipeHandler.instance.registerSmelterySmelting(new Things().add(Blocks.REDSTONE_BLOCK), f, 900);
      TicRecipeHandler.instance.registerBasinCasting(new Things().add(Blocks.REDSTONE_BLOCK), new Things(), f, 900);
    }
  }

}
