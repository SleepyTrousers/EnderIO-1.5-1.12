package crazypants.enderio.render.pipeline;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;

public class CollectedQuadBakedBlockModel implements IBakedModel {

  private final QuadCollector quads;
  private TextureAtlasSprite particleTexture = null;

  public CollectedQuadBakedBlockModel(QuadCollector quads) {
    this.quads = quads;
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing facing) {
    return quads.getQuads(facing, MinecraftForgeClient.getRenderLayer());
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    return quads.getQuads(null, MinecraftForgeClient.getRenderLayer());
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

  @SuppressWarnings("deprecation")
  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return ItemCameraTransforms.DEFAULT;
  }

}
