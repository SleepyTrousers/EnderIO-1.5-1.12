package crazypants.enderio.base.transceiver;

import crazypants.enderio.base.EnderIO;

public class ClientChannelRegister extends ChannelRegister {

  public static final ChannelRegister instance = new ClientChannelRegister();

  private ClientChannelRegister() {
    reset();
  }

  @Override
  public void addChannel(Channel channel) {
    if (channel == null) {
      return;
    }
    if (!channel.isPublic() && !channel.getUser().equals(EnderIO.proxy.getClientPlayer().getGameProfile())) {
      return;
    }
    super.addChannel(channel);
  }

}
