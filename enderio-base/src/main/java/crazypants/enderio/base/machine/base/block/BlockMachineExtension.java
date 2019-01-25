package crazypants.enderio.base.machine.base.block;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.machine.interfaces.ITEProxy;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * This is a dummy block that is automatically placed on top of (e.g.) the enhanced combustion generator to block that space. It doesn't have a render (the
 * generator's render is oversized), and it relays all interactions to the generator.
 *
 */
public class BlockMachineExtension extends BlockEio<TileEntityEio> implements ITEProxy {

  private final @Nonnull AxisAlignedBB AABB;
  private final @Nonnull IModObject parent;
  private final @Nonnull EnumFacing parentOffset;

  public BlockMachineExtension(@Nonnull IModObject modObject, @Nonnull IModObject parent, @Nonnull AxisAlignedBB AABB) {
    super(modObject);
    setCreativeTab(null);
    setHardness(2f);
    setSoundType(SoundType.METAL);
    setHarvestLevel("pickaxe", 0);
    this.parent = parent;
    this.AABB = AABB;
    parentOffset = EnumFacing.DOWN;
    setShape(mkShape(BlockFaceShape.UNDEFINED));
  }

  @Override
  public @Nullable Item createBlockItem(@Nonnull IModObject modObject) {
    return null;
  };

  protected @Nonnull IModObject getParent() {
    return parent;
  }

  protected @Nonnull EnumFacing getParentOffset() {
    return parentOffset;
  }

  protected @Nonnull BlockPos getParentPos(@Nonnull BlockPos pos) {
    return pos.offset(getParentOffset());
  }

  protected @Nonnull IBlockState getParentBlockState(@Nonnull World world, @Nonnull BlockPos pos) {
    return world.getBlockState(getParentPos(pos));
  }

  protected @Nonnull Block getParentBlock(@Nonnull World world, @Nonnull BlockPos pos) {
    return getParentBlockState(world, pos).getBlock();
  }

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
    return AABB;
  }

  protected boolean checkParent(@Nonnull World world, @Nonnull BlockPos pos) {
    if (getParentBlock(world, pos) == getParent().getBlockNN()) {
      return true;
    } else {
      world.setBlockToAir(pos);
      return false;
    }
  }

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer entityPlayer,
      @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    if (checkParent(world, pos)) {
      return getParentBlock(world, pos).onBlockActivated(world, getParentPos(pos), getParentBlockState(world, pos), entityPlayer, hand, side, hitX, hitY, hitZ);
    } else {
      return false;
    }
  }

  @Override
  public boolean canBeWrenched() {
    return true;
  }

  @Override
  public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
    if (checkParent(world, pos)) {
      return getParentBlock(world, pos).removedByPlayer(getParentBlockState(world, pos), world, getParentPos(pos), player, willHarvest);
    } else {
      return true;
    }
  }

  @Override
  public void harvestBlock(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te,
      @Nonnull ItemStack stack) {
    if (checkParent(world, pos)) {
      getParentBlock(world, pos).harvestBlock(world, player, getParentPos(pos), getParentBlockState(world, pos), world.getTileEntity(getParentPos(pos)), stack);
    }
    world.setBlockToAir(pos);
  }

  @Override
  public @Nonnull ItemStack getPickBlock(@Nonnull IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EntityPlayer player) {
    if (checkParent(world, pos)) {
      return getParentBlock(world, pos).getPickBlock(getParentBlockState(world, pos), target, world, getParentPos(pos), player);
    } else {
      return Prep.getEmpty();
    }
  }

  @Override
  public @Nonnull Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune) {
    return Items.AIR;
  }

  @Override
  public @Nonnull EnumPushReaction getMobilityFlag(@Nonnull IBlockState state) {
    return EnumPushReaction.BLOCK;
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return false;
  }

  @Override
  public @Nonnull EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
    return EnumBlockRenderType.INVISIBLE;
  }

  @Override
  public float getExplosionResistance(@Nonnull Entity exploder) {
    return 99999F;
  }

  @Override
  public @Nonnull String getLocalizedName() {
    return getParent().getBlockNN().getLocalizedName();
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
    checkParent(worldIn, pos);
  }

  @Override
  public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    // none
  }

  @Override
  @Nullable
  public TileEntity getParent(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    return world.getTileEntity(getParentPos(pos));
  }

}
