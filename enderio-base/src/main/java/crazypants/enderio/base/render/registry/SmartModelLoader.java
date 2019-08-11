package crazypants.enderio.base.render.registry;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.render.model.SmartModel;
import crazypants.enderio.base.render.registry.SmartModelRegistry.Data;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = Side.CLIENT, modid = EnderIO.MODID)
public final class SmartModelLoader implements ICustomModelLoader {

  private static final @Nonnull SmartModelLoader INSTANCE = new SmartModelLoader();

  private SmartModelLoader() {
  }

  @SubscribeEvent
  public static void foo(@Nonnull EnderIOLifecycleEvent.PreInit event) {
    ModelLoaderRegistry.registerLoader(INSTANCE);
  }

  @Override
  public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
    System.out.println("SmartModelLoader reload");
  }

  @Override
  public boolean accepts(@Nonnull ResourceLocation modelLocation) {
    System.out.println("SmartModelLoader candidate: " + modelLocation);
    return !LOADING.contains(modelLocation) && (modelLocation instanceof ModelResourceLocation)
        && SmartModelRegistry.query((ModelResourceLocation) modelLocation) != null;
    // return EnderIO.MODID.equals(modelLocation.getResourceDomain()) && !LOADING.contains(modelLocation) && (modelLocation instanceof ModelResourceLocation)
    // && (((ModelResourceLocation) modelLocation).getVariant().contains("render=auto")
    // || ((ModelResourceLocation) modelLocation).getVariant().contains("render=defaults"));
  }

  private final @Nonnull Set<ResourceLocation> LOADING = new HashSet<>();

  @Override
  public @Nonnull IModel loadModel(@Nonnull ResourceLocation modelLocation) throws Exception {
    System.out.println("SmartModelLoader: " + modelLocation);
    Data query = SmartModelRegistry.query((ModelResourceLocation) modelLocation);
    for (ModelResourceLocation extra : query.extras.values()) {
      net.minecraftforge.client.model.ModelLoaderRegistry.getModel(extra);
    }
    final ModelResourceLocation location = new ModelResourceLocation(modelLocation.toString()) {
      // ModelLoaderRegistry.getModel() checks "(location.getClass() == loading.getClass()" for circular dependencies
    };
    // final ModelResourceLocation location = new ModelResourceLocation(modelLocation.toString().replace("render=auto", "render=defaults")) {
    // // ModelLoaderRegistry.getModel() checks "(location.getClass() == loading.getClass()" for circular dependencies
    // };
    try {
      LOADING.add(location);
      return new SmartModel(net.minecraftforge.client.model.ModelLoaderRegistry.getModel(location));
    } finally {
      LOADING.remove(location);
    }
  }

}
