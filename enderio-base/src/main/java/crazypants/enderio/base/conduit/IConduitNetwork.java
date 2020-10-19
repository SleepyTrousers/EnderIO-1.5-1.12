package crazypants.enderio.base.conduit;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.ConduitUtil.UnloadedBlockException;
import crazypants.enderio.base.handler.ServerTickHandler.IServerTickListener;
import crazypants.enderio.base.handler.ServerTickHandler.ITickListener;
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
public interface IConduitNetwork<T extends IServerConduit, I extends T> extends ITickListener, IServerTickListener {

  // TODO: Tidy and edit Javadocs
  public void init(@Nonnull IConduitBundle tile, Collection<I> connections, @Nonnull World world) throws UnloadedBlockException;

  public @Nonnull Class<T> getBaseConduitType();

  public void setNetwork(@Nonnull World world, @Nonnull IConduitBundle tile) throws UnloadedBlockException;

  public void addConduit(@Nonnull I newConduit);

  public void destroyNetwork();

  public @Nonnull List<I> getConduits();

  public void sendBlockUpdatesForEntireNetwork();

}
