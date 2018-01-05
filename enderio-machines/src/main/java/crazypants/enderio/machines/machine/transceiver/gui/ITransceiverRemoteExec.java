package crazypants.enderio.machines.machine.transceiver.gui;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface ITransceiverRemoteExec {

  public static final int EXEC_SET_BUFFER = 0;

  public interface GUI extends IRemoteExec.IGui {

    default void doSetBufferStacks(boolean bufferStacks) {
      GuiPacket.send(this, EXEC_SET_BUFFER, bufferStacks);
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    IMessage doSetBufferStacks(boolean bufferStacks);

    @Override
    default IMessage networkExec(int id, GuiPacket message) {
      switch (id) {
      case EXEC_SET_BUFFER:
        return doSetBufferStacks(message.getBoolean(0));
      }
      return null;
    }

  }

}
