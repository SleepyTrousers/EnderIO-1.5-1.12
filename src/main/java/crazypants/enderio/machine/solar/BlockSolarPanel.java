package crazypants.enderio.machine.solar;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.render.EnumMergingBlockRenderMode;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.waila.IWailaInfoProvider;

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
    super(ModObject.blockSolarPanel.unlocalisedName, TileEntitySolarPanel.class, BlockItemSolarPanel.class);
    if(!Config.photovoltaicCellEnabled) {
      setCreativeTab(null);
    }
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
    setLightOpacity(255);
    useNeighborBrightness = true;
    setDefaultState(this.blockState.getBaseState().withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO)
        .withProperty(SolarType.KIND, SolarType.SIMPLE));
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.register(this, EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.DEFAULTS, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER, SolarType.KIND });
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
  public IItemRenderMapper getRenderMapper() {
    return SolarItemRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.SOLID;
  }

  @Override
  public int damageDropped(IBlockState bs) {
    return getMetaFromState(bs);
  }

  @Override
  public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileEntitySolarPanel) {
      ((TileEntitySolarPanel) te).onNeighborBlockChange();
    }
  }

  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, BlockPos pos) {
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

  @Override
  public void setBlockBoundsForItemRender() {
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

  @Override  
  public void addCollisionBoxesToList(World par1World, BlockPos pos, IBlockState state, AxisAlignedBB par5AxisAlignedBB, List<AxisAlignedBB> par6List, Entity par7Entity) {
    setBlockBoundsBasedOnState(par1World, pos);
    super.addCollisionBoxesToList(par1World, pos, state, par5AxisAlignedBB,par6List, par7Entity);
  }

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
        tooltip.add(EnumChatFormatting.RED + EnderIO.lang.localize("tooltip.sunlightBlocked"));
      } else {
        tooltip.add(String.format("%s : %s%.0f%%", EnumChatFormatting.WHITE + EnderIO.lang.localize("tooltip.efficiency") + EnumChatFormatting.RESET,
            EnumChatFormatting.WHITE, efficiency * 100));
      }
    }
  }

  @Override
  public int getDefaultDisplayMask(World world, int x, int y, int z) {
    return 0;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean isFullCube() {
    return false;
  }

  @Override
  public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing face) {
    return face == EnumFacing.UP;
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
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
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
