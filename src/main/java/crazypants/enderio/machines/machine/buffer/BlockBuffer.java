package crazypants.enderio.machines.machine.buffer;

import javax.annotation.Nonnull;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.property.EnumRenderMode;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBuffer extends AbstractMachineBlock<TileBuffer> implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockBuffer create(@Nonnull IModObject modObject) {
    PacketHandler.INSTANCE.registerMessage(PacketBufferIO.class, PacketBufferIO.class, PacketHandler.nextID(), Side.SERVER);
    BlockBuffer res = new BlockBuffer(modObject);
    res.init();
    return res;
  }

  private BlockBuffer(@Nonnull IModObject modObject) {
    super(modObject, TileBuffer.class);
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO).withProperty(BufferType.TYPE, BufferType.ITEM));
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
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
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(@Nonnull Item item, @Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    for (BufferType type : BufferType.values()) {
      list.add(BufferType.getStack(type));
    }
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileBuffer te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerBuffer(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileBuffer te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiBuffer(player.inventory, te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_BUFFER;
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

  @Override
  public int getComparatorInputOverride(@Nonnull IBlockState blockState1, @Nonnull World worldIn, @Nonnull BlockPos pos) {
    return Container.calcRedstone(getTileEntity(worldIn, pos));
  }

}
