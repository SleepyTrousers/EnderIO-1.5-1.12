package crazypants.enderio.conduit;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;

import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.IConduit;
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

  public void writeToNBT(IConduit conduit, NBTTagCompound nbtRoot) {
    NBTTagCompound conduitRoot = new NBTTagCompound();
    ConduitUtil.writeToNBT(conduit, conduitRoot);
    NbtValue.CONDUIT.setTag(nbtRoot, conduitRoot);
  }

  @Nullable
  public static IConduit readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    NBTTagCompound conduitTag = NbtValue.CONDUIT.getTag(nbtRoot);
    if (conduitTag == null) {
      return null;
    }
    return ConduitUtil.readConduitFromNBT(conduitTag);
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return ConduitHandler.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull IConduit object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound root = new NBTTagCompound();
    writeToNBT(object, root);
    nbt.setTag(name, root);
    return true;
  }

  @Override
  public IConduit read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable IConduit object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      NBTTagCompound root = nbt.getCompoundTag(name);
      return readFromNBT(root);
    }
    return null;
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
