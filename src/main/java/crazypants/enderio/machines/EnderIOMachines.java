package crazypants.enderio.machines;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.config.IEnderIOAddon;
import crazypants.enderio.base.registry.Registry;
import crazypants.enderio.machines.config.Config;
import crazypants.enderio.machines.config.ConfigHandler;
import crazypants.enderio.machines.config.RecipeLoaderMachines;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.obelisk.render.ObeliskRenderManager;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.EnderIOMachines.MODID;
import static crazypants.enderio.machines.EnderIOMachines.MOD_NAME;
import static crazypants.enderio.machines.EnderIOMachines.VERSION;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "after:" + crazypants.enderio.base.EnderIO.MODID) // , guiFactory =
                                                                                                                         // "crazypants.enderio.machines.config.gui.ConfigFactory")
@EventBusSubscriber(Side.CLIENT)
public class EnderIOMachines implements IEnderIOAddon {

  public static final @Nonnull String MODID = "enderio-machines";
  public static final @Nonnull String DOMAIN = "enderio";
  public static final @Nonnull String MOD_NAME = "Ender IO Machines";
  public static final @Nonnull String VERSION = "@VERSION@";

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onModelRegister(ModelRegistryEvent event) {
    ObeliskRenderManager.INSTANCE.registerRenderers();
  }

  @EventHandler
  public static void init(FMLPreInitializationEvent event) {
    ConfigHandler.init(event);
  }

  @EventHandler
  public static void init(FMLInitializationEvent event) {
    ConfigHandler.init(event);
    PacketHandler.init(event);

    if (Config.registerRecipes.get()) {
      RecipeLoaderMachines.addRecipes();
    }

    Registry.enableSolarUpgrade(MachineObject.block_solar_panel.getItemNN(),
        new int[] { crazypants.enderio.base.config.Config.darkSteelSolarOneCost, crazypants.enderio.base.config.Config.darkSteelSolarTwoCost,
            crazypants.enderio.base.config.Config.darkSteelSolarThreeCost },
        new int[] { crazypants.enderio.base.config.Config.darkSteelSolarOneGen, crazypants.enderio.base.config.Config.darkSteelSolarTwoGen,
            crazypants.enderio.base.config.Config.darkSteelSolarThreeGen });
  }

  @EventHandler
  public static void init(FMLPostInitializationEvent event) {
    ConfigHandler.init(event);
  }

  @Override
  @Nullable
  public Configuration getConfiguration() {
    return ConfigHandler.config;
  }

}
