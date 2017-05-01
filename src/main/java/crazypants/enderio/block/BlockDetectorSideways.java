package crazypants.enderio.block;

import javax.annotation.Nonnull;

import crazypants.enderio.ModObject;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDetectorSideways extends BlockDetector {

  public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

  public static BlockDetectorSideways create() {
    BlockDetectorSideways result = new BlockDetectorSideways(ModObject.block_detector_block_side.getUnlocalisedName());
    result.init();
    return result;
  }

  public BlockDetectorSideways(@Nonnull String name) {
    super(name);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { IS_ON, PAINTED, FACING });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(IS_ON, (meta & 0x01) != 0).withProperty(PAINTED, (meta & 0x02) != 0).withProperty(FACING,
        EnumFacing.getHorizontal(meta >> 2));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return (state.getValue(IS_ON) ? 1 : 0) + (state.getValue(PAINTED) ? 2 : 0) + (state.getValue(FACING).getHorizontalIndex() << 2);
  }

  @Override
  public IBlockState withRotation(IBlockState state, Rotation rot) {
    return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
  }

  @Override
  public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
    return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
  }

  @Override
  protected IBlockState getItemState() {
    return super.getItemState().withProperty(FACING, EnumFacing.WEST);
  }

  @Override
  public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    return side.getOpposite() != state.getValue(FACING) && state.getValue(IS_ON) ? 15 : 0;
  }

  @Override
  protected boolean isTargetBlockAir(IBlockState state, World world, BlockPos pos) {
    return world.isAirBlock(pos.offset(state.getValue(FACING)));
  }

  @Override
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    final IBlockState state = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, placer.getHorizontalFacing());
    return state.withProperty(IS_ON, isTargetBlockAir(state, worldIn, pos));
  }

  @Override
  public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
    return state.getValue(FACING) == side.getOpposite();
  }

}
