package crazypants.enderio.machine.solar;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.EnergyStorage;

import com.google.common.collect.Lists;
import crazypants.enderio.config.Config;

public class SolarPanelNetwork {

    private List<TileEntitySolarPanel> panels;
    private boolean empty = true;

    private EnergyStorage energy;

    public static final int ENERGY_PER = 10000;

    public static final EnumSet<ForgeDirection> VALID_CONS = EnumSet
            .of(ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST);

    public SolarPanelNetwork() {
        panels = Lists.newArrayList();
        energy = new EnergyStorage(getCapacity());
    }

    SolarPanelNetwork(TileEntitySolarPanel initial) {
        this();
        panels.add(initial);
        energy.setCapacity(getCapacity(initial, 1));
        empty = false;
    }

    void onUpdate(TileEntitySolarPanel panel) {}

    boolean addToNetwork(TileEntitySolarPanel panel) {
        if (panel.network == this && panel.network.isValid() && panels.contains(panel)) {
            return false;
        }

        if (panel.network == null || !panel.network.isValid() || panel.network == this) {
            if (!panels.contains(panel)) {
                panels.add(panel);
            }
            panel.setNetwork(this);
            updateEnergy();
        } else {
            SolarPanelNetwork other = panel.network;
            for (TileEntitySolarPanel otherPanel : other.panels) {
                panels.add(otherPanel);
                otherPanel.setNetwork(this);
            }
            updateEnergy();
            this.energy.setEnergyStored(energy.getEnergyStored() + other.energy.getEnergyStored());
            other.invalidate();
        }

        empty = false;
        return true;
    }

    void removeFromNetwork(TileEntitySolarPanel panel) {
        // build list of formerly connected neighbors
        List<TileEntitySolarPanel> neighbors = Lists.newArrayList();
        for (ForgeDirection dir : VALID_CONS) {
            TileEntity te = panel.getLocation().getLocation(dir).getTileEntity(panel.getWorldObj());
            if (te != null && te instanceof TileEntitySolarPanel) {
                neighbors.add((TileEntitySolarPanel) te);
            }
        }

        // distribute power from split networks evenly to neighbors
        for (TileEntitySolarPanel te : neighbors) {
            int dist = energy.getEnergyStored() / neighbors.size();
            te.destroyedNetworkBuffer = new EnergyStorage(dist);
            te.destroyedNetworkBuffer.setEnergyStored(dist);
        }

        // allow solars to reform networks
        destroyNetwork();
    }

    private int getCapacity() {
        int capacity = ENERGY_PER;

        if (panels.size() > 0) {
            TileEntitySolarPanel masterPanel = getMaster();
            capacity = getCapacity(masterPanel, panels.size());
        }

        return capacity;
    }

    private static int getCapacity(TileEntitySolarPanel panel, int panelsCount) {
        int capacity = ENERGY_PER;

        if (panel != null && panel.hasWorldObj()) {
            int meta = panel.getBlockMetadata();
            switch (meta) {
                case 0: // Default
                    capacity = Config.photovoltaicCellCapacityRF;
                    break;
                case 1: // Advanced
                    capacity = Config.photovoltaicAdvancedCellCapacityRF;
                    break;
                case 2: // Vibrant
                    capacity = Config.photovoltaicVibrantCellCapacityRF;
                    break;
            }
        }

        capacity = capacity * panelsCount;

        return capacity;
    }

    private void updateEnergy() {
        energy.setCapacity(getCapacity());
        energy.setMaxExtract(energy.getMaxEnergyStored());
    }

    /**
     * Actually destroys all references to this network, creating an invalid network for each panel on this current one.
     */
    void destroyNetwork() {
        for (TileEntitySolarPanel te : panels) {
            te.setNetwork(new SolarPanelNetwork());
        }
        invalidate();
    }

    /**
     * Does nothing but clear this network, other panels may still hold a reference to this network.
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
        if (isValid()) {
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

    TileEntitySolarPanel getMaster() {
        return panels.get(0);
    }

    boolean shouldSave(TileEntitySolarPanel panel) {
        return getMaster() == panel;
    }

    public int size() {
        return panels.size();
    }

    void writeToNBT(NBTTagCompound tag) {
        tag.setBoolean("validSolar", true);
        energy.writeToNBT(tag);
    }

    void readFromNBT(TileEntitySolarPanel panel, NBTTagCompound tag) {
        if (tag.getBoolean("validSolar")) {
            energy.readFromNBT(tag);
            addToNetwork(panel);
        }
    }
}
