package crazypants.enderio.render;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MachineSmartModel implements ISmartBlockModel, ISmartItemModel {

  private final Cache<Long, IBakedModel> cache = CacheBuilder.newBuilder().maximumSize(200).<Long, IBakedModel> build();

  private IBakedModel defaults;

  private IBakedModel getDefaults() {
    if (defaults == null) {
      try {
        defaults = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
      } catch (Throwable t) {

      }
    }
    return defaults;
  }

  public MachineSmartModel(IBakedModel defaults) {
    this.defaults = defaults;
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
    return getDefaults().getFaceQuads(p_177551_1_);
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    return getDefaults().getGeneralQuads();
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
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleTexture() {
    return getDefaults().getParticleTexture();
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return ItemCameraTransforms.DEFAULT;
  }

  @Override
  public IBakedModel handleBlockState(IBlockState state) {
    long start = crazypants.util.Profiler.client.start();
    IRenderCache rc = null;
    if (state instanceof BlockStateWrapper) {
      long cacheKey = ((BlockStateWrapper) state).getCacheKey();
      if (cacheKey != 0) {
        IBakedModel cachedModel = cache.getIfPresent(cacheKey);
        if (cachedModel != null) {
          crazypants.util.Profiler.client.stop(start, state.getBlock().getLocalizedName() + " (cached)");
          return cachedModel;
        }
      }
      Block block = state.getBlock();
      IBlockAccess world = ((BlockStateWrapper) state).getWorld();
      BlockPos pos = ((BlockStateWrapper) state).getPos();

      if (block instanceof ISmartRenderAwareBlock) {
        IRenderMapper renderMapper = ((ISmartRenderAwareBlock) block).getRenderMapper(state, world, pos);
        EnderBakedModel bakedModel = new EnderBakedModel(null, renderMapper.mapBlockRender(state, world, pos));
        if (cacheKey != 0) {
          cache.put(cacheKey, bakedModel);
        }
        crazypants.util.Profiler.client.stop(start, state.getBlock().getLocalizedName());
        return bakedModel;
      }
    }

    return this;
  }

  @Override
  public IBakedModel handleItemState(ItemStack stack) {
    if (stack != null) {
      Item item = stack.getItem();
      if (item instanceof ItemBlock) {
        Block block = ((ItemBlock) item).getBlock();
        if (block instanceof ISmartRenderAwareBlock) {
          IRenderMapper renderMapper = ((ISmartRenderAwareBlock) block).getRenderMapper(stack);
          return new EnderBakedModel(getDefaults(), renderMapper.mapBlockRender(block, stack));
        }
      }
    }
    return this;
  }

}
