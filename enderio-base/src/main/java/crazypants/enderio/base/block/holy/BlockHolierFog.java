package crazypants.enderio.base.block.holy;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.config.config.BlockConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHolierFog extends BlockHolyBase {

  public static BlockHolierFog create(@Nonnull IModObject modObject) {
    BlockHolierFog result = new BlockHolierFog(modObject);
    return result;
  }

  protected BlockHolierFog(@Nonnull IModObject modObject) {
    super(modObject);
  }

  @Override
  protected void initDefaultState() {
    setDefaultState(getBlockState().getBaseState());
  }

  @Override
  protected void setQuanta(@Nonnull World world, @Nonnull BlockPos pos, int quanta, int delay) {
    if (!world.isRemote) {
      if (quanta > 0) {
        if (world.getBlockState(pos).getBlock() != getDefaultState().getBlock()) {
          world.setBlockState(pos, getDefaultState());
        }
        HolyChunkData.put(world.getChunkFromBlockCoords(pos), pos, quanta);
        world.scheduleBlockUpdate(pos, this, delay * (1 + world.rand.nextInt(7)), 0);
      } else {
        world.setBlockToAir(pos);
        HolyChunkData.del(world.getChunkFromBlockCoords(pos), pos);
      }
    }
  }

  @Override
  protected int getQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    return world.isRemote ? 1
        : state.getBlock() instanceof BlockHolierFog ? HolyChunkData.get(world.getChunkFromBlockCoords(pos), pos, BlockConfig.holyQuanta::get) : 0;
  }

  @Override
  protected int getMaxQuanta() {
    return Integer.MAX_VALUE / 2;
  }

}
