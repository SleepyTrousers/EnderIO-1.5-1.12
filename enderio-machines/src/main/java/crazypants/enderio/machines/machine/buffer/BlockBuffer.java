package crazypants.enderio.machines.machine.buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.machine.baselegacy.AbstractPowerConsumerBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.ICustomSubItems;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.property.EnumRenderMode;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBuffer extends AbstractPowerConsumerBlock<TileBuffer>
    implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, ICustomSubItems {

  public static BlockBuffer create(@Nonnull IModObject modObject) {
    BlockBuffer res = new BlockBuffer(modObject);
    res.init();
    return res;
  }

  private BlockBuffer(@Nonnull IModObject modObject) {
    super(modObject);
    setDefaultState(getBlockState().getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO).withProperty(BufferType.TYPE, BufferType.ITEM));
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @Override
  public BlockItemBuffer createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlockItemBuffer(this));
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode.RENDER, BufferType.TYPE });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(BufferType.TYPE, BufferType.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return BufferType.getMetaFromType(state.getValue(BufferType.TYPE));
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO);
  }

  @Override
  public int damageDropped(@Nonnull IBlockState st) {
    return getMetaFromState(st);
  }

  @Override
  public @Nonnull TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
    switch (state.getValue(BufferType.TYPE)) {
    case CREATIVE:
      return new TileBuffer.TileBufferCreative();
    case OMNI:
      return new TileBuffer.TileBufferOmni();
    case POWER:
      return new TileBuffer.TileBufferPower();
    case ITEM:
    default:
      return new TileBuffer.TileBufferItem();
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    for (BufferType type : BufferType.values()) {
      list.add(BufferType.getStack(type));
    }
  }

  @Override
  @Nonnull
  public NNList<ItemStack> getSubItems() {
    return getSubItems(this, BufferType.values().length - 1);
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileBuffer te) {
    return new ContainerBuffer(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileBuffer te) {
    return new GuiBuffer(player.inventory, te);
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

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileBuffer tileEntity) {
    blockStateWrapper.addCacheKey(blockStateWrapper.getValue(BufferType.TYPE));
  }

  @Override
  public boolean hasComparatorInputOverride(@Nonnull IBlockState state) {
    return state.getValue(BufferType.TYPE).hasInventory;
  }

}
