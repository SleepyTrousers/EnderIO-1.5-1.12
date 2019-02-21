package crazypants.enderio.machines.machine.light;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.ItemEIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLightNode extends BlockEio<TileLightNode> {

  public static BlockLightNode create(@Nonnull IModObject modObject) {
    BlockLightNode result = new BlockLightNode(modObject);
    result.init();
    return result;
  }

  public static final @Nonnull PropertyBool ACTIVE = PropertyBool.create("active");

  public BlockLightNode(@Nonnull IModObject modObject) {
    super(modObject, Material.AIR);
    setCreativeTab(null);
    setTickRandomly(true);
    setDefaultState(getBlockState().getBaseState().withProperty(ACTIVE, false));
    setShape(mkShape(BlockFaceShape.UNDEFINED));
  }

  @Override
  @Nullable
  public ItemEIO createBlockItem(@Nonnull IModObject modObject) {
    return null;
  }

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
    return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
  }

  @Override
  public @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, ACTIVE);
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(ACTIVE).booleanValue() ? 1 : 0;
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(ACTIVE, meta > 0);
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public boolean isReplaceable(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return true;
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return null;
  }

  @Override
  public @Nonnull EnumBlockRenderType getRenderType(@Nonnull IBlockState bs) {
    return EnumBlockRenderType.INVISIBLE;
  }

  @Override
  public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    TileLightNode te = getTileEntity(world, pos);
    if (te != null) {
      te.onBlockRemoved();
    }
  }

  @Override
  public int getLightValue(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    if (bs.getBlock() != this) {
      return 0;
    }
    return bs.getValue(ACTIVE) ? 15 : 0;
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock,
      @Nonnull BlockPos fromPos) {
    TileLightNode te = getTileEntity(world, pos);
    if (te != null) {
      te.onNeighbourChanged();
    }
  }

  @Override
  public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
    TileLightNode te = getTileEntity(world, pos);
    if (te != null) {
      te.checkParent();
    }
  }

  @Override
  public int quantityDropped(@Nonnull Random p_149745_1_) {
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
  }

}
