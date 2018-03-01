package crazypants.enderio.conduit.init;

import javax.annotation.Nonnull;

import crazypants.enderio.conduit.ConduitBundleStateMapper;
import crazypants.enderio.conduit.EnderIOConduits;
import crazypants.enderio.conduit.render.ConduitBundleRenderManager;
import crazypants.enderio.powertools.machine.capbank.network.ClientNetworkManager;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIOConduits.MODID, value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

  @SubscribeEvent
  public static void onModelRegistryEvent(@Nonnull ModelRegistryEvent event) {
    ConduitBundleStateMapper.create();
  }

  @Override
  public void init(@Nonnull FMLPreInitializationEvent event) {
    super.init(event);
    ConduitBundleRenderManager.instance.init(event);
  }

  @Override
  public void init(@Nonnull FMLInitializationEvent event) {
    super.init(event);
    MinecraftForge.EVENT_BUS.register(ClientNetworkManager.getInstance());
  }

  @Override
  public void init(@Nonnull FMLPostInitializationEvent event) {
    super.init(event);
  }

}
