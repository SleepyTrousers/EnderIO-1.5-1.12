package crazypants.enderio.base.handler.darksteel.gui;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface DSURemoteExec {

  int ID_SET_TAB = 0, ID_SET_NAME = 1;

  public interface GUI extends IRemoteExec.IGui {

    default void setTab(@Nonnull ISlotSelector tab) {
      GuiPacket.send(this, ID_SET_TAB, tab.getTabOrder());
    }

    default void updateItemName(@Nonnull String newName) {
      GuiPacket.send(this, ID_SET_NAME, newName);
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    @Nonnull
    ISlotSelector setTab(int tab);

    void updateItemName(@Nonnull String newName);

    @Override
    default IMessage networkExec(int id, GuiPacket message) {
      if (id == ID_SET_TAB) {
        setTab(message.getInt(0));
      }
      if (id == ID_SET_NAME) {
        updateItemName(NullHelper.first(message.getString(0), ""));
      }
      return null;
    }
  }

}
