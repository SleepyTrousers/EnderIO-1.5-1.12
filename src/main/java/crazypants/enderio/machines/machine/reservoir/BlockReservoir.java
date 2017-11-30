package crazypants.enderio.machines.machine.reservoir;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.util.Prep;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.init.MachineObject.block_reservoir;

public class BlockReservoir extends BlockEio<TileReservoir> implements IResourceTooltipProvider, ISmartRenderAwareBlock, IHaveTESR {

  @SideOnly(Side.CLIENT)
  private static ReservoirItemRenderMapper RENDER_MAPPER;

  public static BlockReservoir create(@Nonnull IModObject modObject) {
    BlockReservoir result = new BlockReservoir(modObject);
    result.init();
    return result;
  }

  private BlockReservoir(@Nonnull IModObject modObject) {
    super(modObject, TileReservoir.class, new Material(MapColor.WATER) {

      @Override
      public boolean isToolNotRequired() {
        return false;
      }

    });
    setSoundType(SoundType.GLASS);
    setDefaultState(this.blockState.getBaseState().withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO));
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.register(this, EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.DEFAULTS, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  public void getSubBlocks(@Nonnull Item itemIn, @Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return 0;
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return state.withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    ReservoirBlockRenderMapper renderMapper = new ReservoirBlockRenderMapper(state, world, pos);
    IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, renderMapper);
    blockStateWrapper.addCacheKey(renderMapper);
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return ReservoirItemRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.SOLID;
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer entityPlayer,
      @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    TileEntity te;
    if (!entityPlayer.isSneaking() && Prep.isValid(entityPlayer.inventory.getCurrentItem()) && (te = world.getTileEntity(pos)) instanceof TileReservoir) {
      TileReservoir tank = ((TileReservoir) te);
      if (ToolUtil.isToolEquipped(entityPlayer, hand)) {
        tank.setAutoEject(!tank.isAutoEject());
        world.notifyBlockUpdate(pos, state, state, 3);
        return true;
      }
      if (tank.tank.getAvailableSpace() >= Fluid.BUCKET_VOLUME && FluidUtil.fillInternalTankFromPlayerHandItem(world, pos, entityPlayer, hand, tank)) {
        return true;
      } else if (!tank.tank.isFull() && FluidUtil.fillInternalTankFromPlayerHandItem(world, pos, entityPlayer, hand, new TankWrapper(tank, world, pos))) {
        return true;
      }
      if (FluidUtil.fillPlayerHandItemFromInternalTank(world, pos, entityPlayer, hand, tank)) {
        return true;
      }
    }
    return super.onBlockActivated(world, pos, state, entityPlayer, hand, side, hitX, hitY, hitZ);
  }

  private static class TankWrapper implements ITankAccess {

    private final List<ITankAccess> parents = new ArrayList<ITankAccess>();
    private SmartTank tank;
    private final World world;
    private final BlockPos pos;

    private TankWrapper(ITankAccess parent, World world, BlockPos pos) {
      this.parents.add(parent);
      this.world = world;
      this.pos = pos;
    }

    @Override
    public FluidTank getInputTank(FluidStack forFluidType) {
      FluidTank parentTank = parents.get(0).getInputTank(forFluidType);
      if (parentTank == null) {
        return null;
      }
      int free = parentTank.getCapacity() - parentTank.getFluidAmount();
      for (EnumFacing face : EnumFacing.values()) {
        TileEntity neighbor = world.getTileEntity(pos.offset(face));
        if (neighbor instanceof ITankAccess) {
          FluidTank tank2 = ((ITankAccess) neighbor).getInputTank(forFluidType);
          if (tank2 != null) {
            free += tank2.getCapacity() - tank2.getFluidAmount();
            parents.add(((ITankAccess) neighbor));
          }
        }
      }
      if (free < Fluid.BUCKET_VOLUME) {
        free = Fluid.BUCKET_VOLUME;
      }
      tank = new SmartTank(parentTank.getFluid(), free);
      return tank;
    }

    @Override
    public @Nonnull FluidTank[] getOutputTanks() {
      return parents.get(0).getOutputTanks();
    }

    @Override
    public void setTanksDirty() {
      FluidStack stack = tank.getFluid();
      if (stack.amount > 0) {
        for (ITankAccess parent : parents) {
          FluidTank ptank = parent.getInputTank(stack);
          stack.amount -= ptank.fill(stack, true);
          parent.setTanksDirty();
          if (stack.amount <= 0) {
            return;
          }
        }
      }
      tank.setCapacity(0);
    }

  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public @Nonnull TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState metadata) {
    return new TileReservoir();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return !(world.getBlockState(pos.offset(side)).getBlock() instanceof BlockReservoir);
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileReservoir.class, new ReservoirRenderer((BlockReservoir) block_reservoir.getBlock()));
  }

}
