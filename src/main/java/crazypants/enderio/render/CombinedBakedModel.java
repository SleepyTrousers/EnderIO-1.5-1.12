package crazypants.enderio.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

/**
 * A baked model that consists of other baked models.
 * <p>
 * This is used to combine models at runtime to be rendered in combinations that can not be expressed in the blockstate json.
 *
 */
public class CombinedBakedModel implements IBakedModel {

  protected final List<IBakedModel> models;

  private CombinedBakedModel(IBakedModel model) {
    this.models = Collections.singletonList(model);
  }

  private CombinedBakedModel(List<IBakedModel> models) {
    this.models = models;
  }

  /**
   * Builds a new baked model that combines the given baked models. If the optional transform model is given, and it is perspective aware, a perspective aware
   * baked model with the transform model's transforms will be created. The transform model will not be added to the rendering list of the result model.
   */
  public static IBakedModel buildFromModels(IBakedModel transforms, List<IBakedModel> models) {
    return transforms instanceof IPerspectiveAwareModel ? new PerspectiveAwareCombinedBakedModel((IPerspectiveAwareModel) transforms, models)
        : new CombinedBakedModel(models);
    // !models.isEmpty() ? xxx : new CombinedBakedModel(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes()
    // .getModelManager().getMissingModel());
  }

  /**
   * Builds a new baked model that combines the baked models for the given ModelResourceLocations. If the optional transform model is given, and it is
   * perspective aware, a perspective aware baked model with the transform model's transforms will be created. The transform model will not be added to the
   * rendering list of the result model.
   */
  public static IBakedModel buildFromLocations(IBakedModel transforms, List<ModelResourceLocation> modelLocations) {
    List<IBakedModel> models = new ArrayList<IBakedModel>();
    ModelManager modelManager = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager();
    IBakedModel missingModel = modelManager.getMissingModel();
    boolean hasMissings = false;
    for (ModelResourceLocation resourceLocation : modelLocations) {
      IBakedModel model = modelManager.getModel(resourceLocation);
      if (model != missingModel) {
        models.add(model);
      } else {
        hasMissings = true;
      }
    }
    if (models.isEmpty() && hasMissings) {
      models.add(missingModel);
    }
    return buildFromModels(transforms, models);
  }

  /**
   * Builds a new baked model that combines the baked models for the given blockstates. If the optional transform model is given, and it is perspective aware, a
   * perspective aware baked model with the transform model's transforms will be created. The transform model will not be added to the rendering list of the
   * result model.
   * 
   * If the state list is empty, nothing will be rendered. If it consists only of missingModels, the missingModel will be rendered. Otherwise missingModels will
   * be ignored---it is easier to see what is missing when you can actually see what is not missing.
   */
  public static IBakedModel buildFromStates(IBakedModel transforms, List<IBlockState> states) {
    List<IBakedModel> models = new ArrayList<IBakedModel>();
    BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    IBakedModel missingModel = modelShapes.getModelManager().getMissingModel();
    boolean hasMissings = false;
    for (IBlockState state : states) {
      IBakedModel model = modelShapes.getModelForState(state);
      if (model != missingModel) {
        models.add(model);
      } else {
        hasMissings = true;
      }
    }
    if (models.isEmpty() && hasMissings) {
      models.add(missingModel);
    }
    return buildFromModels(transforms, models);
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
    return models.isEmpty() ? true : models.get(0).isAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return models.isEmpty() ? true : models.get(0).isGui3d();
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleTexture() {
    return models.isEmpty() ? Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel()
        .getParticleTexture() : models.get(0).getParticleTexture();
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return models.isEmpty() ? Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel()
        .getItemCameraTransforms() : models.get(0).getItemCameraTransforms();
  }

  private static class PerspectiveAwareCombinedBakedModel extends CombinedBakedModel implements IPerspectiveAwareModel {

    protected final IPerspectiveAwareModel transforms;

    private PerspectiveAwareCombinedBakedModel(IPerspectiveAwareModel transforms, IBakedModel model) {
      super(model);
      this.transforms = transforms;
    }

    private PerspectiveAwareCombinedBakedModel(IPerspectiveAwareModel transforms, List<IBakedModel> models) {
      super(models);
      this.transforms = transforms;
    }

    @Override
    public boolean isGui3d() {
      return transforms.isGui3d();
    }

    /**
     * I have no idea what this does. But my Eclipse tells me it is never called by anyone but a wrapper, like this. So I'm ignoring it. --Henry
     */
    @Override
    public VertexFormat getFormat() {
      return transforms.getFormat();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
      return transforms.getItemCameraTransforms();
    }

    @Override
    public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
      Pair<? extends IFlexibleBakedModel, Matrix4f> perspective = transforms.handlePerspective(cameraTransformType);
      return Pair.of(this, perspective.getRight());
    }

  }

}
