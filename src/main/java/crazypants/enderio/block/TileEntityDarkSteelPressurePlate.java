package crazypants.enderio.block;

import net.minecraft.nbt.NBTTagCompound;

import crazypants.enderio.machine.painter.TileEntityPaintedBlock;

public class TileEntityDarkSteelPressurePlate extends TileEntityPaintedBlock {

    private boolean silent;

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtRoot) {
        super.readCustomNBT(nbtRoot);
        silent = nbtRoot.getBoolean("silent");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtRoot) {
        super.writeCustomNBT(nbtRoot);
        nbtRoot.setBoolean("silent", silent);
    }
}
