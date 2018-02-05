package crazypants.enderio.base.render.model;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.pipeline.EnderItemOverrideList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class RelayingBakedModel implements IBakedModel {

  private IBakedModel defaults;
  private final boolean isTESRTransformsOnly;

  private @Nonnull IBakedModel getDefaults() {
    if (defaults == null) {
      try {
        defaults = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
      } catch (Throwable t) {

      }
    }
    return NullHelper.notnullF(defaults, "missing model is missing");
  }

  public static @Nonnull RelayingBakedModel wrapModelForTESRRendering(IBakedModel model) {
    if (model instanceof RelayingBakedModel) {
      RelayingBakedModel rbm = (RelayingBakedModel) model;
      if (rbm.isTESRTransformsOnly) {
        return rbm;
      } else {
        return new RelayingBakedModel(rbm.defaults, true);
      }
    } else {
      return new RelayingBakedModel(model, true);
    }
  }

  public RelayingBakedModel(IBakedModel defaults, boolean isTESRTransformsOnly) {
    this.defaults = defaults;
    this.isTESRTransformsOnly = isTESRTransformsOnly;
  }

  public RelayingBakedModel(@Nonnull IBakedModel defaults) {
    this(defaults, false);
  }

  @Override
  public @Nonnull List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
    long start = crazypants.enderio.util.Profiler.instance.start();
    if (state instanceof BlockStateWrapperBase) {
      IBakedModel model = ((BlockStateWrapperBase) state).getModel();
      if (model instanceof CollectedQuadBakedBlockModel) {
        ((CollectedQuadBakedBlockModel) model).setParticleTexture(getParticleTexture());
      }
      if (model != null) {
        crazypants.enderio.util.Profiler.instance.stop(start, state.getBlock().getLocalizedName() + " (relayed)");
        return model.getQuads(state, side, rand);
      }
    }
    return getDefaults().getQuads(state, side, rand);
  }

  @Override
  public boolean isAmbientOcclusion() {
    return getDefaults().isAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return getDefaults().isGui3d();
  }

  @Override
  public boolean isBuiltInRenderer() {
    return isTESRTransformsOnly;
  }

  @Override
  public @Nonnull TextureAtlasSprite getParticleTexture() {
    return getDefaults().getParticleTexture();
  }

  @Override
  public @Nonnull ItemCameraTransforms getItemCameraTransforms() {
    return net.minecraft.client.renderer.block.model.ItemCameraTransforms.DEFAULT;
  }

  @Override
  public @Nonnull ItemOverrideList getOverrides() {
    return EnderItemOverrideList.instance;
  }

  @Override
  public @Nonnull Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull ItemCameraTransforms.TransformType cameraTransformType) {
    return Pair.of(this, getDefaults().handlePerspective(cameraTransformType).getRight());
  }

}
