package crazypants.enderio.machine.reservoir;

import static crazypants.enderio.machine.MachineObject.blockReservoir;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.BlockEio;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IHaveTESR;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.render.registry.SmartModelAttacher;
import crazypants.enderio.tool.ToolUtil;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReservoir extends BlockEio<TileReservoir> implements IResourceTooltipProvider, ISmartRenderAwareBlock, IHaveTESR {

  @SideOnly(Side.CLIENT)
  private static ReservoirItemRenderMapper RENDER_MAPPER;

  public static BlockReservoir create() {
    BlockReservoir result = new BlockReservoir();
    result.init();
    return result;
  }

  private BlockReservoir() {
    super(MachineObject.blockReservoir.getUnlocalisedName(), TileReservoir.class, new Material(MapColor.WATER) {

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
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER });
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
    if (state != null && world != null && pos != null) {
      ReservoirBlockRenderMapper renderMapper = new ReservoirBlockRenderMapper(state, world, pos);
      IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, renderMapper);
      blockStateWrapper.addCacheKey(renderMapper);
      blockStateWrapper.bakeModel();
      return blockStateWrapper;
    } else {
      return state;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return ReservoirItemRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.SOLID;
  }
  
  @Override
  public boolean canRenderInLayer(BlockRenderLayer layer) {
    return true;
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumHand hand, @Nullable ItemStack heldItem,
      EnumFacing side,
      float hitX, float hitY, float hitZ) {
    TileEntity te;
    if (!entityPlayer.isSneaking() && entityPlayer.inventory.getCurrentItem() != null
        && (te = world.getTileEntity(pos)) instanceof TileReservoir) {
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
    return super.onBlockActivated(world, pos, state, entityPlayer, hand, heldItem, side, hitX, hitY, hitZ);
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
    public FluidTank[] getOutputTanks() {
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
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  public @Nonnull TileEntity createTileEntity(World world, IBlockState metadata) {
    return new TileReservoir();
  }


  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockState bs, IBlockAccess world, BlockPos pos, EnumFacing side) {
    return !(world.getBlockState(pos.offset(side)).getBlock() instanceof BlockReservoir);
  }


  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileReservoir.class, new ReservoirRenderer((BlockReservoir) blockReservoir.getBlock()));
  }

}
