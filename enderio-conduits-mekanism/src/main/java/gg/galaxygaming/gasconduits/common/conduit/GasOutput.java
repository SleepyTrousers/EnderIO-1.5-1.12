package gg.galaxygaming.gasconduits.common.conduit;

import javax.annotation.Nonnull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class GasOutput {

    private final EnumFacing dir;
    private final BlockPos location;

    public GasOutput(@Nonnull BlockPos pos, @Nonnull EnumFacing dir) {
        this.dir = dir;
        this.location = pos;
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + location.hashCode();
        code = 31 * code + dir.hashCode();
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GasOutput other = (GasOutput) obj;
        return location.equals(other.location) && dir == other.dir;
    }

    public EnumFacing getDir() {
        return dir;
    }

    public BlockPos getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "GasOutput [dir=" + dir + ", location=" + location + "]";
    }
}