package crazypants.enderio.machine.hypercube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientChannelRegister {

  static final ClientChannelRegister instance = new ClientChannelRegister();

  private final List<Channel> publicChannels = new ArrayList<Channel>();
  private final List<Channel> publicChannelsRO = Collections.unmodifiableList(publicChannels);

  private final List<Channel> privateChannels = new ArrayList<Channel>();
  private final List<Channel> privateChannelsRO = Collections.unmodifiableList(privateChannels);

  void setPublicChannels(List<Channel> channels) {
    publicChannels.clear();
    publicChannels.addAll(channels);
  }

  void setPrivateChannels(List<Channel> channels) {
    privateChannels.clear();
    privateChannels.addAll(channels);
  }

  public List<Channel> getPublicChannels() {
    return publicChannelsRO;
  }

  public List<Channel> getPrivateChannels() {
    return privateChannelsRO;
  }

  void addChannel(Channel channel) {
    if (channel == null) {
      return;
    }
    if (channel.isPublic()) {
      if (!publicChannels.contains(channel)) {
        publicChannels.add(channel);
      }
    } else {
      if(!privateChannels.contains(channel)) {
        privateChannels.add(channel);
      }
    }
  }

  public void channelAdded(Channel channel) {
    if (channel.isPublic() && !publicChannels.contains(channel)) {
      publicChannels.add(channel);
    }
  }

  public void reset() {
    publicChannels.clear();
    privateChannels.clear();    
  }

  public void channelRemoved(Channel c) {
    if(c.isPublic()) {
      publicChannels.remove(c);
    } else {
      privateChannels.remove(c);
    }
    
  }

}
