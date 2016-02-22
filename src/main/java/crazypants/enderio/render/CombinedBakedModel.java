package crazypants.enderio.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;

public class CombinedBakedModel implements IBakedModel {

  private final List<IBakedModel> models;

  private CombinedBakedModel(IBakedModel model) {
    this.models = Collections.singletonList(model);
  }

  private CombinedBakedModel(List<IBakedModel> models) {
    this.models = models;
  }

  public static CombinedBakedModel buildFromModels(List<IBakedModel> models) {
    IBakedModel missingModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
    return models.size() > 0 ? new CombinedBakedModel(models) : new CombinedBakedModel(missingModel);
  }

  public static CombinedBakedModel buildFromLocations(List<ModelResourceLocation> modelLocations) {
    List<IBakedModel> models = new ArrayList<IBakedModel>();
    ModelManager modelManager = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager();
    IBakedModel missingModel = modelManager.getMissingModel();
    for (ModelResourceLocation resourceLocation : modelLocations) {
      IBakedModel model = modelManager.getModel(resourceLocation);
      if (model != missingModel) {
        models.add(model);
      }
    }
    return models.size() > 0 ? new CombinedBakedModel(models) : new CombinedBakedModel(missingModel);
  }

  public static CombinedBakedModel buildFromStates(List<IBlockState> states) {
    List<IBakedModel> models = new ArrayList<IBakedModel>();
    BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    IBakedModel missingModel = modelShapes.getModelManager().getMissingModel();
    for (IBlockState state : states) {
      IBakedModel model = modelShapes.getModelForState(state);
      if (model != missingModel) {
        models.add(model);
      }
    }
    return models.size() > 0 ? new CombinedBakedModel(models) : new CombinedBakedModel(missingModel);
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
    List<BakedQuad> result = new ArrayList<BakedQuad>();
    for (IBakedModel model : models) {
      result.addAll(model.getFaceQuads(p_177551_1_));
    }
    return result;
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    List<BakedQuad> result = new ArrayList<BakedQuad>();
    for (IBakedModel model : models) {
      result.addAll(model.getGeneralQuads());
    }
    return result;
  }

  @Override
  public boolean isAmbientOcclusion() {
    return models.get(0).isAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return models.get(0).isGui3d();
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleTexture() {
    return models.get(0).getParticleTexture();
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return ItemCameraTransforms.DEFAULT;
  }

}
