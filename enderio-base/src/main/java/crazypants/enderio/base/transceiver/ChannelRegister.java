package crazypants.enderio.base.transceiver;

import java.util.Set;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

public class ChannelRegister {

  protected SetMultimap<ChannelType, Channel> channels = MultimapBuilder.enumKeys(ChannelType.class).hashSetValues().build();

  public Set<Channel> getChannelsForType(ChannelType type) {
    return channels.get(type);
  }

  public void addChannel(Channel channel) {
    if (channel == null) {
      return;
    }
    channels.put(channel.getType(), channel);
  }

  public void removeChannel(Channel channel) {
    if (channel == null) {
      return;
    }
    getChannelsForType(channel.getType()).remove(channel);
  }

  public void reset() {
    channels.clear();
  }

}
