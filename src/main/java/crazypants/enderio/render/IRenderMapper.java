package crazypants.enderio.render;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

/**
 * A render mapper maps the state of a placed block or an item stack into a list of blockstates that will be rendered together.
 * <p>
 * These blockstates can belong to any block.
 *
 */
public interface IRenderMapper {

  /**
   * Get lists of blockstates and pre-baked, pre-rotated models to render for the given block.
   * <p>
   * Will be called in a render thread.
   * <p>
   * May return null. May return one or both of the lists as null.
   */
  @SideOnly(Side.CLIENT)
  Pair<List<IBlockState>, List<IBakedModel>> mapBlockRender(BlockStateWrapper state, IBlockAccess world, BlockPos pos);

  /**
   * Get lists of blockstates to render as overlay layer for the given block. This layer will be rendered no matter what is rendered for the block itself, e.g.
   * if it is painted.
   * <p>
   * Will be called in a render thread.
   * <p>
   * May return null.
   */
  @SideOnly(Side.CLIENT)
  List<IBlockState> mapOverlayLayer(BlockStateWrapper state, IBlockAccess world, BlockPos pos);

  /**
   * Get lists of blockstates and pre-baked, pre-rotated models to render for the given item stack.
   * <p>
   * The given block is the block of the item in the stack. It is given to save the method the effort to get it out of the stack when the caller already had to
   * do it.
   * <p>
   * May return null. May return one or both of the lists as null.
   */
  @SideOnly(Side.CLIENT)
  Pair<List<IBlockState>, List<IBakedModel>> mapBlockRender(Block block, ItemStack stack);

}
