package crazypants.enderio.base.autosave.enderio;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.item.coordselector.TelepadTarget;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;

public class HandleTelepadTarget implements IHandler<TelepadTarget> {

  @Override
  public @Nonnull Class<?> getRootType() {
    return TelepadTarget.class;
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull Type type, @Nonnull String name1,
      @Nonnull TelepadTarget object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound root = new NBTTagCompound();
    object.writeToNBT(root);
    nbt.setTag(name1, root);
    return true;
  }

  @Override
  @Nullable
  public TelepadTarget read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull Type type,
      @Nonnull String name1, @Nullable TelepadTarget object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name1)) {
      NBTTagCompound root = nbt.getCompoundTag(name1);
      return TelepadTarget.readFromNBT(root);
    }
    return new TelepadTarget();
  }

}
