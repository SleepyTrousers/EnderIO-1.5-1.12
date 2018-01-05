package crazypants.enderio.machines.machine.alloy;

import javax.annotation.Nonnull;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IAlloySmelterRemoteExec {

  static final int ID_MODE = 0;

  public interface GUI extends IRemoteExec.IGui {

    default void doSetMode(@Nonnull TileAlloySmelter.Mode mode) {
      GuiPacket.send(this, ID_MODE, mode);
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    IMessage doSetMode(@Nonnull TileAlloySmelter.Mode mode);

    @Override
    default IMessage networkExec(int id, GuiPacket message) {
      switch (id) {
      case ID_MODE:
        return doSetMode(message.getEnum(0, TileAlloySmelter.Mode.class));
      }
      return null;
    }

  }

}
