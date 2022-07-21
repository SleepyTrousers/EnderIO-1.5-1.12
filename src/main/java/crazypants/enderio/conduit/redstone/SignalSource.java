package crazypants.enderio.conduit.redstone;

import java.util.HashSet;
import java.util.Set;
import net.minecraftforge.common.util.ForgeDirection;

public class SignalSource {

    public final Signal signal;

    public final ForgeDirection fromDirection;

    public SignalSource(Signal signal, ForgeDirection fromDirection) {
        this.signal = signal;
        this.fromDirection = fromDirection;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fromDirection == null) ? 0 : fromDirection.hashCode());
        result = prime * result + ((signal == null) ? 0 : signal.hashCode());
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
        SignalSource other = (SignalSource) obj;
        if (fromDirection != other.fromDirection) {
            return false;
        }
        if (signal == null) {
            if (other.signal != null) {
                return false;
            }
        } else if (!signal.equals(other.signal)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SignalSource [signal=" + signal + ", fromDirection=" + fromDirection + "]";
    }

    public static int[] toIntArray(Set<SignalSource> sources) {
        // Each source is 5 ints
        int[] result = new int[sources.size() * 5];
        int i = 0;
        for (SignalSource ss : sources) {
            result[i] = ss.signal.x;
            i++;
            result[i] = ss.signal.y;
            i++;
            result[i] = ss.signal.z;
            i++;
            result[i] = ss.signal.strength;
            i++;
            result[i] = ss.signal.color.ordinal();
            i++;
            result[i] = ss.fromDirection.ordinal();
            i++;
        }
        return result;
    }

    public static Set<SignalSource> fromIntArray(int[] enc) {
        Set<SignalSource> result = new HashSet<SignalSource>();
        for (int i = 0; i < enc.length; i += 5) {
            //      Signal sig = new Signal(enc[i], enc[i + 1], enc[i + 2], enc[i + 3], ForgeDirection.UNKNOWN,14,
            // SignalColor.values()[enc[i + 4]]);
            //      result.add(new SignalSource(sig, ForgeDirection.values()[enc[i + 4]]));
        }
        return result;
    }
}
