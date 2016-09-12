package crazypants.enderio.render.pipeline;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import crazypants.enderio.Log;
import crazypants.enderio.paint.render.PaintedBlockAccessWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;

public class PaintWrangler {

  private static class Memory {
    boolean doActualState = true;
    boolean doExtendedState = true;
    boolean doPaint = true;
  }

  private static final ConcurrentHashMap<Block, Memory> cache = new ConcurrentHashMap<Block, Memory>();

  public static boolean wrangleBakedModel(IBlockAccess blockAccess, BlockPos pos, IBlockState paintSource, QuadCollector quads) {
    IBlockState actualPaintSource = paintSource;
    IBlockState extendedPaintSource = paintSource;
    if (paintSource == null) {
      return false;
    }
    Block block = paintSource.getBlock();
    if (block == null) {
      return false;
    }
    Memory memory = cache.get(block);
    if (memory == null) {
      memory = new Memory();
      cache.put(block, memory);
    }
    if (!memory.doPaint) {
      return false;
    }
    if (memory.doActualState || memory.doExtendedState) {
      final PaintedBlockAccessWrapper fakeWorld = PaintedBlockAccessWrapper.instance(blockAccess);
      if (memory.doActualState) {
        try {
          extendedPaintSource = actualPaintSource = paintSource.getActualState(fakeWorld, pos);
        } catch (Throwable t) {
          Log.warn("Failed to get actual state of block " + paintSource.getBlock() + " to use as paint. Error while rendering: " + t);
          memory.doActualState = false;
        }
      }
      if (memory.doExtendedState) {
        try {
          extendedPaintSource = block.getExtendedState(actualPaintSource, fakeWorld, pos);
        } catch (Throwable t) {
          Log.warn("Failed to get extended state of block " + paintSource.getBlock() + " to use as paint. Error while rendering: " + t);
          memory.doExtendedState = false;
        }
      }
      fakeWorld.free();
    }

    IBakedModel paintModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(actualPaintSource);
    if (paintModel == null) {
      return false;
    }

    final long positionRandom = MathHelper.getPositionRandom(pos);
    BlockRenderLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
    for (BlockRenderLayer layer : quads.getBlockLayers()) {
      if (paintSource.getBlock().canRenderInLayer(extendedPaintSource, layer)) {
        ForgeHooksClient.setRenderLayer(layer);
        List<String> errors = quads.addUnfriendlybakedModel(layer, paintModel, extendedPaintSource, positionRandom);
        if (errors != null) {
          memory.doPaint = false;
          Log.error("Failed to use block " + paintSource.getBlock() + " as paint. Error(s) while rendering: " + errors);
          ForgeHooksClient.setRenderLayer(oldRenderLayer);
          return false;
        }
      }
    }
    ForgeHooksClient.setRenderLayer(oldRenderLayer);
    return true;
  }

}
