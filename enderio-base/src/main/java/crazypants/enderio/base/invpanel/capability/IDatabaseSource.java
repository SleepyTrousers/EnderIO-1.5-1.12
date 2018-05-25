package crazypants.enderio.base.invpanel.capability;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;

/**
 * Represents a source for database information
 * 
 * @param <I>
 *          Type of capability of the source
 */
public interface IDatabaseSource<I> {

  @Nonnull
  BlockPos getPos();

  @Nonnull
  I getSource();

}
