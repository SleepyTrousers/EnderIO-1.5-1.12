package crazypants.enderio.machine.farm;

import java.util.Random;

import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class BlockFarmStation extends AbstractMachineBlock<TileFarmStation> {

  public static BlockFarmStation create() {
    PacketHandler.INSTANCE.registerMessage(PacketFarmAction.class, PacketFarmAction.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketUpdateNotification.class, PacketUpdateNotification.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketFarmLockedSlot.class, PacketFarmLockedSlot.class, PacketHandler.nextID(), Side.SERVER);
    BlockFarmStation result = new BlockFarmStation();
    result.init();
    return result;
  }

  protected BlockFarmStation() {
    super(ModObject.blockFarmStation, TileFarmStation.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileFarmStation) {
      return new FarmStationContainer(player.inventory, (TileFarmStation) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileFarmStation) {
      return new GuiFarmStation(player.inventory, (TileFarmStation) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_FARM_STATATION;
  }

  // @Override
  // protected void registerOverlayIcons(IIconRegister iIconRegister) {
  // super.registerOverlayIcons(iIconRegister);
  // overlays = new IIcon[IoMode.values().length];
  // overlays[IoMode.PULL.ordinal()] =
  // iIconRegister.registerIcon("enderio:overlays/pullSides");
  // overlays[IoMode.PUSH.ordinal()] =
  // iIconRegister.registerIcon("enderio:overlays/pushSides");
  // overlays[IoMode.PUSH_PULL.ordinal()] =
  // iIconRegister.registerIcon("enderio:overlays/pushPullSides");
  // overlays[IoMode.DISABLED.ordinal()] =
  // iIconRegister.registerIcon("enderio:overlays/disabledSides");
  // }
  //
  // @Override
  // public IIcon getOverlayIconForMode(TileFarmStation tile, ForgeDirection
  // face, IoMode mode) {
  // if(face.offsetY != 0 || mode == IoMode.NONE) {
  // return super.getOverlayIconForMode(tile, face, mode);
  // }
  // return overlays[mode.ordinal()];
  // }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return getBackIconKey(active);
  }

  @Override
  protected String getSideIconKey(boolean active) {
    return super.getBackIconKey(active);
  }

  @Override
  protected String getModelIconKey(boolean active) {
    return "enderio:farmModel";
  }

  @Override
  public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    return true;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

}
