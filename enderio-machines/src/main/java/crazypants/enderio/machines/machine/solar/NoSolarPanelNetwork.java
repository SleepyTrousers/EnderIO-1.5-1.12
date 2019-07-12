package crazypants.enderio.machines.machine.solar;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class NoSolarPanelNetwork implements ISolarPanelNetwork {

  public static final @Nonnull ISolarPanelNetwork INSTANCE = new NoSolarPanelNetwork();

  private NoSolarPanelNetwork() {
  }

  @Override
  public boolean isValid() {
    return false;
  }

  @Override
  public void extractEnergy(int maxExtract) {
  }

  @Override
  public int getEnergyAvailableThisTick() {
    return 0;
  }

  @Override
  public int getEnergyAvailablePerTick() {
    return 0;
  }

  @Override
  public int getEnergyMaxPerTick() {
    return 0;
  }

  @Override
  public void destroyNetwork() {
  }

  @Override
  public @Nonnull Set<BlockPos> getPanels() {
    return Collections.emptySet();
  }

  @Override
  @Nonnull
  public NNList<ITextComponent> getConduitProbeInformation(@Nonnull EntityPlayer player, @Nullable EnumFacing side) {
    return NNList.emptyList();
  }

}
