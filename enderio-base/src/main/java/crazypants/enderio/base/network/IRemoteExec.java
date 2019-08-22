package crazypants.enderio.base.network;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IRemoteExec {

  void setGuiID(int id);

  int getGuiID();

  public interface IContainer extends IRemoteExec {

    @Nullable
    IMessage networkExec(int id, @Nonnull GuiPacket message);

  }

  public interface IGui extends IRemoteExec {

  }

}
