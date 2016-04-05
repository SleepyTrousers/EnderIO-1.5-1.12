package crazypants.enderio.render;

import java.util.List;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.TRSRTransformation;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;

public class RotatingSmartItemModel implements IPerspectiveAwareModel {

  private final IPerspectiveAwareModel parent;
  private final int speed;

  public RotatingSmartItemModel(IPerspectiveAwareModel parent, int speed) {
    this.parent = parent;
    this.speed = speed;
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing facing) {
    return parent.getFaceQuads(facing);
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    return parent.getGeneralQuads();
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
  public net.minecraft.client.renderer.block.model.ItemCameraTransforms getItemCameraTransforms() {
    return parent.getItemCameraTransforms();
  }

  @Override
  public VertexFormat getFormat() {
    return parent.getFormat();
  }

  @Override
  public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(
      @SuppressWarnings("deprecation") net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType cameraTransformType) {
    Pair<? extends IFlexibleBakedModel, Matrix4f> perspective = parent.handlePerspective(cameraTransformType);

    double r = (EnderIO.proxy.getTickCount() % 360) + RenderUtil.getTimer().renderPartialTicks;

    TRSRTransformation transformOrig = new TRSRTransformation(perspective.getRight());
    Quat4f leftRot = transformOrig.getLeftRot();
    Quat4f yRotation = new Quat4f();
    yRotation.set(new AxisAngle4d(0, 1, 0, Math.toRadians(r * speed)));
    leftRot.mul(yRotation);
    TRSRTransformation transformNew = new TRSRTransformation(transformOrig.getTranslation(), leftRot, transformOrig.getScale(), transformOrig.getRightRot());

    return Pair.of(perspective.getLeft(), transformNew.getMatrix());
  }

}
