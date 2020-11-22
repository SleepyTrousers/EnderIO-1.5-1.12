package crazypants.enderio.base.render.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.enderio.core.common.vecmath.Vector4d;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.render.model.FacadeSmartItemModel;
import crazypants.enderio.base.render.model.RotatingSmartItemModel;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public final class ItemModelRegistry {

  public interface Registry {
    @Nonnull
    IBakedModel wrap(@Nonnull IBakedModel model);
  }

  private static final Map<ModelResourceLocation, Registry> registries = new HashMap<ModelResourceLocation, Registry>();

  public static void register(@Nonnull String resource, @Nonnull Registry registry) {
    registries.put(new ModelResourceLocation(EnderIO.DOMAIN + ":" + resource + "#inventory"), registry);
  }

  public static void register(@Nonnull ModelResourceLocation resource, @Nonnull Registry registry) {
    registries.put(resource, registry);
  }

  public static void registerRotating(@Nonnull String resource, @Nonnull Vector4d rot) {
    register(resource, new Registry() {
      @Override
      public @Nonnull IBakedModel wrap(@Nonnull IBakedModel model) {
        return new RotatingSmartItemModel(model, transformType -> rot);
      }
    });
  }

  public static void registerRotating(@Nonnull ModelResourceLocation resource, @Nonnull Vector4d rot) {
    register(resource, new Registry() {
      @Override
      public @Nonnull IBakedModel wrap(@Nonnull IBakedModel model) {
        return new RotatingSmartItemModel(model, transformType -> rot);
      }
    });
  }

  public static void registerRotating(@Nonnull ModelResourceLocation resource,
      @Nonnull Function<ItemCameraTransforms.TransformType, Vector4d> targetTransforms) {
    register(resource, new Registry() {
      @Override
      public @Nonnull IBakedModel wrap(@Nonnull IBakedModel model) {
        return new RotatingSmartItemModel(model, targetTransforms);
      }
    });
  }

  public static void registerFacade(@Nonnull ModelResourceLocation resource) {
    register(resource, new Registry() {
      @Override
      public @Nonnull IBakedModel wrap(@Nonnull IBakedModel model) {
        return new FacadeSmartItemModel(model);
      }
    });
  }

  @SubscribeEvent()
  public static void bakeModels(@Nonnull ModelBakeEvent event) {
    for (Entry<ModelResourceLocation, Registry> entry : registries.entrySet()) {
      final ModelResourceLocation resource = entry.getKey();
      if (resource != null) {
        IBakedModel model = event.getModelRegistry().getObject(resource);
        final Registry registry = entry.getValue();
        if (registry != null && model != null) {
          event.getModelRegistry().putObject(resource, registry.wrap(model));
        }
      }
    }
  }

}
