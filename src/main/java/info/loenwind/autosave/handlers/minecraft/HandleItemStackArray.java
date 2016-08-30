package info.loenwind.autosave.handlers.minecraft;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HandleItemStackArray implements IHandler<ItemStack[]> {

  public HandleItemStackArray() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return ItemStack[].class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull ItemStack[] object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setInteger("size", object.length);
    for (int i = 0; i < object.length; i++) {
      ItemStack itemStack = object[i];
      if (itemStack != null) {
        NBTTagCompound subtag = new NBTTagCompound();
        itemStack.writeToNBT(subtag);
        tag.setTag(i + "", subtag);
      }
    }
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public ItemStack[] read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable ItemStack[] object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      NBTTagCompound tag = nbt.getCompoundTag(name);
      int size = tag.getInteger("size");
      if (object == null || object.length != size) {
        object = new ItemStack[size];
      }
      for (int i = 0; i < object.length; i++) {
        if (tag.hasKey(i + "")) {
          object[i] = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(i + ""));
        } else {
          object[i] = null;
        }
      }
    }
    return object;
  }

}
