package crazypants.enderio.base.block.holy;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHolyFog extends BlockHolyBase {

  public static final @Nonnull PropertyInteger GEN = PropertyInteger.create("amount", 0, 15);

  public static BlockHolyFog create(@Nonnull IModObject modObject) {
    BlockHolyFog result = new BlockHolyFog(modObject);
    return result;
  }

  protected BlockHolyFog(@Nonnull IModObject modObject) {
    super(modObject);
  }

  @Override
  protected void initDefaultState() {
    setDefaultState(getBlockState().getBaseState().withProperty(GEN, 15));
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { GEN });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(GEN, meta);
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(GEN);
  }

  @Override
  public @Nonnull IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY,
      float hitZ, int meta, @Nonnull EntityLivingBase placer) {
    return getDefaultState();
  }

  @Override
  protected int getQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    return !(state.getBlock() instanceof BlockHolyFog) ? 0 : state.getValue(GEN) + 1;
  }

  @Override
  protected void setQuanta(@Nonnull World world, @Nonnull BlockPos pos, int quanta, int delay) {
    if (quanta > 0) {
      world.setBlockState(pos, getDefaultState().withProperty(GEN, quanta - 1));
      world.scheduleBlockUpdate(pos, this, delay, 0);
    } else {
      world.setBlockToAir(pos);
    }
  }

  @Override
  protected int getMaxQuanta() {
    return 16;
  }

}
