package crazypants.enderio.base.paint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.Optional.Interface;

/**
 * Master interface for paintable things. Do not implement directly, use one of the sub-interfaces.
 *
 */
public interface IPaintable {

  /**
   * (Re-)Paints a block that exists in the world. It's the caller's responsibility to check that the paint source is valid and appropriate, and to trigger a
   * world re-render.
   */
  default void setPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable IBlockState paintSource) {
    IPaintable.IPaintableTileEntity te = BlockEnder.getAnyTileEntity(world, pos, IPaintable.IPaintableTileEntity.class);
    if (te != null) {
      te.setPaintSource(paintSource);
    }
  }

  /**
   * (Re-)Paints an item stack. It's the caller's responsibility to check that the paint source is valid and appropriate.
   * <p>
   * The given block is the block of the item in the stack. It is given to save the method the effort to get it out of the stack when the caller already had to
   * do it.
   */
  default void setPaintSource(@Nonnull Block block, @Nonnull ItemStack stack, @Nullable IBlockState paintSource) {
    PaintUtil.setSourceBlock(stack, paintSource);
  }

  /**
   * Gets the paint source from a block that exists in the world. Will return null if the block is not painted.
   */
  default @Nullable IBlockState getPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IPaintable.IPaintableTileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos, IPaintable.IPaintableTileEntity.class);
    if (te != null) {
      return te.getPaintSource();
    }
    return null;
  }

  /**
   * Gets the paint source from an item stack. Will return null if the item stack is not painted.
   * <p>
   * The given block is the block of the item in the stack. It is given to save the method the effort to get it out of the stack when the caller already had to
   * do it.
   */
  default @Nullable IBlockState getPaintSource(@Nonnull Block block, @Nonnull ItemStack stack) {
    return PaintUtil.getSourceBlock(stack);
  }

  /**
   * A block that can be painted with a texture. It keeps its model, but applies the texture from the paint source to it.
   */
  public static interface ITexturePaintableBlock extends IPaintable {

  }

  @Interface(iface = "team.chisel.ctm.api.IFacade", modid = "ctm-api")
  public static interface IBlockPaintableBlock extends IPaintable, team.chisel.ctm.api.IFacade {

    @Override
    @Nonnull
    default IBlockState getFacade(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
      final IBlockState blockState = world.getBlockState(pos);
      IBlockState paintSource = getPaintSource(blockState, world, pos);
      return paintSource != null ? paintSource : blockState;
    }

  }

  /**
   * A block that can be painted with a full block. It renders the paint source's model instead of its own. The paint source must be a full, solid block.
   */
  public static interface ISolidBlockPaintableBlock extends IBlockPaintableBlock {

  }

  /**
   * A block that can be painted with any block. It renders the paint source's model instead of its own. The paint source can be any block.
   */
  public static interface INonSolidBlockPaintableBlock extends IBlockPaintableBlock {

  }

  /**
   * Helper interface to make it easier for blocks to talk to their tile entity.
   */
  public static interface IPaintableTileEntity {

    void setPaintSource(@Nullable IBlockState paintSource);

    @Nullable
    IBlockState getPaintSource();
  }

  /**
   * Block marked with this interface won't have their paint rendered when paint is hidden by the wrench. Only valid for IBlockPaintableBlock and its
   * sub-interfaces
   */
  public static interface IWrenchHideablePaint {

  }

}
