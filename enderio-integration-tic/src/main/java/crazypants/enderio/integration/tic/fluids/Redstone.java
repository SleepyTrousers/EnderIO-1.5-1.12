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

public class Redstone {

  private static final ResourceLocation TEX_STILL = new ResourceLocation("tconstruct:blocks/fluids/molten_metal_flow");
  private static final ResourceLocation TEX_FLOWING = new ResourceLocation("tconstruct:blocks/fluids/molten_metal");

  public static Block createRedstone() {
    Fluid f = new Fluid(TicProxy.REDSTONE_FLUID_NAME, TEX_FLOWING, TEX_STILL) {
      @Override
      public int getColor() {
        return 0xFF000000 | 0xff0000;
      }
    };
    f.setUnlocalizedName(EnderIO.DOMAIN + "." + f.getName());
    f.setDensity(1200);
    f.setLuminosity(Blocks.LIT_REDSTONE_ORE.getDefaultState().getLightValue() * 7 / 10);
    f.setTemperature(1700 + 273);
    f.setViscosity(1500);
    if (FluidRegistry.registerFluid(f) && FluidRegistry.isUniversalBucketEnabled()) {
      FluidRegistry.addBucketForFluid(f);
    }

    MoltenRedstone block = new MoltenRedstone(f, Material.WATER, 0xff0000);
    if (!EnderIO.proxy.isDedicatedServer()) {
      Fluids.registerFluidBlockRendering(f);
    }
    block.setFluidStack(new FluidStack(FluidRegistry.getFluid(f.getName()), Fluid.BUCKET_VOLUME));

    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("fluid", f.getName());
    tag.setString("ore", "Redstone");
    tag.setBoolean("toolforge", true);
    FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

    return block;
  }

  public static void registerRedstoneRecipes() {
    Fluid f = FluidRegistry.getFluid(TicProxy.REDSTONE_FLUID_NAME);
    if (f != null) {
      // Note: We match the old TE amounts
      TicHandler.instance.registerSmelterySmelting(new ItemStack(Items.REDSTONE), f, 100);
      TicHandler.instance.registerSmelterySmelting(new ItemStack(Blocks.REDSTONE_BLOCK), f, 900);
      TicHandler.instance.registerBasinCasting(new ItemStack(Blocks.REDSTONE_BLOCK), Prep.getEmpty(), f, 900);
    }
  }

}
