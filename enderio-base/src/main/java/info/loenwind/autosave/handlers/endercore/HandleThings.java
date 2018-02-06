package info.loenwind.autosave.handlers.endercore;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.stackable.Things;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class HandleThings implements IHandler<Things> {

  public HandleThings() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return Things.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull Things object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {

    NBTTagList list = new NBTTagList();
    for (NNIterator<String> itr = object.getNameList().fastIterator(); itr.hasNext();) {
      list.appendTag(new NBTTagString(itr.next()));
    }

    nbt.setTag(name, list);

    return true;
  }

  @Override
  public Things read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable Things object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {

    object = new Things();

    NBTTagList list = nbt.getTagList(name, 8);
    for (int i = 0; i < list.tagCount(); i++) {
      object.add(list.getStringTagAt(i));
    }

    return object;
  }

  public static class HandleThingsNNList extends HandleNNList<Things> {

    public HandleThingsNNList() {
      super(new HandleThings());
    }

  }

}
