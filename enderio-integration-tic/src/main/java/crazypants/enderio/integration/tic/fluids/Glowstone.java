package crazypants.enderio.integration.tic.fluids;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.integration.tic.queues.TicHandler;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Glowstone {

  private static final ResourceLocation GLOWSTONE_TEX_STILL = new ResourceLocation("enderio:blocks/fluid_glowstone_still");
  private static final ResourceLocation GLOWSTONE_TEX_FLOWING = new ResourceLocation("enderio:blocks/fluid_glowstone_flow");

  private static Fluid fluid;

  public static void createFluid() {
    fluid = new Fluid(TicProxy.GLOWSTONE_FLUID_NAME, GLOWSTONE_TEX_STILL, GLOWSTONE_TEX_FLOWING) {
      @Override
      public int getColor() {
        return 0x80FFFFFF; // | 0xffbc5e;
      }
    }.setUnlocalizedName(EnderIO.DOMAIN + "." + TicProxy.GLOWSTONE_FLUID_NAME).setDensity(-500).setGaseous(true).setTemperature(1500 + 273).setViscosity(100);
    if (FluidRegistry.registerFluid(fluid)) {
      FluidRegistry.addBucketForFluid(fluid);
    }
  }

  public static Block createFluidBlock() {
    fluid.setLuminosity(Blocks.GLOWSTONE.getDefaultState().getLightValue());
    // sic! Create the block for our fluid but then set the current fluid
    @SuppressWarnings("null")
    MoltenGlowstone block = new MoltenGlowstone(fluid, Material.WATER, 0xffbc5e);
    block.setFluidStack(new FluidStack(FluidRegistry.getFluid(fluid.getName()), Fluid.BUCKET_VOLUME));

    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("fluid", fluid.getName());
    tag.setString("ore", "Glowstone");
    tag.setBoolean("toolforge", true);
    FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

    return block;
  }

  @SideOnly(Side.CLIENT)
  public static void registerRenderers() {
    Fluids.registerFluidBlockRendering(fluid);
  }

  public static void registerGlowstoneRecipes() {
    Fluid f = FluidRegistry.getFluid(TicProxy.GLOWSTONE_FLUID_NAME);
    if (f != null) {
      // Note: We match the old TE amounts
      TicHandler.instance.registerSmelterySmelting(new ItemStack(Items.GLOWSTONE_DUST), f, 250);
      TicHandler.instance.registerSmelterySmelting(new ItemStack(Blocks.GLOWSTONE), f, 1000);
      TicHandler.instance.registerBasinCasting(new ItemStack(Blocks.GLOWSTONE), Prep.getEmpty(), f, 1000);
    }
  }

}
