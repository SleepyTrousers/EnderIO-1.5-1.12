package crazypants.enderio.machines.machine.solar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.item.conduitprobe.PacketConduitProbe.IHasConduitProbeData;
import crazypants.enderio.base.power.ILegacyPoweredTile;
import crazypants.enderio.base.power.IPowerInterface;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.base.power.forge.InternalPoweredTileWrapper;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import static crazypants.enderio.machines.init.MachineObject.block_solar_panel;

@Storable
public class TileSolarPanel extends TileEntityEio implements ILegacyPoweredTile, IHasConduitProbeData {

  protected ISolarPanelNetwork network = NoSolarPanelNetwork.INSTANCE;

  @Override
  public boolean canConnectEnergy(@Nonnull EnumFacing from) {
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

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return facingIn == EnumFacing.DOWN ? (T) new InternalPoweredTileWrapper(this, facingIn) : null;
    }
    return super.getCapability(capability, facingIn);
  }

  private int idleCounter = 0;

  @Override
  public void doUpdate() {
    if (!hasWorld()) {
      return;
    }
    if (world.isRemote) {
      super.doUpdate(); // disable ticking on the client
      return;
    }

    if (!network.isValid()) {
      SolarPanelNetwork.build(this);
    }

    if (idleCounter > 0) {
      idleCounter--;
      return;
    }

    IPowerInterface receptor = PowerHandlerUtil.getPowerInterface(world.getTileEntity(getPos().offset(EnumFacing.DOWN)), EnumFacing.UP);
    if (receptor != null && receptor.receiveEnergy(1, true) > 0) {
      int canTransmit = network.getEnergyAvailableThisTick(); // <-- potentially expensive operation
      if (canTransmit > 0) {
        network.extractEnergy(receptor.receiveEnergy(canTransmit, false));
      } else {
        idleCounter = world.rand.nextInt(32);
      }
    } else {
      idleCounter = world.rand.nextInt(256);
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

  static int getEnergyPerTick(@Nonnull World world, @Nonnull BlockPos pos) {
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

  @Override
  public boolean displayPower() {
    return true;
  }

  public void setNetwork(ISolarPanelNetwork network) {
    this.network = network;
  }

  @Override
  public @Nonnull BlockPos getLocation() {
    return pos;
  }

  @Nonnull
  @Override
  public String[] getConduitProbeData(@Nonnull EntityPlayer player, @Nullable EnumFacing side) {
    return network.getConduitProbeData(player, side);
  }

}
