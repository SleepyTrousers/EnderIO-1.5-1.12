package crazypants.enderio.machine.transceiver;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class ChannelRegister {

  protected EnumMap<ChannelType, List<Channel>> channels = new EnumMap<ChannelType, List<Channel>>(ChannelType.class);

  public List<Channel> getChannelsForType(ChannelType type) {
    return channels.get(type);
  }

  public void addChannel(Channel channel) {
    if(channel == null) {
      return;
    }
    List<Channel> chans = getChannelsForType(channel.getType());
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
    for (ChannelType type : ChannelType.values()) {
      channels.put(type, new ArrayList<Channel>());
    }
  }

}
