package crazypants.enderio.base.item.darksteel.upgrade.storage;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface StorageRemoteExec {

  static final int ID_SET_TAB = 0;

  public interface GUI extends IRemoteExec.IGui {

    default void setTab(int tab) {
      GuiPacket.send(this, ID_SET_TAB, tab);
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    IMessage setTab(int tab);

    @Override
    default IMessage networkExec(int id, GuiPacket message) {
      if (id == ID_SET_TAB) {
        return setTab(message.getInt(0));
      }
      return null;
    }
  }

}
