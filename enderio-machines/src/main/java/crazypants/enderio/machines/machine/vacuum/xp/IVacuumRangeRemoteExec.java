package crazypants.enderio.machines.machine.vacuum.xp;

import javax.annotation.Nonnull;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IVacuumRangeRemoteExec {

  static final int ID_SET_VACUUM_RANGE = 0;

  public interface GUI extends IRemoteExec.IGui {

    default void doSetVacuumRange(int range) {
      GuiPacket.send(this, ID_SET_VACUUM_RANGE, range);
    }
  }

  public interface Container extends IRemoteExec.IContainer {

    IMessage doSetVacuumRange(int range);

    @Override
    default IMessage networkExec(int id, @Nonnull GuiPacket message) {
      if (id == ID_SET_VACUUM_RANGE) {
        return doSetVacuumRange(message.getInt(0));
      }
      return null;
    }

  }

}
