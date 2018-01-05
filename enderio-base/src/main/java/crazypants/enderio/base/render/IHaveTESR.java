package crazypants.enderio.base.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IHaveTESR {

  @SideOnly(Side.CLIENT)
  void bindTileEntitySpecialRenderer();

}
