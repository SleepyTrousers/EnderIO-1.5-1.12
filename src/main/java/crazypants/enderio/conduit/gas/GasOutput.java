package crazypants.enderio.conduit.gas;

import com.enderio.core.common.util.BlockCoord;
import net.minecraftforge.common.util.ForgeDirection;

public class GasOutput {

    final ForgeDirection dir;
    final BlockCoord location;

    public GasOutput(BlockCoord bc, ForgeDirection dir) {
        this.dir = dir;
        this.location = bc;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((dir == null) ? 0 : dir.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GasOutput other = (GasOutput) obj;
        if (location == null) {
            if (other.location != null) return false;
        } else if (!location.equals(other.location)) return false;
        if (dir != other.dir) return false;
        return true;
    }

    @Override
    public String toString() {
        return "GasOutput [dir=" + dir + ", location=" + location + "]";
    }
}
