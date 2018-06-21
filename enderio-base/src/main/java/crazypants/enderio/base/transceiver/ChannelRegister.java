package crazypants.enderio.base.transceiver;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

import net.minecraft.server.MinecraftServer;

public class ChannelRegister {

  protected SetMultimap<ChannelType, Channel> channels = MultimapBuilder.enumKeys(ChannelType.class).hashSetValues().build();
  private long generation = 0;

  public Set<Channel> getChannelsForType(ChannelType type) {
    return Collections.unmodifiableSet(channels.get(type));
  }

  public Collection<Channel> getAllChannels() {
    return Collections.unmodifiableCollection(channels.values());
  }

  public void addChannel(Channel channel) {
    if (channel == null) {
      return;
    }
    channels.put(channel.getType(), channel);
    generation = MinecraftServer.getCurrentTimeMillis();
  }

  public void removeChannel(Channel channel) {
    if (channel == null) {
      return;
    }
    channels.get(channel.getType()).remove(channel);
    generation = MinecraftServer.getCurrentTimeMillis();
  }

  public void reset() {
    channels.clear();
    generation = 0;
  }

  protected long getGeneration() {
    return generation;
  }

  protected void setGeneration(long generation) {
    this.generation = generation;
  }

}
