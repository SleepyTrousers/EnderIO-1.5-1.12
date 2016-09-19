package crazypants.enderio.machine.solar;

import java.util.List;
import java.util.Random;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.integration.waila.IWailaInfoProvider;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.render.registry.SmartModelAttacher;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSolarPanel extends BlockEio<TileEntitySolarPanel> implements IResourceTooltipProvider, IWailaInfoProvider, ISmartRenderAwareBlock {

  @SideOnly(Side.CLIENT)
  private static SolarItemRenderMapper RENDER_MAPPER;

  public static BlockSolarPanel create() {
    BlockSolarPanel result = new BlockSolarPanel();
    result.init();
    return result;
  }

  private static final float BLOCK_HEIGHT = 2.5f / 16f;
  
  private BlockSolarPanel() {
    super(ModObject.blockSolarPanel.getUnlocalisedName(), TileEntitySolarPanel.class);
    if(!Config.photovoltaicCellEnabled) {
      setCreativeTab(null);
    }    
    setLightOpacity(255);
    useNeighborBrightness = true;
    setDefaultState(this.blockState.getBaseState().withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO)
        .withProperty(SolarType.KIND, SolarType.SIMPLE));
  }

  @Override
  protected ItemBlock createItemBlock() { 
    return new BlockItemSolarPanel(this, getName());
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.register(this, EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.DEFAULTS, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER, SolarType.KIND });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(SolarType.KIND, SolarType.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return SolarType.getMetaFromType(state.getValue(SolarType.KIND));
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return state.withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state != null && world != null && pos != null) {
      SolarBlockRenderMapper renderMapper = new SolarBlockRenderMapper(state, world, pos);
      IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, renderMapper);
      blockStateWrapper.addCacheKey(state.getValue(SolarType.KIND));
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
    return SolarItemRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.SOLID;
  }

  @Override
  public int damageDropped(IBlockState bs) {
    return getMetaFromState(bs);
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {    
    return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

//  @Override  
//  public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn) {    
//    setBlockBoundsBasedOnState(worldIn, pos);
//    super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn);
//  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = getTileEntity(world, new BlockPos(x, y, z));
    if(te instanceof TileEntitySolarPanel) {
      TileEntitySolarPanel solar = (TileEntitySolarPanel) te;
      float efficiency = solar.calculateLightRatio();
      if(!solar.canSeeSun()) {
        tooltip.add(TextFormatting.RED + EnderIO.lang.localize("tooltip.sunlightBlocked"));
      } else {
        tooltip.add(String.format("%s : %s%.0f%%", TextFormatting.WHITE + EnderIO.lang.localize("tooltip.efficiency") + TextFormatting.RESET,
            TextFormatting.WHITE, efficiency * 100));
      }
    }
  }

  @Override
  public int getDefaultDisplayMask(World world, int x, int y, int z) {
    return 0;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  public boolean isFullCube(IBlockState bs) {
    return false;
  }

  @Override
  public boolean doesSideBlockRendering(IBlockState bs, IBlockAccess world, BlockPos pos, EnumFacing face) {
    return face == EnumFacing.DOWN;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    for (SolarType solarType : SolarType.KIND.getAllowedValues()) {
      list.add(new ItemStack(this, 1, damageDropped(getDefaultState().withProperty(SolarType.KIND, solarType))));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
    if (state.getValue(SolarType.KIND) == SolarType.VIBRANT) {
      TileEntity te = getTileEntity(world, pos);
      if (te instanceof TileEntitySolarPanel) {
        TileEntitySolarPanel solar = (TileEntitySolarPanel) te;        
        if (solar.canSeeSun() && solar.calculateLightRatio() / 3 > rand.nextFloat()) {
          double d0 = pos.getX() + 0.5D + (Math.random() - 0.5D) * 0.5D;
          double d1 = pos.getY() + BLOCK_HEIGHT;
          double d2 = pos.getZ() + 0.5D + (Math.random() - 0.5D) * 0.5D;
          world.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, 0x47 / 255d, 0x9f / 255d, 0xa3 / 255d, new int[0]);
        }
      }
    }
  }

}
