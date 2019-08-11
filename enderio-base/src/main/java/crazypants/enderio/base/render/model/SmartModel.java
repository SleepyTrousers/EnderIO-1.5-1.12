package crazypants.enderio.base.render.model;

import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

public class SmartModel implements IModel {

  private final IModel parent;

  public SmartModel(IModel parent) {
    this.parent = parent;
  }

  @Override
  public @Nonnull IBakedModel bake(@Nonnull IModelState state, @Nonnull VertexFormat format,
      @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    return new SmartBakedModel(parent.bake(state, format, bakedTextureGetter));
  }

  public static class SmartBakedModel extends RelayingBakedModel {

    public SmartBakedModel(@Nonnull IBakedModel defaults) {
      super(defaults);
    }

  }

}
