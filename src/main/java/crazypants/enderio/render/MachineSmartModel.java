package crazypants.enderio.render;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
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

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.render.paint.PaintWrangler;

public class MachineSmartModel implements ISmartBlockModel, ISmartItemModel {

  private final Cache<Long, IEnderBakedModel> cache = CacheBuilder.newBuilder().maximumSize(200).<Long, IEnderBakedModel> build();

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

  @SuppressWarnings("deprecation")
  @Override
  public net.minecraft.client.renderer.block.model.ItemCameraTransforms getItemCameraTransforms() {
    return net.minecraft.client.renderer.block.model.ItemCameraTransforms.DEFAULT;
  }

  @Override
  public IBakedModel handleBlockState(IBlockState stateIn) {
    long start = crazypants.util.Profiler.client.start();
    if (stateIn instanceof BlockStateWrapper) {
      final BlockStateWrapper state = (BlockStateWrapper) stateIn;
      Block block = state.getBlock();
      final IBlockAccess world = state.getWorld();
      final BlockPos pos = state.getPos();

      IRenderMapper renderMapper = null;
      List<IBlockState> overlayLayer = null;
      if (block instanceof ISmartRenderAwareBlock) {
        renderMapper = ((ISmartRenderAwareBlock) block).getRenderMapper();
        overlayLayer = renderMapper.mapOverlayLayer(state, world, pos);
      }

      if (block instanceof IBlockPaintableBlock) {
        IBakedModel paintModel = PaintWrangler.handlePaint(state, (IBlockPaintableBlock) block, world, pos);
        if (paintModel != null) {
          if (overlayLayer == null || overlayLayer.isEmpty()) {
            crazypants.util.Profiler.client.stop(start, state.getBlock().getLocalizedName() + " (painted)");
            return paintModel;
          } else {
            IBakedModel bakedModel = new EnderBakedModel(null, Pair.of((List<IBlockState>) null, Collections.singletonList(paintModel)), overlayLayer);
            crazypants.util.Profiler.client.stop(start, state.getBlock().getLocalizedName() + " (painted)");
            return bakedModel;
          }
        }
      }

      if (block instanceof ISmartRenderAwareBlock) {
        final long cacheKey = state.getCacheKey();
        if (cacheKey != 0) {
          IEnderBakedModel bakedModel = cache.getIfPresent(cacheKey);
          if (bakedModel == null) {
            bakedModel = new EnderBakedModel(null, renderMapper.mapBlockRender(state, world, pos));
            if (cacheKey != 0) {
              cache.put(cacheKey, bakedModel);
            }
          }
          if (overlayLayer != null) {
            bakedModel = new OverlayBakedModel(bakedModel, overlayLayer);
          }
          crazypants.util.Profiler.client.stop(start, state.getBlock().getLocalizedName() + " (w/caching)");
          return bakedModel;
        } else {
          IEnderBakedModel bakedModel = new EnderBakedModel(null, renderMapper.mapBlockRender(state, world, pos), overlayLayer);
          crazypants.util.Profiler.client.stop(start, state.getBlock().getLocalizedName());
          return bakedModel;
        }
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
        if (block instanceof IBlockPaintableBlock) {
          IBakedModel paint = PaintWrangler.handlePaint(stack, (IBlockPaintableBlock) block);
          if (paint != null) {
            if (block instanceof ISmartRenderAwareBlock) {
              // Combine(!) it with the mapBlockRender() because that may want to add overlays to mark the block as "painted"
              IRenderMapper renderMapper = ((ISmartRenderAwareBlock) block).getRenderMapper();
              Pair<List<IBlockState>, List<IBakedModel>> paintOverlay = renderMapper.mapItemPaintOverlayRender(block, stack);
              if (paintOverlay == null) {
                IBlockState stdOverlay = BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY);
                EnderBakedModel bakedModel = new EnderBakedModel(getDefaults(), null, Collections.singletonList(stdOverlay));
                return new UnderlayBakedModel(bakedModel, paint);
              } else if ((paintOverlay.getLeft() == null || paintOverlay.getLeft().isEmpty())
                  && (paintOverlay.getRight() == null || paintOverlay.getRight().isEmpty())) {
                return paint;
              } else {
                EnderBakedModel bakedModel = new EnderBakedModel(getDefaults(), paintOverlay);
                return new UnderlayBakedModel(bakedModel, paint);
              }
            }
            return paint;
          }
        }
        if (block instanceof ISmartRenderAwareBlock) {
          IRenderMapper renderMapper = ((ISmartRenderAwareBlock) block).getRenderMapper();
          return new EnderBakedModel(getDefaults(), renderMapper.mapItemRender(block, stack));
        }
      }
    }
    return this;
  }

}
