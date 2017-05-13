package crazypants.enderio.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IRemoteExec {

  void setGuiID(int id);

  int getGuiID();

  public static interface IContainer extends IRemoteExec {

    IMessage networkExec(int id, GuiPacket message);

  }

  public static interface IGui extends IRemoteExec {

  }

}