package crazypants.enderio.render;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

import org.apache.commons.lang3.tuple.Pair;

public class EnderBakedModel implements IPerspectiveAwareModel {

  private final List<BakedQuad> generalQuads = new ArrayList<BakedQuad>();
  private final List<List<BakedQuad>> faceQuads = new ArrayList<List<BakedQuad>>();
  private final boolean ambientOcclusion;
  private final boolean gui3d;
  private final TextureAtlasSprite texture;
  private final ItemCameraTransforms cameraTransforms;
  private final VertexFormat format;
  private final Matrix4f[] transformTypes = new Matrix4f[TransformType.values().length];

  public EnderBakedModel(IBakedModel transforms, Pair<List<IBlockState>, List<IBakedModel>> pair) {
    this((IPerspectiveAwareModel) (transforms instanceof IPerspectiveAwareModel ? transforms : null), pair);
  }

  public EnderBakedModel(IPerspectiveAwareModel transforms, Pair<List<IBlockState>, List<IBakedModel>> data) {
    for (EnumFacing face : EnumFacing.values()) {
      faceQuads.add(new ArrayList<BakedQuad>());
    }

    VertexFormat _format = null;
    List<IBakedModel> models = new ArrayList<IBakedModel>();

    if (data != null) {
      if (data.getLeft() != null) {
        BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
        for (IBlockState state : data.getLeft()) {
          IBakedModel model = modelShapes.getModelForState(state);
          models.add(model);
          if (_format == null && model instanceof IFlexibleBakedModel) {
            _format = ((IFlexibleBakedModel) model).getFormat();
          }
        }
      }
      if (data.getRight() != null) {
        models.addAll(data.getRight());
      }
    }
    if (data == null || (data.getLeft() == null && data.getRight() == null)) {
      models.add(getMissingModel());
    }

    if (transforms != null) {
      _format = transforms.getFormat();
      for (TransformType transformType : TransformType.values()) {
        Pair<? extends IFlexibleBakedModel, Matrix4f> pair = transforms.handlePerspective(transformType);
        this.transformTypes[transformType.ordinal()] = pair.getRight();
      }
    }

    for (IBakedModel bakedModel : models) {
      generalQuads.addAll(bakedModel.getGeneralQuads());
      for (EnumFacing face : EnumFacing.VALUES) {
        faceQuads.get(face.ordinal()).addAll(bakedModel.getFaceQuads(face));
      }
    }

    this.ambientOcclusion = !models.isEmpty() ? models.get(0).isAmbientOcclusion() : true;
    this.gui3d = transforms != null ? transforms.isGui3d() : !models.isEmpty() ? models.get(0).isGui3d() : true;
    this.texture = (!models.isEmpty() ? models.get(0) : getMissingModel()).getParticleTexture();
    this.cameraTransforms = (transforms != null ? transforms : !models.isEmpty() ? models.get(0) : getMissingModel()).getItemCameraTransforms();
    this.format = _format != null ? _format : Attributes.DEFAULT_BAKED_FORMAT;
  }

  private static IBakedModel getMissingModel() {
    return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
  }

  @Override
  public VertexFormat getFormat() {
    return format;
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing facing) {
    return (List) this.faceQuads.get(facing.ordinal());
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    return generalQuads;
  }

  @Override
  public boolean isAmbientOcclusion() {
    return ambientOcclusion;
  }

  @Override
  public boolean isGui3d() {
    return gui3d;
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleTexture() {
    return texture;
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return cameraTransforms;
  }

  @Override
  public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
    return Pair.of(this, transformTypes[cameraTransformType.ordinal()]);
  }

}
