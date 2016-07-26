package crazypants.enderio.machine.painter.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;

import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.recipe.BasicPainterTemplate;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintHelper;
import crazypants.enderio.paint.render.PaintRegistry;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockPaintedGlowstone extends BlockGlowstone implements ITileEntityProvider, IPaintable.IBlockPaintableBlock {

  public static BlockPaintedGlowstone create() {
    BlockPaintedGlowstone result = new BlockPaintedGlowstoneSolid(ModObject.blockPaintedGlowstoneSolid.getUnlocalisedName());
    result.init();

    BlockPaintedGlowstone result2 = new BlockPaintedGlowstoneNonSolid(ModObject.blockPaintedGlowstone.getUnlocalisedName());
    result2.init();
    return result;
  }

  public static class BlockPaintedGlowstoneSolid extends BlockPaintedGlowstone implements IPaintable.ISolidBlockPaintableBlock {

    protected BlockPaintedGlowstoneSolid(String name) {
      super(name);
    }

  }

  public static class BlockPaintedGlowstoneNonSolid extends BlockPaintedGlowstone implements IPaintable.INonSolidBlockPaintableBlock {

    protected BlockPaintedGlowstoneNonSolid(String name) {
      super(name);
      useNeighborBrightness = true;
      setLightOpacity(0);
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

  }

  private final String name;

  protected BlockPaintedGlowstone(String name) {
    super(Material.GLASS);
    this.name = name;
    setHardness(0.3F);
    setSoundType(SoundType.GLASS);
    setLightLevel(1.0F);
    setCreativeTab(null);
    setUnlocalizedName(name);
    setRegistryName(name);
  }

  private void init() {
    GameRegistry.register(this);
    GameRegistry.register(new BlockItemPaintedBlock(this, name));
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.getUnlocalisedName(),
        new BasicPainterTemplate<BlockPaintedGlowstone>(this, Blocks.GLOWSTONE));
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("cube_all", new ResourceLocation("minecraft", "block/cube_all"), PaintRegistry.PaintMode.ALL_TEXTURES);
  }

  @Override
  public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {    
     return world != null && pos != null && world.getTileEntity(pos) instanceof IPaintable.IPaintableTileEntity;
  }

  
  @Override
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        
    //if silk touch is required, the painted drop is handled in harvestBlock as that has the required te
    if (world == null || pos == null || Config.paintedGlowstoneRequireSilkTouch) {
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
  public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
        
    if(Config.paintedGlowstoneRequireSilkTouch && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) <= 0 ) {
      super.harvestBlock(worldIn, player, pos, state, te, stack);
      return;
    }

    //need special code so we can get the paint source from the te
    player.addStat(StatList.getBlockStats(this));
    player.addExhaustion(0.025F);

    List<ItemStack> items = new java.util.ArrayList<ItemStack>();        
    items.add(createPaintedDrop(te));
    ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
    for (ItemStack item : items) {
      spawnAsEntity(worldIn, pos, item);
    }
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    return new TileEntityPaintedBlock();
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    setPaintSource(state, world, pos, PainterUtil2.getSourceBlock(stack));
    if (!world.isRemote) {
      world.notifyBlockUpdate(pos, state, state, 3);
    }
  }

  @Override
  public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
    final ItemStack pickBlock = super.getPickBlock(state, target, world, pos, player);
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
  public IBlockState getFacade(IBlockAccess world, BlockPos pos, EnumFacing side) {
    IBlockState paintSource = getPaintSource(getDefaultState(), world, pos);
    return paintSource != null ? paintSource : world.getBlockState(pos);
  }

  @Override
  public boolean canRenderInLayer(BlockRenderLayer layer) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
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
