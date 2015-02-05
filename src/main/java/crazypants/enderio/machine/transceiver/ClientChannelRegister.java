package crazypants.enderio.machine.transceiver;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.util.PlayerUtil;

import java.util.UUID;


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
