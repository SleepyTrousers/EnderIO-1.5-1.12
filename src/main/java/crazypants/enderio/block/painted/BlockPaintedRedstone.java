package crazypants.enderio.block.painted;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintHelper;
import crazypants.enderio.paint.render.PaintRegistry;
import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.enderio.recipe.painter.BasicPainterTemplate;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.registry.SmartModelAttacher;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCompressedPowered;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockPaintedRedstone extends BlockCompressedPowered implements ITileEntityProvider, IPaintable.IBlockPaintableBlock {

  public static BlockPaintedRedstone create_solid(@Nonnull IModObject modObject) {
    BlockPaintedRedstone result = new BlockPaintedRedstoneSolid(modObject);
    result.init(modObject);
    return result;
  }

  public static BlockPaintedRedstone create(@Nonnull IModObject modObject) {
    BlockPaintedRedstone result = new BlockPaintedRedstoneNonSolid(modObject);
    result.init(modObject);
    return result;
  }

  public static class BlockPaintedRedstoneSolid extends BlockPaintedRedstone implements IPaintable.ISolidBlockPaintableBlock {

    protected BlockPaintedRedstoneSolid(@Nonnull IModObject modObject) {
      super(modObject);
    }

  }

  public static class BlockPaintedRedstoneNonSolid extends BlockPaintedRedstone implements IPaintable.INonSolidBlockPaintableBlock {

    protected BlockPaintedRedstoneNonSolid(@Nonnull IModObject modObject) {
      super(modObject);
      useNeighborBrightness = true;
      setLightOpacity(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getAmbientOcclusionLightValue(@Nonnull IBlockState bs) {
      return 1;
    }

    @Override
    public boolean doesSideBlockRendering(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
      return false;
    }

  }

  protected BlockPaintedRedstone(@Nonnull IModObject modObject) {
    super(Material.IRON, MapColor.TNT);
    setHardness(5.0F);
    setResistance(10.0F);
    setSoundType(SoundType.METAL);
    setCreativeTab(null);
    modObject.apply(this);
  }

  private void init(@Nonnull IModObject modObject) {
    GameRegistry.register(this);
    GameRegistry.register(modObject.apply(new BlockItemPaintedBlock(this)));
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new BasicPainterTemplate<BlockPaintedRedstone>(this, Blocks.REDSTONE_BLOCK));
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("cube_all", new ResourceLocation("minecraft", "block/cube_all"), PaintRegistry.PaintMode.ALL_TEXTURES);
  }

  @Override
  public boolean removedByPlayer(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
    if (willHarvest) {
      return true;
    }
    return super.removedByPlayer(bs, world, pos, player, willHarvest);
  }

  @Override
  public void harvestBlock(@Nonnull World worldIn, @Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te,
      @Nonnull ItemStack stack) {
    super.harvestBlock(worldIn, player, pos, state, te, stack);
    super.removedByPlayer(state, worldIn, pos, player, true);
  }

  @Override
  public @Nonnull List<ItemStack> getDrops(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
    List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
    for (ItemStack drop : drops) {
      PainterUtil2.setSourceBlock(NullHelper.notnullM(drop, "null stack from getDrops()"), getPaintSource(state, world, pos));
    }
    return drops;
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
    return new TileEntityPaintedBlock();
  }

  @Override
  public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull ItemStack stack) {
    setPaintSource(state, world, pos, PainterUtil2.getSourceBlock(stack));
    if (!world.isRemote) {
      world.notifyBlockUpdate(pos, state, state, 3);
    }
  }

  @Override
  public @Nonnull ItemStack getPickBlock(@Nonnull IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EntityPlayer player) {
    final ItemStack pickBlock = super.getPickBlock(state, target, world, pos, player);
    PainterUtil2.setSourceBlock(pickBlock, getPaintSource(state, world, pos));
    return pickBlock;
  }

  @Override
  public void setPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      ((IPaintableTileEntity) te).setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(@Nonnull Block block, @Nonnull ItemStack stack, @Nullable IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    TileEntity te = BlockEnder.getAnyTileEntitySafe(world, pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      return ((IPaintableTileEntity) te).getPaintSource();
    }
    return null;
  }

  @Override
  public IBlockState getPaintSource(@Nonnull Block block, @Nonnull ItemStack stack) {
    return PainterUtil2.getSourceBlock(stack);
  }

  @Override
  public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, null);
    blockStateWrapper.addCacheKey(0);
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  @Override
  public @Nonnull IBlockState getFacade(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    IBlockState paintSource = getPaintSource(getDefaultState(), world, pos);
    return paintSource != null ? paintSource : world.getBlockState(pos);
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(@Nonnull Item itemIn, @Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (tab == EnderIOTab.tabNoTab) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(@Nonnull IBlockState state, @Nonnull World world, @Nonnull RayTraceResult target, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addHitEffects(state, world, target, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addDestroyEffects(world, pos, effectRenderer);
  }

}
