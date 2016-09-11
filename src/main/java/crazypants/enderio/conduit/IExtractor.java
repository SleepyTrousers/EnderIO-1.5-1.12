package crazypants.enderio.conduit;

import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.machine.RedstoneControlMode;

public interface IExtractor extends IConduit {

  void setExtractionRedstoneMode(RedstoneControlMode mode, ForgeDirection dir);

  RedstoneControlMode getExtractionRedstoneMode(ForgeDirection dir);

  void setExtractionSignalColor(ForgeDirection dir, DyeColor col);

  DyeColor getExtractionSignalColor(ForgeDirection dir);

}
