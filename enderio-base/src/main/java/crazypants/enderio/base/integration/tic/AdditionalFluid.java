package crazypants.enderio.base.integration.tic;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.BlockFluidEnder;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.fluid.BlockFluidEio;
import crazypants.enderio.base.fluid.BlockFluidEio.MoltenEnder;
import crazypants.enderio.base.fluid.BlockFluidEio.MoltenGlowstone;
import crazypants.enderio.base.fluid.BlockFluidEio.MoltenRedstone;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static crazypants.enderio.base.material.material.Material.POWDER_ENDER;

@EventBusSubscriber(modid = EnderIO.MODID)
public class AdditionalFluid {

  @SubscribeEvent
  public static void registerBlocks(@Nonnull RegistryEvent.Register<Block> event) {
    if (TicProxy.isLoaded()) {
      event.getRegistry().register(createGlowstone());
      event.getRegistry().register(createRedstone());
      event.getRegistry().register(createEnder());
      NNList.of(Alloy.class).apply(new Callback<Alloy>() {
        @Override
        public void apply(@Nonnull Alloy alloy) {
          event.getRegistry().register(createMetal(alloy));
        }
      });
    }
  }

  static final ResourceLocation GLOWSTONE_TEX_STILL = new ResourceLocation("enderio:blocks/fluid_glowstone_still");
  static final ResourceLocation GLOWSTONE_TEX_FLOWING = new ResourceLocation("enderio:blocks/fluid_glowstone_flow");
  static final ResourceLocation TEX_STILL = new ResourceLocation("tconstruct:blocks/fluids/molten_metal_flow");
  static final ResourceLocation TEX_FLOWING = new ResourceLocation("tconstruct:blocks/fluids/molten_metal");

  public static final String GLOWSTONE_FLUID_NAME = "glowstone";
  public static final String REDSTONE_FLUID_NAME = "redstone";
  public static final String ENDER_FLUID_NAME = "ender";

  static void init(FMLPostInitializationEvent event) {
    glowstone(event);
    redstone(event);
    ender(event);
  }

  private static Block createGlowstone() {
    Fluid f = new Fluid(GLOWSTONE_FLUID_NAME, GLOWSTONE_TEX_STILL, GLOWSTONE_TEX_FLOWING) {

      @Override
      public int getColor() {
        return 0x80FFFFFF; // | 0xffbc5e;
      }
    };
    f.setUnlocalizedName(EnderIO.DOMAIN + "." + f.getName());
    f.setDensity(-500);
    f.setGaseous(true);
    f.setLuminosity(Blocks.GLOWSTONE.getDefaultState().getLightValue());
    f.setTemperature(1500 + 273);
    f.setViscosity(100);
    if (FluidRegistry.registerFluid(f) && FluidRegistry.isUniversalBucketEnabled()) {
      FluidRegistry.addBucketForFluid(f);
    }

    // sic! Create the block for our fluid but then set the current fluid
    MoltenGlowstone block = new BlockFluidEio.MoltenGlowstone(f, Material.WATER, 0xffbc5e);
    if (!EnderIO.proxy.isDedicatedServer()) {
      Fluids.registerFluidBlockRendering(f);
    }
    block.setFluidStack(new FluidStack(FluidRegistry.getFluid(f.getName()), Fluid.BUCKET_VOLUME));

    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("fluid", f.getName());
    tag.setString("ore", "Glowstone");
    tag.setBoolean("toolforge", true);
    FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

    return block;
  }

  private static void glowstone(FMLPostInitializationEvent event) {
    Fluid f = FluidRegistry.getFluid(GLOWSTONE_FLUID_NAME);
    if (f != null) {
      // Note: We match the old TE amounts
      TicProxy.registerSmelterySmelting(new ItemStack(Items.GLOWSTONE_DUST), f, 250);
      TicProxy.registerSmelterySmelting(new ItemStack(Blocks.GLOWSTONE), f, 1000);
      TicProxy.registerBasinCasting(new ItemStack(Blocks.GLOWSTONE), Prep.getEmpty(), f, 1000);
    }
  }

  private static Block createRedstone() {
    Fluid f = new Fluid(REDSTONE_FLUID_NAME, TEX_FLOWING, TEX_STILL) {
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

    MoltenRedstone block = new BlockFluidEio.MoltenRedstone(f, Material.WATER, 0xff0000);
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

  private static void redstone(FMLPostInitializationEvent event) {
    Fluid f = FluidRegistry.getFluid(REDSTONE_FLUID_NAME);
    if (f != null) {
      // Note: We match the old TE amounts
      TicProxy.registerSmelterySmelting(new ItemStack(Items.REDSTONE), f, 100);
      TicProxy.registerSmelterySmelting(new ItemStack(Blocks.REDSTONE_BLOCK), f, 900);
      TicProxy.registerBasinCasting(new ItemStack(Blocks.REDSTONE_BLOCK), Prep.getEmpty(), f, 900);
    }
  }

  private static Block createEnder() {
    Fluid f = new Fluid(ENDER_FLUID_NAME, TEX_FLOWING, TEX_STILL) {
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

    MoltenEnder block = new BlockFluidEio.MoltenEnder(f, Material.WATER, 0x1b7b6b);
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

  private static void ender(FMLPostInitializationEvent event) {
    Fluid f = FluidRegistry.getFluid(ENDER_FLUID_NAME);
    if (f != null) {
      // Note: We match the old TE amounts
      TicProxy.registerSmelterySmelting(new ItemStack(Items.ENDER_PEARL), f, 250);
      // Need to do this late because of the cast
      Things cast = new Things("tconstruct:cast_custom:2");
      NNList<ItemStack> casts = cast.getItemStacks();
      if (!casts.isEmpty()) {
        TicProxy.registerTableCast(new ItemStack(Items.ENDER_PEARL), casts.get(0), f, 250);
      }
    }
    TicProxy.registerSmelterySmelting(POWDER_ENDER.getStack(), f, 250 / 9);
  }

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
    Block block = BlockFluidEnder.MoltenMetal.create(f, Material.LAVA, alloy.getColor());
    if (!EnderIO.proxy.isDedicatedServer()) {
      Fluids.registerFluidBlockRendering(f);
    }
    FluidRegistry.addBucketForFluid(f);

    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("fluid", f.getName());
    tag.setString("ore", alloy.getOreName());
    tag.setBoolean("toolforge", true);
    FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

    return block;
  }

}
