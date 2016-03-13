package crazypants.enderio.render;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

import org.apache.commons.lang3.tuple.Pair;

public class EnderBakedModel implements IEnderBakedModel {

  private final List<BakedQuad> generalQuads = new ArrayList<BakedQuad>();
  private final List<List<BakedQuad>> faceQuads = new ArrayList<List<BakedQuad>>();
  private final boolean ambientOcclusion;
  private final boolean gui3d;
  private final TextureAtlasSprite texture;
  @SuppressWarnings("deprecation")
  private final net.minecraft.client.renderer.block.model.ItemCameraTransforms cameraTransforms;
  private final VertexFormat format;
  @SuppressWarnings("deprecation")
  private final Matrix4f[] transformTypes = new Matrix4f[net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.values().length];

  public EnderBakedModel(IBakedModel transforms, Pair<List<IBlockState>, List<IBakedModel>> pair, List<IBlockState> overlays) {
    this((IPerspectiveAwareModel) (transforms instanceof IPerspectiveAwareModel ? transforms : null), pair, overlays);
  }

  public EnderBakedModel(IBakedModel transforms, Pair<List<IBlockState>, List<IBakedModel>> pair) {
    this((IPerspectiveAwareModel) (transforms instanceof IPerspectiveAwareModel ? transforms : null), pair, null);
  }

  public EnderBakedModel(IPerspectiveAwareModel transforms, Pair<List<IBlockState>, List<IBakedModel>> data) {
    this(transforms, data, null);
  }

  public EnderBakedModel(IPerspectiveAwareModel transforms, Pair<List<IBlockState>, List<IBakedModel>> data, List<IBlockState> overlays) {
    for (@SuppressWarnings("unused")
    EnumFacing face : EnumFacing.values()) {
      faceQuads.add(new ArrayList<BakedQuad>());
    }

    List<IBakedModel> models = new ArrayList<IBakedModel>();
    List<IBlockState> states = new ArrayList<IBlockState>();

    if (data != null) {
      if (data.getLeft() != null) {
        states.addAll(data.getLeft());
      }
      if (data.getRight() != null) {
        models.addAll(data.getRight());
      }
    }

    if (overlays != null) {
      states.addAll(overlays);
    }

    BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    for (IBlockState state : states) {
      models.add(modelShapes.getModelForState(state));
    }

    if (transforms != null) {
      for (@SuppressWarnings("deprecation")
      net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType transformType : net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
          .values()) {
        Pair<? extends IFlexibleBakedModel, Matrix4f> pair = transforms.handlePerspective(transformType);
        this.transformTypes[transformType.ordinal()] = pair.getRight();
      }
    }

    for (IBakedModel bakedModel : models) {
      generalQuads.addAll(nonNullList(bakedModel.getGeneralQuads()));
      for (EnumFacing face : EnumFacing.values()) {
        faceQuads.get(face.ordinal()).addAll(nonNullList(bakedModel.getFaceQuads(face)));
      }
    }

    this.ambientOcclusion = !models.isEmpty() ? models.get(0).isAmbientOcclusion() : true;
    this.gui3d = transforms != null ? transforms.isGui3d() : !models.isEmpty() ? models.get(0).isGui3d() : true;
    this.texture = (!models.isEmpty() ? models.get(0) : getMissingModel()).getParticleTexture();
    @SuppressWarnings("deprecation")
    final net.minecraft.client.renderer.block.model.ItemCameraTransforms itemCameraTransforms = (transforms != null ? transforms : !models.isEmpty() ? models
        .get(0) : getMissingModel()).getItemCameraTransforms();
    this.cameraTransforms = itemCameraTransforms;
    this.format = transforms != null ? transforms.getFormat() : Attributes.DEFAULT_BAKED_FORMAT;
  }

  private static List<BakedQuad> nonNullList(List<BakedQuad> list) {
    return list != null ? list : new ArrayList<BakedQuad>();
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
    return this.faceQuads.get(facing.ordinal());
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

  @SuppressWarnings("deprecation")
  @Override
  public net.minecraft.client.renderer.block.model.ItemCameraTransforms getItemCameraTransforms() {
    return cameraTransforms;
  }

  @Override
  public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(
      @SuppressWarnings("deprecation") net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType cameraTransformType) {
    return Pair.of(this, transformTypes[cameraTransformType.ordinal()]);
  }

  @Override
  public Matrix4f[] getTransformTypes() {
    return transformTypes;
  }

}
