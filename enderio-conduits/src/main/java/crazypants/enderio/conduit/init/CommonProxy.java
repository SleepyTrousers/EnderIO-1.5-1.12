package crazypants.enderio.conduit.init;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
  
  public void init(@Nonnull FMLPreInitializationEvent event) {
  }
  
  public void init(@Nonnull FMLInitializationEvent event) {
    if (Config.registerRecipes) {
//       ConduitRecipes.addRecipes();
    }
  }

  public void init(@Nonnull FMLPostInitializationEvent event) {
  }

}
