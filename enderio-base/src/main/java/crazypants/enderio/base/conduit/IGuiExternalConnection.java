package crazypants.enderio.base.conduit;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IGuiScreen;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumFacing;

public interface IGuiExternalConnection extends IGuiScreen {

  /**
   * Gets the direction of the conduit's connection
   */
  @Nonnull
  EnumFacing getDir();

  /**
   * Gets the conduit container
   */
  IExternalConnectionContainer getContainer();

  // The following are handled by the Gui class already but are needed here for abstraction to base
  int getGuiTop();

  FontRenderer getFontRenderer();

  void setGuiID(int id);

  int getGuiID();

}
