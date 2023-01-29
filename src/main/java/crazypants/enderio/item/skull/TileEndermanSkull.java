package crazypants.enderio.item.skull;

import net.minecraft.nbt.NBTTagCompound;

import crazypants.enderio.TileEntityEio;

public class TileEndermanSkull extends TileEntityEio {

    float yaw;

    @Override
    protected void writeCustomNBT(NBTTagCompound root) {
        root.setFloat("yaw", yaw);
    }

    @Override
    protected void readCustomNBT(NBTTagCompound root) {
        yaw = root.getFloat("yaw");
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Override
    public boolean shouldUpdate() {
        return false;
    }
}
