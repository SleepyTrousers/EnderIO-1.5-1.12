package crazypants.enderio.render;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;

import crazypants.enderio.Log;

public class PaintRegistry {

  public static enum PaintMode {
    ALL_TEXTURES,
    TAGGED_TEXTURES;
  }

  private static class PaintRegistryServer {
    private PaintRegistryServer() {
    }

    protected void init() {
    }

    public void registerModel(String name, ResourceLocation location, PaintMode paintMode) {
    }

    public <T> T getModel(Class<T> clazz, String name, IBlockState paintSource, IModelState rotation) {
      return null;
    }
  }

  private static class PaintRegistryClient extends PaintRegistryServer {
    @SideOnly(Side.CLIENT)
    private ConcurrentMap<String, Pair<ResourceLocation, PaintMode>> modelLocations;

    @SideOnly(Side.CLIENT)
    private ConcurrentMap<String, IModel> models;

    @SideOnly(Side.CLIENT)
    private ConcurrentMap<String, ConcurrentMap<Pair<IBlockState, IModelState>, IBakedModel>> cache;

    private PaintRegistryClient() {
      init();
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void init() {
      modelLocations = new ConcurrentHashMap<String, Pair<ResourceLocation, PaintMode>>();
      models = new ConcurrentHashMap<String, IModel>();
      cache = new ConcurrentHashMap<String, ConcurrentMap<Pair<IBlockState, IModelState>, IBakedModel>>();
      modelLocations.put("_missing", Pair.of((ResourceLocation) null, PaintMode.ALL_TEXTURES));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModel(String name, ResourceLocation location, PaintMode paintMode) {
      modelLocations.put(name, Pair.of(location, paintMode));
      models.remove(name);
      cache.remove(name);
    }

    @SubscribeEvent()
    @SideOnly(Side.CLIENT)
    public void bakeModels(ModelBakeEvent event) {
      cache.clear();

      for (Entry<String, Pair<ResourceLocation, PaintMode>> entry : modelLocations.entrySet()) {
        try {
          ResourceLocation resourceLocation = entry.getValue().getLeft();
          if (resourceLocation != null) {
            IModel model = event.modelLoader.getModel(resourceLocation);
            models.put(entry.getKey(), model);
          } else {
            models.put(entry.getKey(), event.modelLoader.getMissingModel());
          }
        } catch (IOException e) {
          Log.warn("Model '" + entry.getValue() + "' failed to load: " + e);
        }
      }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public <T> T getModel(Class<T> clazz, String name, IBlockState paintSource, IModelState rotation) {
      if (!cache.containsKey(name)) {
        cache.put(name, new ConcurrentHashMap<Pair<IBlockState, IModelState>, IBakedModel>());
      }
      ConcurrentMap<Pair<IBlockState, IModelState>, IBakedModel> subcache = cache.get(name);
      Pair<IBlockState, IModelState> key = Pair.of(paintSource, rotation);
      IBakedModel bakedModel = subcache.get(key);
      if (bakedModel == null) {
        IModel sourceModel = models.get(name);
        if (sourceModel == null) {
          sourceModel = models.get("_missing");
        }
        if (sourceModel != null) {
          bakedModel = paintModel(sourceModel, paintSource, rotation, getPaintMode(name));
        }
        if (bakedModel == null) {
          bakedModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
        }
        subcache.putIfAbsent(key, bakedModel);
      }
      return clazz == IBakedModel.class ? (T) bakedModel : null;
    }

    @SideOnly(Side.CLIENT)
    private PaintMode getPaintMode(String name) {
      Pair<ResourceLocation, PaintMode> pair = modelLocations.get(name);
      return pair == null ? null : pair.getRight();
    }

    @SideOnly(Side.CLIENT)
    private IBakedModel paintModel(IModel sourceModel, final IBlockState paintSource, IModelState rotation, final PaintMode paintMode) {
      IModelState state = sourceModel.getDefaultState();
      state = rotation == null ? state : new ModelStateComposition(state, rotation);
      return sourceModel.bake(state, Attributes.DEFAULT_BAKED_FORMAT, new Function<ResourceLocation, TextureAtlasSprite>() {
        @Override
        public TextureAtlasSprite apply(ResourceLocation location) {
          String locationString = location.toString();
          if (paintMode != PaintMode.TAGGED_TEXTURES || locationString.endsWith("PAINT")) {
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(paintSource);
          } else {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(locationString);
          }
        }
      });
    }

  }

  private static PaintRegistryServer instance = null;

  public static void create() {
    if (instance == null) {
      instance = new PaintRegistryClient();
      MinecraftForge.EVENT_BUS.register(instance);
    }
  }

  public static void registerModel(String name, ResourceLocation location) {
    registerModel(name, location, PaintMode.TAGGED_TEXTURES);
  }

  public static void registerModel(String name, ResourceLocation location, PaintMode paintMode) {
    create();
    instance.registerModel(name, location, paintMode);
  }

  public <T> T getModel(Class<T> clazz, String name, IBlockState paintSource, IModelState rotation) {
    return instance.getModel(clazz, name, paintSource, rotation);
  }
}
