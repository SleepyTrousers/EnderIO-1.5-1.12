package crazypants.enderio.api.teleport;

import javax.annotation.Nullable;

public interface ITelePad {

  /**
   * If this piece of the telepad is the master, meaning it is connected on all
   * 4 sides. This does <i>not</i> guarantee that it is in a connected state.
   * 
   * @return True if this TE is the master TE, false otherwise.
   */
  public boolean isMaster();

  /**
   * Gets the master telepad that this one is connected to.
   * 
   * @return The master telepad TE. {@code null} if not in a network. Itself if
   *         it is the master.
   */
  @Nullable
  public ITelePad getMaster();

  /**
   * If this telepad piece is in a network, meaning it is connected and in a
   * valid configuration.
   * 
   * @return True if this TE is in a network, false otherwise.
   */
  public boolean inNetwork();

  /**
   * The X coordinate stored in this telepad network. Always 0 if this is
   * {@link #inNetwork()} returns false.
   * 
   * @return The target X coordinate.
   */
  public int getX();

  /**
   * The Y coordinate stored in this telepad network. Always 0 if this is
   * {@link #inNetwork()} returns false.
   * 
   * @return The target Y coordinate.
   */
  public int getY();

  /**
   * The Z coordinate stored in this telepad network. Always 0 if this is
   * {@link #inNetwork()} returns false.
   * 
   * @return The target Z coordinate.
   */
  public int getZ();

  /**
   * Sets the target X coordinate of the network. Has no effect if
   * {@link #inNetwork()} returns false.
   * 
   * @return The master telepad TE. DOES NOT ALWAYS return itself.
   */
  public ITelePad setX(int x);

  /**
   * Sets the target Y coordinate of the network. Has no effect if
   * {@link #inNetwork()} returns false.
   * 
   * @return The master telepad TE. DOES NOT ALWAYS return itself.
   */
  public ITelePad setY(int y);

  /**
   * Sets the target Z coordinate of the network. Has no effect if
   * {@link #inNetwork()} returns false.
   * 
   * @return The master telepad TE. DOES NOT ALWAYS return itself.
   */
  public ITelePad setZ(int z);
}
