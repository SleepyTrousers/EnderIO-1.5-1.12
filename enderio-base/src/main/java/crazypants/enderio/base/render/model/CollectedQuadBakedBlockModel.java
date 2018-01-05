package crazypants.enderio.base.render.model;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.render.util.QuadCollector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.client.MinecraftForgeClient;

public class CollectedQuadBakedBlockModel implements IBakedModel {

  private final @Nonnull QuadCollector quads;
  private TextureAtlasSprite particleTexture = null;

  private static final @Nonnull ItemOverrideList itemOverrideList = new ItemOverrideList(Collections.<ItemOverride> emptyList()) {
    @Override
    public @Nonnull IBakedModel handleItemState(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world,
        @Nullable EntityLivingBase entity) {
      return originalModel;
    }
  };

  public CollectedQuadBakedBlockModel(@Nonnull QuadCollector quads) {
    this.quads = quads;
  }

  @Override
  public @Nonnull List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
    return quads.getQuads(side, MinecraftForgeClient.getRenderLayer());
  }

  @Override
  public boolean isAmbientOcclusion() {
    return true;
  }

  @Override
  public boolean isGui3d() {
    return true;
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }

  @Override
  public @Nonnull TextureAtlasSprite getParticleTexture() {
    return particleTexture != null ? particleTexture
        : Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel().getParticleTexture();
  }

  protected void setParticleTexture(TextureAtlasSprite particleTexture) {
    this.particleTexture = particleTexture;
  }

  @Override
  public @Nonnull ItemCameraTransforms getItemCameraTransforms() {
    return ItemCameraTransforms.DEFAULT;
  }

  @Override
  public @Nonnull ItemOverrideList getOverrides() {
    return itemOverrideList;
  }

}
