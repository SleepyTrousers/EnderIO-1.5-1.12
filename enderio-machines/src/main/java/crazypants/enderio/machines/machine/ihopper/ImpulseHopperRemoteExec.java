package crazypants.enderio.machines.machine.ihopper;

import javax.annotation.Nonnull;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface ImpulseHopperRemoteExec {

  static final int ID_LOCK_OUTPUT = 0;

  public interface GUI extends IRemoteExec.IGui {

    default void doOpenFilterGui(boolean isLocked) {
      GuiPacket.send(this, ID_LOCK_OUTPUT, isLocked);
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    IMessage doOpenFilterGui(boolean isLocked);

    @Override
    default IMessage networkExec(int id, @Nonnull GuiPacket message) {
      if (id == ID_LOCK_OUTPUT) {
        return doOpenFilterGui(message.getBoolean(0));
      }
      return null;
    }
  }

}
