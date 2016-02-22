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
 * <li>Implement the property EnumRenderMode and have a matching blockstate json:
 * <ul>
 * <li>Defaults (e.g. particles, item transformations) go into defaultState+EnumRenderMode.DEFAULTS, the model doesn't matter but is needed for the
 * transformations to work
 * <li>All states that have EnumRenderMode.AUTO will be replaced with the MachineSmartModel, so their content doesn't matter
 * <li>The other EnumRenderModes should have the matching data, but is it in the decision of the render mapper to use them or not
 * </ul>
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
