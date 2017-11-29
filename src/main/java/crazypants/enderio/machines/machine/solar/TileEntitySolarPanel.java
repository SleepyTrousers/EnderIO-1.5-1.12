package crazypants.enderio.machines.machine.solar;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.TileEntityEio;
import crazypants.enderio.item.conduitprobe.PacketConduitProbe.IHasConduitProbeData;
import crazypants.enderio.power.ILegacyPoweredTile;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.enderio.power.forge.InternalPoweredTileWrapper;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import static crazypants.enderio.machines.init.MachineObject.block_solar_panel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Storable
public class TileEntitySolarPanel extends TileEntityEio implements ILegacyPoweredTile, IHasConduitProbeData {

  @Store
  private boolean forceNetworkSearch = true;

  protected SolarPanelNetwork network = new SolarPanelNetwork();

  
  @Override
  public boolean canConnectEnergy(EnumFacing from) {
    return from == EnumFacing.DOWN;
  }

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
    if (!hasWorld() || world.isRemote) {
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
    if (blockState.getBlock() == block_solar_panel.getBlock()) {
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
  public boolean displayPower() {
    return true;
  }

  public void setNetwork(SolarPanelNetwork network) {
    this.network = network;
  }

  @Override
  public BlockPos getLocation() {
    return pos;
  }

  @Nonnull
  @Override
  public String[] getConduitProbeData(@Nonnull EntityPlayer player, @Nullable EnumFacing side) {
    return network.getConduitProbeData(player, side);
  }


}
