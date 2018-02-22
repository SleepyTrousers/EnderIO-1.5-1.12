package crazypants.enderio.machines.machine.teleport.telepad;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.machines.machine.teleport.telepad.gui.ContainerDialingDevice;
import crazypants.enderio.machines.machine.teleport.telepad.gui.GuiDialingDevice;
import crazypants.enderio.machines.machine.teleport.telepad.gui.GuiDialingDeviceNoTelepad;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDialingDevice extends BlockEio<TileDialingDevice>
    implements IEioGuiHandler.WithPos, ITileEntityProvider, IResourceTooltipProvider, IHaveRenderers {

  public static BlockDialingDevice create(@Nonnull IModObject modObject) {

    BlockDialingDevice ret = new BlockDialingDevice(modObject);
    ret.init();
    return ret;
  }

  public static final @Nonnull PropertyEnum<DialerFacing> FACING = PropertyEnum.create("facing", DialerFacing.class);

  public BlockDialingDevice(@Nonnull IModObject modObject) {
    super(modObject);
    setLightOpacity(255);
    useNeighborBrightness = true;
    setDefaultState(blockState.getBaseState().withProperty(FACING, DialerFacing.UP_TONORTH));
    setShape(mkShape(BlockFaceShape.UNDEFINED)); // TODO 'bottom' side SOLID
  }

  @Override
  public ItemBlock createBlockItem(@Nonnull IModObject mo) {
    return mo.apply(new BlockItemDialingDevice(this));
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    return new TileDialingDevice();
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1) {
    TileDialingDevice te = getTileEntity(world, pos);
    if (te == null) {
      return null;
    }
    return new ContainerDialingDevice(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1) {
    TileDialingDevice te = getTileEntity(world, pos);
    if (te == null) {
      return null;
    }
    TileTelePad telepad = te.findTelepad();
    return telepad == null ? new GuiDialingDeviceNoTelepad(player.inventory, te) : new GuiDialingDevice(player.inventory, te, telepad);
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    TileDialingDevice tile = getTileEntitySafe(worldIn, pos);
    if (tile != null) {
      DialerFacing facing = tile.getDialerFacing();
      return state.withProperty(FACING, facing);
    } else {
      return state;
    }
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return 0;
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING);
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject mo) {
    ClientUtil.registerDefaultItemRenderer(mo);
  }

}
