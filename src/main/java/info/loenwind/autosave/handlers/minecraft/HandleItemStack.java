package info.loenwind.autosave.handlers.minecraft;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HandleItemStack implements IHandler<ItemStack> {

  public HandleItemStack() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return ItemStack.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull ItemStack object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    object.writeToNBT(tag);
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public ItemStack read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nullable ItemStack object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      NBTTagCompound tag = nbt.getCompoundTag(name);
      if (object != null) {
        object.readFromNBT(tag);
      } else {
        return ItemStack.loadItemStackFromNBT(tag);
      }
    }
    return object;
  }

}
