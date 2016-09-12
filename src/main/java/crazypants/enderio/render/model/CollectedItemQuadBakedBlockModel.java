package crazypants.enderio.render.model;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.render.util.ItemQuadCollector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

public class CollectedItemQuadBakedBlockModel implements IPerspectiveAwareModel {

  private final IBakedModel parent;
  private final ItemQuadCollector quads;

  private static final ItemOverrideList itemOverrideList = new ItemOverrideList(Collections.<ItemOverride> emptyList()) {
    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
      return originalModel;
    }
  };

  public CollectedItemQuadBakedBlockModel(IBakedModel parent, ItemQuadCollector quads) {
    this.parent = parent;
    this.quads = quads;
  }

  @Override
  public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
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
  public TextureAtlasSprite getParticleTexture() {
    return parent.getParticleTexture();
  }

  @SuppressWarnings("deprecation")
  @Override
  public net.minecraft.client.renderer.block.model.ItemCameraTransforms getItemCameraTransforms() {
    return parent.getItemCameraTransforms();
  }

  @Override
  public Pair<? extends IBakedModel, Matrix4f> handlePerspective(
      net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType cameraTransformType) {
    if (parent instanceof IPerspectiveAwareModel) {
      Pair<? extends IBakedModel, Matrix4f> perspective = ((IPerspectiveAwareModel) parent).handlePerspective(cameraTransformType);
      return Pair.of(this, perspective.getRight());
    }
    return Pair.of(this, null);
  }

  @Override
  public ItemOverrideList getOverrides() {
    return itemOverrideList;
  }

}
