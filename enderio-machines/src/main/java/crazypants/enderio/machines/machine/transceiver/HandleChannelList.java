package crazypants.enderio.machines.machine.transceiver;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.enderio.core.common.util.NullHelper;
import com.google.common.reflect.TypeToken;

import crazypants.enderio.autosave.handlers.EIOHandlers;
import crazypants.enderio.base.transceiver.Channel;
import crazypants.enderio.base.transceiver.ChannelList;
import crazypants.enderio.base.transceiver.ChannelType;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.util.DelegatingHandler;

public class HandleChannelList extends DelegatingHandler<ChannelList, EnumMap<ChannelType, Set<Channel>>> {

  public HandleChannelList() {
    super(ChannelList.class, getDelegate(), channels -> channels,
        map -> {
          ChannelList ret = new ChannelList();
          ret.putAll(map);
          // Error recorvery code
          for (Entry<ChannelType, Set<Channel>> entry : ret.entrySet()) {
            Iterator<Channel> iterator = entry.getValue().iterator();
            while (iterator.hasNext()) {
              if (iterator.next() == null) {
                iterator.remove();
              }
            }
          }
          return ret;
        });
  }
  
  @SuppressWarnings({ "unchecked", "serial" })
  private static IHandler<EnumMap<ChannelType, Set<Channel>>> getDelegate() {
    try {
      return EIOHandlers.REGISTRY.findHandlers(NullHelper.notnull(new TypeToken<EnumMap<ChannelType, Set<Channel>>>(){}.getType(), "TypeToken#getType")).stream()
          .filter(Objects::nonNull)
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("Could not find delegate handler for ChannelList"));
    } catch (IllegalAccessException | InstantiationException e) {
      throw new RuntimeException(e);
    }
  }
}
