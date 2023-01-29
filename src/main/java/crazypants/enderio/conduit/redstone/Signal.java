package crazypants.enderio.conduit.redstone;

import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.DyeColor;

public class Signal {

    public final int x;
    public final int y;
    public final int z;
    public final ForgeDirection dir;
    public final int strength;
    public final DyeColor color;

    public Signal(int x, int y, int z, ForgeDirection dir, int strength, DyeColor color) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dir = dir;
        this.strength = strength;
        this.color = color;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + ((dir == null) ? 0 : dir.hashCode());
        result = prime * result + strength;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Signal other = (Signal) obj;
        if (color != other.color) {
            return false;
        }
        if (dir != other.dir) {
            return false;
        }
        if (strength != other.strength) {
            return false;
        }
        if (x != other.x) {
            return false;
        }
        if (y != other.y) {
            return false;
        }
        if (z != other.z) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Signal [x=" + x
                + " y="
                + y
                + " z="
                + z
                + " side= "
                + dir
                + " strength="
                + strength
                + " color="
                + color
                + "]";
    }
}
