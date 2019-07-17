package crazypants.enderio.machines.machine.solar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.fluid.BlockFluidEio;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.item.conduitprobe.PacketConduitProbe.IHasConduitProbeData;
import crazypants.enderio.base.power.IPowerInterface;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.base.power.forge.tile.ILegacyPoweredTile;
import crazypants.enderio.base.power.forge.tile.InternalGeneratorTileWrapper;
import crazypants.enderio.machines.config.config.SolarConfig;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

import static crazypants.enderio.machines.init.MachineObject.block_solar_panel;

@Storable
public class TileSolarPanel extends TileEntityEio implements ILegacyPoweredTile, IHasConduitProbeData {

  protected ISolarPanelNetwork network = NoSolarPanelNetwork.INSTANCE;

  public TileSolarPanel() {
    addICap(CapabilityEnergy.ENERGY, facing -> facing == EnumFacing.DOWN ? InternalGeneratorTileWrapper.get(this, facing) : null);
  }

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

  static boolean isPowered(@Nonnull World world, BlockPos pos) {
    return isSolarPowered(world, pos) || isArtificiallyPowered(world, pos);
  }

  static boolean isSolarPowered(@Nonnull World world, BlockPos pos) {
    return world.canBlockSeeSky(pos.up());
  }

  static boolean isArtificiallyPowered(@Nonnull World world, BlockPos pos) {
    // TODO 1.14: Change to check if panel is waterlogged by liquid sunshine
    return SolarConfig.solarPoweredBySunshine.get() && world.getBlockState(pos.up()).getBlock() == Fluids.LIQUID_SUNSHINE.getBlock();
  }

  static float calculateLocalLightRatio(@Nonnull World world, BlockPos pos, float baseRatio) {
    float ratio = isSolarPowered(world, pos) ? baseRatio : 0;
    if (ratio < 1 && SolarConfig.solarPoweredBySunshine.get()) {
      IBlockState blockState = world.getBlockState(pos.up());
      if (blockState.getBlock() == Fluids.LIQUID_SUNSHINE.getBlock()) {
        @SuppressWarnings("null")
        float value = ((BlockFluidEio.LiquidSunshine) Fluids.LIQUID_SUNSHINE.getBlock()).getScaledLevel(blockState, world, pos.up());
        ratio = Math.max(value, ratio);
      }
    }
    return ratio;
  }

  public static float calculateLightRatio(@Nonnull World world) {
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

  @Override
  @Nonnull
  public NNList<ITextComponent> getConduitProbeInformation(@Nonnull EntityPlayer player, @Nullable EnumFacing side) {
    return network.getConduitProbeInformation(player, side);
  }

}
