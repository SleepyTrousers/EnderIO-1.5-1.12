package crazypants.enderio.base.conduit;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;

/**
 * 
 * @author EpicSquid315
 *
 * @param <T>
 *          Base Conduit Class
 * @param <I>
 *          Implementation of the Conduit Class
 */
public interface IConduitNetwork<T extends IConduit, I extends T> {

  // TODO: Tidy and edit Javadocs
  public void init(@Nonnull IConduitBundle tile, Collection<I> connections, @Nonnull World world);

  public @Nonnull Class<T> getBaseConduitType();

  public void setNetwork(@Nonnull World world, @Nonnull IConduitBundle tile);

  public void addConduit(@Nonnull I newConduit);

  public void destroyNetwork();

  public @Nonnull List<I> getConduits();

  public void sendBlockUpdatesForEntireNetwork();

  public void doNetworkTick(@Nonnull Profiler profiler);

}
