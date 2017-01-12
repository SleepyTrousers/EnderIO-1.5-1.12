package crazypants.enderio.render.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import crazypants.enderio.EnderIO;
import crazypants.enderio.render.model.FacadeSmartItemModel;
import crazypants.enderio.render.model.RotatingSmartItemModel;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemModelRegistry {

  private ItemModelRegistry() {
  }

  public static void create() {
    MinecraftForge.EVENT_BUS.register(new ItemModelRegistry());
  }

  public static interface Registry {
    IBakedModel wrap(IBakedModel model);
  }

  private static final Map<ModelResourceLocation, Registry> registries = new HashMap<ModelResourceLocation, Registry>();

  public static void register(String resource, Registry registry) {
    registries.put(new ModelResourceLocation(EnderIO.DOMAIN + ":" + resource + "#inventory"), registry);
  }

  public static void register(ModelResourceLocation resource, Registry registry) {
    registries.put(resource, registry);
  }

  public static void registerRotating(String resource, final int speed) {
    register(resource, new Registry() {
      @Override
      public IBakedModel wrap(IBakedModel model) {
        return new RotatingSmartItemModel((IPerspectiveAwareModel) model, speed);
      }
    });
  }

  public static void registerFacade(ModelResourceLocation resource) {
    register(resource, new Registry() {
      @Override
      public IBakedModel wrap(IBakedModel model) {
        return new FacadeSmartItemModel((IPerspectiveAwareModel) model);
      }
    });
  }

  @SubscribeEvent()
  public void bakeModels(ModelBakeEvent event) {
    for (Entry<ModelResourceLocation, Registry> entry : registries.entrySet()) {
      IBakedModel model = event.getModelRegistry().getObject(entry.getKey());
      event.getModelRegistry().putObject(entry.getKey(), entry.getValue().wrap(model));

    }
  }

}
