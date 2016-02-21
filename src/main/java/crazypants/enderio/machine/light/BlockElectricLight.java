package crazypants.enderio.machine.light;

import com.enderio.core.common.vecmath.Vector3f;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockElectricLight extends BlockEio<TileElectricLight> implements IRedstoneConnectable {

  static final float BLOCK_HEIGHT = 0.05f;
  static final float BLOCK_WIDTH = 0.3f;

  static final float BLOCK_EDGE_MAX = 0.5f + (BLOCK_WIDTH / 2);
  static final float BLOCK_EDGE_MIN = 0.5f - (BLOCK_WIDTH / 2);

  public static BlockElectricLight create() {
    BlockElectricLight result = new BlockElectricLight();
    result.init();
    return result;
  }

  public BlockElectricLight() {
    super(ModObject.blockElectricLight.unlocalisedName, TileElectricLight.class);
    setLightOpacity(0);
    setBlockBounds(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN, BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, BlockItemElectricLight.class, ModObject.blockElectricLight.unlocalisedName);
    GameRegistry.registerTileEntity(TileElectricLight.class, ModObject.blockElectricLight.unlocalisedName + "TileEntity");
  }

  @Override
  public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, EnumFacing from) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
    return null;
  }

  // @Override
  // @SideOnly(Side.CLIENT)
  // public void registerBlockIcons(IIconRegister iIconRegister) {
  // blockIcon = iIconRegister.registerIcon("enderio:blockElectricLightFace");
  // blockIconOff =
  // iIconRegister.registerIcon("enderio:blockElectricLightFaceOff");
  // blockIconSide = iIconRegister.registerIcon("enderio:conduitConnector");
  // }

  @Override
  public int getLightValue(IBlockAccess world, BlockPos pos) {
    IBlockState bs = world.getBlockState(pos);
    Block block = bs.getBlock();
    if (block != null && block != this) {
      return block.getLightValue(world, pos);
    }
    int meta = block.getMetaFromState(bs);
    return meta > 0 ? 15 : 0;
  }

  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, BlockPos pos) {
    EnumFacing onFace = EnumFacing.DOWN;
    TileEntity te = blockAccess.getTileEntity(pos);
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

    setBlockBounds(min.x, min.y, min.z, max.x, max.y, max.z);
  }

  @Override
  public void setBlockBoundsForItemRender() {
    setBlockBounds(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN, BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block blockID) {
    TileElectricLight te = getTileEntity(worldIn, pos);
    if (te != null) {
      te.onNeighborBlockChange(blockID);
    }
  }

  @Override
  public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
    TileElectricLight te = getTileEntity(worldIn, pos);
    if (te != null) {
      te.onBlockRemoved();
    }
  }

  @Override
  protected void processDrop(IBlockAccess world, BlockPos pos, TileElectricLight light, ItemStack drop) {
    if (light == null) {
      return;
    }
    int meta = light.isInvereted() ? 1 : 0;
    if (!light.isRequiresPower()) {
      meta += 2;
    } else if (light.isWireless()) {
      meta += 4;
    }
    drop.setItemDamage(meta);
  }

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {
    return false;
  }

}
