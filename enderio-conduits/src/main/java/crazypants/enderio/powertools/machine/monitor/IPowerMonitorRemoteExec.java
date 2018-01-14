package crazypants.enderio.powertools.machine.monitor;

import javax.annotation.Nonnull;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IPowerMonitorRemoteExec {

  static final int ID_CONFIG = 0;

  public interface GUI extends IRemoteExec.IGui {

    default void doSetConfig(@Nonnull TilePowerMonitor tile, boolean engineControlEnabled, float startLevel, float stopLevel) {
      GuiPacket.send(this, ID_CONFIG, engineControlEnabled, Float.floatToIntBits(startLevel), Float.floatToIntBits(stopLevel));
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    IMessage doSetConfig(boolean engineControlEnabled, float startLevel, float stopLevel);

    @Override
    default IMessage networkExec(int id, GuiPacket message) {
      switch (id) {
      case ID_CONFIG:
        return doSetConfig(message.getBoolean(0), Float.intBitsToFloat(message.getInt(1)), Float.intBitsToFloat(message.getInt(2)));
      }
      return null;
    }

  }

}
