package crazypants.enderio.conduit;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.machine.RedstoneControlMode;
import net.minecraft.util.EnumFacing;

public interface IExtractor extends IConduit {

  void setExtractionRedstoneMode(RedstoneControlMode mode, EnumFacing dir);

  RedstoneControlMode getExtractionRedstoneMode(EnumFacing dir);

  void setExtractionSignalColor(EnumFacing dir, DyeColor col);

  DyeColor getExtractionSignalColor(EnumFacing dir);

}
