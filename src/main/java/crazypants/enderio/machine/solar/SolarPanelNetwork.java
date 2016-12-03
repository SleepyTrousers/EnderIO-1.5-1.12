package crazypants.enderio.machine.solar;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.EnergyStorage;

import com.enderio.core.common.util.BlockCoord;
import com.google.common.collect.Lists;

public class SolarPanelNetwork {

  private List<BlockCoord> panels;
  private World world;
  private boolean empty = true;

  private EnergyStorage energy;

  public static final int ENERGY_PER = 10000;

  public static final EnumSet<ForgeDirection> VALID_CONS = EnumSet.of(ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST);

  public SolarPanelNetwork() {
    this((World) null);
  }

  public SolarPanelNetwork(World world) {
    panels = Lists.newArrayList();
    energy = new EnergyStorage(ENERGY_PER);
    this.world = world;
  }

  SolarPanelNetwork(TileEntitySolarPanel initial) {
    this(initial.getWorldObj());
    panels.add(initial.getLocation());
    energy.setEnergyStored(initial.getEnergyStored());
    empty = false;
  }

  void onUpdate(TileEntitySolarPanel panel) {
    if (world == null) {
      world = panel.getWorldObj();
    }
  }

  boolean contains(TileEntitySolarPanel panel) {
    if (world == null) {
      world = panel.getWorldObj();
    }
    return panels.contains(panel.getLocation());
  }

  boolean addToNetwork(TileEntitySolarPanel panel) {
    if (world == null) {
      world = panel.getWorldObj();
    }
    if (panel.network == this && isValid() && panels.contains(panel.getLocation())) {
      return false;
    }

    cleanupMemberlist();

    if(panel.network == null || !panel.network.isValid() || panel.network == this) {
      if (!panels.contains(panel.getLocation())) {
        panels.add(panel.getLocation());
      }
      panel.setNetwork(this);
      updateEnergy();
    } else {
      SolarPanelNetwork other = panel.network;
      for (BlockCoord otherPanel : other.panels) {
        TileEntity tileEntity = otherPanel.getTileEntity(world);
        if (tileEntity instanceof TileEntitySolarPanel) {
          panels.add(otherPanel);
          ((TileEntitySolarPanel) tileEntity).setNetwork(this);
        }
      }
      updateEnergy();
      this.energy.setEnergyStored(energy.getEnergyStored() + other.energy.getEnergyStored());
      other.invalidate();
    }

    empty = false;
    return true;
  }

  void cleanupMemberlist() {
    if (!panels.isEmpty()) {
      int energyPerPanel = energy.getEnergyStored() / panels.size();
      Iterator<BlockCoord> iterator = panels.iterator();
      while (iterator.hasNext()) {
        BlockCoord panel = iterator.next();
        boolean isGood = false;
        if (world.blockExists(panel.x, panel.y, panel.z)) {
          TileEntity tileEntity = panel.getTileEntity(world);
          if (tileEntity instanceof TileEntitySolarPanel) {
            if (((TileEntitySolarPanel) tileEntity).network == this) {
              isGood = true;
            }
          }
        }
        if (!isGood) {
          iterator.remove();
          energy.extractEnergy(energyPerPanel, false);
          // The missing panel should have saved its energy (nbt). So try not to
          // dupe it. Which won't work at all because panels in different chunks
          // will be saved at different times.
        }
      }
    }
  }

  void removeFromNetwork(TileEntitySolarPanel panel) {
    if (world == null) {
      world = panel.getWorldObj();
    }
    // build list of formerly connected neighbors
    List<TileEntitySolarPanel> neighbors = Lists.newArrayList();
    for (ForgeDirection dir : VALID_CONS) {
      TileEntity te = panel.getLocation().getLocation(dir).getTileEntity(world);
      if(te != null && te instanceof TileEntitySolarPanel) {
        neighbors.add((TileEntitySolarPanel) te);
      }
    }

    if (!neighbors.isEmpty()) {
      // distribute power from split networks evenly to neighbors
      int dist = energy.getEnergyStored() / neighbors.size();
      for (TileEntitySolarPanel te : neighbors) {
        te.destroyedNetworkBuffer = new EnergyStorage(dist);
        te.destroyedNetworkBuffer.setEnergyStored(dist);
      }
      energy.setEnergyStored(0);
    }

    // allow solars to reform networks
    destroyNetwork();
  }

  private void updateEnergy() {
    energy.setCapacity(ENERGY_PER * panels.size());
    energy.setMaxExtract(energy.getMaxEnergyStored());
  }

  /**
   * Actually destroys all references to this network, creating an invalid
   * network for each panel on this current one.
   */
  void destroyNetwork() {
    for (BlockCoord panel : panels) {
      TileEntity tileEntity = panel.getTileEntity(world);
      if (tileEntity instanceof TileEntitySolarPanel) {
        ((TileEntitySolarPanel) tileEntity).setNetwork(new SolarPanelNetwork(world));
      }
    }
    invalidate();
  }

  /**
   * Does nothing but clear this network, other panels may still hold a
   * reference to this network.
   */
  private void invalidate() {
    panels.clear();
    empty = true;
  }

  public boolean isValid() {
    return !empty;
  }

  public int extractEnergy(int maxExtract, boolean simulate) {
    return isValid() ? energy.extractEnergy(maxExtract, simulate) : 0;
  }

  public int getEnergyStored() {
    return energy.getEnergyStored();
  }

  public int setEnergyStored(int energy) {
    if(isValid()) {
      this.energy.setEnergyStored(energy);
    }
    return this.energy.getEnergyStored();
  }

  public int getMaxEnergyStored() {
    return energy.getMaxEnergyStored();
  }

  public int getMaxEnergyExtracted() {
    return energy.getMaxExtract();
  }

  public void addBuffer(EnergyStorage destroyedNetworkBuffer) {
    energy.receiveEnergy(destroyedNetworkBuffer.getEnergyStored(), false);
  }

  boolean shouldSave(TileEntitySolarPanel panel) {
    return panels.size() > 0;
  }

  public int size() {
    return panels.size();
  }

  void writeToNBT(NBTTagCompound tag) {
    tag.setInteger("Energy", energy.getEnergyStored() / panels.size());
  }

  void writeToNBTAll(NBTTagCompound tag) {
    tag.setInteger("Energy", energy.getEnergyStored());
  }

  void readFromNBT(TileEntitySolarPanel panel, NBTTagCompound tag) {
    if (tag.hasKey("Energy")) {
      addToNetwork(panel);
      int amount = tag.getInteger("Energy");
      energy.receiveEnergy(amount, false);
    }
  }

  @Override
  public String toString() {
    return super.toString() + " [size=" + size() + "]";
  }
}
