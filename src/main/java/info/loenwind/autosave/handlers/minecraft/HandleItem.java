package info.loenwind.autosave.handlers.minecraft;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HandleItem implements IHandler<Item> {

  public HandleItem() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return ItemStack.class.isAssignableFrom(clazz);
  }

  // Note: We can use the item ID here because ItemStack also uses it in its NBT-methods. So if the ID breaks, ItemStacks break even harder than we do here. 

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull Item object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    nbt.setShort(name, (short) Item.getIdFromItem(object));
    return true;
  }

  @Override
  public Item read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable Item object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      return Item.getItemById(nbt.getShort(name));
    }
    return object;
  }

}
