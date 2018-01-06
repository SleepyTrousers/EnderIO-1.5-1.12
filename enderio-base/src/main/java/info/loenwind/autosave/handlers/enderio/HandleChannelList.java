package info.loenwind.autosave.handlers.enderio;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;

import crazypants.enderio.base.transceiver.Channel;
import crazypants.enderio.base.transceiver.ChannelList;
import crazypants.enderio.base.transceiver.ChannelType;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.internal.HandleStorable;
import info.loenwind.autosave.handlers.java.HandleAbstractCollection;
import info.loenwind.autosave.handlers.java.HandleAbstractEnumMap;
import net.minecraft.nbt.NBTTagCompound;

public class HandleChannelList extends HandleAbstractEnumMap<ChannelType, Set<Channel>> {

  public HandleChannelList() {
    super(ChannelType.class, new HandleAbstractCollection<Channel, Set<Channel>>(new HandleStorable<>()) {

      @Override
      protected @Nonnull Set<Channel> makeCollection() {
        return new HashSet<>();
      }

      @Override
      protected @Nullable Channel makeEmptyValueObject() {
        return new Channel("(internal error)", ChannelType.ITEM);
      }
    });
  }
  
  @Override
  public boolean canHandle(Class<?> clazz) {
    return ChannelList.class.isAssignableFrom(clazz);
  }

  @Override
  protected @Nonnull EnumMap<ChannelType, Set<Channel>> makeMap() {
    return new ChannelList();
  }
  
  @Override
  public EnumMap<ChannelType, Set<Channel>> read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field,
      @Nonnull String name, @Nullable EnumMap<ChannelType, Set<Channel>> object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    object = super.read(registry, phase, nbt, field, name, object);
    if (object != null) {
      // error recovery code
      for (Entry<ChannelType, Set<Channel>> entry : object.entrySet()) {
        Iterator<Channel> iterator = entry.getValue().iterator();
        while (iterator.hasNext()) {
          if (iterator.next() == null) {
            iterator.remove();
          }
        }
      }
    }
    return object;
  }
}
