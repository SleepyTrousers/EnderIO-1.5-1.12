package crazypants.enderio.render;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

import net.minecraft.util.EnumFacing;

/**
 * A property for the IO overlays of the block sides. These are implemented in the MachineIO dummy block and can be pulled from there for rendering using these
 * blockstates.
 */
public class IOMode implements Comparable<IOMode> {

  private final EnumFacing direction;
  private final EnumIOMode iomode;

  public static enum EnumIOMode {
    NONE,
    DISABLED,
    DISABLEDNOCENTER,
    DISABLEDSIDES,
    PULL,
    PULLSIDES,
    PULLTOPBOTTOM,
    PUSH,
    PUSHPULL,
    PUSHPULLSIDES,
    PUSHPULLTOPBOTTOM,
    PUSHSIDES,
    PUSHTOPBOTTOM,
    SELECTEDFACE,
    TRANSCIEVERDISABLED,
    TRANSCIEVERPULL,
    TRANSCIEVERPUSH,
    TRANSCIEVERPUSHPULL,
    CAPACITORBANK,
    CAPACITORBANKINPUT,
    CAPACITORBANKLOCKED,
    CAPACITORBANKOUTPUT,
    CAPACITORBANKINPUTSMALL,
    CAPACITORBANKLOCKEDSMALL,
    CAPACITORBANKOUTPUTSMALL,
    RESERVOIR;
  }

  private static final Map<String, IOMode> values = new HashMap<String, IOMode>();

  public static final PropertyIO IO = PropertyIO.getInstance();

  public static IOMode get(EnumFacing direction, EnumIOMode iomode) {
    String key = direction.toString().toLowerCase(Locale.US) + "_" + iomode.toString().toLowerCase(Locale.US);
    if (!values.containsKey(key)) {
      IOMode result = new IOMode(direction, iomode);
      values.put(key, result);
    }
    return values.get(key);
  }

  private IOMode(EnumFacing direction, EnumIOMode iomode) {
    this.direction = direction;
    this.iomode = iomode;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((direction == null) ? 0 : direction.hashCode());
    result = prime * result + ((iomode == null) ? 0 : iomode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IOMode other = (IOMode) obj;
    if (direction != other.direction)
      return false;
    if (iomode != other.iomode)
      return false;
    return true;
  }

  @Override
  public int compareTo(IOMode o) {
    int d = direction.compareTo(o.direction);
    if (d != 0) {
      return d;
    }
    return iomode.ordinal() < o.iomode.ordinal() ? -1 : iomode.ordinal() > o.iomode.ordinal() ? 1 : 0;
  }

  @Override
  public String toString() {
    return direction.toString().toLowerCase(Locale.US) + "_" + iomode.toString().toLowerCase(Locale.US);
  }

}
