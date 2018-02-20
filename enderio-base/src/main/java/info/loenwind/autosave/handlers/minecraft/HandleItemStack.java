package info.loenwind.autosave.handlers.minecraft;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;

import crazypants.enderio.util.Prep;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.engine.StorableEngine;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.endercore.HandleNNList;
import info.loenwind.autosave.handlers.java.HandleArrayList;
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
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull ItemStack object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (Prep.isInvalid(object)) {
      nbt.setBoolean(name + StorableEngine.EMPTY_POSTFIX, true);
    } else {
      NBTTagCompound tag = new NBTTagCompound();
      object.writeToNBT(tag);
      nbt.setTag(name, tag);
    }
    return true;
  }

  @Override
  public ItemStack read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable ItemStack object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      NBTTagCompound tag = nbt.getCompoundTag(name);
      return new ItemStack(tag);
    } else if (nbt.hasKey(name + StorableEngine.EMPTY_POSTFIX)) {
      return Prep.getEmpty();
    }
    return object;
  }

  public static class HandleItemStackArrayList extends HandleArrayList<ItemStack> {

    public HandleItemStackArrayList() {
      super(new HandleItemStack());
    }

    @Override
    protected @Nonnull ItemStack makeEmptyValueObject() {
      return Prep.getEmpty();
    }
  }

  public static class HandleItemStackNNList extends HandleNNList<ItemStack> {

    public HandleItemStackNNList() {
      super(new HandleItemStack());
    }

    @Override
    protected @Nonnull ItemStack makeEmptyValueObject() {
      return Prep.getEmpty();
    }

  }

}
