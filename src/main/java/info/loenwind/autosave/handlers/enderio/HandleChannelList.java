package info.loenwind.autosave.handlers.enderio;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import crazypants.enderio.transceiver.Channel;
import crazypants.enderio.transceiver.ChannelList;
import crazypants.enderio.transceiver.ChannelType;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.internal.HandleStorable;
import info.loenwind.autosave.handlers.java.HandleAbstractCollection;
import info.loenwind.autosave.handlers.java.HandleAbstractEnumMap;

public class HandleChannelList extends HandleAbstractEnumMap<ChannelType, Set<Channel>> {

  protected HandleChannelList(Class<ChannelType> enumClass, IHandler<Set<Channel>> valueHandler) {
    super(ChannelType.class, new HandleAbstractCollection<Channel, Set<Channel>>(new HandleStorable<>()) {

      @Override
      protected Set<Channel> makeCollection() {
        return new HashSet<>();
      }

    });
  }
  
  @Override
  public boolean canHandle(Class<?> clazz) {
    return ChannelList.class.isAssignableFrom(clazz);
  }

  @Override
  protected EnumMap<ChannelType, Set<Channel>> makeMap() {
    return new ChannelList();
  }
  
}
