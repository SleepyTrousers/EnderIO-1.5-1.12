package crazypants.enderio.machine.painter.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.recipe.BasicPainterTemplate;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintRegistry;
import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.render.dummy.BlockMachineBase;

public abstract class BlockPaintedSlab extends BlockSlab implements ITileEntityProvider, IPaintable.ITexturePaintableBlock, ISmartRenderAwareBlock,
    IRenderMapper.IRenderLayerAware {

  public static BlockPaintedSlab create() {
    BlockPaintedHalfSlab woodHalfSlab = new BlockPaintedHalfSlab(Material.wood, ModObject.blockPaintedSlab.unlocalisedName);
    woodHalfSlab.setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood);
    woodHalfSlab.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new BasicPainterTemplate<BlockPaintedSlab>(woodHalfSlab,
        Blocks.wooden_slab));

    BlockPaintedDoubleSlab woodDoubleSlab = new BlockPaintedDoubleSlab(Material.wood, ModObject.blockPaintedDoubleSlab.unlocalisedName, woodHalfSlab);
    woodDoubleSlab.setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood);
    woodDoubleSlab.init();

    GameRegistry.registerItem(new BlockItemPaintedSlab(woodHalfSlab, woodDoubleSlab), ModObject.blockPaintedSlab.unlocalisedName);

    BlockPaintedHalfSlab rockHalfSlab = new BlockPaintedHalfSlab(Material.rock, ModObject.blockPaintedSlab.unlocalisedName + "2");
    rockHalfSlab.setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood);
    rockHalfSlab.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new BasicPainterTemplate<BlockPaintedSlab>(rockHalfSlab,
        Blocks.stone_slab, Blocks.stone_slab2));

    BlockPaintedDoubleSlab rockDoubleSlab = new BlockPaintedDoubleSlab(Material.rock, ModObject.blockPaintedDoubleSlab.unlocalisedName + "2",
        rockHalfSlab);
    rockDoubleSlab.setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood);
    rockDoubleSlab.init();

    GameRegistry.registerItem(new BlockItemPaintedSlab(rockHalfSlab, rockDoubleSlab), ModObject.blockPaintedSlab.unlocalisedName + "2");

    GameRegistry.registerTileEntity(TileEntityPaintedBlock.TileEntityTwicePaintedBlock.class, ModObject.blockPaintedSlab.unlocalisedName + "TileEntity");

    return woodHalfSlab;
  }

  public static class BlockPaintedHalfSlab extends BlockPaintedSlab {
    public BlockPaintedHalfSlab(Material material, String name) {
      super(material, name, null);
    }

    @Override
    public boolean isDouble() {
      return false;
    }
  }

  public static class BlockPaintedDoubleSlab extends BlockPaintedSlab {
    public BlockPaintedDoubleSlab(Material material, String name, Block halfVariant) {
      super(material, name, halfVariant);
    }

    @Override
    public boolean isDouble() {
      return true;
    }

  }

  private final String name;
  private final Block halfVariant;

  public BlockPaintedSlab(Material material, String name, Block halfVariant) {
    super(material);
    IBlockState iblockstate = this.blockState.getBaseState();

    if (!this.isDouble()) {
      iblockstate = iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
    }

    this.setDefaultState(iblockstate);
    this.setCreativeTab(null);
    this.name = name;
    setUnlocalizedName(name);
    this.halfVariant = halfVariant != null ? halfVariant : this;
    useNeighborBrightness = true;
  }

  protected void init() {
    GameRegistry.registerBlock(this, null, name);
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("slab_lo", new ResourceLocation("minecraft", "block/half_slab_stone"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("slab_hi", new ResourceLocation("minecraft", "block/upper_slab_stone"), PaintRegistry.PaintMode.ALL_TEXTURES);
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    return new TileEntityPaintedBlock.TileEntityTwicePaintedBlock();
  }

  @Override
  public String getUnlocalizedName(int meta) {
    return getUnlocalizedName();
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    setPaintSource(state, world, pos, PainterUtil2.getSourceBlock(stack));
    if (!world.isRemote) {
      world.markBlockForUpdate(pos);
    }
  }

  @Override
  public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    if (willHarvest) {
      return true;
    }
    return super.removedByPlayer(world, pos, player, willHarvest);
  }

  @Override
  public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
    super.harvestBlock(worldIn, player, pos, state, te);
    super.removedByPlayer(worldIn, pos, player, true);
  }

  @Override
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
    boolean first = true;
    for (ItemStack drop : drops) {
      if (first || !isDouble()) {
        PainterUtil2.setSourceBlock(drop, getPaintSource(state, world, pos));
        first = false;
      } else {
        PainterUtil2.setSourceBlock(drop, getPaintSource2(state, world, pos));
      }
    }
    return drops;
  }

  @Override
  public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
    final ItemStack pickBlock = super.getPickBlock(target, world, pos, player);
    if (!isDouble()) {
      PainterUtil2.setSourceBlock(pickBlock, getPaintSource(null, world, pos));
    } else {
      if ((target.hitVec.yCoord - (int) target.hitVec.yCoord) > 0.5) {
        PainterUtil2.setSourceBlock(pickBlock, getPaintSource2(null, world, pos));
      } else {
        PainterUtil2.setSourceBlock(pickBlock, getPaintSource(null, world, pos));
      }
    }
    return pickBlock;
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return Item.getItemFromBlock(halfVariant);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Item getItem(World worldIn, BlockPos pos) {
    return Item.getItemFromBlock(halfVariant);
  }

  @Override
  public IProperty<?> getVariantProperty() {
    return null;
  }

  @Override
  public Object getVariant(ItemStack stack) {
    return null;
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
  protected BlockState createBlockState() {
    return this.isDouble() ? new BlockState(this, new IProperty[] {}) : new BlockState(this, new IProperty[] { HALF });
  }

  @Override
  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      ((IPaintableTileEntity) te).setPaintSource(paintSource);
    }
  }

  public void setPaintSource2(IBlockState state, IBlockAccess world, BlockPos pos, IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileEntityPaintedBlock.TileEntityTwicePaintedBlock) {
      ((TileEntityPaintedBlock.TileEntityTwicePaintedBlock) te).setPaintSource2(paintSource);
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

  public IBlockState getPaintSource2(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileEntityPaintedBlock.TileEntityTwicePaintedBlock) {
      return ((TileEntityPaintedBlock.TileEntityTwicePaintedBlock) te).getPaintSource2();
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
  @SideOnly(Side.CLIENT)
  public Pair<List<IBlockState>, List<IBakedModel>> mapBlockRender(BlockStateWrapper state, IBlockAccess world, BlockPos pos) {
    if (isDouble()) {
      List<IBakedModel> result = new ArrayList<IBakedModel>();
      IBlockState paintSource = getPaintSource2(state, world, pos);
      if (paintSource != null && paintSource.getBlock().canRenderInLayer(MinecraftForgeClient.getRenderLayer())) {
        result.add(PaintRegistry.getModel(IBakedModel.class, "slab_hi", paintSource, null));
      }
      paintSource = getPaintSource(state, world, pos);
      if (paintSource != null && paintSource.getBlock().canRenderInLayer(MinecraftForgeClient.getRenderLayer())) {
        result.add(PaintRegistry.getModel(IBakedModel.class, "slab_lo", paintSource, null));
      }
      if (result.isEmpty()) {
        return null;
      } else {
        return Pair.of(null, result);
      }
    } else {
      IBlockState paintSource = getPaintSource(state, world, pos);
      if (paintSource != null && paintSource.getBlock().canRenderInLayer(MinecraftForgeClient.getRenderLayer())) {
        if (state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
          return Pair.of(null, Collections.singletonList(PaintRegistry.getModel(IBakedModel.class, "slab_hi", paintSource, null)));
        } else {
          return Pair.of(null, Collections.singletonList(PaintRegistry.getModel(IBakedModel.class, "slab_lo", paintSource, null)));
        }
      } else {
        return null;
      }
    }
  }
  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapOverlayLayer(BlockStateWrapper state, IBlockAccess world, BlockPos pos) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Pair<List<IBlockState>, List<IBakedModel>> mapItemRender(Block block, ItemStack stack) {
    IBlockState paintSource = getPaintSource(block, stack);
    if (paintSource != null) {
      IBlockState stdOverlay = BlockMachineBase.block.getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY);
      IBakedModel model1 = PaintRegistry.getModel(IBakedModel.class, "slab_lo", paintSource, null);
      IBakedModel model2 = PaintRegistry.getModel(IBakedModel.class, "slab_lo", stdOverlay, PaintRegistry.OVERLAY_TRANSFORMATION3);
      List<IBakedModel> list = new ArrayList<IBakedModel>();
      list.add(model1);
      list.add(model2);
      return Pair.of(null, list);
    } else {
      return null;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Pair<List<IBlockState>, List<IBakedModel>> mapItemPaintOverlayRender(Block block, ItemStack stack) {
    return null;
  }

  @Override
  public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
    return true;
  }

  @Override
  public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
    return getMaterial() == Material.wood ? 20 : super.getFlammability(world, pos, face);
  }

  @Override
  public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
    return getMaterial() == Material.wood ? 5 : super.getFireSpreadSpeed(world, pos, face);
  }

  @Override
  public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing face) {
    return false;
  }

  @Override
  public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos there, EnumFacing side) {
    IBlockState blockState2 = worldIn.getBlockState(there);
    Block block2 = blockState2.getBlock();
    if (block2 instanceof BlockPaintedSlab) {
      BlockPaintedSlab otherBlock = (BlockPaintedSlab) block2;
      BlockPos here = there.offset(side.getOpposite());
      IBlockState ourBlockState = worldIn.getBlockState(here);
      if (isDouble()) {
        if (!otherBlock.isDouble()) {
          return true;
        } else {
          return getPaintSource(ourBlockState, worldIn, here) != getPaintSource(blockState2, worldIn, there)
              || getPaintSource2(ourBlockState, worldIn, here) != getPaintSource2(blockState2, worldIn, there);
        }
      } else {
        if (!otherBlock.isDouble() && blockState2.getValue(HALF) != ourBlockState.getValue(HALF)) {
          return true;
        }
        IBlockState paintSource = getPaintSource(ourBlockState, worldIn, here);
        if (otherBlock.isDouble() && ourBlockState.getValue(HALF) == EnumBlockHalf.TOP) {
          return paintSource != getPaintSource2(blockState2, worldIn, there);
        }
        return paintSource != getPaintSource(blockState2, worldIn, there);
      }
    }
    return super.shouldSideBeRendered(worldIn, there, side);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

}