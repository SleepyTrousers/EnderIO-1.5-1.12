package crazypants.enderio.base.render.pipeline;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.paint.render.PaintedBlockAccessWrapper;
import crazypants.enderio.base.render.util.QuadCollector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;

public class PaintWrangler {

  private static final BlockRenderLayer BREAKING = null;

  private static class Memory {
    boolean doActualState = true;
    boolean doExtendedState = true;
    boolean doPaint = true;
  }

  private static final @Nonnull ConcurrentHashMap<Block, Memory> cache = new ConcurrentHashMap<Block, Memory>();

  public static boolean wrangleBakedModel(@Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, @Nullable IBlockState paintSource,
      @Nonnull QuadCollector quads) {
    if (paintSource == null || paintSource == Blocks.AIR.getDefaultState()) {
      return false;
    }
    IBlockState actualPaintSource = paintSource;
    IBlockState extendedPaintSource = paintSource;
    Block block = paintSource.getBlock();
    Memory memory = cache.get(block);
    if (memory == null) {
      memory = new Memory();
      cache.put(block, memory);
    }
    if (!memory.doPaint) {
      return false;
    }
    PaintedBlockAccessWrapper fakeWorld = null;
    if (memory.doActualState || memory.doExtendedState) {
      fakeWorld = PaintedBlockAccessWrapper.instance(blockAccess);
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
    }

    IBakedModel paintModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(actualPaintSource);
    if (paintModel == Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel()) {
      if (fakeWorld != null) {
        fakeWorld.free();
      }
      return false;
    }

    final long positionRandom = MathHelper.getPositionRandom(pos);
    BlockRenderLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
    for (BlockRenderLayer layer : quads.getBlockLayers()) {
      if (layer == BREAKING
          || paintSource.getBlock().canRenderInLayer(extendedPaintSource, NullHelper.notnullJ(layer, "'a == null || b(a)' gave null to b()"))) {
        ForgeHooksClient.setRenderLayer(layer);
        List<String> errors = quads.addUnfriendlybakedModel(layer, paintModel, extendedPaintSource, positionRandom);
        if (errors != null) {
          memory.doPaint = false;
          Log.error("Failed to use block " + paintSource.getBlock() + " as paint. Error(s) while rendering: " + errors);
          ForgeHooksClient.setRenderLayer(oldRenderLayer);
          if (fakeWorld != null) {
            fakeWorld.free();
          }
          return false;
        }
      }
    }
    ForgeHooksClient.setRenderLayer(oldRenderLayer);
    if (fakeWorld != null) {
      fakeWorld.free();
    }
    return true;
  }

}
