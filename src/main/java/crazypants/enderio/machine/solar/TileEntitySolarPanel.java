package crazypants.enderio.machine.solar;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.enderio.waila.IWailaNBTProvider;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

@Storable
public class TileEntitySolarPanel extends TileEntityEio implements IInternalPoweredTile, IWailaNBTProvider {

  private boolean forceNetworkSearch = true;

  protected SolarPanelNetwork network = new SolarPanelNetwork();

  
  @Override
  public boolean canConnectEnergy(EnumFacing from) {
    return from == EnumFacing.DOWN;
  }

  //RF
//  @Override
//  public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
//    return 0;
//  }
//
//  @Override
//  public int getEnergyStored(EnumFacing from) {
//    return getEnergyStored();
//  }
//
//  @Override
//  public int getMaxEnergyStored(EnumFacing from) {
//    return getMaxEnergyStored();
//  }
 
  @Override
  public int getEnergyStored() {
    return network.getEnergyAvailablePerTick();
  }

  @Override
  public int getMaxEnergyStored() {
    return network.getEnergyMaxPerTick();
  }

  @Override
  public void setEnergyStored(int stored) {
  }

  @Override
  public void doUpdate() {
    if (!hasWorldObj() || worldObj.isRemote) {
      return;
    }

    if (!network.isValid()) {
      network = new SolarPanelNetwork(this);
    }

    network.onUpdate(this, forceNetworkSearch);
    forceNetworkSearch = false;

    if (network.isValid()) {
      transmitEnergy();
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();
    network.destroyNetwork();
  }

  int getEnergyPerTick() {
    return getEnergyPerTick(worldObj, pos);
  }

  static int getEnergyPerTick(World world, BlockPos pos) {
    final IBlockState blockState = world.getBlockState(pos);
    if (blockState.getBlock() == EnderIO.blockSolarPanel) {
      return blockState.getValue(SolarType.KIND).getRfperTick();
    } else {
      return -1;
    }
  }

  float calculateLightRatio() {
    return calculateLightRatio(worldObj);
  }

  boolean canSeeSun() {
    return canSeeSun(worldObj, pos);
  }

  static boolean canSeeSun(World world, BlockPos pos) {
    return world.canBlockSeeSky(pos.up());
  }

  public static float calculateLightRatio(World world) {
    int lightValue = EnumSkyBlock.SKY.defaultLightValue - world.getSkylightSubtracted();
    float sunAngle = world.getCelestialAngleRadians(1.0F);

    if (sunAngle < (float) Math.PI) {
      sunAngle += (0.0F - sunAngle) * 0.2F;
    } else {
      sunAngle += (((float) Math.PI * 2F) - sunAngle) * 0.2F;
    }

    lightValue = Math.round(lightValue * MathHelper.cos(sunAngle));

    lightValue = MathHelper.clamp_int(lightValue, 0, 15);
    return lightValue / 15f;
  }

  private void transmitEnergy() {
    IPowerInterface receptor = PowerHandlerUtil.create(worldObj.getTileEntity(getPos().offset(EnumFacing.DOWN)));
    if (receptor != null) {
      int canTransmit = network.getEnergyAvailableThisTick(); // <-- potentially expensive operation
      if (canTransmit > 0) {
        network.extractEnergy(receptor.recieveEnergy(EnumFacing.UP, canTransmit));
      }
    }
  }

  @Override
  public void readCustomNBT(NBTTagCompound tag) {
    super.readCustomNBT(tag);
    forceNetworkSearch = true;
  }

  @Override
  public void writeCustomNBT(NBTTagCompound tag) {
    super.writeCustomNBT(tag);
    if (network.isValid()) {
      tag.setInteger("rfCap", network.getEnergyMaxPerTick()); // for WAILA
    }
  }

  @Override
  public boolean displayPower() {
    return true;
  }

  public void setNetwork(SolarPanelNetwork network) {
    this.network = network;
  }

  @Override
  public void getData(NBTTagCompound tag) {
    if (network.isValid()) {
      writeToNBT(tag);
    }
  }

  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(pos);
  }
}
