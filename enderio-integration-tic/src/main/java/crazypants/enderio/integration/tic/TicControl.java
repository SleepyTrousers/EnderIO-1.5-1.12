package crazypants.enderio.integration.tic;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.integration.tic.fluids.Ender;
import crazypants.enderio.integration.tic.fluids.Glowstone;
import crazypants.enderio.integration.tic.fluids.Metal;
import crazypants.enderio.integration.tic.fluids.Redstone;
import crazypants.enderio.integration.tic.queues.TicHandler;
import crazypants.enderio.integration.tic.recipes.TicRegistration;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

public class TicControl {

  public static void injectBlocks(@Nonnull IForgeRegistry<Block> registry) {
    registry.register(Glowstone.createGlowstone());
    registry.register(Redstone.createRedstone());
    registry.register(Ender.createEnder());
    NNList.of(Alloy.class).apply(new Callback<Alloy>() {
      @Override
      public void apply(@Nonnull Alloy alloy) {
        registry.register(Metal.createMetal(alloy));
      }
    });

  }

  public static void init(FMLPreInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(TicControl.class);
    TicProxy.register(TicHandler.instance);
  }

  public static void init(FMLInitializationEvent event) {
    // This is the place to integrate materials. All items, block, fluids and oredicts have been registered.
    // TicMaterials.register();
  }

  public static void init(FMLPostInitializationEvent event) {
    Glowstone.registerGlowstoneRecipes();
    Redstone.registerRedstoneRecipes();
    Ender.registerEnderRecipes();

    TicRegistration.registerSmeltings();
    TicRegistration.registerAlloys();
    TicRegistration.registerTableCasting();
    TicRegistration.registerBasinCasting();
  }

}
