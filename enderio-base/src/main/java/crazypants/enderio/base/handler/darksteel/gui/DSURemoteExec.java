package crazypants.enderio.base.handler.darksteel.gui;

import javax.annotation.Nonnull;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface DSURemoteExec {

  int ID_SET_TAB = 0;

  public interface GUI extends IRemoteExec.IGui {

    default void setTab(@Nonnull ISlotSelector tab) {
      GuiPacket.send(this, ID_SET_TAB, tab.getTabOrder());
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    @Nonnull
    ISlotSelector setTab(int tab);

    @Override
    default IMessage networkExec(int id, GuiPacket message) {
      if (id == ID_SET_TAB) {
        setTab(message.getInt(0));
      }
      return null;
    }
  }

}
