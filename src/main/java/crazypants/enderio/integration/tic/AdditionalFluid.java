package crazypants.enderio.integration.tic;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.fluid.BlockFluidEio;
import crazypants.enderio.fluid.BlockFluidEio.MoltenEnder;
import crazypants.enderio.fluid.BlockFluidEio.MoltenGlowstone;
import crazypants.enderio.fluid.BlockFluidEio.MoltenRedstone;
import crazypants.util.Prep;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static crazypants.enderio.material.material.Material.POWDER_ENDER;

public class AdditionalFluid {

  static final ResourceLocation GLOWSTONE_TEX_STILL = new ResourceLocation("enderio:blocks/fluid_glowstone_still");
  static final ResourceLocation GLOWSTONE_TEX_FLOWING = new ResourceLocation("enderio:blocks/fluid_glowstone_flowing");

  public static final String GLOWSTONE_FLUID_NAME = "glowstone";
  public static final String REDSTONE_FLUID_NAME = "redstone";
  public static final String ENDER_FLUID_NAME = "ender";

  private static final boolean REGISTER_GLOWSTONE_BLOCK = true;
  private static final boolean REGISTER_REDSTONE_BLOCK = true;
  private static final boolean REGISTER_ENDER_BLOCK = true;

  static void init(FMLPreInitializationEvent event) {
    if (!Loader.isModLoaded("thermalfoundation")) {
      glowstone(event);
      redstone(event);
      ender(event);
    }
  }

  static void init(FMLPostInitializationEvent event) {
    glowstone(event);
    redstone(event);
    ender(event);
  }

  private static boolean registerVanillaRecipesForGlowstone = false;

  private static boolean registerVanillaRecipesForRedstone = false;

  private static boolean registerVanillaRecipesForEnder = false;

  private static Fluid glowstone(FMLPreInitializationEvent event) {
    Fluid f = new Fluid(GLOWSTONE_FLUID_NAME, GLOWSTONE_TEX_STILL,
        GLOWSTONE_TEX_FLOWING) {

      @Override
      public int getColor() {
        return 0x80FFFFFF; // | 0xffbc5e;
      }
    };
    f.setDensity(-500);
    f.setGaseous(true);
    f.setLuminosity(Blocks.GLOWSTONE.getDefaultState().getLightValue());
    f.setTemperature(1500 + 273);
    f.setViscosity(100);
    if (FluidRegistry.registerFluid(f)) {
      if (REGISTER_GLOWSTONE_BLOCK) {
        MoltenGlowstone block = new BlockFluidEio.MoltenGlowstone(f, Material.WATER, 0xffbc5e);
        block.init();
        f.setBlock(block);
        if (!EnderIO.proxy.isDedicatedServer()) {
          EnderIO.fluids.registerFluidBlockRendering(f);
        }
      }
      FluidRegistry.addBucketForFluid(f);

      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("fluid", f.getName());
      tag.setString("ore", "Glowstone");
      tag.setBoolean("toolforge", true);
      FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

      registerVanillaRecipesForGlowstone = true;
    }
    return f;
  }

  private static void glowstone(FMLPostInitializationEvent event) {
    Fluid f = FluidRegistry.getFluid(GLOWSTONE_FLUID_NAME);
    if (f == null) {
      Log.warn("Thermal Foundation fluid '" + GLOWSTONE_FLUID_NAME + "' is unexpectedly missing. Late registering our own.");
      f = glowstone((FMLPreInitializationEvent) null);
    }
    if (registerVanillaRecipesForGlowstone) {
      // Note: We match the old TE amounts
      TicProxy.registerSmelterySmelting(new ItemStack(Items.GLOWSTONE_DUST), f, 250);
      TicProxy.registerSmelterySmelting(new ItemStack(Blocks.GLOWSTONE), f, 1000);
      TicProxy.registerBasinCasting(new ItemStack(Blocks.GLOWSTONE), Prep.getEmpty(), f, 1000);
    }
  }

  private static Fluid redstone(FMLPreInitializationEvent event) {
    Fluid f = new Fluid(REDSTONE_FLUID_NAME, TicProxy.TEX_FLOWING, TicProxy.TEX_STILL) {
      @Override
      public int getColor() {
        return 0xFF000000 | 0xff0000;
      }
    };
    f.setDensity(1200);
    f.setLuminosity(Blocks.LIT_REDSTONE_ORE.getDefaultState().getLightValue() * 7 / 10);
    f.setTemperature(1700 + 273);
    f.setViscosity(1500);
    if (FluidRegistry.registerFluid(f)) {
      if (REGISTER_REDSTONE_BLOCK) {
        MoltenRedstone block = new BlockFluidEio.MoltenRedstone(f, Material.WATER, 0xff0000);
        block.init();
        f.setBlock(block);
        if (!EnderIO.proxy.isDedicatedServer()) {
          EnderIO.fluids.registerFluidBlockRendering(f);
        }
      }
      if (FluidRegistry.isUniversalBucketEnabled()) {
        FluidRegistry.addBucketForFluid(f);
      }

      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("fluid", f.getName());
      tag.setString("ore", "Redstone");
      tag.setBoolean("toolforge", true);
      FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

      registerVanillaRecipesForRedstone = true;
    }
    return f;
  }

  private static void redstone(FMLPostInitializationEvent event) {
    Fluid f = FluidRegistry.getFluid(REDSTONE_FLUID_NAME);
    if (f == null) {
      Log.warn("Thermal Foundation fluid '" + REDSTONE_FLUID_NAME + "' is unexpectedly missing. Late registering our own.");
      f = redstone((FMLPreInitializationEvent) null);
    }
    if (registerVanillaRecipesForRedstone) {
      // Note: We match the old TE amounts
      TicProxy.registerSmelterySmelting(new ItemStack(Items.REDSTONE), f, 100);
      TicProxy.registerSmelterySmelting(new ItemStack(Blocks.REDSTONE_BLOCK), f, 900);
      TicProxy.registerBasinCasting(new ItemStack(Blocks.REDSTONE_BLOCK), Prep.getEmpty(), f, 900);
    }
  }

  private static Fluid ender(FMLPreInitializationEvent event) {
    Fluid f = new Fluid(ENDER_FLUID_NAME, TicProxy.TEX_FLOWING, TicProxy.TEX_STILL) {
      @Override
      public int getColor() {
        return 0xFF000000 | 0x1b7b6b;
      }
    };
    f.setDensity(4000);
    f.setLuminosity(3);
    f.setTemperature(1000 + 273);
    f.setViscosity(35);
    if (FluidRegistry.registerFluid(f)) {
      if (REGISTER_ENDER_BLOCK) {
        MoltenEnder block = new BlockFluidEio.MoltenEnder(f, Material.WATER, 0x1b7b6b);
        block.init();
        f.setBlock(block);
        if (!EnderIO.proxy.isDedicatedServer()) {
          EnderIO.fluids.registerFluidBlockRendering(f);
        }
      }
      if (FluidRegistry.isUniversalBucketEnabled()) {
        FluidRegistry.addBucketForFluid(f);
      }

      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("fluid", f.getName());
      tag.setString("ore", "Ender");
      tag.setBoolean("toolforge", true);
      FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

      registerVanillaRecipesForEnder = true;
    }
    return f;
  }

  private static void ender(FMLPostInitializationEvent event) {
    Fluid f = FluidRegistry.getFluid(ENDER_FLUID_NAME);
    if (f == null) {
      Log.warn("Thermal Foundation fluid '" + ENDER_FLUID_NAME + "' is unexpectedly missing. Late registering our own.");
      f = ender((FMLPreInitializationEvent) null);
    }
    if (registerVanillaRecipesForEnder) {
      // Note: We match the old TE amounts
      TicProxy.registerSmelterySmelting(new ItemStack(Items.ENDER_PEARL), f, 250);
      // Need to do this later because of the cast
      Things cast = new Things("tconstruct:cast_custom:2");
      NNList<ItemStack> casts = cast.getItemStacks();
      if (!casts.isEmpty()) {
        TicProxy.registerTableCast(new ItemStack(Items.ENDER_PEARL), casts.get(0), f, 250);
      }
    }
    TicProxy.registerSmelterySmelting(POWDER_ENDER.getStack(), f, 250 / 9);
  }

}
