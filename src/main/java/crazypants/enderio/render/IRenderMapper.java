package crazypants.enderio.render;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.pipeline.QuadCollector;

/**
 * A render mapper maps the state of a placed block or an item stack into a list of blockstates that will be rendered together.
 * <p>
 * These blockstates can belong to any block.
 *
 */
public interface IRenderMapper {

  /**
   * Render mappers that implement this sub-interface will be called each for block render layer. They are expected to check for the current render layer and
   * decide what to return.
   * <p>
   * Without this interface, the mapper will only be called if the render layer matches the block's getBlockLayer(). This allows painted blocks to simply render
   * in all layers without having to check layers themselves.
   *
   */
  public static interface IRenderLayerAware extends IRenderMapper {

  }

  /**
   * Get a list of blockstates to render for the given block. Optionally, this method can add quads to the given quad collector directly.
   * <p>
   * May return null.
   * <p>
   * Note: This will be called once for the block's getBlockLayer() or once per render layer if the render mapper is IRenderLayerAware.
   */
  @SideOnly(Side.CLIENT)
  List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, EnumWorldBlockLayer blockLayer, QuadCollector quadCollector);

  @SideOnly(Side.CLIENT)
  @Deprecated
  Pair<List<IBlockState>, List<IBakedModel>> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos);

  /**
   * Get a mapping of EnumFacing to EnumIOMode to render as overlay layer for the given block.
   * <p>
   * May return null.
   * <p>
   * Note: This will only be called once, with isPainted either true or false. The render layer is determined by the IO block.
   */
  @SideOnly(Side.CLIENT)
  EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted);

  /**
   * Get lists of blockstates and pre-baked, pre-rotated models to render for the given item stack.
   * <p>
   * The given block is the block of the item in the stack. It is given to save the method the effort to get it out of the stack when the caller already had to
   * do it.
   * <p>
   * May return null. May return one or both of the lists as null.
   */
  @SideOnly(Side.CLIENT)
  Pair<List<IBlockState>, List<IBakedModel>> mapItemRender(Block block, ItemStack stack);

  /**
   * Get lists of blockstates and pre-baked, pre-rotated models to render for the given item stack when it is painted. These are rendered in addition to the
   * paint. If an empty result is returned, nothing is rendered. If null is returned, the generic, full-block "is painted" overlay is rendered.
   * <p>
   * The given block is the block of the item in the stack. It is given to save the method the effort to get it out of the stack when the caller already had to
   * do it.
   * <p>
   * May return null. May return one or both of the lists as null.
   */
  @SideOnly(Side.CLIENT)
  Pair<List<IBlockState>, List<IBakedModel>> mapItemPaintOverlayRender(Block block, ItemStack stack);

}
