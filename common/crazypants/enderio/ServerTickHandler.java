package crazypants.enderio;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler {
  
  private static final EnumSet<TickType> TICKS =  EnumSet.of(TickType.SERVER);

  private final List<WeakReference<TickListener>> listeners = new CopyOnWriteArrayList<WeakReference<TickListener>>();
  
  public void addListener(TickListener listener) {
    listeners.add(new WeakReference<TickListener>(listener)); 
  }
  
  public void removeListener(TickListener listener) {
    WeakReference<TickListener> toRemove = null; 
    for(WeakReference<TickListener> ref : listeners) {
      if(ref.get() == listener) {
        toRemove = ref;
        break;
      }
    }
    if(toRemove != null) {
      listeners.remove(toRemove);
    }
  }
  
  
  @Override
  public void tickStart(EnumSet<TickType> type, Object... tickData) {
    List<TickListener> lists = processListeners();
    for(TickListener listener : lists) {
      listener.tickStart(type, tickData);
    }
  }

  @Override
  public void tickEnd(EnumSet<TickType> type, Object... tickData) {
    List<TickListener> lists = processListeners();
    for(TickListener listener : lists) {
      listener.tickEnd(type, tickData);
    }
  }
  
  private List<TickListener> processListeners() {
    List<TickListener> result = new ArrayList<TickListener>(listeners.size());
    List<WeakReference<TickListener>> toRemove = new ArrayList<WeakReference<TickListener>>();
    for(WeakReference<TickListener> ref : listeners) {
      TickListener l = ref.get();
      if(l == null) {
        toRemove.add(ref);
      } else {
        result.add(l);
      }
    }    
    return result;
  }
  
  public void serverStopped() {
    listeners.clear();    
  }

  @Override
  public EnumSet<TickType> ticks() {    
    return TICKS;
  }

  @Override
  public String getLabel() {
    return "EnderIO Server Tick Handler";
  }

  
}
