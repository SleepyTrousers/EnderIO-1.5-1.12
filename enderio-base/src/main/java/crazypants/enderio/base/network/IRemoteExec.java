package crazypants.enderio.base.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IRemoteExec {

  void setGuiID(int id);

  int getGuiID();

  public interface IContainer extends IRemoteExec {

    IMessage networkExec(int id, GuiPacket message);

  }

  public interface IGui extends IRemoteExec {

  }

}
