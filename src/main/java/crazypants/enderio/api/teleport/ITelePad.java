package crazypants.enderio.api.teleport;

import com.enderio.core.common.util.BlockCoord;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;

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
     * The target dimension ID for the telepad. Defaults to the dimension the
     * block is placed in.
     *
     * @return An int dimension ID.
     */
    int getTargetDim();

    /**
     * Sets the target X coordinate of the network. Has no effect if
     * {@link #inNetwork()} returns false.
     *
     * @param x
     *          The X coord.
     * @return The master telepad TE. DOES NOT ALWAYS return itself.
     */
    ITelePad setX(int x);

    /**
     * Sets the target Y coordinate of the network. Has no effect if
     * {@link #inNetwork()} returns false.
     *
     * @param y
     *          The Y coord.
     * @return The master telepad TE. DOES NOT ALWAYS return itself.
     */
    ITelePad setY(int y);

    /**
     * Sets the target Z coordinate of the network. Has no effect if
     * {@link #inNetwork()} returns false.
     *
     * @param z
     *          The Z coord.
     * @return The master telepad TE. DOES NOT ALWAYS return itself.
     */
    ITelePad setZ(int z);

    /**
     * Sets the target dimension of the network. Has no effect if
     * {@link #inNetwork()} returns false.
     *
     * @param dimID
     *          The dimension ID.
     * @return The master telepad TE. DOES NOT ALWAYS return itself.
     */
    ITelePad setTargetDim(int dimID);

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

    /**
     * Teleports a specific entity to the destination if:
     * <ul>
     * <li>The entity is in range (standing on top)</li>
     * <li>There is sufficient power to perform the operation</li>
     * </ul>
     *
     * @param entity
     *          The entity to teleport
     */
    void teleportSpecific(Entity entity);

    /**
     * Teleports all entities in range (in no particular order) until either there
     * are none left or the power runs out.
     */
    void teleportAll();
}
