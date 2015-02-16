package crazypants.util;

import net.minecraft.tileentity.TileEntity;

public interface IProgressTile {
  
  float getProgress();
  
  void setProgress(float progress);
  
  TileEntity getTileEntity();

}
