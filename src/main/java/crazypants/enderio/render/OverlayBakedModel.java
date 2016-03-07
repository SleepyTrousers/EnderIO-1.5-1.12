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
import net.minecraftforge.client.model.IFlexibleBakedModel;

import org.apache.commons.lang3.tuple.Pair;

public class OverlayBakedModel implements IEnderBakedModel {

  private final List<BakedQuad> generalQuads;
  private final List<List<BakedQuad>> faceQuads = new ArrayList<List<BakedQuad>>();
  private final boolean ambientOcclusion;
  private final boolean gui3d;
  private final TextureAtlasSprite texture;
  private final ItemCameraTransforms cameraTransforms;
  private final VertexFormat format;
  private final Matrix4f[] transformTypes;

  public OverlayBakedModel(IEnderBakedModel model, List<IBlockState> overlays) {

    generalQuads = new ArrayList<BakedQuad>(model.getGeneralQuads());
    for (EnumFacing face : EnumFacing.values()) {
      faceQuads.add(new ArrayList<BakedQuad>(model.getFaceQuads(face)));
    }

    BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    for (IBlockState state : overlays) {
      IBakedModel bakedModel = modelShapes.getModelForState(state);
      generalQuads.addAll(bakedModel.getGeneralQuads());
      for (EnumFacing face : EnumFacing.values()) {
        faceQuads.get(face.ordinal()).addAll(bakedModel.getFaceQuads(face));
      }
    }

    ambientOcclusion = model.isAmbientOcclusion();
    gui3d = model.isGui3d();
    texture = model.getParticleTexture();
    cameraTransforms = model.getItemCameraTransforms();
    format = model.getFormat();
    transformTypes = model.getTransformTypes();
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

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return cameraTransforms;
  }

  @Override
  public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
    return Pair.of(this, transformTypes[cameraTransformType.ordinal()]);
  }

  @Override
  public Matrix4f[] getTransformTypes() {
    return transformTypes;
  }

}
