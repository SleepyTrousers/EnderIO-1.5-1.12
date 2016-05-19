package crazypants.enderio.render.pipeline;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import crazypants.enderio.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.paint.IPaintable.IWrenchHideablePaint;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.dummy.BlockMachineBase;

public class EnderItemOverrideList /* extends ItemOverrideList */{

  private final static Cache<Pair<Block, Long>, ItemQuadCollector> cache = CacheBuilder.newBuilder().maximumSize(500)
      .<Pair<Block, Long>, ItemQuadCollector> build();

  public static final EnderItemOverrideList instance = new EnderItemOverrideList();

  public static IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
    if (originalModel == null) {
      throw new NullPointerException("Missing parameter 'IBakedModel originalModel'");
    }
    if (stack == null) {
      throw new NullPointerException("Missing parameter 'ItemStack stack'");
    }
    Block block = Block.getBlockFromItem(stack.getItem());
    if (block == null) {
      throw new NullPointerException("Wrong parameter 'ItemStack stack', not an ItemBlock");
    }

    if (block instanceof IBlockPaintableBlock && (!(block instanceof IWrenchHideablePaint) || !YetaUtil.shouldHeldItemHideFacades())) {
      IBlockState paintSource = ((IBlockPaintableBlock) block).getPaintSource(block, stack);
      if (paintSource != null) {
        Pair<Block, Long> cacheKey = Pair.of((Block) null, new CacheKey().addCacheKey(paintSource).getCacheKey());
        ItemQuadCollector quads = cache.getIfPresent(cacheKey);
        if (quads == null) {
          quads = new ItemQuadCollector();
          quads.addItemBlockState(paintSource, null);
          quads.addBlockState(BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY), null);
          cache.put(cacheKey, quads);
        }
        return new CollectedItemQuadBakedBlockModel(originalModel, quads);
      }
    }

    if (block instanceof ISmartRenderAwareBlock) {
      IRenderMapper.IItemRenderMapper renderMapper = ((ISmartRenderAwareBlock) block).getItemRenderMapper();
      if (renderMapper != null) {

        Pair<Block, Long> cacheKey = Pair.of(block, renderMapper.getCacheKey(block, stack, new CacheKey().addCacheKey(stack.getMetadata())).getCacheKey());
        ItemQuadCollector quads = cacheKey.getRight() == null ? null : cache.getIfPresent(cacheKey);
        if (quads == null) {
          quads = new ItemQuadCollector();

          if (renderMapper instanceof IRenderMapper.IItemRenderMapper.IItemStateMapper) {
            quads.addBlockStates(((IRenderMapper.IItemRenderMapper.IItemStateMapper) renderMapper).mapItemRender(block, stack, quads), stack, block);
          } else if (renderMapper instanceof IRenderMapper.IItemRenderMapper.IItemModelMapper) {
            List<IBakedModel> bakedModels = ((IRenderMapper.IItemRenderMapper.IItemModelMapper) renderMapper).mapItemRender(block, stack);
            if (bakedModels != null) {
              for (IBakedModel bakedModel : bakedModels) {
                quads.addItemBakedModel(bakedModel);
              }
            }
          } else {
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
          }

          if (cacheKey.getRight() != null) {
            cache.put(cacheKey, quads);
          }
        }

        if (renderMapper instanceof IItemRenderMapper.IDynamicOverlayMapper) {
          quads = quads.combine(((IItemRenderMapper.IDynamicOverlayMapper) renderMapper).mapItemDynamicOverlayRender(block, stack));
        }

        return new CollectedItemQuadBakedBlockModel(originalModel, quads);
      }
    }

    return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
  }

}
