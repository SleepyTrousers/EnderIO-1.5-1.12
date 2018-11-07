package crazypants.enderio.base.autosave.endercore;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nullable;

import com.enderio.core.common.util.UserIdent;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;

public class HandleUserIdent implements IHandler<UserIdent> {

  public HandleUserIdent() {
  }

  @Override
  public Class<?> getRootType() {
    return UserIdent.class;
  }

  @Override
  public boolean store(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name, UserIdent object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    object.saveToNbt(nbt, name);
    return true;
  }

  @Override
  public UserIdent read(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name,
      @Nullable UserIdent object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    return UserIdent.readfromNbt(nbt, name);
  }
}
