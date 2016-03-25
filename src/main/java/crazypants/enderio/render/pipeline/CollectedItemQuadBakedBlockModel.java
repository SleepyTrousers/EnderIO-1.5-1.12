package crazypants.enderio.render.pipeline;

import java.util.List;

import javax.vecmath.Matrix4f;

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

public class CollectedItemQuadBakedBlockModel implements IPerspectiveAwareModel {

  private final IBakedModel parent;
  private final ItemQuadCollector quads;

  public CollectedItemQuadBakedBlockModel(IBakedModel parent, ItemQuadCollector quads) {
    this.parent = parent;
    this.quads = quads;
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing facing) {
    return quads.getQuads(facing);
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    return quads.getQuads(null);
  }

  @Override
  public boolean isAmbientOcclusion() {
    return parent.isAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return parent.isGui3d();
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleTexture() {
    return parent.getParticleTexture();
  }

  @SuppressWarnings("deprecation")
  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return parent.getItemCameraTransforms();
  }

  @Override
  public VertexFormat getFormat() {
    return Attributes.DEFAULT_BAKED_FORMAT;
  }

  @SuppressWarnings("deprecation")
  @Override
  public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
    if (parent instanceof IPerspectiveAwareModel) {
      Pair<? extends IFlexibleBakedModel, Matrix4f> perspective = ((IPerspectiveAwareModel) parent).handlePerspective(cameraTransformType);
      return Pair.of(this, perspective.getRight());
    }
    return Pair.of(this, null);
  }

}
