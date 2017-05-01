package crazypants.enderio.network;

public interface IRemoteExec {

  void setGuiID(int id);

  int getGuiID();

  public static interface IContainer extends IRemoteExec {

    void networkExec(int id, GuiPacket message);

  }

  public static interface IGui extends IRemoteExec {

  }

}