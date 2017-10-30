package crazypants.enderio.machine.solar;

import static crazypants.enderio.machine.MachineObject.blockSolarPanel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.conduitprobe.PacketConduitProbe.IHasConduitProbeData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SolarPanelNetwork implements IHasConduitProbeData {

  private Set<BlockPos> panels = new HashSet<BlockPos>();
  private World world = null;
  private boolean valid = false;

  private int energyMaxPerTick = 0;
  private int energyAvailablePerTick = 0;
  private int energyAvailableThisTick = 0;
  private long lastTick = -1, nextCollectTick = 0;

  public SolarPanelNetwork() {
    this((World) null);
  }

  public SolarPanelNetwork(World world) {
    this.world = world;
  }

  public SolarPanelNetwork(TileEntitySolarPanel panel) {
    this.world = panel.getWorld();
    this.valid = true;
  }

  void onUpdate(TileEntitySolarPanel panel, boolean force) {
    if (valid && (force || !contains(panel))) {
      panels.add(panel.getPos().toImmutable());
      nextCollectTick = 0;
      cleanupMemberlist();
    }
  }

  boolean contains(TileEntitySolarPanel panel) {
    if (world == null) {
      world = panel.getWorld();
    }
    return panels.contains(panel.getPos().toImmutable());
  }

  void cleanupMemberlist() {
    if (!panels.isEmpty()) {
      Iterator<BlockPos> iterator = panels.iterator();
      Set<BlockPos> candidates = new HashSet<BlockPos>();
      while (iterator.hasNext()) {
        BlockPos panel = iterator.next();
        if (world.isBlockLoaded(panel)) {
          TileEntity tileEntity = world.getTileEntity(panel);
          if (tileEntity instanceof TileEntitySolarPanel && !tileEntity.isInvalid() && tileEntity.hasWorld()) {
            if (((TileEntitySolarPanel) tileEntity).network == this) {
              for (EnumFacing neighborDir : EnumFacing.Plane.HORIZONTAL) {
                final BlockPos neighbor = panel.offset(neighborDir);
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
        List<BlockPos> candidateList = new ArrayList<BlockPos>(candidates);
        for (BlockPos candidate : candidateList) {
          if (!panels.contains(candidate) && canConnect(candidate)) {
            TileEntity tileEntity = world.getTileEntity(candidate);
            if (tileEntity instanceof TileEntitySolarPanel && !tileEntity.isInvalid() && tileEntity.hasWorld()) {
              panels.add(candidate.toImmutable());
              final SolarPanelNetwork otherNetwork = ((TileEntitySolarPanel) tileEntity).network;
              if (otherNetwork != this) {
                ((TileEntitySolarPanel) tileEntity).setNetwork(this);
                for (BlockPos other : otherNetwork.panels) {
                  if (!panels.contains(other) && world.isBlockLoaded(other)) {
                    candidates.add(other);
                  }
                }
                otherNetwork.destroyNetwork();
                for (EnumFacing neighborDir : EnumFacing.Plane.HORIZONTAL) {
                  final BlockPos neighbor = candidate.offset(neighborDir);
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
  }

  private boolean canConnect(BlockPos other) {
    if (Config.photovoltaicCanTypesJoins || panels.isEmpty()) {
      return true;
    }
    SolarType otherType = null;
    IBlockState otherState = world.getBlockState(other);
    if (otherState.getBlock() == blockSolarPanel.getBlock()) {
      otherType = otherState.getValue(SolarType.KIND);
    }
    for (BlockPos panel : panels) {
      if (world.isBlockLoaded(panel)) {
        IBlockState state = world.getBlockState(panel);
        if (state.getBlock() == blockSolarPanel.getBlock()) {
          return state.getValue(SolarType.KIND) == otherType;
        }
      }
    }
    return false;
  }

  /**
   * Actually destroys all references to this network, creating an invalid
   * network for each panel on this current one.
   */
  void destroyNetwork() {
    for (BlockPos panel : panels) {
      if (world.isBlockLoaded(panel)) {
        TileEntity tileEntity = world.getTileEntity(panel);
        if (tileEntity instanceof TileEntitySolarPanel && ((TileEntitySolarPanel) tileEntity).network == this) {
          ((TileEntitySolarPanel) tileEntity).setNetwork(new SolarPanelNetwork(world));
        }
      }
    }
    panels.clear();
    valid = false;
  }

  public boolean isValid() {
    return valid;
  }

  private int rfMax = -1;

  private void updateEnergy() {
    long tick = EnderIO.proxy.getServerTickCount();
    if (tick != lastTick && world != null) {
      lastTick = tick;
      if (tick > nextCollectTick) {
        nextCollectTick = tick + Config.photovoltaicRecalcSunTick;
        energyMaxPerTick = energyAvailablePerTick = 0;
        float lightRatio = TileEntitySolarPanel.calculateLightRatio(world);
        for (BlockPos panel : panels) {
          if (world.isBlockLoaded(panel)) {
            if (rfMax < 0 || Config.photovoltaicCanTypesJoins) {
              final int energyPerTick = TileEntitySolarPanel.getEnergyPerTick(world, panel);
              if (energyPerTick < 0) {
                // a panel was removed without its TE being invalidated. Force rebuilding the network.
                destroyNetwork();
                energyAvailableThisTick = energyAvailablePerTick = 0;
                return;
              }
              rfMax = energyPerTick;
            }
            energyMaxPerTick += rfMax;
            if (TileEntitySolarPanel.canSeeSun(world, panel)) {
              energyAvailablePerTick += rfMax * lightRatio;
            }
          }
        }
      }
      energyAvailableThisTick = energyAvailablePerTick;
    }
  }

  public void extractEnergy(int maxExtract) {
    energyAvailableThisTick = Math.max(energyAvailableThisTick - maxExtract, 0);
  }

  public int getEnergyAvailableThisTick() {
    updateEnergy();
    return energyAvailableThisTick;
  }

  public int getEnergyAvailablePerTick() {
    updateEnergy();
    return energyAvailablePerTick;
  }

  public int getEnergyMaxPerTick() {
    updateEnergy();
    return energyMaxPerTick;
  }

  @Nonnull
  @Override
  public String[] getConduitProbeData(@Nonnull EntityPlayer player, @Nullable EnumFacing side) {
    return new String[] { toString() };
  }

  @Override
  public String toString() {
    return "SolarPanelNetwork [panels=" + panels.size() + ", valid=" + valid + ", energyMaxPerTick=" + energyMaxPerTick + ", energyAvailablePerTick="
        + energyAvailablePerTick + ", energyAvailableThisTick=" + energyAvailableThisTick + ", lastTick=" + lastTick + ", nextCollectTick=" + nextCollectTick
        + ", rfMax=" + rfMax + "]";
  }

}
