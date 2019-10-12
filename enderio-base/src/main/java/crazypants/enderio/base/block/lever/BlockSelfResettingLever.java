package crazypants.enderio.base.block.lever;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.render.IDefaultRenderers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSelfResettingLever extends BlockLever implements IDefaultRenderers, IModObject.WithBlockItem {

  public static Block create5(@Nonnull IModObject modObject) {
    return create(modObject, 5, false);
  }

  public static Block create10(@Nonnull IModObject modObject) {
    return create(modObject, 10, false);
  }

  public static Block create30(@Nonnull IModObject modObject) {
    return create(modObject, 30, false);
  }

  public static Block create60(@Nonnull IModObject modObject) {
    return create(modObject, 60, false);
  }

  public static Block create300(@Nonnull IModObject modObject) {
    return create(modObject, 300, false);
  }

  public static Block create5i(@Nonnull IModObject modObject) {
    return create(modObject, 5, true);
  }

  public static Block create10i(@Nonnull IModObject modObject) {
    return create(modObject, 10, true);
  }

  public static Block create30i(@Nonnull IModObject modObject) {
    return create(modObject, 30, true);
  }

  public static Block create60i(@Nonnull IModObject modObject) {
    return create(modObject, 60, true);
  }

  public static Block create300i(@Nonnull IModObject modObject) {
    return create(modObject, 300, true);
  }

  private static Block create(@Nonnull IModObject modObject, int seconds, boolean inverted) {
    return new BlockSelfResettingLever(modObject, seconds * 20, inverted);
  }

  private final int delay;
  private final boolean inverted;

  public BlockSelfResettingLever(@Nonnull IModObject modObject, int delay, boolean inverted) {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHardness(0.5F);
    setSoundType(SoundType.WOOD);
    this.delay = delay;
    this.inverted = inverted;
    modObject.apply(this);
  }

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    if (world.isRemote) {
      return true;
    } else {
      if (!state.getValue(POWERED)) {
        world.scheduleBlockUpdate(pos, this, delay, 0);
      }
      return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
  }

  @SuppressWarnings("null")
  @Override
  public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
    if (!world.isRemote && state.getValue(POWERED)) {
      super.onBlockActivated(world, pos, state, null, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0f, 0f, 0f);
    }
  }

  @Override
  public int getWeakPower(@Nonnull IBlockState state, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    final int tmp = super.getWeakPower(state, blockAccess, pos, side);
    return inverted ? 15 - tmp : tmp;
  }

  @Override
  public int getStrongPower(@Nonnull IBlockState state, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    final int tmp = super.getStrongPower(state, blockAccess, pos, side);
    return inverted ? 15 - tmp : tmp;
  }

}
