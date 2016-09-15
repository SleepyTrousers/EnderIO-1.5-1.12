package crazypants.enderio.machine.gauge;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.BlockEnder;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.registry.SmartModelAttacher;
import crazypants.enderio.render.registry.TextureRegistry;
import crazypants.enderio.render.registry.TextureRegistry.TextureSupplier;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGauge extends BlockEio<TileGauge> implements IResourceTooltipProvider, ISmartRenderAwareBlock {

  public static final TextureSupplier gaugeIcon = TextureRegistry.registerTexture("blocks/blockGaugeOverlay");

  public static BlockGauge create() {
    BlockGauge result = new BlockGauge();
    result.init();
    return result;
  }

  private BlockGauge() {
    super(ModObject.blockGauge.getUnlocalisedName(), TileGauge.class, Material.GLASS);
    setLightOpacity(255);
    useNeighborBrightness = true;
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.registerItemOnly(this);
  }

  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
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
  public EnumBlockRenderType getRenderType(IBlockState bs) {
    return EnumBlockRenderType.INVISIBLE;
  }

  private static final double px = 1d / 16d;

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
    Map<EnumFacing, IPowerInterface> sides = getDisplays(world, pos);
    if (sides.isEmpty()) {
      return FULL_BLOCK_AABB;
    }

    double minX = 16 * px, maxX = 0, minY = 2 * px, maxY = 14 * px, minZ = 16 * px, maxZ = 0;

    if (sides.containsKey(EnumFacing.NORTH) || sides.containsKey(EnumFacing.SOUTH)) {
      minX = Math.min(minX, 6 * px);
      maxX = Math.max(maxX, 10 * px);
      if (sides.containsKey(EnumFacing.NORTH)) {
        minZ = Math.min(minZ, 0 * px);
        maxZ = Math.max(maxZ, .5 * px);
      }
      if (sides.containsKey(EnumFacing.SOUTH)) {
        minZ = Math.min(minZ, 15.5 * px);
        maxZ = Math.max(maxZ, 16 * px);
      }
    }
    if (sides.containsKey(EnumFacing.EAST) || sides.containsKey(EnumFacing.WEST)) {
      minZ = Math.min(minZ, 6 * px);
      maxZ = Math.max(maxZ, 10 * px);
      if (sides.containsKey(EnumFacing.WEST)) {
        minX = Math.min(minX, 0 * px);
        maxX = Math.max(maxX, 1.5 * px);
      }
      if (sides.containsKey(EnumFacing.EAST)) {
        minX = Math.min(minX, 15.5 * px);
        maxX = Math.max(maxX, 16 * px);
      }
    }

    return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
  }

  protected static Map<EnumFacing, IPowerInterface> getDisplays(IBlockAccess world, BlockPos pos) {
    Map<EnumFacing, IPowerInterface> sides = new EnumMap<EnumFacing, IPowerInterface>(EnumFacing.class);
    for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
      BlockPos neighbor = pos.offset(face);
      TileEntity tile = BlockEnder.getAnyTileEntitySafe(world, neighbor);
      IPowerInterface eh = PowerHandlerUtil.getPowerInterface(tile, face.getOpposite());
      if (eh != null && !(tile instanceof TileCapBank) && !(tile instanceof IConduitBundle)) {
        sides.put(face, eh);
      }
    }
    return sides;
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
    return null;
  }

  @Override
  public IItemRenderMapper getItemRenderMapper() {
    return RenderMapperGauge.instance;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

}
