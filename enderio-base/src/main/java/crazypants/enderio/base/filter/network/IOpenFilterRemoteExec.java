package crazypants.enderio.base.filter.network;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IOpenFilterRemoteExec {

  static final int ID_OPEN_FILTER = 0;

  public interface GUI extends IRemoteExec.IGui {

    default void doOpenFilterGui(int filterIndex) {
      GuiPacket.send(this, ID_OPEN_FILTER, filterIndex);
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    IMessage doOpenFilterGui(int filterIndex);

    @Override
    default IMessage networkExec(int id, GuiPacket message) {
      if (id == ID_OPEN_FILTER) {
        return doOpenFilterGui(message.getInt(0));
      }
      return null;
    }
  }

}
