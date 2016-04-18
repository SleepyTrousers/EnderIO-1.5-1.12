package info.loenwind.autosave.handlers.enderio;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.xp.ExperienceContainer;

public class HandleExperienceContainer implements IHandler<ExperienceContainer> {

  public HandleExperienceContainer() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return ExperienceContainer.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull ExperienceContainer object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    object.writeToNBT(tag);
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public ExperienceContainer read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nullable ExperienceContainer object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name) && object != null) {
      object.readFromNBT(nbt.getCompoundTag(name));
    }
    return object;
  }

}
