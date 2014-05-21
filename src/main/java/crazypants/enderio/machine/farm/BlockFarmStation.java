package crazypants.enderio.machine.farm;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;

public class BlockFarmStation extends AbstractMachineBlock<TileFarmStation> {

  public static BlockFarmStation create() {
    EnderIO.packetPipeline.registerPacket(PacketFarmAction.class);
    BlockFarmStation result = new BlockFarmStation();
    result.init();
    return result;
  }

  public static int renderId;

  protected BlockFarmStation() {
    super(ModObject.blockFarmStation, TileFarmStation.class);
    setBlockBounds(0, 0, 0, 1, 0.85f, 1);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileFarmStation) {
      return new FarmStationContainer(player.inventory, (TileFarmStation)te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileFarmStation) {
      return new GuiFarmStation(player.inventory, (TileFarmStation) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_FARM_STATATION;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return getBackIconKey(active);
  }

  @Override
  protected String getSideIconKey(boolean active) {
    return super.getBackIconKey(active);
  }

  @Override
  public int getRenderType() {
    return renderId;
  }

  @Override
  public int getLightValue(IBlockAccess world, int x, int y, int z) {
//    TileEntity te = world.getTileEntity(x, y, z);
//    if(te instanceof TileFarmStation) {
//      int res = ((TileFarmStation)te).isActive() ? 15 : 0;
//      return res;
//    }
    return 0;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  public IIcon getFrontIcon() {
    return iconBuffer[0][3];
  }

  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

}
