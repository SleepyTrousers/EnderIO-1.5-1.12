package crazypants.enderio.autosave.handlers.endercore;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.stackable.Things;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class HandleThings implements IHandler<Things> {

  public HandleThings() {
  }

  @Override
  public Class<?> getRootType() {
    return Things.class;
  }

  @Override
  public boolean store(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name, Things object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {

    NBTTagList list = new NBTTagList();
    for (NNIterator<String> itr = object.getNameList().fastIterator(); itr.hasNext();) {
      list.appendTag(new NBTTagString(itr.next()));
    }

    nbt.setTag(name, list);

    return true;
  }

  @Override
  public Things read(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name,
      @Nullable Things object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {

    object = new Things();

    NBTTagList list = nbt.getTagList(name, 8);
    for (int i = 0; i < list.tagCount(); i++) {
      object.add(list.getStringTagAt(i));
    }

    return object;
  }
}
