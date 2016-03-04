package crazypants.enderio;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IHaveRenderers {

  void registerRenderers();
  
}
