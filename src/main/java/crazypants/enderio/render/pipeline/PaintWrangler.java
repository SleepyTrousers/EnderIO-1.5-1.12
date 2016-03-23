package crazypants.enderio.render.pipeline;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ISmartBlockModel;
import crazypants.enderio.Log;
import crazypants.enderio.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.paint.render.PaintedBlockAccessWrapper;

public class PaintWrangler {

  private static class Memory {
    boolean doActualStateWithTe = true;
    boolean doActualStateWithOutTe = false;
    boolean doExtendedStateWithTe = true;
    boolean doExtendedStateWithOutTe = false;
    boolean dohandleBlockState = true;
    boolean doPaint = true;

    @Override
    public String toString() {
      return "Memory [doActualStateWithTe=" + doActualStateWithTe + ", doActualStateWithOutTe=" + doActualStateWithOutTe + ", doExtendedStateWithTe="
          + doExtendedStateWithTe + ", doExtendedStateWithOutTe=" + doExtendedStateWithOutTe + ", dohandleBlockState=" + dohandleBlockState + "]";
    }
  }

  private static final ConcurrentHashMap<Block, Memory> cache = new ConcurrentHashMap<Block, Memory>();

  public static boolean wrangleBakedModel(IBlockAccess blockAccess, BlockPos pos, IBlockState paint, QuadCollector quads) {

    Block block = paint.getBlock();
    Memory memory = cache.get(block);
    if (memory == null) {
      memory = new Memory();
      cache.put(block, memory);
    }

    if (!memory.doPaint) {
      return false;
    }

    if (memory.doActualStateWithTe) {
      try {
        paint = block.getActualState(paint, new PaintedBlockAccessWrapper(blockAccess, true), pos);
      } catch (Throwable t) {
        memory.doActualStateWithTe = false;
        memory.doActualStateWithOutTe = true;
      }
    }

    if (memory.doActualStateWithOutTe) {
      try {
        paint = block.getActualState(paint, new PaintedBlockAccessWrapper(blockAccess, false), pos);
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

    IBakedModel paintModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(paint);

    if (Minecraft.getMinecraft().gameSettings.allowBlockAlternatives && paintModel instanceof WeightedBakedModel) {
      paintModel = ((WeightedBakedModel) paintModel).getAlternativeModel(MathHelper.getPositionRandom(pos));
    }

    if (memory.dohandleBlockState && paintModel instanceof ISmartBlockModel) {
      try {
        paintModel = ((ISmartBlockModel) paintModel).handleBlockState(paint);
      } catch (Throwable t) {
        memory.dohandleBlockState = false;
      }
    }

    List<String> errors = quads.addUnfriendlybakedModel(MinecraftForgeClient.getRenderLayer(), paintModel, paint, MathHelper.getPositionRandom(pos));
    if (errors != null) {
      memory.doPaint = false;
      Log.error("Failed to use block " + paint.getBlock() + " as paint. Error(s) while rendering: " + errors);
      return false;
    }

    return true;
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
