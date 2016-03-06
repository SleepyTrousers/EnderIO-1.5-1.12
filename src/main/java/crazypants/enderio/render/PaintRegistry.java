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

  private static final ConcurrentMap<String, Pair<ResourceLocation, PaintMode>> modelLocations = new ConcurrentHashMap<String, Pair<ResourceLocation, PaintMode>>();

  private static final ConcurrentMap<String, IModel> models = new ConcurrentHashMap<String, IModel>();

  private static final ConcurrentMap<String, ConcurrentMap<IBlockState, IBakedModel>> cache = new ConcurrentHashMap<String, ConcurrentMap<IBlockState, IBakedModel>>();

  static {
    modelLocations.put("_missing", Pair.of((ResourceLocation) null, PaintMode.ALL_TEXTURES));
  }

  public static void create() {
    MinecraftForge.EVENT_BUS.register(new PaintRegistry());
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

  public static void registerModel(String name, ResourceLocation location) {
    registerModel(name, location, PaintMode.TAGGED_TEXTURES);
  }

  public static void registerModel(String name, ResourceLocation location, PaintMode paintMode) {
    modelLocations.put(name, Pair.of(location, paintMode));
    models.remove(name);
    cache.remove(name);
  }

  public static IBakedModel getModel(String name, IBlockState paintSource) {
    if (!cache.containsKey(name)) {
      cache.put(name, new ConcurrentHashMap<IBlockState, IBakedModel>());
    }
    ConcurrentMap<IBlockState, IBakedModel> subcache = cache.get(name);
    IBakedModel bakedModel = subcache.get(paintSource);
    if (bakedModel == null) {
      IModel sourceModel = models.get(name);
      if (sourceModel == null) {
        sourceModel = models.get("_missing");
      }
      if (sourceModel != null) {
        bakedModel = paintModel(sourceModel, paintSource, getPaintMode(name));
      }
      if (bakedModel == null) {
        bakedModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
      }
      subcache.putIfAbsent(paintSource, bakedModel);
    }
    return bakedModel;
  }

  private static PaintMode getPaintMode(String name) {
    Pair<ResourceLocation, PaintMode> pair = modelLocations.get(name);
    return pair == null ? null : pair.getRight();
  }

  private static IBakedModel paintModel(IModel sourceModel, final IBlockState paintSource, final PaintMode paintMode) {
    return sourceModel.bake(sourceModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, new Function<ResourceLocation, TextureAtlasSprite>() {
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
