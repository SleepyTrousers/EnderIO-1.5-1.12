package crazypants.enderio.machines.machine.solar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.machines.config.config.SolarConfig;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import static crazypants.enderio.machines.init.MachineObject.block_solar_panel;

public class SolarPanelNetwork implements ISolarPanelNetwork {

  private final @Nonnull Set<BlockPos> panels = new HashSet<BlockPos>();
  private final @Nonnull World world;
  private boolean valid = false;

  private int energyMaxPerTick = 0;
  private int energyAvailablePerTick = 0;
  private int energyAvailableThisTick = 0;
  private long lastTick = -1, nextCollectTick = 0;

  public static void build(@Nonnull TileSolarPanel panel) {
    new SolarPanelNetwork(panel).isValid();
  }

  private SolarPanelNetwork(@Nonnull TileSolarPanel panel) {
    this.world = panel.getWorld();
    this.valid = true;
    panels.add(panel.getPos().toImmutable());
    panel.setNetwork(this);
    nextCollectTick = 0;
    cleanupMemberlist();
  }

  @Override
  public @Nonnull Set<BlockPos> getPanels() {
    return panels;
  }

  void cleanupMemberlist() {
    if (!panels.isEmpty()) {
      Iterator<BlockPos> iterator = panels.iterator();
      Set<BlockPos> candidates = new HashSet<BlockPos>();
      while (iterator.hasNext()) {
        BlockPos panel = iterator.next();
        if (panel != null && world.isBlockLoaded(panel)) {
          TileEntity tileEntity = world.getTileEntity(panel);
          if (tileEntity instanceof TileSolarPanel && !tileEntity.isInvalid() && tileEntity.hasWorld()) {
            if (((TileSolarPanel) tileEntity).network == this) {
              for (NNIterator<EnumFacing> facings = NNList.FACING_HORIZONTAL.fastIterator(); facings.hasNext();) {
                final BlockPos neighbor = panel.offset(facings.next());
                if (!panels.contains(neighbor) && world.isBlockLoaded(neighbor)) {
                  candidates.add(neighbor);
                }
              }
              continue;
            }
          }
        }
        iterator.remove();
      }
      while (!candidates.isEmpty()) {
        NNList<BlockPos> candidateList = new NNList<BlockPos>(candidates);
        for (NNIterator<BlockPos> candidateItr = candidateList.iterator(); candidateItr.hasNext();) {
          BlockPos candidate = candidateItr.next();
          if (!panels.contains(candidate) && canConnect(candidate)) {
            TileEntity tileEntity = world.getTileEntity(candidate);
            if (tileEntity instanceof TileSolarPanel && !tileEntity.isInvalid() && tileEntity.hasWorld()) {
              panels.add(candidate.toImmutable());
              final ISolarPanelNetwork otherNetwork = ((TileSolarPanel) tileEntity).network;
              if (otherNetwork != this) {
                ((TileSolarPanel) tileEntity).setNetwork(this);
                for (BlockPos other : otherNetwork.getPanels()) {
                  if (other != null && !panels.contains(other) && world.isBlockLoaded(other)) {
                    candidates.add(other);
                  }
                }
                otherNetwork.destroyNetwork();
                for (NNIterator<EnumFacing> facings = NNList.FACING_HORIZONTAL.fastIterator(); facings.hasNext();) {
                  final BlockPos neighbor = candidate.offset(facings.next());
                  if (!panels.contains(neighbor) && world.isBlockLoaded(neighbor)) {
                    candidates.add(neighbor);
                  }
                }
              }
            }
          }
          candidates.remove(candidate);
        }
      }
    }
    if (panels.isEmpty()) {
      destroyNetwork();
    }
  }

  private boolean canConnect(@Nonnull BlockPos other) {
    if (SolarConfig.canSolarTypesJoin.get() || panels.isEmpty()) {
      return true;
    }
    IBlockState otherState = world.getBlockState(other);
    if (otherState.getBlock() == block_solar_panel.getBlock()) {
      for (BlockPos panel : panels) {
        if (panel != null && world.isBlockLoaded(panel)) {
          IBlockState state = world.getBlockState(panel);
          if (state.getBlock() == block_solar_panel.getBlock()) {
            return state.getValue(SolarType.KIND).connectTo(otherState.getValue(SolarType.KIND));
          }
        }
      }
    }
    return false;
  }

  @Override
  public void destroyNetwork() {
    energyMaxPerTick = energyAvailablePerTick = energyAvailableThisTick = 0;
    nextCollectTick = Long.MAX_VALUE;
    panels.clear();
    valid = false;
  }

  @Override
  public boolean isValid() {
    return valid;
  }

  /**
   * If different kinds of panels can not joint together, this is the amount of energy any panel of this network can produce per tick.
   * 
   * Otherwise it is just a ever changing local value.
   */
  private int energyMaxPerTickPerPanel = -1;

  private void updateEnergy() {
    long tick = EnderIO.proxy.getServerTickCount();
    if (tick != lastTick) {
      lastTick = tick;
      if (tick > nextCollectTick) {
        nextCollectTick = tick + SolarConfig.solarRecalcSunTick.get();
        energyMaxPerTick = energyAvailablePerTick = 0;
        float lightRatio = TileSolarPanel.calculateLightRatio(world);
        for (BlockPos panel : panels) {
          if (panel != null && world.isBlockLoaded(panel)) {
            if (energyMaxPerTickPerPanel < 0 || SolarConfig.canSolarTypesJoin.get()) {
              energyMaxPerTickPerPanel = TileSolarPanel.getEnergyPerTick(world, panel);
              if (energyMaxPerTickPerPanel < 0) {
                // a panel was removed without its TE being invalidated. Force rebuilding the network.
                destroyNetwork();
                return;
              }
            }
            energyMaxPerTick += energyMaxPerTickPerPanel;
            energyAvailablePerTick += energyMaxPerTickPerPanel * TileSolarPanel.calculateLocalLightRatio(world, panel, lightRatio);
          }
        }
      }
      energyAvailableThisTick = energyAvailablePerTick;
    }
  }

  @Override
  public void extractEnergy(int maxExtract) {
    energyAvailableThisTick = Math.max(energyAvailableThisTick - maxExtract, 0);
  }

  @Override
  public int getEnergyAvailableThisTick() {
    updateEnergy();
    return energyAvailableThisTick;
  }

  @Override
  public int getEnergyAvailablePerTick() {
    updateEnergy();
    return energyAvailablePerTick;
  }

  @Override
  public int getEnergyMaxPerTick() {
    updateEnergy();
    return energyMaxPerTick;
  }

  @Nonnull
  @Override
  public String[] getConduitProbeData(@Nonnull EntityPlayer player, @Nullable EnumFacing side) {
    return new String[0];
  }

  @Override
  @Nonnull
  public NNList<ITextComponent> getConduitProbeInformation(@Nonnull EntityPlayer player, @Nullable EnumFacing side) {
    NNList<ITextComponent> result = new NNList<>();
    ITextComponent text = Lang.PROBE_SOLAR_LAST.toChatServer(EnderIO.proxy.getServerTickCount() - lastTick);
    result.add(Lang.PROBE_SOLAR_PROD.toChatServer(panels.size(), getEnergyAvailablePerTick(), getEnergyMaxPerTick()));
    result.add(text);
    for (BlockPos panel : panels) {
      if (!TileSolarPanel.isPowered(world, panel)) {
        result.add(Lang.PROBE_SOLAR_NOSUN.toChatServer(BlockCoord.chatString(panel, TextFormatting.RESET)));
      }
    }
    if (result.size() == 2) {
      result.add(Lang.PROBE_SOLAR_ALLSUN.toChatServer());
    }
    return result;
  }

  @Override
  public String toString() {
    return "SolarPanelNetwork [panels=" + panels.size() + ", valid=" + valid + ", energyMaxPerTick=" + energyMaxPerTick + ", energyAvailablePerTick="
        + energyAvailablePerTick + ", energyAvailableThisTick=" + energyAvailableThisTick + ", lastTick=" + lastTick + ", nextCollectTick=" + nextCollectTick
        + ", rfMax=" + energyMaxPerTickPerPanel + "]";
  }

}
