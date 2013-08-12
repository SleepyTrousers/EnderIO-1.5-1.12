package crazypants.enderio;

import java.util.EnumSet;

import cpw.mods.fml.common.TickType;

public interface TickListener {

  void tickStart(EnumSet<TickType> type, Object... tickData);

  void tickEnd(EnumSet<TickType> type, Object... tickData);
  
}
