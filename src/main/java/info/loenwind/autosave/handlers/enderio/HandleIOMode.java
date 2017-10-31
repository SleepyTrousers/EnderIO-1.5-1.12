package info.loenwind.autosave.handlers.enderio;

import crazypants.enderio.machine.modes.IoMode;
import info.loenwind.autosave.handlers.java.HandleAbstractEnum2EnumMap;
import net.minecraft.util.EnumFacing;

public class HandleIOMode extends HandleAbstractEnum2EnumMap<EnumFacing, IoMode> {

  public HandleIOMode() {
    super(EnumFacing.class, IoMode.class);
  }
}
