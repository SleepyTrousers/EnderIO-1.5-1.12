package info.loenwind.autosave.handlers.enderio;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import crazypants.enderio.base.transceiver.Channel;
import crazypants.enderio.base.transceiver.ChannelList;
import crazypants.enderio.base.transceiver.ChannelType;
import info.loenwind.autosave.handlers.internal.HandleStorable;
import info.loenwind.autosave.handlers.java.HandleAbstractCollection;
import info.loenwind.autosave.handlers.java.HandleAbstractEnumMap;

public class HandleChannelList extends HandleAbstractEnumMap<ChannelType, Set<Channel>> {

  public HandleChannelList() {
    super(ChannelType.class, new HandleAbstractCollection<Channel, Set<Channel>>(new HandleStorable<>()) {

      @Override
      protected @Nonnull Set<Channel> makeCollection() {
        return new HashSet<>();
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
  
}
