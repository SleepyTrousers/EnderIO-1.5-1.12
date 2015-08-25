package crazypants.enderio.machine.transceiver;

import java.util.Collection;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

public class ChannelRegister {

  protected Multimap<ChannelType, Channel> channels = MultimapBuilder.enumKeys(ChannelType.class).arrayListValues().build();

  public Collection<Channel> getChannelsForType(ChannelType type) {
    return channels.get(type);
  }

  public void addChannel(Channel channel) {
    if(channel == null) {
      return;
    }
    Collection<Channel> chans = getChannelsForType(channel.getType());
    if(!chans.contains(channel)) {
      chans.add(channel);
    }
  }

  public void removeChannel(Channel channel) {
    if(channel == null) {
      return;
    }
    getChannelsForType(channel.getType()).remove(channel);
  }

  public void reset() {
    channels.clear();
  }
}
