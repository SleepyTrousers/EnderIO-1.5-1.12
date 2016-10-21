package crazypants.enderio.machine.light;

import com.enderio.core.common.vecmath.Vector3f;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockElectricLight extends BlockEio<TileElectricLight> implements IRedstoneConnectable, IHaveRenderers {

  static final float BLOCK_HEIGHT = 0.05f;
  static final float BLOCK_WIDTH = 0.3f;

  static final float BLOCK_EDGE_MAX = 0.5f + (BLOCK_WIDTH / 2);
  static final float BLOCK_EDGE_MIN = 0.5f - (BLOCK_WIDTH / 2);

  public static final PropertyEnum<LightType> TYPE = PropertyEnum.<LightType> create("type", LightType.class);
  public static final PropertyBool ACTIVE = PropertyBool.create("active");
  public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.<EnumFacing> create("face", EnumFacing.class);  
  
  public static BlockElectricLight create() {
    BlockElectricLight result = new BlockElectricLight();
    result.init();
    return result;
  }

  public BlockElectricLight() {
    super(ModObject.blockElectricLight.getUnlocalisedName(), TileElectricLight.class);
    setLightOpacity(0);    
    setDefaultState(blockState.getBaseState().withProperty(TYPE, LightType.ELECTRIC).withProperty(ACTIVE, false).withProperty(FACING, EnumFacing.DOWN));
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    
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
      //return new AxisAlignedBB(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN, BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
  }

  @Override
  protected ItemBlock createItemBlock() {
    return new BlockItemElectricLight(this, getName());
  }

  @Override
  public BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, TYPE, ACTIVE, FACING);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    boolean active = state.getValue(ACTIVE).booleanValue();
    int type = state.getValue(TYPE).getMetadata();
    if(active) {
      type |= 8;
    }
    return type;
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    LightType type = LightType.fromMetadata(meta);
    return getDefaultState().withProperty(TYPE, type).withProperty(ACTIVE, meta > 7);
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileElectricLight te = getTileEntitySafe(world, pos);
    return state.withProperty(FACING, te == null ? EnumFacing.DOWN : te.getFace());    
  }

  @Override
  public int damageDropped(IBlockState state) {
    return state.getValue(TYPE).ordinal();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {    
    Item item = Item.getItemFromBlock(this);    
    int numTypes = LightType.values().length;
    for (int i = 0; i < numTypes; i++) {     
      ClientUtil.regRenderer(item, i, name);
    }
  }
  
  
  @Override
  public int getLightValue(IBlockState bs, IBlockAccess world, BlockPos pos) {    
    Block block = bs.getBlock();
    if (block != null && block != this) {
      return block.getLightValue(bs, world, pos);
    } 
    return bs.getValue(ACTIVE) ? 15 : 0;
  }
  
  @Override
  public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, EnumFacing from) {
    
    return true;
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
    return null;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }
  
  @Override
  public boolean isFullCube(IBlockState bs) {
    return false;
  }

  @Override
  public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock) {
    TileElectricLight te = getTileEntity(worldIn, pos);
    if (te != null) {
      te.onNeighborBlockChange(neighborBlock);
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
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {
    return true;
  }

}
