package crazypants.enderio.integration.tic;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.integration.tic.book.EioBook;
import crazypants.enderio.integration.tic.book.TicBook;
import crazypants.enderio.integration.tic.fluids.Ender;
import crazypants.enderio.integration.tic.fluids.Glowstone;
import crazypants.enderio.integration.tic.fluids.Metal;
import crazypants.enderio.integration.tic.fluids.Redstone;
import crazypants.enderio.integration.tic.modifiers.TicModifiers;
import crazypants.enderio.integration.tic.queues.TicHandler;
import crazypants.enderio.integration.tic.recipes.TicRegistration;
import net.minecraft.block.Block;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class TicControl {

  public static void initPreTic(FMLPreInitializationEvent event) {
    TicProxy.register(TicHandler.instance);
    Glowstone.createFluid();
    Redstone.createFluid();
    Ender.createFluid();
    NNList.of(Alloy.class).apply(new Callback<Alloy>() {
      @Override
      public void apply(@Nonnull Alloy alloy) {
        Metal.createFluid(alloy);
      }
    });
  }

  public static void injectBlocks(@Nonnull IForgeRegistry<Block> registry) {
    registry.register(Glowstone.createFluidBlock());
    registry.register(Redstone.createFluidBlock());
    registry.register(Ender.createFluidBlock());
    NNList.of(Alloy.class).apply(new Callback<Alloy>() {
      @Override
      public void apply(@Nonnull Alloy alloy) {
        registry.register(Metal.createFluidBlock(alloy));
      }
    });
  }

  public static void initPreTic(FMLInitializationEvent event) {
    NNList.of(Alloy.class).apply(new Callback<Alloy>() {
      @Override
      public void apply(@Nonnull Alloy alloy) {
        Metal.createTraits(alloy);
      }
    });

    TicModifiers.register();
    if (!EnderIO.proxy.isDedicatedServer()) {
      TicBook.integrate();
      EioBook.integrate();
    }
  }

  public static void initPreTic(FMLPostInitializationEvent event) {
    Glowstone.registerGlowstoneRecipes();
    Redstone.registerRedstoneRecipes();
    Ender.registerEnderRecipes();
  }

  public static void initPostTic(FMLPostInitializationEvent event) {
    TicRegistration.registerSmeltings();
    TicRegistration.registerAlloys();
    TicRegistration.registerTableCasting();
    TicRegistration.registerBasinCasting();
  }

  @SideOnly(Side.CLIENT)
  public static void registerRenderers(ModelRegistryEvent event) {
    Glowstone.registerRenderers();
    Redstone.registerRenderers();
    Ender.registerRenderers();
    NNList.of(Alloy.class).apply(new Callback<Alloy>() {
      @Override
      public void apply(@Nonnull Alloy alloy) {
        Metal.registerRenderers(alloy);
      }
    });
  }

}
