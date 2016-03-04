package crazypants.enderio.machine.reservoir;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.FluidUtil;

import static net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.render.EnumMergingBlockRenderMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.tool.SmartTank;
import crazypants.enderio.tool.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReservoir extends BlockEio implements IResourceTooltipProvider, ISmartRenderAwareBlock {

  @SideOnly(Side.CLIENT)
  private static ReservoirRenderMapper RENDER_MAPPER;

  public static BlockReservoir create() {
    BlockReservoir result = new BlockReservoir();
    result.init();
    return result;
  }

  private BlockReservoir() {
    super(ModObject.blockReservoir.unlocalisedName, TileReservoir.class, Material.rock);
    setStepSound(Block.soundTypeStone);
    setDefaultState(this.blockState.getBaseState().withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO));
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.register(this, EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.DEFAULTS, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return 0;
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return state.withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    return getMapper().getExtendedState(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper getRenderMapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    return getMapper();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper getRenderMapper(ItemStack stack) {
    return getMapper();
  }

  @SideOnly(Side.CLIENT)
  public ReservoirRenderMapper getMapper() {
    if (RENDER_MAPPER == null) {
      RENDER_MAPPER = new ReservoirRenderMapper();
    }
    return RENDER_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.SOLID;
  }
  
  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumFacing side, float hitX, float hitY, float hitZ) {
    TileEntity te;
    if (!entityPlayer.isSneaking() && entityPlayer.inventory.getCurrentItem() != null
        && (te = world.getTileEntity(pos)) instanceof TileReservoir) {
      TileReservoir tank = ((TileReservoir) te);
      if (ToolUtil.isToolEquipped(entityPlayer)) {
        tank.setAutoEject(!tank.isAutoEject());
        world.markBlockForUpdate(pos);
        return true;
      }
      if (tank.tank.getAvailableSpace() >= BUCKET_VOLUME && FluidUtil.fillInternalTankFromPlayerHandItem(world, pos, entityPlayer, tank)) {
        return true;
      } else if (!tank.tank.isFull() && FluidUtil.fillInternalTankFromPlayerHandItem(world, pos, entityPlayer, new TankWrapper(tank))) {
        return true;
      }
      if (FluidUtil.fillPlayerHandItemFromInternalTank(world, pos, entityPlayer, tank)) {
        return true;
      }
    }
    return super.onBlockActivated(world, pos, state, entityPlayer, side, hitX, hitY, hitZ);
  }

  private static class TankWrapper implements ITankAccess {

    private final ITankAccess parent;
    FluidTank parentTank;
    private SmartTank tank;

    private TankWrapper(ITankAccess parent) {
      this.parent = parent;
    }

    @Override
    public FluidTank getInputTank(FluidStack forFluidType) {
      parentTank = parent.getInputTank(forFluidType);
      tank = new SmartTank(parentTank.getFluid(), parentTank.getCapacity() + BUCKET_VOLUME);
      return tank;
    }

    @Override
    public FluidTank[] getOutputTanks() {
      return parent.getOutputTanks();
    }

    @Override
    public void setTanksDirty() {
      tank.setCapacity(parentTank.getCapacity());
      parentTank.setFluid(tank.getFluid());
      parent.setTanksDirty();
    }

  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public TileEntity createTileEntity(World world, IBlockState metadata) {
    return new TileReservoir();
  }


  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
    return !(world.getBlockState(pos).getBlock() instanceof BlockReservoir);
  }


  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

}
