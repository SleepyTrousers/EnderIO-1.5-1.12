package crazypants.enderio.base.transceiver;

import java.util.Set;

import javax.annotation.Nonnull;

public interface IChanneledMachine {

  @Nonnull
  Set<Channel> getSendChannels(@Nonnull ChannelType type);

  @Nonnull
  Set<Channel> getRecieveChannels(@Nonnull ChannelType type);

}
