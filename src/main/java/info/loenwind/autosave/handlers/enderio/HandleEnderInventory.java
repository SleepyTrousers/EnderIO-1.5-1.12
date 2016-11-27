package info.loenwind.autosave.handlers.enderio;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.capability.EnderInventory;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;

public class HandleEnderInventory implements IHandler<EnderInventory> {

  public HandleEnderInventory() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return EnderInventory.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull EnderInventory object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    nbt.setTag(name, object.writeToNBT());
    return true;
  }

  @Override
  public EnderInventory read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable EnderInventory object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (object != null && nbt.hasKey(name)) {
      object.readFromNBT(nbt, name);
    }
    return object;
  }

}
