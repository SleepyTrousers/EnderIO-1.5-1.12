package crazypants.enderio.material.fusedQuartz;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.blocks.TileEntityPaintedBlock;
import crazypants.enderio.machine.painter.recipe.BasicPainterTemplate;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.render.pipeline.QuadCollector;

public class BlockPaintedFusedQuartz extends BlockFusedQuartzBase<TileEntityPaintedBlock> implements ITileEntityProvider, IPaintable.IBlockPaintableBlock,
    IRenderMapper {
  
  @SideOnly(Side.CLIENT)
  private static FusedQuartzRenderMapper RENDER_MAPPER;

  public static BlockPaintedFusedQuartz create() {
    BlockPaintedFusedQuartz result = new BlockPaintedFusedQuartz();
    result.init();
    return result;
  }

  private BlockPaintedFusedQuartz() {
    super(ModObject.blockFusedQuartz.unlocalisedName + "2", TileEntityPaintedBlock.class);
    setCreativeTab(null);
    setDefaultState(this.blockState.getBaseState().withProperty(FusedQuartzType.KIND, FusedQuartzType.FUSED_QUARTZ));
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.registerNoProps(this);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new BasicPainterTemplate<BlockPaintedFusedQuartz>(this,
        EnderIO.blockFusedQuartz) {
      @Override
      public ItemStack isUnpaintingOp(ItemStack paintSource, ItemStack target) {
        if (paintSource == null || target == null) {
          return null;
        }

        Block paintBlock = Block.getBlockFromItem(paintSource.getItem());
        Block targetBlock = Block.getBlockFromItem(target.getItem());
        if (paintBlock == null || targetBlock == null) {
          return null;
        }

        if (paintBlock == EnderIO.blockFusedQuartz && paintSource.getItemDamage() == target.getItemDamage()) {
          return new ItemStack(EnderIO.blockFusedQuartz, 1, target.getItemDamage());
        }

        return null;
      }

    });
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] { FusedQuartzType.KIND });
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    return new BlockStateWrapper(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper getRenderMapper() {
    return this;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public float getAmbientOcclusionLightValue() {
    return 1;
  }

  @Override
  public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing face) {
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
      world.markBlockForUpdate(pos);
    }
  }

  @Override
  public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
    final ItemStack pickBlock = super.getPickBlock(target, world, pos, player);
    PainterUtil2.setSourceBlock(pickBlock, getPaintSource(null, world, pos));
    return pickBlock;
  }

  @Override
  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      ((IPaintableTileEntity) te).setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(Block block, ItemStack stack, IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
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
  @SideOnly(Side.CLIENT)
  public Pair<List<IBlockState>, List<IBakedModel>> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Pair<List<IBlockState>, List<IBakedModel>> mapItemRender(Block block, ItemStack stack) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Pair<List<IBlockState>, List<IBakedModel>> mapItemPaintOverlayRender(Block block, ItemStack stack) {
    return null;
  }

  @Override
  public IBlockState getFacade(IBlockAccess world, BlockPos pos, EnumFacing side) {
    return getPaintSource(getDefaultState(), world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
    IBlockState paintSource = getPaintSource(null, worldIn, pos);
    if (paintSource != null) {
      try {
        return paintSource.getBlock().colorMultiplier(worldIn, pos, renderPass);
      } catch (Throwable e) {
      }
    }
    return super.colorMultiplier(worldIn, pos, renderPass);
  }

  @Override
  public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
    return true;
  }

  @Override
  public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, EnumWorldBlockLayer blockLayer,
      QuadCollector quadCollector) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EnumMap<EnumFacing, EnumIOMode> mapOverlayLayer(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, boolean isPainted) {
    return null;
  }

}
