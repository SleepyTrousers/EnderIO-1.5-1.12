package crazypants.enderio.invpanel.chest;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.ICustomSubItems;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.property.EnumRenderMode;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInventoryChest extends AbstractMachineBlock<TileInventoryChest>
    implements IResourceTooltipProvider, ISmartRenderAwareBlock, ICustomSubItems, IPaintable.IBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockInventoryChest create(@Nonnull IModObject mo) {
    TileInventoryChest.create();
    BlockInventoryChest res = new BlockInventoryChest(mo);
    res.init();
    return res;
  }

  protected BlockInventoryChest(@Nonnull IModObject mo) {
    super(mo);
    initDefaultState();
  }

  @Override
  protected void initDefaultState() {
    setDefaultState(
        getBlockState().getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO).withProperty(EnumChestSize.SIZE, EnumChestSize.TINY));
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode.RENDER, EnumChestSize.SIZE });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(EnumChestSize.SIZE, EnumChestSize.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return EnumChestSize.getMetaFromType(state.getValue(EnumChestSize.SIZE));
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO);
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileInventoryChest tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive()).addCacheKey(blockStateWrapper.getValue(EnumChestSize.SIZE));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  // NO GUI

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileInventoryChest te) {
    return null;
  }

  @Override
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileInventoryChest te) {
    return null;
  }

  @Override
  protected boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
    return false;
  }

  // NO SMOKING

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {

  }

  @Override
  public boolean hasComparatorInputOverride(@Nonnull IBlockState state) {
    return true;
  }

  @Override
  public int getComparatorInputOverride(@Nonnull IBlockState blockStateIn, @Nonnull World worldIn, @Nonnull BlockPos pos) {
    TileInventoryChest te = getTileEntitySafe(worldIn, pos);
    if (te != null) {
      return te.getComparatorInputOverride();
    }
    return 0;
  }

  @Override
  public @Nonnull TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
    TileInventoryChest te = TileInventoryChest.create(state.getValue(EnumChestSize.SIZE));
    te.init();
    return te;
  }

  @Override
  public boolean hasTileEntity(@Nonnull IBlockState state) {
    return true;
  }

  @Override
  @Nullable
  public Item createBlockItem(@Nonnull IModObject modObject) {
    ItemBlock ib = new ItemBlock(this) {
      @Override
      public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
        return EnumChestSize.getTypeFromMeta(stack.getMetadata()).getUnlocalizedName(this);
      }

      @Override
      public int getMetadata(int damage) {
        return damage;
      }
    };
    ib.setHasSubtypes(true);
    modObject.apply(ib);
    return ib;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    for (EnumChestSize size : EnumChestSize.values()) {
      list.add(new ItemStack(this, 1, EnumChestSize.getMetaFromType(size)));
    }
  }

  @Override
  public int damageDropped(@Nonnull IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  @Nonnull
  public NNList<ItemStack> getSubItems() {
    return getSubItems(this, EnumChestSize.values().length);
  }

}
