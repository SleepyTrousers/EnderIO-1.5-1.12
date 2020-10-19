package crazypants.enderio.util;

import java.util.Map;

/**
 * Marker interfaces to allow {@link Map}s that safely use different objects as the lookup keys. This requires those objects to implement
 * {@link #equals(Object)}/{@link #hashCode()} in a way to recognize each other. This interface doesn't magically make that happen.
 *
 */
public interface IMapKey {

}
