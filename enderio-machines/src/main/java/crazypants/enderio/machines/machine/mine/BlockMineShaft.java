package crazypants.enderio.machines.machine.mine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.machine.interfaces.ITEProxy;
import crazypants.enderio.base.render.IDefaultRenderers;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMineShaft extends BlockEio<TileMineShaft> implements IDefaultRenderers, ITEProxy {

  protected BlockMineShaft(@Nonnull IModObject modObject) {
    super(modObject);
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    setHardness(20f);
    setSoundType(SoundType.STONE);
    setHarvestLevel("pickaxe", 2);
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  protected @Nullable BlockPos getParentPos(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    TileMineShaft te = getTileEntity(world, pos);
    if (te != null) {
      return te.getParent();
    }
    return null;
  }

  @Override
  @Nullable
  public TileMine getParent(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    BlockPos parentPos = getParentPos(world, pos);
    if (parentPos != null) {
      return getAnyTileEntity(world, parentPos, TileMine.class);
    }
    return null;
  }

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer entityPlayer,
      @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    BlockPos parentPos = getParentPos(world, pos);
    if (parentPos != null) {
      IBlockState parentState = world.getBlockState(parentPos);
      return parentState.getBlock().onBlockActivated(world, parentPos, parentState, entityPlayer, hand, side, hitX, hitY, hitZ);
    } else {
      return false;
    }
  }

  @Override
  public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    TileMine parent = getParent(world, pos, state);
    if (parent != null) {
      parent.onBlockRemoved(pos);
    }
  }

  @Override
  public @Nonnull EnumPushReaction getMobilityFlag(@Nonnull IBlockState state) {
    return EnumPushReaction.BLOCK;
  }

  @Override
  public float getExplosionResistance(@Nonnull Entity exploder) {
    return 99999F;
  }

}
