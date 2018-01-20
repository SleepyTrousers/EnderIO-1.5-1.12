package crazypants.enderio.integration.tic.fluids;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.integration.tic.queues.TicHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import static crazypants.enderio.base.material.material.Material.POWDER_ENDER;

public class Ender {

  private static final ResourceLocation TEX_STILL = new ResourceLocation("tconstruct:blocks/fluids/molten_metal_flow");
  private static final ResourceLocation TEX_FLOWING = new ResourceLocation("tconstruct:blocks/fluids/molten_metal");

  public static Block createEnder() {
    Fluid f = new Fluid(TicProxy.ENDER_FLUID_NAME, TEX_FLOWING, TEX_STILL) {
      @Override
      public int getColor() {
        return 0xFF000000 | 0x1b7b6b;
      }
    };
    f.setUnlocalizedName(EnderIO.DOMAIN + "." + f.getName());
    f.setDensity(4000);
    f.setLuminosity(3);
    f.setTemperature(1000 + 273);
    f.setViscosity(35);
    if (FluidRegistry.registerFluid(f) && FluidRegistry.isUniversalBucketEnabled()) {
      FluidRegistry.addBucketForFluid(f);
    }

    MoltenEnder block = new MoltenEnder(f, Material.WATER, 0x1b7b6b);
    if (!EnderIO.proxy.isDedicatedServer()) {
      Fluids.registerFluidBlockRendering(f);
    }
    block.setFluidStack(new FluidStack(FluidRegistry.getFluid(f.getName()), Fluid.BUCKET_VOLUME));

    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("fluid", f.getName());
    tag.setString("ore", "Ender");
    tag.setBoolean("toolforge", true);
    FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

    return block;
  }

  public static void registerEnderRecipes() {
    Fluid f = FluidRegistry.getFluid(TicProxy.ENDER_FLUID_NAME);
    if (f != null) {
      // Note: We match the old TE amounts
      TicHandler.instance.registerSmelterySmelting(new ItemStack(Items.ENDER_PEARL), f, 250);
      // Need to do this late because of the cast
      Things cast = new Things("tconstruct:cast_custom:2");
      NNList<ItemStack> casts = cast.getItemStacks();
      if (!casts.isEmpty()) {
        TicHandler.instance.registerTableCast(new ItemStack(Items.ENDER_PEARL), casts.get(0), f, 250);
      }
    }
    TicHandler.instance.registerSmelterySmelting(POWDER_ENDER.getStack(), f, 250 / 9);
  }

}
