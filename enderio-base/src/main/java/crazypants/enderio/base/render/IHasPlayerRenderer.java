package crazypants.enderio.base.render;

import javax.annotation.Nullable;

import crazypants.enderio.base.handler.darksteel.IRenderUpgrade;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IHasPlayerRenderer {

  @Nullable
  @SideOnly(Side.CLIENT)
  IRenderUpgrade getRender();

}