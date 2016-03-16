package crazypants.enderio.render;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;

public class EmptyBakedModel implements IBakedModel {

  public static final IBakedModel instance = new EmptyBakedModel();

  private EmptyBakedModel() {
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing facing) {
    return Collections.<BakedQuad> emptyList();
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    return Collections.<BakedQuad> emptyList();
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

  // Note: This is never called from anywhere where it will actually be used for rendering.
  @Override
  public TextureAtlasSprite getParticleTexture() {
    return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel().getParticleTexture();
  }

  @SuppressWarnings("deprecation")
  @Override
  public net.minecraft.client.renderer.block.model.ItemCameraTransforms getItemCameraTransforms() {
    return net.minecraft.client.renderer.block.model.ItemCameraTransforms.DEFAULT;
  }

}
