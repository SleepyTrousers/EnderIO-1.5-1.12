package crazypants.enderio.render.model;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import crazypants.enderio.render.util.QuadCollector;
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

  private final QuadCollector quads;
  private TextureAtlasSprite particleTexture = null;

  private static final ItemOverrideList itemOverrideList = new ItemOverrideList(Collections.<ItemOverride> emptyList()) {
    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
      return originalModel;
    }
  };

  public CollectedQuadBakedBlockModel(QuadCollector quads) {
    this.quads = quads;
  }

  @Override
  public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
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
  public TextureAtlasSprite getParticleTexture() {
    return particleTexture != null ? particleTexture : Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager()
        .getMissingModel().getParticleTexture();
  }

  protected void setParticleTexture(TextureAtlasSprite particleTexture) {
    this.particleTexture = particleTexture;
  }

  @Override
  public net.minecraft.client.renderer.block.model.ItemCameraTransforms getItemCameraTransforms() {
    return ItemCameraTransforms.DEFAULT;
  }

  @Override
  public ItemOverrideList getOverrides() {
    return itemOverrideList;
  }

}
