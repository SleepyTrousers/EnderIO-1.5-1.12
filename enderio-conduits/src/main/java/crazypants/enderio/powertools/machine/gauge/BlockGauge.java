package crazypants.enderio.powertools.machine.gauge;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.power.IPowerInterface;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGauge extends BlockEio<TileGauge> implements IResourceTooltipProvider, ISmartRenderAwareBlock, IHaveTESR {

  public static final TextureSupplier gaugeIcon = TextureRegistry.registerTexture("blocks/block_gauge_overlay");

  public static BlockGauge create(@Nonnull IModObject modObject) {
    BlockGauge result = new BlockGauge(modObject);
    result.init();
    return result;
  }

  private BlockGauge(@Nonnull IModObject modObject) {
    super(modObject, Material.GLASS);
    setLightOpacity(255);
    useNeighborBrightness = true;
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.registerItemOnly(this);
  }

  @Override
  public void getSubBlocks(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(tab, list);
    }
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
  public @Nonnull EnumBlockRenderType getRenderType(@Nonnull IBlockState bs) {
    return EnumBlockRenderType.INVISIBLE;
  }

  private static final double px = 1d / 16d;

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
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

  protected static @Nonnull Map<EnumFacing, IPowerInterface> getDisplays(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    Map<EnumFacing, IPowerInterface> sides = new EnumMap<EnumFacing, IPowerInterface>(EnumFacing.class);
    NNList.FACING_HORIZONTAL.apply(new Callback<EnumFacing>() {
      @Override
      public void apply(@Nonnull EnumFacing face) {
        BlockPos neighbor = pos.offset(face);
        TileEntity tile = getAnyTileEntitySafe(world, neighbor);
        if (!(tile instanceof TileCapBank) && !(tile instanceof IConduitBundle)) {
          IPowerInterface eh = PowerHandlerUtil.getPowerInterface(tile, face.getOpposite());
          if (eh != null) {
            sides.put(face, eh);
          }
        }
      }
    });
    return sides;
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState blockStateIn, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return null;
  }

  @Override
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return RenderMapperGauge.instance;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileGauge.class, new TESRGauge());
  }

}
