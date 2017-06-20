package crazypants.enderio.machine.transceiver;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ChannelList extends EnumMap<ChannelType, Set<Channel>> {

  private static final long serialVersionUID = 3922673078596352247L;

  public ChannelList() {
    super(ChannelType.class);
    for (ChannelType channelType : ChannelType.values()) {
      super.put(channelType, new HashSet<Channel>());
    }
  }

  @Override
  @Deprecated // so accidental usage shows up
  public Set<Channel> put(ChannelType key, Set<Channel> value) {
    throw new NotImplementedException();
  }

  @Override
  @Deprecated // so accidental usage shows up
  public Set<Channel> remove(Object key) {
    throw new NotImplementedException();
  }

  @Override
  public void putAll(Map<? extends ChannelType, ? extends Set<Channel>> source) {
    for (Set<Channel> chan : source.values()) {
      for (Channel channel : chan) {
        add(channel);
      }
    }
  }

  @Override
  public void clear() {
    for (Set<Channel> set : values()) {
      set.clear();
    }
  }

  public boolean add(Channel channel) {
    if (channel != null) {
      return get(channel.getType()).add(channel);
    }
    return false;
  }

}