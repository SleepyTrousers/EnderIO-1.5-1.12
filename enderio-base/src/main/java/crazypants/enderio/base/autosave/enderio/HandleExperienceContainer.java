package crazypants.enderio.base.autosave.enderio;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nullable;

import crazypants.enderio.base.xp.ExperienceContainer;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;

public class HandleExperienceContainer implements IHandler<ExperienceContainer> {

  public HandleExperienceContainer() {
  }

  @Override
  public Class<?> getRootType() {
    return ExperienceContainer.class;
  }

  @Override
  public boolean store(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name,
      ExperienceContainer object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    object.writeToNBT(tag);
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  @Nullable
  public ExperienceContainer read(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type,
      String name, @Nullable ExperienceContainer object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name) && object != null) {
      object.readFromNBT(nbt.getCompoundTag(name));
    }
    return object;
  }

}
