package crazypants.enderio.machine.solar;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.TileEntityEio;
import crazypants.enderio.integration.waila.IWailaNBTProvider;
import crazypants.enderio.item.PacketConduitProbe.IHasConduitProbeData;
import crazypants.enderio.power.ILegacyPoweredTile;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.enderio.power.forge.InternalPoweredTileWrapper;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import static crazypants.enderio.ModObject.blockSolarPanel;

@Storable
public class TileEntitySolarPanel extends TileEntityEio implements ILegacyPoweredTile, IWailaNBTProvider, IHasConduitProbeData {

  private boolean forceNetworkSearch = true;

  protected SolarPanelNetwork network = new SolarPanelNetwork();

  
  @Override
  public boolean canConnectEnergy(EnumFacing from) {
    return from == EnumFacing.DOWN;
  }

  @Override
  public int getEnergyStored(EnumFacing from) {
    return network.getEnergyAvailablePerTick();
  }

  @Override
  public int getMaxEnergyStored(EnumFacing facing) {
    return network.getEnergyMaxPerTick();
  }

  @Override
  public void setEnergyStored(int stored) {
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY ) {
      return facingIn == EnumFacing.DOWN;
    }
    return super.hasCapability(capability, facingIn);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return facingIn != EnumFacing.DOWN ? null : (T) new InternalPoweredTileWrapper(this, facingIn);
    }
    return super.getCapability(capability, facingIn);
  }
  
  @Override
  public void doUpdate() {
    if (!hasworld() || world.isRemote) {
      if (world.isRemote) {
        super.doUpdate(); // disable ticking on the client
      }
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
    return getEnergyPerTick(world, pos);
  }

  static int getEnergyPerTick(World world, BlockPos pos) {
    final IBlockState blockState = world.getBlockState(pos);
    if (blockState.getBlock() == blockSolarPanel.getBlock()) {
      return blockState.getValue(SolarType.KIND).getRfperTick();
    } else {
      return -1;
    }
  }

  float calculateLightRatio() {
    return calculateLightRatio(world);
  }

  boolean canSeeSun() {
    return canSeeSun(world, pos);
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

    lightValue = MathHelper.clamp(lightValue, 0, 15);
    return lightValue / 15f;
  }

  private void transmitEnergy() {
    IPowerInterface receptor = PowerHandlerUtil.getPowerInterface(world.getTileEntity(getPos().offset(EnumFacing.DOWN)), EnumFacing.UP);
    if (receptor != null) {
      int canTransmit = network.getEnergyAvailableThisTick(); // <-- potentially expensive operation
      if (canTransmit > 0) {
        network.extractEnergy(receptor.receiveEnergy(canTransmit, false));
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

  @Override
  public String[] getConduitProbeData() {
    return network.getConduitProbeData();
  }
}
