package crazypants.enderio.paint;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;

/**
 * Master interface for paintable things. Do not implement directly, use one of the sub-interfaces.
 *
 */
public interface IPaintable {

  /**
   * (Re-)Paints a block that exists in the world. It's the caller's responsibility to check that the paint source is valid and appropriate, and to trigger a
   * world re-render.
   */
  void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable IBlockState paintSource);

  /**
   * (Re-)Paints an item stack. It's the caller's responsibility to check that the paint source is valid and appropriate.
   * <p>
   * The given block is the block of the item in the stack. It is given to save the method the effort to get it out of the stack when the caller already had to
   * do it.
   */
  void setPaintSource(Block block, ItemStack stack, @Nullable IBlockState paintSource);

  /**
   * Gets the paint source from a block that exists in the world. Will return null if the block is not painted.
   */
  IBlockState getPaintSource(IBlockState state, IBlockAccess world, BlockPos pos);

  /**
   * Gets the paint source from an item stack. Will return null if the item stack is not painted.
   * <p>
   * The given block is the block of the item in the stack. It is given to save the method the effort to get it out of the stack when the caller already had to
   * do it.
   */
  IBlockState getPaintSource(Block block, ItemStack stack);

  /**
   * A block that can be painted with a texture. It keeps its model, but applies the texture from the paint source to it.
   */
  public static interface ITexturePaintableBlock extends IPaintable {

  }

  @InterfaceList({ @Interface(iface = "team.chisel.api.IFacade", modid = "ChiselAPI"), @Interface(iface = "team.chisel.ctm.api.IFacade", modid = "ctm-api") })
  public static interface IBlockPaintableBlock extends IPaintable, team.chisel.api.IFacade, team.chisel.ctm.api.IFacade {

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

    IBlockState getPaintSource();
  }

  /**
   * Block marked with this interface won't have their paint rendered when paint is hidden by the wrench. Only valid for IBlockPaintableBlock and its
   * sub-interfaces
   */
  public static interface IWrenchHideablePaint {

  }

}
