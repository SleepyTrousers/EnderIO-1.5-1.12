package crazypants.enderio.teleport.telepad;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.teleport.telepad.gui.ContainerDialingDevice;
import crazypants.enderio.teleport.telepad.gui.GuiDialingDevice;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class BlockDialingDevice extends BlockEio<TileDialingDevice> implements IGuiHandler, ITileEntityProvider {

  public static BlockDialingDevice create() {
    BlockDialingDevice ret = new BlockDialingDevice();
    ret.init();
    return ret;
  }

  public static PropertyEnum<DialerFacing> FACING = PropertyEnum.create("facing", DialerFacing.class);

  public BlockDialingDevice() {
    super(ModObject.blockDialingDevice.getUnlocalisedName(), TileDialingDevice.class);
    setLightOpacity(255);
    useNeighborBrightness = true;
    setDefaultState(blockState.getBaseState().withProperty(FACING, DialerFacing.UP_TONORTH));
  }

  @Override
  protected final void init() {
    super.init();
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TELEPAD_DIALING_DEVICE, this);
  }

  @Override
  protected ItemBlock createItemBlock() {
    return new BlockItemDialingDevice(this, getName());
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileDialingDevice();
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileDialingDevice te = getTileEntity(world, new BlockPos(x,y,z));
    if(te == null) {
      return null;
    }
    return new ContainerDialingDevice(player.inventory, te);
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileDialingDevice te = getTileEntity(world, new BlockPos(x,y,z));
    if(te == null) {
      return null;
    }
    return new GuiDialingDevice(player.inventory, te);
  }
  
  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    if (!world.isRemote) {
      entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_TELEPAD_DIALING_DEVICE, world, pos.getX(), pos.getY(), pos.getZ());
    }
    return true;
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    TileDialingDevice tile = getTileEntitySafe(worldIn, pos);
    if (tile != null) {
      DialerFacing facing = tile.getFacing();
      return state.withProperty(FACING, facing);
    } else {
      return state;
    }
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return 0;
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING);
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  public boolean isFullCube(IBlockState bs) {
    return false;
  }

}
