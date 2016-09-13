package crazypants.enderio.block;

import javax.annotation.Nonnull;

import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.paint.render.PaintedBlockAccessWrapper;
import crazypants.enderio.render.property.EnumDecoBlock;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import static crazypants.enderio.ModObject.blockDecoration2;

public class BlockDecorationFacing extends BlockDecoration {

  public static BlockDecorationFacing create() {
    BlockDecorationFacing blockDecorationFacing = new BlockDecorationFacing(blockDecoration2.getUnlocalisedName());
    blockDecorationFacing.init();
    return blockDecorationFacing;
  }

  public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);

  private BlockDecorationFacing(@Nonnull String name) {
    super(name);
  }

  @Override
  protected void initDefaultState() {
    setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH));
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumDecoBlock.TYPE, FACING });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return super.getStateFromMeta(meta).withProperty(FACING, EnumFacing.SOUTH);
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    if (worldIn instanceof PaintedBlockAccessWrapper) {
      TileEntity tileEntity = ((PaintedBlockAccessWrapper) worldIn).getRealTileEntity(pos);
      if (tileEntity instanceof AbstractMachineEntity) {
        return state.withProperty(FACING, ((AbstractMachineEntity) tileEntity).getFacing());
      }
    }
    return state.withProperty(FACING, EnumFacing.SOUTH);
  }

}
