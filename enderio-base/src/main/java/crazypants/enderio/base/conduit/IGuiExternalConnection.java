package crazypants.enderio.base.conduit;

import com.enderio.core.api.client.gui.IGuiScreen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumFacing;

public interface IGuiExternalConnection extends IGuiScreen {

  /**
   * Gets the direction of the conduit's connection
   */
  EnumFacing getDir();

  /**
   * Gets the conduit container
   */
  IExternalConnectionContainer getContainer();

  // The following are handled by the Gui class already but are needed here for abstraction to base
  int getGuiTop();

  FontRenderer getFontRenderer();
}
