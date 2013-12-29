package crazypants.enderio.machine.monitor;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.util.DyeColor;

public class BlockPowerMonitor extends AbstractMachineBlock<TilePowerMonitor> implements ITileEntityProvider {

  public static BlockPowerMonitor create() {
    PacketHandler.instance.addPacketProcessor(new PowerMonitorPacketHandler());

    BlockPowerMonitor result = new BlockPowerMonitor();
    result.init();
    return result;
  }

  protected BlockPowerMonitor() {
    super(ModObject.blockPowerMonitor, TilePowerMonitor.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof TilePowerMonitor) {
      return new GuiPowerMonitor((TilePowerMonitor) te);
    }
    return null;

  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_POWER_MONITOR;
  }

  @Override
  public boolean canProvidePower() {
    return true;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return "enderio:powerMonitor";
  }

  @Override
  public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z,
      int side) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof TilePowerMonitor) {

      return ((TilePowerMonitor) te).getRednetOutputValue(ForgeDirection.values()[side], DyeColor.RED.ordinal());
    }
    return 0;
  }

  @Override
  public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side) {
    //    TileEntity te = world.getBlockTileEntity(x, y, z);
    //    if(te instanceof TilePowerMonitor) {
    //      return ((TilePowerMonitor) te).getRednetOutputValues(side);
    //    }
    return new int[16];
  }

  @Override
  public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet) {
    //    TileEntity te = world.getBlockTileEntity(x, y, z);
    //    if(te instanceof TilePowerMonitor) {
    //      return ((TilePowerMonitor) te).getRednetOutputValue(side, subnet);
    //    }
    return 0;
  }

  @Override
  public void onInputsChanged(World world, int x, int y, int z, ForgeDirection side, int[] inputValues) {
  }

  @Override
  public void onInputChanged(World world, int x, int y, int z, ForgeDirection side, int inputValue) {
  }

  @Override
  public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side) {
    return RedNetConnectionType.PlateSingle;
  }

}
