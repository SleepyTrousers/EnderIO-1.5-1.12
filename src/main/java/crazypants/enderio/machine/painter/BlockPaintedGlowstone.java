package crazypants.enderio.machine.painter;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlowstone;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.render.paint.IPaintable;
import crazypants.enderio.render.paint.PaintRegistry;

public class BlockPaintedGlowstone extends BlockGlowstone implements ITileEntityProvider, IPaintable.ISolidBlockPaintableBlock, ISmartRenderAwareBlock,
    IRenderMapper {

  public static BlockPaintedGlowstone create() {
    BlockPaintedGlowstone result = new BlockPaintedGlowstone();
    result.init();
    return result;
  }

  protected BlockPaintedGlowstone() {
    super(Material.glass);
    setHardness(0.3F);
    setStepSound(soundTypeGlass);
    setLightLevel(1.0F);
    setCreativeTab(null);
    setUnlocalizedName(ModObject.blockPaintedGlowstone.unlocalisedName);
  }

  private void init() {
    GameRegistry.registerBlock(this, null, ModObject.blockPaintedGlowstone.unlocalisedName);
    GameRegistry.registerItem(new BlockItemPaintedBlock(this), ModObject.blockPaintedGlowstone.unlocalisedName);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new BasicPainterTemplate<BlockPaintedGlowstone>(this,
        Blocks.glowstone));
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("cube_all", new ResourceLocation("minecraft", "block/cube_all"), PaintRegistry.PaintMode.ALL_TEXTURES);
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
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    return new BlockStateWrapper(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper getRenderMapper() {
    return this;
  }

  @Override
  public Pair<List<IBlockState>, List<IBakedModel>> mapBlockRender(BlockStateWrapper state, IBlockAccess world, BlockPos pos) {
    return null;
  }

  @Override
  public List<IBlockState> mapOverlayLayer(BlockStateWrapper state, IBlockAccess world, BlockPos pos) {
    return null;
  }

  @Override
  public Pair<List<IBlockState>, List<IBakedModel>> mapItemRender(Block block, ItemStack stack) {
    return null;
  }

  @Override
  public Pair<List<IBlockState>, List<IBakedModel>> mapItemPaintOverlayRender(Block block, ItemStack stack) {
    return null;
  }

  @Override
  public IBlockState getFacade(IBlockAccess world, BlockPos pos, EnumFacing side) {
    return getPaintSource(getDefaultState(), world, pos);
  }

}
