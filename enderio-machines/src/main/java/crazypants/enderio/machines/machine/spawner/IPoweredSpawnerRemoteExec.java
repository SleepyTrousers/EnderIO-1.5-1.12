package crazypants.enderio.machines.machine.spawner;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IPoweredSpawnerRemoteExec {

  static final int ID_SET_MODE = 0;

  public interface GUI extends IRemoteExec.IGui {

    default void doSetSpawnMode(boolean isSpawn) {
      GuiPacket.send(this, ID_SET_MODE, isSpawn);
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    IMessage doSetSpawnMode(boolean isSpawn);

    @Override
    default IMessage networkExec(int id, GuiPacket message) {
      if (id == ID_SET_MODE) {
        return doSetSpawnMode(message.getBoolean(0));
      }
      return null;
    }

  }

}
