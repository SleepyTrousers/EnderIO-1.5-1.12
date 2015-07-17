package crazypants.enderio.machine.transceiver;


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
    super.addChannel(channel);
  }

  
  
}
