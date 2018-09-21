package crazypants.enderio.conduits.conduit;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.util.NbtValue;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.java.util.HandleSimpleCollection;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;

public class ConduitHandler implements IHandler<IConduit> {

  @Override
  public @Nonnull Class<?> getRootType() {
    return IConduit.class;
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull Type type, @Nonnull String name, @Nonnull IConduit object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (object instanceof IServerConduit) {
      NBTTagCompound root = new NBTTagCompound();
      ConduitUtil.writeToNBT((IServerConduit) object, root);
      nbt.setTag(name, root);
    } else {
      Log.error("Logic error: Attempting to store client conduit procy as NBT for phase(S) " + phase);
    }
    return true;
  }

  @Override
  public IConduit read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull Type type, @Nonnull String name,
      @Nullable IConduit object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      NBTTagCompound root = nbt.getCompoundTag(name);
      object = read(phase, root);
      if (object == null) {
        // TODO: remove, this is for compatibility with early 1.12.2 dev builds
        NBTTagCompound conduitTag = NbtValue.CONDUIT.getTag(root);
        if (conduitTag != null) {
          object = read(phase, conduitTag);
        }
      }
    }
    return object;
  }

  private IConduit read(@Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound conduitTag) {
    return phase.contains(NBTAction.CLIENT) ? ConduitUtil.readClientConduitFromNBT(conduitTag) : ConduitUtil.readConduitFromNBT(conduitTag);
  }
  
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static class List extends HandleSimpleCollection<CopyOnWriteArrayList<IConduit>> {

    public List() throws NoHandlerFoundException {
      super((Class<CopyOnWriteArrayList<IConduit>>) (Class) CopyOnWriteArrayList.class);
    }
    
    protected List(Registry registry) throws NoHandlerFoundException {
      super((Class<CopyOnWriteArrayList<IConduit>>) (Class) CopyOnWriteArrayList.class, CopyOnWriteArrayList::new, registry, IConduit.class);
    }

    @Override
    protected IHandler<? extends CopyOnWriteArrayList<IConduit>> create(@Nonnull Registry registry, @Nonnull Type... types) throws NoHandlerFoundException {
      if (types[0] == IConduit.class) {
        return new List(registry);
      }
      return null;
    }

    @Override
    public CopyOnWriteArrayList<IConduit> read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull Type type,
        @Nonnull String name, @Nullable CopyOnWriteArrayList<IConduit> object)
        throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
      final CopyOnWriteArrayList<IConduit> result = super.read(registry, phase, nbt, type, name, object);
      if (result != null) {
        // Remove null (missing) conduits
        while (result.remove(null)) {
        }
      }
      return result;
    }

  }
}
