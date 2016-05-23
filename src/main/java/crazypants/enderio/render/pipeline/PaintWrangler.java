package crazypants.enderio.render.pipeline;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import crazypants.enderio.Log;
import crazypants.enderio.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.paint.render.PaintedBlockAccessWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;

public class PaintWrangler {

  private static class Memory {
    boolean doActualStateWithTe = true;
    boolean doActualStateWithOutTe = false;
    boolean doExtendedStateWithTe = true;
    boolean doExtendedStateWithOutTe = false;
    boolean doPaint = true;

    @Override
    public String toString() {
      return "Memory [doActualStateWithTe=" + doActualStateWithTe + ", doActualStateWithOutTe=" + doActualStateWithOutTe + ", doExtendedStateWithTe="
          + doExtendedStateWithTe + ", doExtendedStateWithOutTe=" + doExtendedStateWithOutTe + "]";
    }
  }

  private static final ConcurrentHashMap<Block, Memory> cache = new ConcurrentHashMap<Block, Memory>();

  public static boolean wrangleBakedModel(IBlockAccess blockAccess, BlockPos pos, IBlockState rawPaintSource, IBlockState paint, QuadCollector quads) {
    Block block = paint.getBlock();
    Memory memory = cache.get(block);
    if (memory == null) {
      memory = new Memory();
      cache.put(block, memory);
    }

    if (!memory.doPaint) {
      return false;
    }

    IBakedModel paintModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(rawPaintSource);

    List<String> errors = quads.addUnfriendlybakedModel(MinecraftForgeClient.getRenderLayer(), paintModel, paint, MathHelper.getPositionRandom(pos));
    if (errors != null) {
      memory.doPaint = false;
      Log.error("Failed to use block " + paint.getBlock() + " as paint. Error(s) while rendering: " + errors);
      return false;
    }

    return true;
  }

  public static IBlockState getDynamicBlockState(IBlockAccess blockAccess, BlockPos pos, IBlockState paint) {
    if (paint == null) {
      return null;
    }

    Block block = paint.getBlock();
    Memory memory = cache.get(block);
    if (memory == null) {
      memory = new Memory();
      cache.put(block, memory);
    }

    if (memory.doPaint) {

      if (memory.doActualStateWithTe) {
        try {
          paint = paint.getActualState(new PaintedBlockAccessWrapper(blockAccess, true), pos);          
        } catch (Throwable t) {
          memory.doActualStateWithTe = false;
          memory.doActualStateWithOutTe = true;
        }
      }

      if (memory.doActualStateWithOutTe) {
        try {
          paint = paint.getActualState(new PaintedBlockAccessWrapper(blockAccess, false), pos);
        } catch (Throwable t) {
          memory.doActualStateWithOutTe = false;
        }
      }

      if (memory.doExtendedStateWithTe) {
        try {
          paint = block.getExtendedState(paint, new PaintedBlockAccessWrapper(blockAccess, true), pos);
        } catch (Throwable t) {
          memory.doExtendedStateWithTe = false;
          memory.doExtendedStateWithOutTe = true;
        }
      }

      if (memory.doExtendedStateWithOutTe) {
        try {
          paint = block.getExtendedState(paint, new PaintedBlockAccessWrapper(blockAccess, false), pos);
        } catch (Throwable t) {
          memory.doExtendedStateWithOutTe = false;
        }
      }
    }
    return paint;
  }

  public static IBakedModel handlePaint(ItemStack stack, IBlockPaintableBlock block) {
    IBlockState paintSource = block.getPaintSource((Block) block, stack);
    if (paintSource != null) {
      int damage = paintSource.getBlock().damageDropped(paintSource);
      ItemStack paint = new ItemStack(paintSource.getBlock(), 1, damage);
      return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(paint);
    }
    return null;

  }
}
