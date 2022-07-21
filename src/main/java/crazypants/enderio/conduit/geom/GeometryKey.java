package crazypants.enderio.conduit.geom;

import crazypants.enderio.conduit.IConduit;
import net.minecraftforge.common.util.ForgeDirection;

public final class GeometryKey {

    public final ForgeDirection dir;
    public final boolean isStub;
    public final Offset offset;
    public final String className;

    public GeometryKey(ForgeDirection dir, boolean isStub, Offset offset, Class<? extends IConduit> type) {
        this.dir = dir;
        this.isStub = isStub;
        this.offset = offset;
        className = type != null ? type.getName() : null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((dir == null) ? 0 : dir.hashCode());
        result = prime * result + (isStub ? 1231 : 1237);
        result = prime * result + ((offset == null) ? 0 : offset.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GeometryKey other = (GeometryKey) obj;
        if (className == null) {
            if (other.className != null) return false;
        } else if (!className.equals(other.className)) return false;
        if (dir != other.dir) return false;
        if (isStub != other.isStub) return false;
        if (offset != other.offset) return false;
        return true;
    }
}
