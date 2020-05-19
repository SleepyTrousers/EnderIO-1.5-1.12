package crazypants.enderio.machines.machine.light;

import javax.annotation.Nonnull;

import com.enderio.core.common.vecmath.Vector3f;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockElectricLight extends BlockEio<TileElectricLight> implements IHaveRenderers {

  static final float BLOCK_HEIGHT = 0.05f;
  static final float BLOCK_WIDTH = 0.3f;

  static final float BLOCK_EDGE_MAX = 0.5f + (BLOCK_WIDTH / 2);
  static final float BLOCK_EDGE_MIN = 0.5f - (BLOCK_WIDTH / 2);

  public static final @Nonnull PropertyEnum<LightType> TYPE = PropertyEnum.<LightType> create("type", LightType.class);
  public static final @Nonnull PropertyBool ACTIVE = PropertyBool.create("active");
  public static final @Nonnull PropertyEnum<EnumFacing> FACING = PropertyEnum.<EnumFacing> create("face", EnumFacing.class);

  public static BlockElectricLight create(@Nonnull IModObject modObject) {
    BlockElectricLight result = new BlockElectricLight(modObject);
    result.init();
    return result;
  }

  public BlockElectricLight(@Nonnull IModObject modObject) {
    super(modObject);
    setLightOpacity(0);
    setDefaultState(getBlockState().getBaseState().withProperty(TYPE, LightType.ELECTRIC).withProperty(ACTIVE, false).withProperty(FACING, EnumFacing.DOWN));
    setShape(mkShape(BlockFaceShape.UNDEFINED));
  }

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {

    EnumFacing onFace = EnumFacing.DOWN;
    TileEntity te = source.getTileEntity(pos);
    if (te instanceof TileElectricLight) {
      onFace = ((TileElectricLight) te).getFace();
    }

    Vector3f min = new Vector3f();
    Vector3f max = new Vector3f();
    switch (onFace) {
    case UP:
      min.set(BLOCK_EDGE_MIN, 1F - BLOCK_HEIGHT, BLOCK_EDGE_MIN);
      max.set(BLOCK_EDGE_MAX, 1F, BLOCK_EDGE_MAX);
      break;
    case DOWN:
      min.set(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN);
      max.set(BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
      break;
    case EAST:
      min.set(1 - BLOCK_HEIGHT, BLOCK_EDGE_MIN, BLOCK_EDGE_MIN);
      max.set(1, BLOCK_EDGE_MAX, BLOCK_EDGE_MAX);
      break;
    case WEST:
      min.set(0, BLOCK_EDGE_MIN, BLOCK_EDGE_MIN);
      max.set(BLOCK_HEIGHT, BLOCK_EDGE_MAX, BLOCK_EDGE_MAX);
      break;
    case NORTH:
      min.set(BLOCK_EDGE_MIN, BLOCK_EDGE_MIN, 0);
      max.set(BLOCK_EDGE_MAX, BLOCK_EDGE_MAX, BLOCK_HEIGHT);
      break;
    case SOUTH:
      min.set(BLOCK_EDGE_MIN, BLOCK_EDGE_MIN, 1 - BLOCK_HEIGHT);
      max.set(BLOCK_EDGE_MAX, BLOCK_EDGE_MAX, 1);
      break;
    default:
      min.set(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN);
      max.set(BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
      break;
    }

    return new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);
  }

  @Override
  public BlockItemElectricLight createBlockItem(@Nonnull IModObject mo) {
    return mo.apply(new BlockItemElectricLight(this));
  }

  @Override
  public @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, TYPE, ACTIVE, FACING);
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    boolean active = state.getValue(ACTIVE).booleanValue();
    int type = state.getValue(TYPE).getMetadata();
    if (active) {
      type |= 8;
    }
    return type;
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    LightType type = LightType.fromMetadata(meta & 7);
    return getDefaultState().withProperty(TYPE, type).withProperty(ACTIVE, meta > 7);
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    TileElectricLight te = getTileEntitySafe(world, pos);
    return state.withProperty(FACING, te == null ? EnumFacing.DOWN : te.getFace());
  }

  @Override
  public int damageDropped(@Nonnull IBlockState state) {
    return state.getValue(TYPE).getMetadata();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject mo) {
    Item item = Item.getItemFromBlock(this);
    int numTypes = LightType.values().length;
    for (int i = 0; i < numTypes; i++) {
      ClientUtil.regRenderer(item, i, mo.getUnlocalisedName());
    }
  }

  @Override
  public int getLightValue(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    Block block = bs.getBlock();
    if (block != this) {
      return block.getLightValue(bs, world, pos);
    }
    return bs.getValue(ACTIVE) ? 15 : 0;
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return null;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
    TileElectricLight te = getTileEntity(worldIn, pos);
    if (te != null) {
      te.onNeighborBlockChange(blockIn);
    }
  }

  @Override
  public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    TileElectricLight te = getTileEntity(worldIn, pos);
    if (te != null) {
      te.onBlockRemoved();
    }
    super.breakBlock(worldIn, pos, state);
  }

  @Override
  public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    for (LightType type : LightType.values()) {
      list.add(new ItemStack(this, 1, type.getMetadata()));
    }
  }

}
