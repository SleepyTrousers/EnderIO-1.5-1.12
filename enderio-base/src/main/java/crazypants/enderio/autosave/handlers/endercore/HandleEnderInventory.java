package crazypants.enderio.autosave.handlers.endercore;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.inventory.EnderInventory;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;

public class HandleEnderInventory implements IHandler<EnderInventory> {

  public HandleEnderInventory() {
  }

  @Override
  public @Nonnull Class<?> getRootType() {
    return EnderInventory.class;
  }

  @Override
  public boolean store(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name,
      EnderInventory object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    nbt.setTag(name, object.writeToNBT());
    return true;
  }

  @Override
  @Nullable
  public EnderInventory read(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name,
      @Nullable EnderInventory object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (object != null && nbt.hasKey(name)) {
      object.readFromNBT(nbt, name);
    }
    return object;
  }

}
