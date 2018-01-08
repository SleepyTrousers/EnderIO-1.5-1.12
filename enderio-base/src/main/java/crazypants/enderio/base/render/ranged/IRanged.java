package crazypants.enderio.base.render.ranged;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IRanged {

  @SideOnly(Side.CLIENT)
  boolean isShowingRange();

  @Nonnull
  BoundingBox getBounds();

}
