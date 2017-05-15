package crazypants.enderio.render.property;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.NNMap;

import net.minecraft.util.EnumFacing;

/**
 * A property for the IO overlays of the block sides. These are implemented in the MachineIO dummy block and can be pulled from there for rendering using these
 * blockstates.
 */
public class IOMode implements Comparable<IOMode> {

  private final @Nonnull EnumFacing direction;
  private final @Nonnull EnumIOMode iomode;

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

    public static final NNList<EnumIOMode> IOMODES = NNList.of(EnumIOMode.class);
  }

  private static final @Nonnull NNMap<String, IOMode> VALUES = new NNMap.Brutal<String, IOMode>();
  public static final NNList<IOMode> MODES = new NNList<>();

  public static final @Nonnull PropertyIO IO = PropertyIO.getInstance();

  static {
    NNIterator<EnumFacing> faces = NNList.FACING.iterator();
    while (faces.hasNext()) {
      EnumFacing facing = faces.next();
      NNIterator<EnumIOMode> iomodes = IOMode.EnumIOMode.IOMODES.iterator();
      while (iomodes.hasNext()) {
        MODES.add(IOMode.get(facing, iomodes.next()));
      }
    }
  }

  public static @Nonnull IOMode get(@Nonnull EnumFacing direction, @Nonnull EnumIOMode iomode) {
    String key = direction.toString().toLowerCase(Locale.US) + "_" + iomode.toString().toLowerCase(Locale.US);
    if (!VALUES.containsKey(key)) {
      IOMode result = new IOMode(direction, iomode);
      VALUES.put(key, result);
    }
    return VALUES.get(key);
  }

  private IOMode(@Nonnull EnumFacing direction, @Nonnull EnumIOMode iomode) {
    this.direction = direction;
    this.iomode = iomode;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + direction.hashCode();
    result = prime * result + iomode.hashCode();
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
  public @Nonnull String toString() {
    return direction.toString().toLowerCase(Locale.US) + "_" + iomode.toString().toLowerCase(Locale.US);
  }

  public @Nonnull EnumFacing getDirection() {
    return direction;
  }

  public @Nonnull EnumIOMode getIomode() {
    return iomode;
  }

}
