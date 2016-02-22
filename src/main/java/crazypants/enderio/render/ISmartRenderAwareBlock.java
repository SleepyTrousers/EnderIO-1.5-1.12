package crazypants.enderio.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * An interface for blocks to allow them to be involved in the rendering process.
 * <p>
 * A block must also:
 * <ul>
 * <li>Register with SmartModelAttacher
 * <li>Implement the sole property EnumRenderMode and have a matching blockstate json
 * <li>Force its blockstate to EnumRenderMode.AUTO
 * <li>Return a BlockStateWrapper from getExtendedProperties
 * </ul>
 *
 */
public interface ISmartRenderAwareBlock {

  /**
   * Return a render mapper for the block.
   * <p>
   * This is called in a render thread.
   */
  IRenderMapper getRenderMapper(IBlockState state, IBlockAccess world, BlockPos pos);

  /**
   * Return a render mapper for the given item stack.
   */
  IRenderMapper getRenderMapper(ItemStack stack);

}
