package crazypants.enderio.invpanel.invpanel;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.property.EnumRenderMode6;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.invpanel.init.InvpanelObject;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInventoryPanel extends AbstractMachineBlock<TileInventoryPanel> {

  private static final float BLOCK_SIZE = 4f / 16f;

  public static BlockInventoryPanel create(@Nonnull IModObject mo) {
    BlockInventoryPanel panel = new BlockInventoryPanel(mo);
    panel.init();
    return panel;
  }

  public BlockInventoryPanel(@Nonnull IModObject modObj) {
    super(InvpanelObject.blockInventoryPanel);
    modObj.apply(this);
    setShape(mkShape(BlockFaceShape.UNDEFINED));
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlockItemInventoryPanel(this));
  }

  @Override
  protected void initDefaultState() {
    setDefaultState(getBlockState().getBaseState().withProperty(EnumRenderMode6.RENDER, EnumRenderMode6.AUTO));
  }

  @Override
  protected void registerInSmartModelAttacher() {
    SmartModelAttacher.register(this, EnumRenderMode6.RENDER, EnumRenderMode6.DEFAULTS, EnumRenderMode6.AUTO);
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode6.RENDER });
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public boolean isBlockNormalCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    EnumFacing facing = getFacing(world, pos);
    return getBoundingBox(facing);
  }

  public AxisAlignedBB getBoundingBox(EnumFacing facing) {
    int x = 0;
    int y = 0;
    int z = 0;
    switch (facing) {
    case DOWN:
      return new AxisAlignedBB(x, y + (1 - BLOCK_SIZE), z, x + 1, y + 1, z + 1);
    case UP:
      return new AxisAlignedBB(x, y, z, x + 1, y + BLOCK_SIZE, z + 1);
    case NORTH:
      return new AxisAlignedBB(x, y, z + (1 - BLOCK_SIZE), x + 1, y + 1, z + 1);
    case SOUTH:
      return new AxisAlignedBB(x, y, z, x + 1, y + 1, z + BLOCK_SIZE);
    case WEST:
      return new AxisAlignedBB(x + (1 - BLOCK_SIZE), y, z, x + 1, y + 1, z + 1);
    case EAST:
      return new AxisAlignedBB(x, y, z, x + BLOCK_SIZE, y + 1, z + 1);
    default:
      return new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
    }
  }

  private EnumFacing getFacing(IBlockAccess world, BlockPos pos) {
    TileEntity te = getTileEntitySafe(world, pos);
    if (te instanceof TileInventoryPanel) {
      return ((TileInventoryPanel) te).getFacing();
    }
    return EnumFacing.NORTH;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileInventoryPanel te) {
    return new InventoryPanelContainer(player.inventory, te);
  }

  @Override
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileInventoryPanel te) {
    return new GuiInventoryPanel(te, new InventoryPanelContainer(player.inventory, te));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return InvPanelRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return InvPanelRenderMapper.instance;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileInventoryPanel tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

  @Override
  public boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
    return super.openGui(world, pos, entityPlayer, side);
  }

}
