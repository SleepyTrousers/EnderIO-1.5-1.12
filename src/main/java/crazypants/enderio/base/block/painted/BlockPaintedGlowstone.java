package crazypants.enderio.base.block.painted;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.PainterUtil2;
import crazypants.enderio.base.paint.render.PaintHelper;
import crazypants.enderio.base.paint.render.PaintRegistry;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.BasicPainterTemplate;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlowstone;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockPaintedGlowstone extends BlockGlowstone implements ITileEntityProvider, IPaintable.IBlockPaintableBlock, IModObject.WithBlockItem {

  public static BlockPaintedGlowstone create(@Nonnull IModObject modObject) {
    BlockPaintedGlowstone result = new BlockPaintedGlowstoneNonSolid(modObject);
    result.init(modObject);
    return result;
  }

  public static BlockPaintedGlowstone create_solid(@Nonnull IModObject modObject) {
    BlockPaintedGlowstone result = new BlockPaintedGlowstoneSolid(modObject);
    result.init(modObject);
    return result;
  }

  public static class BlockPaintedGlowstoneSolid extends BlockPaintedGlowstone implements IPaintable.ISolidBlockPaintableBlock {

    protected BlockPaintedGlowstoneSolid(@Nonnull IModObject modObject) {
      super(modObject);
    }

  }

  public static class BlockPaintedGlowstoneNonSolid extends BlockPaintedGlowstone implements IPaintable.INonSolidBlockPaintableBlock {

    protected BlockPaintedGlowstoneNonSolid(@Nonnull IModObject modObject) {
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

  protected BlockPaintedGlowstone(@Nonnull IModObject modObject) {
    super(Material.GLASS);
    setHardness(0.3F);
    setSoundType(SoundType.GLASS);
    setLightLevel(1.0F);
    Prep.setNoCreativeTab(this);
    modObject.apply(this);
  }

  private void init(@Nonnull IModObject modObject) {
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new BasicPainterTemplate<BlockPaintedGlowstone>(this, Blocks.GLOWSTONE));
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("cube_all", new ResourceLocation("minecraft", "block/cube_all"), PaintRegistry.PaintMode.ALL_TEXTURES);
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlockItemPaintedBlock(this));
  }

  @Override
  public boolean canSilkHarvest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player) {
    return world.getTileEntity(pos) instanceof IPaintable.IPaintableTileEntity;
  }

  @Override
  public @Nonnull List<ItemStack> getDrops(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {

    // if silk touch is required, the painted drop is handled in harvestBlock as that has the required te
    if (Config.paintedGlowstoneRequireSilkTouch) {
      return super.getDrops(world, pos, state, fortune);
    }

    List<ItemStack> res = new ArrayList<ItemStack>();
    TileEntity te = world.getTileEntity(pos);
    res.add(createPaintedDrop(te));
    return res;
  }

  private ItemStack createPaintedDrop(TileEntity te) {
    if (te instanceof IPaintable.IPaintableTileEntity) {
      ItemStack itemstack = new ItemStack(this);
      IBlockState paintSource = ((IPaintableTileEntity) te).getPaintSource();
      PainterUtil2.setSourceBlock(itemstack, paintSource);
      return itemstack;

    }
    return new ItemStack(Blocks.GLOWSTONE);
  }

  @Override
  public void harvestBlock(@Nonnull final World worldIn, @Nonnull EntityPlayer player, @Nonnull final BlockPos pos, @Nonnull IBlockState state,
      @Nullable TileEntity te, @Nonnull ItemStack stack) {

    if (Config.paintedGlowstoneRequireSilkTouch && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) <= 0) {
      super.harvestBlock(worldIn, player, pos, state, te, stack);
      return;
    }

    // need special code so we can get the paint source from the te
    supressed(player);
    player.addExhaustion(0.025F);

    NNList<ItemStack> items = new NNList<ItemStack>(createPaintedDrop(te));
    ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
    items.apply(new NNList.Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack itemStack) {
        spawnAsEntity(worldIn, pos, itemStack);
      }
    });
  }

  @SuppressWarnings("null")
  private void supressed(EntityPlayer player) {
    player.addStat(StatList.getBlockStats(this));
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

  @SuppressWarnings("null")
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
