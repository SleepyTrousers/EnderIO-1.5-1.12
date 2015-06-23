package crazypants.enderio.machine.transceiver;

import com.enderio.core.common.util.PlayerUtil;

import crazypants.enderio.EnderIO;

public class ClientChannelRegister extends ChannelRegister {

  public static final ChannelRegister instance = new ClientChannelRegister();

  private ClientChannelRegister() {
    reset();
  }

  @Override
  public void addChannel(Channel channel) {
    if(channel == null) {
      return;
    }
    if(channel.getUser() != null && !channel.getUser().equals(PlayerUtil.getPlayerUUID(EnderIO.proxy.getClientPlayer().getGameProfile().getName()))) {
      return;
    }
    super.addChannel(channel);
  }

  
  
}
