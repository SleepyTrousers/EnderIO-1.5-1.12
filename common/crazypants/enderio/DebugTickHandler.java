package crazypants.enderio;

import java.util.EnumSet;
import java.util.LinkedList;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class DebugTickHandler implements ITickHandler {

  long tickStart = -1;
  
  LinkedList<Entry> serverTicks;
  
  @Override
  public void tickStart(EnumSet<TickType> type, Object... tickData) {
    tickStart = System.nanoTime();
    if(type.contains(TickType.SERVER)) {
      
    }
    
  }

  @Override
  public void tickEnd(EnumSet<TickType> type, Object... tickData) {    
    long tickDuration = System.nanoTime() - tickStart;
  }

  @Override
  public EnumSet<TickType> ticks() {  
    return EnumSet.of(TickType.SERVER, TickType.WORLD);
  }

  @Override
  public String getLabel() {
    // TODO Auto-generated method stub
    return null;
  }
  
  private static class Entry {
    
    long atTimeMilli;
    long tickDurationNano;
    
    Entry(long atTimeMilli, long tickDurationNano) {    
      this.atTimeMilli = atTimeMilli;
      this.tickDurationNano = tickDurationNano;
    }
    
    
  }

}
