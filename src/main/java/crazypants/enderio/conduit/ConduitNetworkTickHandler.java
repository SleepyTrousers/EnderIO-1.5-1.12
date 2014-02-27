package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ConduitNetworkTickHandler implements ITickHandler {

  public static final ConduitNetworkTickHandler instance = new ConduitNetworkTickHandler();

  public static interface TickListener {
    public void tickStart(EnumSet<TickType> type, Object... tickData);

    public void tickEnd(EnumSet<TickType> type, Object... tickData);
  }

  private final List<TickListener> listeners = new ArrayList<TickListener>();

  public void addListener(TickListener listener) {
    listeners.add(listener);
  }

  public void removeListener(TickListener listener) {
    listeners.remove(listener);
  }

  @Override
  public void tickStart(EnumSet<TickType> type, Object... tickData) {
    for (TickListener h : listeners) {
      h.tickStart(type, tickData);
    }
  }

  @Override
  public void tickEnd(EnumSet<TickType> type, Object... tickData) {
    for (TickListener h : listeners) {
      h.tickEnd(type, tickData);
    }
    listeners.clear();
  }

  @Override
  public EnumSet<TickType> ticks() {
    return EnumSet.of(TickType.WORLD);
  }

  @Override
  public String getLabel() {
    return null;
  }

}
