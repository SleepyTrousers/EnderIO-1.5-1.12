package crazypants.enderio.base.filter.gui;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IFilterGuiRemoteExec {

  static final int ID_WHITELIST_MODE = 0;
  static final int ID_MATCH_META_MODE = 1;

  public interface GUI extends IRemoteExec.IGui {

    default void doSetWhitelistMode(boolean isBlacklist) {
      GuiPacket.send(this, ID_WHITELIST_MODE, isBlacklist);
    }

    default void doSetMatchMeta(boolean isMatchMeta) {
      GuiPacket.send(this, ID_MATCH_META_MODE, isMatchMeta);
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    IMessage doSetWhitelistMode(boolean isBlacklist);

    IMessage doSetMatchMetaMode(boolean isMatchMeta);

    @Override
    default IMessage networkExec(int id, GuiPacket message) {
      if (id == ID_WHITELIST_MODE) {
        return doSetWhitelistMode(message.getBoolean(0));
      } else if (id == ID_MATCH_META_MODE) {
        return doSetWhitelistMode(message.getBoolean(0));
      }
      return null;
    }
  }
}
