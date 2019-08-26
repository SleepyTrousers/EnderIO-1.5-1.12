package crazypants.enderio.machines.machine.teleport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import crazypants.enderio.machines.machine.teleport.anchor.TileTravelAnchor;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ITravelAccessableRemoteExec {

  static final int EXEC_ACCESS_MODE = 0;
  static final int EXEC_LABEL = 1;
  static final int EXEC_VISIBLE = 2;
  static final int EXEC_CLOSE_GUI = 3;

  public interface GUI extends IRemoteExec.IGui {

    default void doSetAccessMode(@Nonnull TileTravelAnchor.AccessMode accesmode) {
      GuiPacket.send(this, EXEC_ACCESS_MODE, accesmode);
    }

    default void doSetLabel(@Nullable String label) {
      GuiPacket.send(this, EXEC_LABEL, label);
    }

    default void doSetVisible(boolean visible) {
      GuiPacket.send(this, EXEC_VISIBLE, visible);
    }

    default void doCloseGui() {
      GuiPacket.send(this, EXEC_CLOSE_GUI);
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    IMessage doSetAccessMode(@Nonnull TileTravelAnchor.AccessMode accesmode);

    IMessage doSetLabel(@Nullable String label);

    IMessage doSetVisible(boolean visible);

    IMessage doCloseGui();

    @Override
    default IMessage networkExec(int id, @Nonnull GuiPacket message) {
      switch (id) {
      case EXEC_ACCESS_MODE:
        return doSetAccessMode(message.getEnum(0, TileTravelAnchor.AccessMode.class));
      case EXEC_LABEL:
        return doSetLabel(message.getString(0));
      case EXEC_VISIBLE:
        return doSetVisible(message.getBoolean(0));
      case EXEC_CLOSE_GUI:
        return doCloseGui();
      }
      return null;
    }

  }

}
