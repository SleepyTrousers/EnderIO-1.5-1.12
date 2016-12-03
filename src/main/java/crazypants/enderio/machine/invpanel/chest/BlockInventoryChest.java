package crazypants.enderio.machine.invpanel.chest;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.GuiID;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.RenderMappers;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.property.EnumRenderMode;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInventoryChest extends AbstractMachineBlock<TileInventoryChest>
    implements IResourceTooltipProvider, ISmartRenderAwareBlock, IPaintable.IBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockInventoryChest create() {
    TileInventoryChest.create();
    BlockInventoryChest res = new BlockInventoryChest();
    res.init();
    return res;
  }

  protected BlockInventoryChest() {
    super(ModObject.blockInventoryChest, TileInventoryChest.class);
    initDefaultState();
  }

  @Override
  protected void initDefaultState() {
    setDefaultState(
        this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO).withProperty(EnumChestSize.SIZE, EnumChestSize.TINY));
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode.RENDER, EnumChestSize.SIZE });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(EnumChestSize.SIZE, EnumChestSize.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return EnumChestSize.getMetaFromType(state.getValue(EnumChestSize.SIZE));
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO);
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileInventoryChest tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(blockStateWrapper.getValue(EnumChestSize.SIZE));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  // NO GUI

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return null;
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    return false;
  }

  @Override
  public boolean hasComparatorInputOverride(IBlockState state) {
    return true;
  }

  @Override
  public int getComparatorInputOverride(IBlockState blockStateIn, World worldIn, BlockPos pos) {
    TileInventoryChest te = getTileEntitySafe(worldIn, pos);
    if (te != null) {
      return te.getComparatorInputOverride();
    }
    return 0;
  }

  @Override
  public @Nonnull TileEntity createTileEntity(World world, IBlockState state) {
    TileInventoryChest te = TileInventoryChest.create(state.getValue(EnumChestSize.SIZE));
    te.init();
    return te;
  }

  @Override
  public boolean hasTileEntity(IBlockState state) {
    return true;
  }

  @Override
  protected ItemBlock createItemBlock() {
    ItemBlock ib = new ItemBlock(this) {
      @Override
      public String getUnlocalizedName(ItemStack stack) {
        return EnumChestSize.getTypeFromMeta(stack.getMetadata()).getUnlocalizedName(this);
      }

      @Override
      public int getMetadata(int damage) {
        return damage;
      }
    };
    ib.setRegistryName(getName());
    ib.setHasSubtypes(true);
    return ib;
  }


  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    for (EnumChestSize size : EnumChestSize.values()) {
      list.add(new ItemStack(itemIn, 1, EnumChestSize.getMetaFromType(size)));
    }
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

}
