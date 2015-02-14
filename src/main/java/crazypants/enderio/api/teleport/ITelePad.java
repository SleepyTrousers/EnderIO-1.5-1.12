package crazypants.enderio.api.teleport;

import javax.annotation.Nullable;

import crazypants.util.BlockCoord;

public interface ITelePad extends ITravelAccessable {

  /**
   * If this piece of the telepad is the master, meaning it is connected on all
   * 4 sides. This does <i>not</i> guarantee that it is in a connected state.
   * 
   * @return True if this TE is the master TE, false otherwise.
   */
  boolean isMaster();

  /**
   * Gets the master telepad that this one is connected to.
   * 
   * @return The master telepad TE. {@code null} if not in a network. Itself if
   *         it is the master.
   */
  @Nullable
  ITelePad getMaster();

  /**
   * If this telepad piece is in a network, meaning it is connected and in a
   * valid configuration.
   * 
   * @return True if this TE is in a network, false otherwise.
   */
  boolean inNetwork();

  /**
   * The X coordinate stored in this telepad network. Always 0 if this is
   * {@link #inNetwork()} returns false.
   * 
   * @return The target X coordinate.
   */
  int getX();

  /**
   * The Y coordinate stored in this telepad network. Always 0 if this is
   * {@link #inNetwork()} returns false.
   * 
   * @return The target Y coordinate.
   */
  int getY();

  /**
   * The Z coordinate stored in this telepad network. Always 0 if this is
   * {@link #inNetwork()} returns false.
   * 
   * @return The target Z coordinate.
   */
  int getZ();

  /**
   * Sets the target X coordinate of the network. Has no effect if
   * {@link #inNetwork()} returns false.
   * 
   * @return The master telepad TE. DOES NOT ALWAYS return itself.
   */
  ITelePad setX(int x);

  /**
   * Sets the target Y coordinate of the network. Has no effect if
   * {@link #inNetwork()} returns false.
   * 
   * @return The master telepad TE. DOES NOT ALWAYS return itself.
   */
  ITelePad setY(int y);

  /**
   * Sets the target Z coordinate of the network. Has no effect if
   * {@link #inNetwork()} returns false.
   * 
   * @return The master telepad TE. DOES NOT ALWAYS return itself.
   */
  ITelePad setZ(int z);

  /**
   * Util method to set all coords using a {@link BlockCoord} object.
   * 
   * @see #setX(int)
   * @see #setY(int)
   * @see #setZ(int)
   * 
   * @param coords
   *          The coords to set this telepad to.
   */
  void setCoords(BlockCoord coords);
}
