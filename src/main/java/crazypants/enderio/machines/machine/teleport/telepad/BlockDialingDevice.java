package crazypants.enderio.machines.machine.teleport.telepad;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.teleport.telepad.gui.ContainerDialingDevice;
import crazypants.enderio.machines.machine.teleport.telepad.gui.GuiDialingDevice;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTargetList;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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
    implements GuiID.IEioGuiHandler, ITileEntityProvider, IResourceTooltipProvider, IHaveRenderers {

  public static BlockDialingDevice create() {

    PacketHandler.INSTANCE.registerMessage(PacketTargetList.class, PacketTargetList.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketTargetList.class, PacketTargetList.class, PacketHandler.nextID(), Side.CLIENT);

    BlockDialingDevice ret = new BlockDialingDevice();
    ret.init();
    return ret;
  }

  public static final @Nonnull PropertyEnum<DialerFacing> FACING = PropertyEnum.create("facing", DialerFacing.class);

  public BlockDialingDevice() {
    super(MachineObject.block_dialing_device, TileDialingDevice.class);
    setLightOpacity(255);
    useNeighborBrightness = true;
    setDefaultState(blockState.getBaseState().withProperty(FACING, DialerFacing.UP_TONORTH));
  }

  @Override
  protected final void init() {
    super.init();
    GuiID.registerGuiHandler(GuiID.GUI_ID_TELEPAD_DIALING_DEVICE, this);
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
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileDialingDevice te = getTileEntity(world, pos);
    if (te == null) {
      return null;
    }
    return new ContainerDialingDevice(player.inventory, te);
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileDialingDevice te = getTileEntity(world, pos);
    if (te == null) {
      return null;
    }
    return new GuiDialingDevice(player.inventory, te);
  }

  @Override
  protected boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
    GuiID.GUI_ID_TELEPAD_DIALING_DEVICE.openGui(world, pos, entityPlayer, side);
    return true;
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    TileDialingDevice tile = getTileEntitySafe(worldIn, pos);
    if (tile != null) {
      DialerFacing facing = tile.getFacing();
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
