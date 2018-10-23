package crazypants.enderio.base.autosave.enderio;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nullable;

import crazypants.enderio.util.CapturedMob;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;

public class HandleCapturedMob implements IHandler<CapturedMob> {

  public HandleCapturedMob() {
  }

  @Override
  public Class<?> getRootType() {
    return CapturedMob.class;
  }

  @Override
  public boolean store(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name, CapturedMob object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    nbt.setTag(name, object.toNbt(null));
    return true;
  }

  @Override
  @Nullable
  public CapturedMob read(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name,
      @Nullable CapturedMob object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      return CapturedMob.create(nbt.getCompoundTag(name));
    }
    return null;
  }

}
