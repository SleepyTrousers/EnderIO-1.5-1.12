package crazypants.enderio.machines.machine.wireless;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.machine.interfaces.ITEProxy;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAntenna extends BlockEio<TileEntityEio> implements IResourceTooltipProvider, IHaveRenderers, ITEProxy {

  public static final @Nonnull PropertyEnum<EnumFacing> BASE = PropertyEnum.create("base", EnumFacing.class, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.DOWN,
      EnumFacing.NORTH, EnumFacing.SOUTH);

  public static BlockAntenna create(@Nonnull IModObject modObject) {
    BlockAntenna res = new BlockAntenna(modObject, MachineObject.block_enhanced_wireless_charger);
    res.init();
    return res;
  }

  private final @Nonnull IModObject base;

  private BlockAntenna(@Nonnull IModObject modObject, @Nonnull IModObject base) {
    super(modObject);
    setLightOpacity(1);
    setDefaultState(getBlockState().getBaseState().withProperty(BASE, EnumFacing.DOWN));
    setShape(mkShape(BlockFaceShape.BOWL));
    this.base = base;
  }

  @Override
  protected void init() {
  }

  @Override
  @Nonnull
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { BASE });
  }

  @SuppressWarnings("null")
  @Override
  @Nonnull
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(BASE, EnumFacing.VALUES[meta]);
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(BASE).ordinal();
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
    if (worldIn.getBlockState(pos.offset(state.getValue(BASE))).getBlock() != base.getBlockNN()) {
      EnumFacing newBase = findBase(worldIn, pos);
      if (newBase != null) {
        worldIn.setBlockState(pos, state.withProperty(BASE, newBase));
      } else {
        dropBlockAsItem(worldIn, pos, state, 0);
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
      }
    }
  }

  @SuppressWarnings("null")
  protected @Nullable EnumFacing findBase(@Nonnull World worldIn, @Nonnull BlockPos pos) {
    for (EnumFacing face : EnumFacing.VALUES) {
      if (face != EnumFacing.UP && worldIn.getBlockState(pos.offset(face)).getBlock() == base.getBlockNN()) {
        return face;
      }
    }
    return null;
  }

  @Override
  public boolean canPlaceBlockAt(@Nonnull World worldIn, @Nonnull BlockPos pos) {
    return super.canPlaceBlockAt(worldIn, pos) && findBase(worldIn, pos) != null;
  }

  @Override
  public @Nonnull IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ,
      int meta, @Nonnull EntityLivingBase placer, @Nonnull EnumHand hand) {
    EnumFacing newBase = findBase(world, pos);
    return newBase != null ? getDefaultState().withProperty(BASE, newBase) : getDefaultState();
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public void registerRenderers(@Nonnull IModObject modObject) {
    ClientUtil.registerDefaultItemRenderer(MachineObject.block_wireless_charger_extension);
  }

  @Override
  public TileWirelessCharger getParent(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    return getAnyTileEntity(world, pos.offset(state.getValue(BASE)), TileWirelessCharger.class);
  }

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer entityPlayer,
      @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    if (hand == EnumHand.MAIN_HAND && entityPlayer.getHeldItem(hand).isEmpty()) {
      TileWirelessCharger te = getParent(world, pos, state);
      if (te != null) {
        te.toggleRange();
      }
      return true;
    }
    return super.onBlockActivated(world, pos, state, entityPlayer, hand, side, hitX, hitY, hitZ);
  }

}
