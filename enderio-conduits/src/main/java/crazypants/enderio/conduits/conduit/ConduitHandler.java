package crazypants.enderio.conduits.conduit;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.util.NbtValue;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.java.HandleAbstractCollection;
import net.minecraft.nbt.NBTTagCompound;

public class ConduitHandler implements IHandler<IConduit> {

  static {
    Registry.GLOBAL_REGISTRY.register(new ConduitHandler());
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return ConduitHandler.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull IConduit object)
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
  public IConduit read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
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

  public static class ConduitCopyOnWriteArrayListHandler extends HandleAbstractCollection<IConduit, CopyOnWriteArrayList<IConduit>> {
    public ConduitCopyOnWriteArrayListHandler() {
      super(new ConduitHandler());
    }

    @Override
    protected @Nonnull CopyOnWriteArrayList<IConduit> makeCollection() {
      return new CopyOnWriteArrayList<IConduit>();
    }

    @Override
    public CopyOnWriteArrayList<IConduit> read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field,
        @Nonnull String name, @Nullable CopyOnWriteArrayList<IConduit> object)
        throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
      final CopyOnWriteArrayList<IConduit> result = super.read(registry, phase, nbt, field, name, object);
      if (result != null) {
        while (result.remove(null)) {
        }
      }
      return result;
    }

  }

}
