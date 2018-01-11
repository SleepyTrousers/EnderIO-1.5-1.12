package crazypants.enderio.base.conduit;

import net.minecraft.util.EnumFacing;

public interface IGuiExternalConnection {

  /**
   * Gets the direction of the conduit's connection
   */
  EnumFacing getDir();

  /**
   * Gets the conduit container
   */
  IExternalConnectionContainer getContainer();

}
