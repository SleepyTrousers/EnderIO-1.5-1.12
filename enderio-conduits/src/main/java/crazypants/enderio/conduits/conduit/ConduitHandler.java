package crazypants.enderio.conduits.conduit;

import java.lang.reflect.Field;
import java.util.Set;

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
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;

public class ConduitHandler implements IHandler<IConduit> {

  @Override
  public Class<?> getRootType() {
    return IConduit.class;
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
}
