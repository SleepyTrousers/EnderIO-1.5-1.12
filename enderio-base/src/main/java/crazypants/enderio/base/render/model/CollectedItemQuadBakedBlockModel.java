package crazypants.enderio.base.render.model;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.base.render.util.ItemQuadCollector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class CollectedItemQuadBakedBlockModel implements IBakedModel {

  private final @Nonnull IBakedModel parent;
  private final @Nonnull ItemQuadCollector quads;

  private static final @Nonnull ItemOverrideList itemOverrideList = new ItemOverrideList(Collections.<ItemOverride> emptyList()) {
    @Override
    public @Nonnull IBakedModel handleItemState(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world,
        @Nullable EntityLivingBase entity) {
      return originalModel;
    }
  };

  public CollectedItemQuadBakedBlockModel(@Nonnull IBakedModel parent, @Nonnull ItemQuadCollector quads) {
    this.parent = parent;
    this.quads = quads;
  }

  @Override
  public @Nonnull List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
    return quads.getQuads(side);
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
  public @Nonnull TextureAtlasSprite getParticleTexture() {
    return parent.getParticleTexture();
  }

  @SuppressWarnings("deprecation")
  @Override
  public @Nonnull ItemCameraTransforms getItemCameraTransforms() {
    return parent.getItemCameraTransforms();
  }

  @Override
  public @Nonnull Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull ItemCameraTransforms.TransformType cameraTransformType) {
    return Pair.of(this, parent.handlePerspective(cameraTransformType).getRight());
  }

  @Override
  public @Nonnull ItemOverrideList getOverrides() {
    return itemOverrideList;
  }

}
