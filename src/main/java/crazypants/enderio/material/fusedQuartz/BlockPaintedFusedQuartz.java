package crazypants.enderio.material.fusedQuartz;

import java.util.List;

import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.blocks.TileEntityPaintedBlock;
import crazypants.enderio.machine.painter.recipe.BasicPainterTemplate;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintHelper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.registry.SmartModelAttacher;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockFusedQuartz;

public class BlockPaintedFusedQuartz extends BlockFusedQuartzBase<TileEntityPaintedBlock> implements ITileEntityProvider, IPaintable.IBlockPaintableBlock {
  
  public static BlockPaintedFusedQuartz create() {
    BlockPaintedFusedQuartz result = new BlockPaintedFusedQuartz();
    result.init();
    return result;
  }

  private BlockPaintedFusedQuartz() {
    super(ModObject.blockPaintedFusedQuartz.getUnlocalisedName(), TileEntityPaintedBlock.class);
    setCreativeTab(null);
    setDefaultState(
        this.blockState.getBaseState().withProperty(FusedQuartzType.KIND, FusedQuartzType.FUSED_QUARTZ));
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.registerNoProps(this);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(), new BasicPainterTemplate<BlockPaintedFusedQuartz>(this,
        blockFusedQuartz.getBlock()) {
      @Override
      public ItemStack isUnpaintingOp(ItemStack paintSource, ItemStack target) {
        if (paintSource == null || target == null) {
          return null;
        }

        Block paintBlock = PainterUtil2.getBlockFromItem(paintSource);
        Block targetBlock = Block.getBlockFromItem(target.getItem());
        if (paintBlock == null || targetBlock == null) {
          return null;
        }

        if (paintBlock == blockFusedQuartz.getBlock() && paintSource.getItemDamage() == target.getItemDamage()) {
          return new ItemStack(blockFusedQuartz.getBlock(), 1, target.getItemDamage());
        }

        return null;
      }

    });
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { FusedQuartzType.KIND });
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state != null && world != null && pos != null) {
      IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, null);
      blockStateWrapper.addCacheKey(0);
      blockStateWrapper.bakeModel();
      return blockStateWrapper;
    } else {
      return state;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public float getAmbientOcclusionLightValue(IBlockState bs) {
    return 1;
  }

  @Override
  public boolean doesSideBlockRendering(IBlockState bs, IBlockAccess world, BlockPos pos, EnumFacing face) {
    return false;
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    return new TileEntityPaintedBlock();
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    setPaintSource(state, world, pos, PainterUtil2.getSourceBlock(stack));
    if (!world.isRemote) {      
      IBlockState bs = world.getBlockState(pos);
      world.notifyBlockUpdate(pos, bs, bs, 3);
    }
  }

  @Override
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
    for (ItemStack drop : drops) {
      PainterUtil2.setSourceBlock(drop, getPaintSource(state, world, pos));
    }
    return drops;
  }

  @Override
  public ItemStack getPickBlock(IBlockState bs, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
    final ItemStack pickBlock = super.getPickBlock(bs, target, world, pos, player);
    PainterUtil2.setSourceBlock(pickBlock, getPaintSource(null, world, pos));
    return pickBlock;
  }

  @Override
  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      ((IPaintableTileEntity) te).setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(Block block, ItemStack stack, @Nullable IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      return ((IPaintableTileEntity) te).getPaintSource();
    }
    return null;
  }

  @Override
  public IBlockState getPaintSource(Block block, ItemStack stack) {
    return PainterUtil2.getSourceBlock(stack);
  }

  @Override
  public IBlockState getFacade(IBlockAccess world, BlockPos pos, EnumFacing side) {
    IBlockState paintSource = getPaintSource(getDefaultState(), world, pos);
    return paintSource != null ? paintSource : world.getBlockState(pos);
  }

  @Override
  public boolean canRenderInLayer(BlockRenderLayer layer) {
    return true;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager effectRenderer) {
    return PaintHelper.addHitEffects(state, world, target, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager effectRenderer) {
    return PaintHelper.addDestroyEffects(world, pos, effectRenderer);
  }

}
