package crazypants.enderio.base.filter.network;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface ICloseFilterRemoteExec {

  static final int ID_CLOSE_FILTER = 0;

  public interface GUI extends IRemoteExec.IGui {

    default void doCloseFilterGui() {
      GuiPacket.send(this, ID_CLOSE_FILTER);
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    IMessage doCloseFilterGui();

    @Override
    default IMessage networkExec(int id, GuiPacket message) {
      if (id == ID_CLOSE_FILTER) {
        return doCloseFilterGui();
      }
      return null;
    }
  }

}
