package info.loenwind.autosave.handlers.enderio;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeRegistry;

public class HandleIMachineRecipe implements IHandler<IMachineRecipe> {

  public HandleIMachineRecipe() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return IMachineRecipe.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull IMachineRecipe object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    nbt.setString(name, object.getUid());
    return true;
  }

  @Override
  public IMachineRecipe read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nullable IMachineRecipe object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      return MachineRecipeRegistry.instance.getRecipeForUid(nbt.getString(name));
    }
    return object;
  }

}
