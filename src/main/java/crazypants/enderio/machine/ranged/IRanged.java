package crazypants.enderio.machine.ranged;

import com.enderio.core.client.render.BoundingBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IRanged {

  @SideOnly(Side.CLIENT)
  boolean isShowingRange();

  BoundingBox getBounds();

}
