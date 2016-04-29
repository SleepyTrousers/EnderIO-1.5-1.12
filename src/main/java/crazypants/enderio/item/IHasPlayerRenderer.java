package crazypants.enderio.item;

import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.item.darksteel.upgrade.IRenderUpgrade;

public interface IHasPlayerRenderer {

  @Nullable
  @SideOnly(Side.CLIENT)
  IRenderUpgrade getRender();

}