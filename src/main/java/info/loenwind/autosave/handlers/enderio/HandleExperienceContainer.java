package info.loenwind.autosave.handlers.enderio;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.xp.ExperienceContainer;
import info.loenwind.autosave.Registry;
import com.enderio.core.common.NBTAction;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;

public class HandleExperienceContainer implements IHandler<ExperienceContainer> {

  public HandleExperienceContainer() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return ExperienceContainer.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull ExperienceContainer object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    object.writeToNBT(tag);
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public ExperienceContainer read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field,
      @Nonnull String name, @Nullable ExperienceContainer object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name) && object != null) {
      object.readFromNBT(nbt.getCompoundTag(name));
    }
    return object;
  }

}
